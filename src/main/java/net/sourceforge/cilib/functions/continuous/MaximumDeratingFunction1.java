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
package net.sourceforge.cilib.functions.continuous;

import net.sourceforge.cilib.functions.ContinuousFunction;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 * R^1
 */
public class MaximumDeratingFunction1 implements ContinuousFunction {

    private static final long serialVersionUID = -2963512750988478604L;
    private double radius = 0.25;
    private double alpha = 2.0;

    /**
     * Set the value of the radius.
     * @param radius The value to set.
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }

    /**
     * Get the value of the radius.
     * @return The value of the radius.
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Set the value of <code>alpha</code>.
     * @param alpha The value to set.
     */
    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    /**
     * Get the value of alpha.
     * @return The value of alpha.
     */
    public double getAlpha() {
        return alpha;
    }

    /**
     * {@inheritDoc}
     */
    public Double apply(Vector input) {
        // the derating function is only to be used with Derating Function Problem
        // if the this class is misused, then prcocess will exit inorder to prevent
        // errorneous results.
        //if (parm1.length > 1) {
        if (input.size() > 1) {
            throw new RuntimeException("derating function may only be used in one dimension");
        }

        if (input.doubleValueOf(0) >= radius) {
            return 1.0;
        }

        return Math.pow(input.doubleValueOf(0) / radius, alpha);
    }
}
