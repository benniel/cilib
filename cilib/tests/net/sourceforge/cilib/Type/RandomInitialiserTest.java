/*
 * RandomInitialiserTest.java
 * JUnit based test
 *
 * Created on January 21, 2003, 4:45 PM
 *
 * 
 * Copyright (C) 2003, 2004 - CIRG@UP 
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

package net.sourceforge.cilib.Type;

import java.util.Random;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author espeer
 */
public class RandomInitialiserTest extends TestCase {
    
    public RandomInitialiserTest(java.lang.String testName) {
        super(testName);
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(RandomInitialiserTest.class);
        
        return suite;
    }
    
    public void setUp() {
    	initialiser = new RandomInitialiser(new Random());
    	parser = new DomainParser(initialiser);
    }
    
    public void testBit() {
    	int ones = 0;
    	int zeroes = 0;
    	for (int i = 0; i < 100; ++i) {
    		initialiser.reset();
    		parser.build("B");    	
    		Bit tmp = (Bit) initialiser.getResult();
    		if (tmp.getBit()) {
    			++ones;
    		}
    		else {
    			++zeroes;
    		}
    	}
    	assertTrue(ones > 10);
    	assertTrue(zeroes > 10);
    }
    
    public void testInt() {
    	int total = 0;
    	for (int i = 0; i < 100; ++i) {
    		initialiser.reset();
    		parser.build("Z(0, 100)");
    		Int tmp = (Int) initialiser.getResult();
    		assertTrue(tmp.getInt() <= 100);
    		assertTrue(tmp.getInt() >= 0);
    		total += tmp.getInt();
    	}
    	assertTrue(total >= 10);
    }
    
    public void testReal() {
    	double total = 0;
    	for (int i = 0; i < 100; ++i) {
    		initialiser.reset();
    		parser.build("R(0, 1)");
    		Real tmp = (Real) initialiser.getResult();
    		assertTrue(tmp.getReal() >= 0);
    		assertTrue(tmp.getReal() <= 1);
    		total += tmp.getReal();
    	}
    	assertTrue(total > 10);
    }
    
    public void testPrefixBitVector() {
    	parser.build("[B,B,B,B,B]");
    	BitVector tmp = (BitVector) initialiser.getResult();
    	assertEquals(5, tmp.getDimension());
    }
    
    public void testPostfixBitVector() {
    	parser.build("B^128");
    	BitVector tmp = (BitVector) initialiser.getResult();
    	assertEquals(128, tmp.getDimension());
    }
    
    public void testPostfixBitVectorWithSlack() {
    	int total = 0;
    	for (int i = 0; i < 100; ++i) {
    		initialiser.reset();
    		parser.build("B^10~5");
    		BitVector tmp = (BitVector) initialiser.getResult();
    		assertTrue(tmp.getDimension() >= 10);
    		assertTrue(tmp.getDimension() <= 15);
    		total += tmp.getDimension();
    	}
    	assertFalse(total == 1000);
    	assertFalse(total == 1500);
    }
    
    public void testPrefixRealVector() {
    	parser.build("[R, R]");
    	
    	MixedVector tmp = (MixedVector) initialiser.getResult();
    	assertEquals(2, tmp.getDimension());
    }
    
    public void testPostfixRealVector() {
    	parser.build("R(1, 10)^50");
    	
    	MixedVector tmp = (MixedVector) initialiser.getResult();
    	assertEquals(50, tmp.getDimension());
    	double total = 0;
    	for (int i = 0; i < 50; ++i) {
    		assertTrue(tmp.getReal(i) >= 1);
    		assertTrue(tmp.getReal(i) <= 10);
    		total += tmp.getReal(i);
    	}
    	assertFalse(total == 50);
    	assertFalse(total == 500);
    }
    
    public void testMixedRealBitVector() {
        parser.build("[R(-1, 1)^30, B^20]");
        Vector tmp = (Vector) initialiser.getResult();
    	assertEquals(50, tmp.getDimension());
  
    	for (int i = 0; i < 30; ++i) {
    		assertTrue(tmp.get(i) instanceof Real);
    		assertTrue(tmp.getReal(i) >= -1 && tmp.getReal(i) <= 1);
    	}
    	
    	for (int i = 31; i < 50; ++i) {
    		assertTrue(tmp.get(i) instanceof Bit);
    	}    		
    }
    
    public void testFlattenComposite() {
    	parser.build("[R(0,1),R,R^3,[[R,R], [R^10],R,R]^2,R]");
    	Vector tmp = (Vector) initialiser.getResult();
    	assertEquals(34, tmp.getDimension());
    
    	assertTrue(tmp.getReal(0) >= 0 && tmp.getReal(0) <= 1);
    	
    	for (int i = 0; i < tmp.getDimension(); ++i) {
    		assertTrue(tmp.get(i) instanceof Real);
    		assertFalse(Double.isNaN(tmp.getReal(i)));
    		assertFalse(Double.isInfinite(tmp.getReal(i)));
    	}
    }
    
    public void testAlternatingComposite() {
    	parser.build("[R,B]^5");
    	Vector tmp = (Vector) initialiser.getResult();
    	assertEquals(10, tmp.getDimension());
    	
    	for (int i = 0; i < 10; i +=2) {
    		assertTrue(tmp.get(i) instanceof Real);
    	}
    	
    	for (int i = 1; i < 10; i +=2) {
    		assertTrue(tmp.get(i) instanceof Bit);
    	}
    }
    
    public void testRealMatrix() {
    	parser.build("R^3^3");
    	Vector tmp = (Vector) initialiser.getResult();
    	assertEquals(9, tmp.getDimension());
    	
    	for (int i = 0; i < tmp.getDimension(); ++i) {
    		assertTrue(tmp.get(i) instanceof Real);
    		assertFalse(Double.isNaN(tmp.getReal(i)));
    		assertFalse(Double.isInfinite(tmp.getReal(i)));
    	}
    	    	
    }
    
    public void testPathologicalCase() {
    	parser.build("[[R,B^10]^4^3, [R, [R], [[R]], [Z,R, [R, R,[R, [[R]]]]^2], B],R,R,B^13,Z(-10,10)^3 ]^10");
    	Vector tmp = (Vector) initialiser.getResult();
    	assertEquals(1640, tmp.getDimension()); // ((11 * 4 * 3) + 5 + 8 + 1 + 2 + 13 + 3) * 10 = 1680
    	
    	// TODO: Check the contents
    }
    
    private DomainParser parser;
    private RandomInitialiser initialiser;
}
