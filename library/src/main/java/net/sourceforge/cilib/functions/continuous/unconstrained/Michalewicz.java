/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.functions.continuous.unconstrained;

import net.sourceforge.cilib.functions.ContinuousFunction;
import net.sourceforge.cilib.functions.Gradient;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 * <p>Michalewicz funtion 12.</p>
 *
 ** <p><b>Reference:</b>
 *  http://www.geatbx.com/docu/fcnindex-01.html#TopOfPage</p>
 *
 * <p>Minimum:
 * <ul>
 * <li> if n=5 then f(<b>x</b>*) = -4.687 </li>
 * <li> if n=10 then f(<b>x</b>*) = -9.66 </li>
 * <li> for x<sub>i</sub> in [0, pi]</li>
 * </ul>
 * </p>
 *
 * Characteristics:
 * <ul>
 * <li>Multi-modal</li>
 * <li>Has n! local minima</li>
 * <li>Non-separable</li>
 * </ul>
 *
 * R(0, 3.141592653589793)^10
 *
 */
public class Michalewicz extends ContinuousFunction implements Gradient {

    private static final long serialVersionUID = -4391269929189674709L;
    /**
     * m controls the steepness of the valleys; the larger m, the
     * more difficult the search
     */
    private int m = 10;

    /**
     * {@inheritDoc}
     */
    @Override
    public Double f(Vector input) {
        double sumsq = 0.0;

        for (int i = 0; i < input.size(); i++) {
            double x = input.doubleValueOf(i);
            sumsq += Math.sin(x) * Math.pow(Math.sin((x * x)/Math.PI), 2.0*m);
        }

        return -sumsq;
    }

    public Double df(Vector input, int i){
    double result=0.0;
    double exp1=-Math.cos(input.doubleValueOf(i-1))*Math.pow(Math.sin((input.doubleValueOf(i-1)*input.doubleValueOf(i-1))/Math.PI), 2.0*m);
    double exp2=((40.0*input.doubleValueOf(i-1))/Math.PI)*Math.sin(input.doubleValueOf(i-1))*Math.pow(Math.sin(input.doubleValueOf(i-1)*input.doubleValueOf(i-1)/Math.PI),19.0)*Math.cos(input.doubleValueOf(i-1)*input.doubleValueOf(i-1)/Math.PI);
    
    result=exp1-exp2;
    
    return result;
    }
    
    
    /**
     * Get the current value of <code>M</code>.
     * @return The value of <code>M</code>.
     */
    public int getM() {
        return m;
    }

    /**
     * Set the value of <code>M</code>.
     * @param m The value to set.
     */
    public void setM(int m) {
        this.m = m;
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
}
