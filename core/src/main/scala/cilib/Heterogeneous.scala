package cilib

import scalaz._
import Scalaz._

object Heterogeneous {

  type Behaviour[A] = (List[A], A) => RVar[A] // this is the same as an iteration i think

  case class Element[A](behaviour: Behaviour[A], score: Double)
  type Pool[A] = List[Element[A]]

  // TODO: use this behavior user thing instead of (A, Behaviour[A])
  // case class BehaviourUser[A](thing: A, element: Element[A]) // Le naming sucks

  import Selection._
  import Update._
  import Behaviour._

  def dHPSO[A]: Pool[A] => (List[(A, Behaviour[A])], (A, Behaviour[A])) => RVar[(A, Behaviour[A])]= 
    pool => (col, s) => random(pool)(s._1, s._2).flatMap(use(col, _))

  // TODO: condition thingy = if A improved, how to update pool??? different runner? who knows??!!?
  def fkPSO[A](n: Int): Pool[A] => (List[(A, Behaviour[A])], (A, Behaviour[A])) => RVar[(A, Behaviour[A])] = 
    pool => (col, s) => conditional[A]((_: A) => RVar.point(true), tournament(n))(pool)(s._1, s._2).flatMap(use(col, _))

  def sHPSO[A]: Pool[A] => (List[(A, Behaviour[A])], (A, Behaviour[A])) => RVar[(A, Behaviour[A])] = 
    pool => (col, s) => use(col, s)

  def charged[A](n: Int): Pool[A] => (List[(A, Behaviour[A])], (A, Behaviour[A])) => RVar[(A, Behaviour[A])] = sHPSO


  object Initialization {
    def random[A](coll: List[A], pool: Pool[A]): RVar[List[(A, Behaviour[A])]] =
      coll.map(e => RVar.choose(pool).map(b => (e, b.behaviour))).sequence

    def charged[F[_], A, S](cRatio: Double)(C: Charge[F, S, A])(coll: List[(S, Position[F, A])], pool: Pool[(S, Position[F, A])]): RVar[List[((S, Position[F, A]), Behaviour[(S, Position[F, A])])]] = ???
  }

  object Pool {
    def mkPool[A](w: Double, xs: Behaviour[A]*) =
      xs.map{ Element(_, w) }.toList

    def mkEvenPool[A](xs: Behaviour[A]*) =
      mkPool(1.0 / xs.length, xs: _*)

    def mkZeroPool[A](xs: Behaviour[A]*) =
      mkPool(0.0, xs: _*)
  }

  object Selection {
    // TODO: will need swarm as well for some of them
    type BehaviourSelection[A] = Pool[A] => (A, Behaviour[A]) => RVar[(A, Behaviour[A])]

    def select[A](selector: Pool[A] => RVar[Behaviour[A]]): BehaviourSelection[A] =
      pool => (p, b) => selector(pool) map { (p, _) }

    def random[A]: BehaviourSelection[A] =
      select((x: Pool[A]) => RVar.choose(x).map(_.behaviour))

    def rouletteWheel[A]: BehaviourSelection[A] = ???

    def tournament[A](n: Int): BehaviourSelection[A] = ???

    def boltzmann[A]: BehaviourSelection[A] = ??? //DPP-PSO

    // use with schedule
    def conditional[A](f: A => RVar[Boolean], g: BehaviourSelection[A]): BehaviourSelection[A] =
      pool => (p, b) => f(p) flatMap {
        if (_) g(pool)(p, b) else RVar.point((p, b))
      }
  }

  object Schedule {
    def random[A](prob: Double): A => RVar[Boolean] =
      _ => Dist.stdUniform map ( _ < prob)

    def periodic[A](n: Int): A => RVar[Boolean] = ??? // how to get iteration number?

    def pbestStagnation[A](n: Int): A => RVar[Boolean] = ??? // need stagnation counter

    def fitnessStagnation[A](n: Int): A => RVar[Boolean] = ??? // need stagnation counter
  }

  object Behaviour {
    def use[A]: (List[(A, Behaviour[A])], (A, Behaviour[A])) => RVar[(A, Behaviour[A])] =
      (col, s) => s._2(col.map(_._1), s._1) map ( (_, s._2) )
  }

  object Update {
    def identity[A]: Pool[A] => (A, Behaviour[A]) => Pool[A] =
      pool => (p, b) => pool

    def increment[A](f: A => Boolean): Pool[A] => (A, Behaviour[A]) => Pool[A] =
      pool => (p, b)  => pool.map(_ match {
        case Element(x, y) =>
          if (x == b && f(p))
            Element(x, y + 1.0)
          else
            Element(x, y)
      })

    def pheromone[A] = ???

  }
}

