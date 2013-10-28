/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.functions.continuous.unconstrained;

import fj.data.List;
import net.sourceforge.cilib.functions.ContinuousFunction;
import net.sourceforge.cilib.functions.Gradient;

import net.sourceforge.cilib.problem.solution.OptimisationSolution;

import net.sourceforge.cilib.functions.NichingFunction;

import net.sourceforge.cilib.type.types.container.Vector;

/**
 * Multimodal1 function.
 *
 * Minimum: 0.0
 * R(0, 1)^1
 *
 */
public class EqualMinima extends ContinuousFunction implements Gradient, NichingFunction {

    private static final long serialVersionUID = -5261002551096587662L;

    /**
     * {@inheritDoc}
     */
    @Override
        public Double f(Vector input) {
        double sum = 0.0;
        for (int i = 0; i < input.size(); ++i) {
            sum += Math.pow(Math.sin(5.0 * Math.PI * input.doubleValueOf(i)), 6.0);
        }
        return -sum;
        
        
    }
    
    public Double df(Vector input, int i) {
        double res = 30.0*Math.PI*Math.pow(Math.sin(5.0 * Math.PI * input.doubleValueOf(i-1)), 5.0)*Math.cos(5.0 * Math.PI * input.doubleValueOf(i-1));
        return -res;
    }
    
    public double GetGradientVectorAverage ( Vector x)
    {
        
        double sum = 0;
        
        for (int i = 1; i <= x.size(); ++i)
        {
            sum += this.df(x,i);
        }
           
        return sum/x.size();
    }
    
    public double GetGradientVectorLength (Vector x)
    {
        double sumsqrt = 0;
        
        for (int i = 1; i <= x.size(); ++i)
        {
            sumsqrt += this.df(x,i)*this.df(x,i);
        }
        
        return Math.sqrt(sumsqrt);
    }
    
    public Vector GetGradientVector (Vector x)
    {
        Vector.Builder vectorBuilder = Vector.newBuilder();
        
        for (int i = 1; i <= x.size(); ++i)
        {
             vectorBuilder.add(this.df(x,i));
        }
        
        return vectorBuilder.build();
    }

	@Override
	public double getNicheRadius() {
		return 0.01;
	}
}