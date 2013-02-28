/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.measurement.single.dynamic;

import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.functions.DynamicFunction;
import net.sourceforge.cilib.functions.continuous.dynamic.GeneralisedMovingPeaks;
import net.sourceforge.cilib.measurement.Measurement;
import net.sourceforge.cilib.problem.DynamicOptimisationProblem;
import net.sourceforge.cilib.problem.FunctionOptimisationProblem;
import net.sourceforge.cilib.type.types.StringType;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 * Give the current value of the global optimum of the
 * function.
 *
 *
 */
public class OptimumPosition implements Measurement<StringType> {

    private static final long serialVersionUID = 2658868675629949642L;

    public OptimumPosition() {
    }

    public OptimumPosition(OptimumPosition rhs) {
    }

    @Override
    public OptimumPosition clone() {
        return new OptimumPosition(this);
    }

    @Override
    public StringType getValue(Algorithm algorithm) {
        DynamicFunction<Vector, Double> problem = (DynamicFunction) ((DynamicOptimisationProblem)algorithm.getOptimisationProblem()).getFunction();
        
        StringBuilder position = new StringBuilder();
        Vector peak = problem.getOptimumPosition();
        
        position.append("\n");
        position.append(peak.doubleValueOf(0));
        position.append("\t");
        position.append(peak.doubleValueOf(1));
        position.append("\t");
        position.append(3);
        position.append("\t");
        position.append(30);
        
        return new StringType(position.toString());
    }

    @Override
    public Measurement getClone() {
        return new OptimumPosition(this);
    }
}
