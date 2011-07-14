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
package net.cilib.entity;

import fj.data.List;
import fj.data.Option;
import net.cilib.problem.Evaluatable;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

/**
 * @author gpampara
 */
public class IndividualProviderTest {

    @Test(expected = IllegalStateException.class)
    public void solutionRequired() {
        IndividualProvider provider = new IndividualProvider(null);
        provider.get();
    }

    @Test
    public void individualCreation() {
        FitnessProvider fitnessProvider = new FitnessProvider(new Evaluatable() {
            @Override
            public Option<Double> evaluate(List<Double> a) {
                return Option.some(1.0);
            }
        });

        IndividualProvider provider = new IndividualProvider(fitnessProvider);
        Individual i = provider.solution(List.<Double>list(0.0)).get();

        Assert.assertNotNull(i);
        Assert.assertThat(i.fitness().some(), equalTo(1.0));
    }
}
