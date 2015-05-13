/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.functions.continuous.am;

import com.google.common.base.Preconditions;
import net.sourceforge.cilib.functions.ContinuousFunction;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 * The standard modulation function used in angle modulation problems.
 */
public class SimpleAngleModulationFunction extends ContinuousFunction implements ModulationFunction {
    public Double f(Vector input) {
        Preconditions.checkState(input.size() == 2, "This function is only defined for two dimensions.");
        
        double x = input.doubleValueOf(0);
        double v = input.doubleValueOf(1);
        
        return Math.sin(v*(x+1));
    }

    public int coefficients() {
        return 1;
    }
}
