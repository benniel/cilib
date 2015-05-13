/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.measurement.single;

import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.measurement.Measurement;
import net.sourceforge.cilib.problem.AngleModulationProblem;
import net.sourceforge.cilib.problem.FunctionOptimisationProblem;
import net.sourceforge.cilib.problem.IntegerMappingProblem;
import net.sourceforge.cilib.problem.decorators.MinMaxAngleModulationProblem;
import net.sourceforge.cilib.type.types.StringType;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 * Print the position of the best entity within a topology.
 *
 */
public class BestBinarySolution implements Measurement<StringType> {
    private static final long serialVersionUID = 5808686984197365658L;

    /**
     * {@inheritDoc}
     */
    @Override
    public BestBinarySolution getClone() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringType getValue(Algorithm algorithm) {
        Vector binarySolution = null;
        
        if(algorithm.getOptimisationProblem() instanceof AngleModulationProblem) {
            Vector solution = (Vector) algorithm.getBestSolution().getPosition();
            binarySolution = ((AngleModulationProblem)algorithm.getOptimisationProblem()).getBinarySolution(solution);
        } else if(algorithm.getOptimisationProblem() instanceof FunctionOptimisationProblem) {
            binarySolution = (Vector) algorithm.getBestSolution().getPosition();
        } else if(algorithm.getOptimisationProblem() instanceof IntegerMappingProblem) {
            Vector solution = (Vector) algorithm.getBestSolution().getPosition();
            binarySolution = ((IntegerMappingProblem)algorithm.getOptimisationProblem()).getBinarySolution(solution);
        } else if(algorithm.getOptimisationProblem() instanceof MinMaxAngleModulationProblem) {
            Vector solution = (Vector) algorithm.getBestSolution().getPosition();
            binarySolution = ((MinMaxAngleModulationProblem)algorithm.getOptimisationProblem()).getBinarySolution(solution);
        }
        
        
        return new StringType(binarySolution.toString());
    }

}
