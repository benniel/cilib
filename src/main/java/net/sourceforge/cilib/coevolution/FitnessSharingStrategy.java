/**
 * Computational Intelligence Library (CIlib)
 * Copyright (C) 2003 - 2010
 * Computational Intelligence Research Group (CIRG@UP)
 * Department of Computer Science
 * University of Pretoria
 * South Africa
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.cilib.coevolution;

import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.problem.Fitness;
import net.sourceforge.cilib.util.Cloneable;

/**
 * @author Julien Duhain
 *
 */
public abstract class FitnessSharingStrategy implements Cloneable {
    private static final long serialVersionUID = 6191655650337123237L;

    /**
     * modifies the fitness of the entity according to a specific fitness sharing strategy.
     * @param CoevolutionAlgorithm
     * @param ent Entity whose fitness is to be calculated
     * @return the fitness of the entity
     */
    public abstract Fitness modifyFitness(CoevolutionAlgorithm ca, Entity ent);
    public abstract FitnessSharingStrategy getClone();
}
