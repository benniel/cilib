/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.measurement.single;

import fj.Equal;
import fj.F;
import fj.F2;
import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.algorithm.population.MultiPopulationBasedAlgorithm;
import net.sourceforge.cilib.algorithm.population.SinglePopulationBasedAlgorithm;
import net.sourceforge.cilib.controlparameter.ConstantControlParameter;
import net.sourceforge.cilib.controlparameter.ControlParameter;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.measurement.Measurement;
import net.sourceforge.cilib.problem.solution.OptimisationSolution;
import net.sourceforge.cilib.type.types.container.TypeList;
import net.sourceforge.cilib.type.types.container.Vector;
import net.sourceforge.cilib.util.functions.Algorithms;
import fj.Ord;
import fj.Ordering;
import fj.data.List;
import net.sourceforge.cilib.algorithm.AbstractAlgorithm;
import net.sourceforge.cilib.functions.Gradient;
import net.sourceforge.cilib.problem.FunctionOptimisationProblem;
import net.sourceforge.cilib.problem.Problem;
import net.sourceforge.cilib.type.types.Real;
import net.sourceforge.cilib.util.distancemeasure.EuclideanDistanceMeasure;

public class LocalNiches implements Measurement<TypeList> {

private ControlParameter nicheRadius = ConstantControlParameter.of(0.01);
	
	public void setNicheRadius(ControlParameter nicheRadius) {
		this.nicheRadius = nicheRadius;
	}

    @Override
    public LocalNiches getClone() {
        return this;
    }

    @Override
    public TypeList getValue(Algorithm algorithm) {

        MultiPopulationBasedAlgorithm multi;
        if (algorithm instanceof SinglePopulationBasedAlgorithm) {
            multi = neighbourhood2populations((SinglePopulationBasedAlgorithm<Entity>) algorithm);
        } else {
            multi = (MultiPopulationBasedAlgorithm) algorithm;
        }
        
        Problem prob = algorithm.getOptimisationProblem();
        final GradientVectorLength gvl = new GradientVectorLength();
        
        List<Vector> niches = merge(List.iterableList(multi.getPopulations())
                                    .sort(
                                        Ord.ord(new F2<SinglePopulationBasedAlgorithm, SinglePopulationBasedAlgorithm, Ordering>() {
                                            public Ordering f(SinglePopulationBasedAlgorithm a, SinglePopulationBasedAlgorithm b) {
                                                    return Ordering.values()[-gvl.getValue(a).compareTo(gvl.getValue(b)) + 1];
                                            }
                                        }.curry()))
                                    .map(Algorithms.<SinglePopulationBasedAlgorithm>getBestSolution()),
        		List.<Vector>nil());
                
        Problem d = algorithm.getOptimisationProblem();
        FunctionOptimisationProblem fop = (FunctionOptimisationProblem)d;
        Gradient df = (Gradient)fop.getFunction();
        
        //return Real.valueOf(df.GetGradientVectorLength((Vector)best.getPosition()));
        
        TypeList tl = new TypeList();
        for (Vector element : niches) {
                TypeList tl2 = new TypeList();
                tl2.add(Real.valueOf(df.GetGradientVectorLength(element)));
                tl2.add(element);
                tl2.add(Real.valueOf(0));
                tl.add(tl2);
        }
        
        return tl; 
    }
    
    private List<Vector> merge(final List<OptimisationSolution> swarm, final List<Vector> acc) {
    	if (swarm.isEmpty()) {
    		return acc;
    	}
    	
    	if (swarm.tail().isEmpty()) {
    		return merge(List.<OptimisationSolution>nil(), acc.cons((Vector) swarm.head().getPosition()));
    	}
    	
    	List<OptimisationSolution> toMerge = swarm.tail().filter(new F<OptimisationSolution, Boolean>() {
			@Override
			public Boolean f(OptimisationSolution e) {
				return new EuclideanDistanceMeasure()
					.distance((Vector) swarm.head().getPosition(), (Vector) e.getPosition()) < nicheRadius.getParameter();
			}
		}).cons(swarm.head());
    	
    	return merge(swarm.minus(Equal.<OptimisationSolution>anyEqual(), toMerge), 
    			acc.cons((Vector) toMerge.maximum(Ord.<OptimisationSolution>comparableOrd()).getPosition()));
    }

    private <E extends Entity> MultiPopulationBasedAlgorithm neighbourhood2populations(SinglePopulationBasedAlgorithm<E> s) {
        MultiPopulationBasedAlgorithm m = new MultiPopulationBasedAlgorithm() {
            @Override
            protected void algorithmIteration() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public AbstractAlgorithm getClone() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public OptimisationSolution getBestSolution() {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public Iterable<OptimisationSolution> getSolutions() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };
        
        for (E e : s.getTopology()) {
            SinglePopulationBasedAlgorithm dummyPopulation = s.getClone();
            dummyPopulation.setTopology(List.list(e));
            m.addPopulationBasedAlgorithm(dummyPopulation);
        }
        
        return m;
    }
}
