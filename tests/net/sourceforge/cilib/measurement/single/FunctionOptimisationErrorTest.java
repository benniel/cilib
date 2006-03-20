/*
 * FunctionOptimisationErrorTest.java
 * 
 * Created on Oct 25, 2005
 *
 * Copyright (C) 2003 - 2006 
 * Computational Intelligence Research Group (CIRG@UP)
 * Department of Computer Science 
 * University of Pretoria
 * South Africa
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package net.sourceforge.cilib.measurement.single;

import net.sourceforge.cilib.measurement.Measurement;
import net.sourceforge.cilib.measurement.single.FunctionOptimisationError;
import net.sourceforge.cilib.type.DomainParser;
import net.sourceforge.cilib.type.types.Real;
import net.sourceforge.cilib.type.types.Vector;
import junit.framework.TestCase;

/**
 * 
 * @author Gary Pampara
 */
public class FunctionOptimisationErrorTest extends TestCase {

	public void testFunctionOptimisationErrorDomain() {
		Measurement m = new FunctionOptimisationError();
		
		DomainParser parser = DomainParser.getInstance();
		parser.parse(m.getDomain());
		
		Vector vector = (Vector) parser.getBuiltRepresentation();
		
		assertEquals(1, vector.getDimension());
		assertTrue(vector.get(0) instanceof Real);
	}
}