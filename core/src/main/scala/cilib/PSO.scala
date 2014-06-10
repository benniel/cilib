package cilib

import scalaz._
import Scalaz._

import spire.math._
import spire.algebra._
import Position._

// These are some typeclasses that are needed to make the code much more generic and simpler, and can be extended.
trait Memory[F[_], S, A] {
  def mem: Lens[S, Position[F, A]]
}

object Memory {
  implicit def basicMemory[F[_], A] = new Memory[F, Mem[F, A], A] {
    def mem: Lens[Mem[F, A], Position[F, A]] = Lens.lensu((a, b) => a.copy(b = b), _.b)
  }
}

trait Neighbour[F[_], A] {
  type Collection = List[Position[F, A]]

  def neighbour: (Collection, Position[F, A]) => Position[F, A]
}

object Neighbour {
  def gbest[F[_], A] = new Neighbour[F, A] {
    def neighbour = (col, x) => x
  }
}

trait StepSize[F[_], S, A] {
  def step: Lens[S, Position[F, A]]
}

object StepSize {
  implicit def memStepSize[F[_], A] = new StepSize[F, Mem[F, A], A] {
    def step: Lens[Mem[F, A], Position[F, A]] = Lens.lensu((a, b) => a.copy(v = b), _.v)
  }
}

trait Charge[F[_], S, A] {
  def charge: Lens[S, Double]
}

object Charge {
  implicit def memCharge[F[_], A] = new Charge[F, ChargedMem[F, A], A] {
    def charge: Lens[ChargedMem[F, A], Double] = Lens.lensu((a, b) => a.copy(c = b), _.c)
  }
}

case class Mem[F[_], A](b: Position[F, A], v: Position[F, A])
case class ChargedMem[F[_], A](m: Mem[F, A], c: Double)

object PSO {

  def stdVelocity[S, F[_]: Zip: Traverse, A: Numeric](globalG: Neighbour[F, A], M: Memory[F, S, A], V: StepSize[F, S, A])(w: Double, c1: Double, c2: Double)(coll: List[(S, Position[F, A])], x: (S, Position[F, A])): RVar[(S, Position[F, A])] = {
    val n = implicitly[Numeric[A]]
    val newVel: RVar[Position[F, A]] = for {
      b <- (M.mem.get(x._1) - x._2).traverse(q => Dist.stdUniform.map(r => n.fromDouble(r * c1 * n.toDouble(q))))
      a <- (globalG.neighbour(coll.map(_._2), x._2) - x._2).traverse(q => Dist.stdUniform.map(r => n.fromDouble(r * c2 * n.toDouble(q))))
    } yield n.fromDouble(w) *: x._2 + b + a

    newVel.map(y => (V.step.set(x._1, y), x._2))
  }

  def chargedVelocity[S, F[_]: Zip: Traverse, A: Numeric](C: Charge[F, S, A], V: StepSize[F, S, A])(distance: (Position[F, A], Position[F, A]) => Double, rc: Double, rp: Double, vel: (List[(S, Position[F, A])], (S, Position[F, A])) => RVar[(S, Position[F, A])])(coll: List[(S, Position[F, A])], x: (S, Position[F, A])): RVar[(S, Position[F, A])] = {
    val n = implicitly[Numeric[A]]
    val acc: Position[F, A] = coll.foldLeft(x._2)((p1, p2) => {
      val d = distance(x._2, p2._2)
      p2 match {
        case `x` => p1
        case _ =>
          if (d > rp)
            p1
          else if (d < rc)
            n.fromDouble(C.charge.get(x._1) * C.charge.get(p2._1) / (d * rc * rc)) *: (x._2 - p2._2) + p1
          else
            n.fromDouble(C.charge.get(x._1) * C.charge.get(p2._1) / (d * d * d)) *: (x._2 - p2._2) + p1
      }
    })

    vel(coll, x).map(y => (V.step.set(x._1, acc + y._2), x._2))
  }

