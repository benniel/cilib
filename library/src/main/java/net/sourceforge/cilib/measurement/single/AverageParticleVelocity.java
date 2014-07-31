/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.measurement.single;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.math.Stats;
import net.sourceforge.cilib.measurement.Measurement;
import net.sourceforge.cilib.pso.PSO;
import net.sourceforge.cilib.pso.particle.Particle;
import net.sourceforge.cilib.type.types.StringType;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 */
public class AverageParticleVelocity implements Measurement<StringType> {
    private static final long serialVersionUID = -7891715753767819344L;

    /**
     * {@inheritDoc}
     */
    @Override
    public AverageParticleVelocity getClone() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringType getValue(Algorithm algorithm) {
        PSO pso = (PSO) algorithm;
        fj.data.List<Particle> topology = pso.getTopology();
        List<Double> velocities = new ArrayList<Double>();
        
        Iterator<Particle> i = topology.iterator();
        
        while (i.hasNext()) {
            Particle p = i.next();
            
            velocities.add(((Vector)p.getVelocity()).length());
        }

        return new StringType(Stats.mean(velocities).toString());
    }

}
