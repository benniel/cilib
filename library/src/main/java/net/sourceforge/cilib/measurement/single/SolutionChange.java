/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.measurement.single;

import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.measurement.Measurement;
import net.sourceforge.cilib.type.types.StringType;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 */
public class SolutionChange implements Measurement<StringType> {
    private static final long serialVersionUID = -7485598441585703760L;
    private Vector previous = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public SolutionChange getClone() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringType getValue(Algorithm algorithm) {
        Vector best = (Vector) algorithm.getBestSolution().getPosition();
        
        if (!best.equals(previous)) {
            previous = best;
            return new StringType("10");
        }
        
        return new StringType("0");
    }

}
