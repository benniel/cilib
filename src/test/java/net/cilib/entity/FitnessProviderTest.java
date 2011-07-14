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

import fj.function.Doubles;
import fj.data.List;
import fj.data.Option;
import net.cilib.problem.Evaluatable;
import org.junit.Assert;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.equalTo;
import static net.cilib.predef.Predef.solution;

/**
 *
 * @author gpampara
 */
public class FitnessProviderTest {

    @Test
    public void testFinalize() {
        FitnessProvider provider = new FitnessProvider(new Evaluatable() {
            @Override
            public Option<Double> evaluate(List<Double> a) {
                return Option.some(a.foldLeft(Doubles.add, 0.0));
            }
        });

        Assert.assertThat(provider.evaluate(solution(1.0, 2.0)).some(), equalTo(3.0));
    }
}