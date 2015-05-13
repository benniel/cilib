/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.measurement.single;

import net.sourceforge.cilib.algorithm.Algorithm;
import net.sourceforge.cilib.algorithm.population.HasTopology;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.measurement.Measurement;
import net.sourceforge.cilib.pso.particle.Particle;
import net.sourceforge.cilib.type.types.StringType;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 */
public class pBestChange implements Measurement<StringType> {
    /**
     * {@inheritDoc}
     */
    @Override
    public pBestChange getClone() {
        return this;
    }

    /**
     * Return 0.1 if any personal best values changed at the current iteration
     */
    @Override
    public StringType getValue(Algorithm algorithm) {
        fj.data.List<Entity> topology = ((HasTopology<Entity>)algorithm).getTopology();
        
        for (Entity e : topology) {
            Vector pbest = (Vector) ((Particle)e).getLocalGuide();
            Vector pos = (Vector) e.getCandidateSolution();
            Vector vel = (Vector) ((Particle)e).getVelocity();
            
            if (pos.equals(pbest) && vel.length() != 0) {
                return new StringType("0.1");
            }
        }
        
        return new StringType("0.0");
    }

}
