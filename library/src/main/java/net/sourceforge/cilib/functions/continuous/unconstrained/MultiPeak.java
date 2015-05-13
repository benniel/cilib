/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.cilib.functions.continuous.unconstrained;

import net.sourceforge.cilib.algorithm.AbstractAlgorithm;
import net.sourceforge.cilib.functions.ContinuousFunction;
import net.sourceforge.cilib.math.random.UniformDistribution;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 *
 * @author bennie
 */
public class MultiPeak extends ContinuousFunction {
    private double H = 30;
    private double W = 20;
    private int numPeaks = 3;
    private double [][] peaks = null;
    
    public MultiPeak() {
    }

    @Override
    public Double f(Vector a) {
        if (peaks == null) {
            initialise();
        }
        
        double max = Double.MIN_VALUE;
        
        for (int p = 0; p < numPeaks; p++) {
            double peakVal;
            double sum = 0.0;
            
            for (int d = 0; d < 2; d++) {
                sum += Math.pow(a.doubleValueOf(d) - peaks[p][d], 2);
            }
            
            peakVal = H / (1 + W * sum);
            
            if (peakVal > max) {
                max = peakVal;
            }
        }
        
        return max;
    }
    
    private void initialise() {
        System.out.println("1");
        peaks = new double[numPeaks][2];
        System.out.println("2");
        UniformDistribution rand = new UniformDistribution();
        System.out.println("3");
        //get problem domain boundaries
        Vector bounds = (Vector) AbstractAlgorithm.get().getOptimisationProblem().getDomain().getBuiltRepresentation();
        System.out.println("4");
        double upper = bounds.boundsOf(0).getUpperBound();
        double lower = bounds.boundsOf(0).getLowerBound();
        System.out.println("5");
        for (int p = 0; p < numPeaks; p++) {
            for (int d = 0; d < 2; d++) {
                peaks[p][d] = rand.getRandomNumber(lower, upper);
            }
        }
        System.out.println("6");
    }

    public void setH(double H) {
        this.H = H;
    }

    public void setW(double W) {
        this.W = W;
    }

    public void setNumPeaks(int numPeaks) {
        this.numPeaks = numPeaks;
    }    
}