  def stdPos[F[_]: Zip: Functor, A: Numeric, S](vel: StepSize[F, S, A])(x: (S, Position[F, A])): RVar[(S, Position[F, A])] =
    RVar.point((x._1, x._2 + vel.step.get(x._1)))

  // Iterations are of the form: A => RVar[A], where A = (Mem[F,A], Position[F,A])
  def stdPSOIter[F[_]:Zip:Traverse, A:Numeric] = (coll: List[(Mem[F, A], Position[F, A])], a: (Mem[F, A], Position[F, A])) => for {
    vel <- stdVelocity[Mem[F, A], F, A](Neighbour.gbest, Memory.basicMemory, StepSize.memStepSize)(0.8, 1.4, 1.4)(coll, a)
    pos <- stdPos[F, A, Mem[F, A]](StepSize.memStepSize)(vel)
  } yield pos

}

case class Result[S, A](run: RVar[(S, A)]) {
  def mapfst[X](f: S => X) =
    Result(run map { x => (f(x._1), x._2) })

  def mapsnd[B](f: A => B) =
    Result(run map { x => (x._1, f(x._2)) })
}

object Runner {
  def syncIter[A, B](s: B => (List[A], A) => RVar[A]): B => List[A] => RVar[List[A]] =
    b => col => col.map { s(b)(col, _) }.sequence

  def asyncIter[A, B](s: B => (List[A], A) => RVar[A]): B => List[A] => RVar[List[A]] = b => col => {
    def one(col: List[A], acc: List[RVar[A]]): List[RVar[A]] =
      col match {
        case x :: xs => one(xs, acc :+ (acc.sequence.flatMap { c => s(b)(c ++ col, x) }))
        case Nil => acc
      }
    one(col, Nil).sequence
  }

  // for "hybrid" iteration strategies
  // def composedIter[A](s: (List[A], A) => RVar[A]) = syncIter(s)(_: List[A]).flatMap(asyncIter(s))

  // TODO: parsUpdate should use collection and iteration number somehow
  def runner[A, B](iter: B => List[A] => RVar[List[A]], parsUpdate: B => RVar[B], n: Int)(pars: B)(col: List[A]): RVar[(B, List[A])] = {
    def one = (args: (B, List[A]), i: Int) => for {
      newC <- iter(args._1)(args._2)
      newP <- parsUpdate(args._1) //TODO: use newC somehow
    } yield (newP, newC)

    (1 to n).toList.foldLeftM[RVar, (B, List[A])]((pars, col))(one)
  }
}

object TestRun {

  import Runner._
  import PSO._

  def initParticle(d: Int) =
    for {
      pos <- Dist.stdUniform.replicateM(d)
    } yield {
      val p = pos.toVector
      (Mem(Solution(p), Solution(p map {_ * 0})), Solution(p))
    }

  val x = (1 to 30).map(_ => initParticle(3)).toList.sequence.run(RNG.init(0)).run
  val col = x._2
  val rng = x._1

  //val i = runner((b: Int) => syncIter(stdPSOIter[Vector, Double]), RVar.point(_: Int), 10)(0)(col)


  import Heterogeneous._

  val pool = Pool.mkEvenPool(
    stdPSOIter[Vector, Double],
    stdPSOIter[Vector, Double],
    stdPSOIter[Vector, Double],
    stdPSOIter[Vector, Double]
  )

  val x2 = Initialization.random(col, pool).run(rng).run
  val col2 = x2._2
  val rng2 = x2._1

  type E = (Mem[Vector, Double], Position[Vector, Double])
  type B = (E, Behaviour[E])

  //val k: Int = syncIter[B, Pool[E]](dHPSO[E])
  val i = runner[B, Pool[E]](syncIter[B, Pool[E]](dHPSO[E]), RVar.point _, 10)(pool)(col2)
}


/*
next pso work:
==============
- dynamic psos (quantum, charged, etc) bennie
- vepso / dvepso (robert afer moo & dmoo)
- cooperative & variations
- heterogenous filipe

- niching (less important for now)

commonalities:
- subswarms

functions:
- moo & dmoo functions (benchmarks) robert

*/
