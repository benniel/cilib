/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sourceforge.cilib.functions.continuous.unconstrained;

import net.sourceforge.cilib.functions.ContinuousFunction;
import net.sourceforge.cilib.math.random.UniformDistribution;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 *
 * @author bennie
 */
public class CF extends ContinuousFunction {

    private static final long serialVersionUID = -7803711986955989075L;

    /* (non-Javadoc)
     * @see net.sourceforge.cilib.functions.redux.ContinuousFunction#evaluate(net.sourceforge.cilib.type.types.container.Vector)
     */
    @Override
    public Double f(Vector input) {
        UniformDistribution rand = new UniformDistribution();
        
        return rand.getRandomNumber(-1000, 1000);
    }
}
