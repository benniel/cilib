/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.cilib.functions.continuous.unconstrained;

import net.sourceforge.cilib.functions.ContinuousFunction;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 *
 * @author bennie
 */
public class SinglePeak extends ContinuousFunction {
    private double H = 30;
    private double W = 20;
    private double X1 = 1.698;
    private double X2 = 0.452;

    @Override
    public Double f(Vector a) {
        return (H / (1 + W * (Math.pow(a.doubleValueOf(0) - X1, 2) + Math.pow(a.doubleValueOf(1) - X2, 2))));
    }

    public void setH(double H) {
        this.H = H;
    }

    public void setW(double W) {
        this.W = W;
    }
}
