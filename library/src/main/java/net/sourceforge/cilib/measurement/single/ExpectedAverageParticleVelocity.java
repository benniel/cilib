/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.measurement.single;

import java.util.Iterator;
import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.measurement.Measurement;
import net.sourceforge.cilib.pso.PSO;
import net.sourceforge.cilib.pso.particle.Particle;
import net.sourceforge.cilib.pso.velocityprovider.StandardVelocityProvider;
import net.sourceforge.cilib.type.types.StringType;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 */
public class ExpectedAverageParticleVelocity implements Measurement<StringType> {
    /**
     * {@inheritDoc}
     */
    @Override
    public ExpectedAverageParticleVelocity getClone() {
        return this;
    }

    /**
     * Calculate the expected average particle velocity for the next iteration.
     * This measurement assumes that the standard velocity provider is used, and that c1 = c2.
     */
    @Override
    public StringType getValue(Algorithm algorithm) {
        double total = 0;

        PSO pso = (PSO) algorithm;
        Particle any = pso.getTopology().head();
        
        double c = ((StandardVelocityProvider)any.getVelocityProvider()).getCognitiveAcceleration().getParameter();
        double w = ((StandardVelocityProvider)any.getVelocityProvider()).getInertiaWeight().getParameter();
        
        Iterator<Particle> i = pso.getTopology().iterator();
        while (i.hasNext()) {
            Particle particle = i.next();
            
            Vector nbest = (Vector) particle.getNeighbourhoodBest().getPosition();
            Vector pbest = (Vector) particle.getBestPosition();
            
            double norm = nbest.subtract(pbest).length();
            
            total += norm;
        }
        
        total /= (double) pso.getTopology().length();
        total *= 0.5 * Math.sqrt((c * (w + 1)) / (c * (5 * w - 7) - (12 * w * w) + 12));

        return new StringType(Double.toString(total));
    }

}
