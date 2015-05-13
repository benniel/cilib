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
public class SpatialDisconnectFunction extends ContinuousFunction {
    private double interval = 5;
    
    @Override
    public Double f(Vector a) {
        return -Math.pow(Math.E, a.doubleValueOf(0) % interval);
    }

    public void setInterval(double interval) {
        this.interval = interval;
    }
}
