/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.problem.decorators;

import net.sourceforge.cilib.functions.continuous.am.samplingstrategies.MinMaxAMSamplingStrategy;
import net.sourceforge.cilib.problem.AngleModulationProblem;
import net.sourceforge.cilib.problem.solution.Fitness;
import net.sourceforge.cilib.type.DomainRegistry;
import net.sourceforge.cilib.type.types.Type;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 * Decorates an AngleModulationProblem with additional dimensions to optimize
 * the sampling range of the generating function.
 */
public class MinMaxAngleModulationProblem extends AngleModulationProblem {
    private AngleModulationProblem delegate;

    public MinMaxAngleModulationProblem() {    
    }
    
    public MinMaxAngleModulationProblem(MinMaxAngleModulationProblem copy) {
        this.delegate = copy.delegate.getClone();
    }

    @Override
    public AngleModulationProblem getClone() {
        return new MinMaxAngleModulationProblem(this);
    }
    
    public void setDelegate(AngleModulationProblem delegate) {
        this.delegate = delegate;
    }

    public AngleModulationProblem getDelegate() {
        return delegate;
    }
    
    @Override
    public DomainRegistry getDomain() {
        DomainRegistry domain = delegate.getDomain().getClone();
        String dimension = String.valueOf(domain.getDimension() + 2);
        domain.setDomainString(domain.getDomainString().replaceAll("\\^.*", "^" + dimension));
        
        return domain;
    }
    
    public Vector getBinarySolution(Type solution) {
        delegate.getGeneratingFunction().setGeneratingFunctions(generatingFunctions);
        
        Vector solutionVector = (Vector) solution;
        StringBuilder sb = new StringBuilder();
        int coefficients = (solutionVector.size() / generatingFunctions) - 2;
        
        for (int g = 0; g < generatingFunctions; g++) {
            Vector.Builder vb = Vector.newBuilder();
            int start = g * coefficients;
            
            for (int b = start; b < start + coefficients + 2; b++) {
                vb.add(solutionVector.doubleValueOf(b));
            }
            
            Vector currentGf = vb.build();
            
            Double[] partialSolution = new Double[currentGf.size() - 2];
            for (int i = 0; i < partialSolution.length; i++) {
                partialSolution[i] = currentGf.doubleValueOf(i);
            }
            
            double min = currentGf.doubleValueOf(currentGf.size() - 2);
            double max = currentGf.doubleValueOf(currentGf.size() - 1);
            
            delegate.getGeneratingFunction().setSampler(new MinMaxAMSamplingStrategy(min, max));
            
            sb.append(delegate.getGeneratingFunction().f(Vector.of(partialSolution)));
        }
        
        String bitString = sb.toString();
        
//        Double[] partialSolution = new Double[solutionVector.size() - 2];
//        for (int i = 0; i < partialSolution.length; i++) {
//            partialSolution[i] = solutionVector.doubleValueOf(i);
//        }
//        
//        double min = solutionVector.doubleValueOf(solutionVector.size() - 2);
//        double max = solutionVector.doubleValueOf(solutionVector.size() - 1);
//        
//        delegate.getGeneratingFunction().setSampler(new MinMaxAMSamplingStrategy(min, max));
//        
//        String bitString = delegate.getGeneratingFunction().f(Vector.of(partialSolution));
        return delegate.decodeBitString(bitString, delegate.getGeneratingFunction().getBitsPerDimension());
    }

    @Override
    protected Fitness calculateFitness(Type solution) {

        return delegate.getGeneratingFunction().getDelegate().getFitness(getBinarySolution(solution));
    }
}
