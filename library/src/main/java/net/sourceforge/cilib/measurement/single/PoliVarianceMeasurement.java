/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.measurement.single;

import java.util.Iterator;
import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.math.VectorMath;
import net.sourceforge.cilib.measurement.Measurement;
import net.sourceforge.cilib.pso.PSO;
import net.sourceforge.cilib.pso.particle.Particle;
import net.sourceforge.cilib.type.types.StringType;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 */
public class PoliVarianceMeasurement implements Measurement<StringType> {
    private static final long serialVersionUID = -7891715753767819344L;

    /**
     * {@inheritDoc}
     */
    @Override
    public PoliVarianceMeasurement getClone() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringType getValue(Algorithm algorithm) {
        double total = 0;

        PSO pso = (PSO) algorithm;
        
        Iterator<Particle> i = pso.getTopology().iterator();
        while (i.hasNext()) {
            Particle particle = i.next();
            
            Vector nbest = (Vector) particle.getNeighbourhoodBest().getPosition();
            Vector pbest = (Vector) particle.getBestPosition();
            
            double norm = nbest.subtract(pbest).length();
            
            total += norm;
        }
        
        total /= (double) pso.getTopology().length();
        total *= 1.0432;
        //total *= total;

        return new StringType(Double.toString(total));
    }

}
