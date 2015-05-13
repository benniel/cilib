/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.problem;

import net.sourceforge.cilib.problem.solution.Fitness;
import net.sourceforge.cilib.type.StringBasedDomainRegistry;
import net.sourceforge.cilib.type.types.Numeric;
import net.sourceforge.cilib.type.types.Type;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 * Integer mapping problem class. 
 */
public class IntegerMappingProblem extends AbstractProblem {
    protected FunctionOptimisationProblem delegate;
    protected int bitsPerDimension;
    protected int desiredBitsPerDimension;

    public IntegerMappingProblem() {
        domainRegistry = new StringBasedDomainRegistry();
    }

    public IntegerMappingProblem(IntegerMappingProblem copy) {
        this.domainRegistry = copy.domainRegistry.getClone();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public IntegerMappingProblem getClone() {
        return new IntegerMappingProblem(this);
    }
    
    @Override
    public void setDomain(String domain) {
        this.domainRegistry.setDomainString(domain);
        this.bitsPerDimension = calculateBitsPerDimension();
    }

    private int calculateBitsPerDimension() {
        Vector bounds = (Vector) this.domainRegistry.getBuiltRepresentation();
        double lowerBound = bounds.boundsOf(0).getLowerBound();
        double upperBound = bounds.boundsOf(0).getUpperBound();
        
        if (lowerBound != 0) {
            throw new IllegalArgumentException("Lower bound must be 0 to perform integer mapping.");
        }
        
        return (int) (Math.log(upperBound) / Math.log(2));
    }

    public void setDesiredBitsPerDimension(int desiredBitsPerDimension) {
        this.desiredBitsPerDimension = desiredBitsPerDimension;
        checkBitsPerDimension();
    }
    
    private void checkBitsPerDimension() {
        if (bitsPerDimension != desiredBitsPerDimension) {
            throw new IllegalStateException("Desired bits per dimension doesn't match actual bits per dimension. Adjust problem domain.");
        }
    }

    public Vector getGreyCode(Vector solution) {
//        for (Numeric n : (Vector)solution) {
//            System.out.print(n + " ");
//        }
//        System.out.println("");
        
//        System.out.println("solution: " + solution.size());
        
        Vector.Builder vector = Vector.newBuilder();
        StringBuilder sb = new StringBuilder();
        
        sb.append("%");
        sb.append(bitsPerDimension);
        sb.append("s");
        String format = sb.toString();
        
//        System.out.println("bpd: " + bitsPerDimension);
        sb = new StringBuilder();
        
        for (Numeric d : solution) {
            int integer = (int) Math.floor(d.doubleValue());
            
//            System.out.println("int: " + integer);
            
            char[] pre_binary = String.format(format, Integer.toBinaryString(integer)).replace(' ', '0').toCharArray();
            
            char[] binary = new char[bitsPerDimension];
            
            for (int i = 0; i < bitsPerDimension; i++) {
                binary[i] = pre_binary[i];
            }
            
//            System.out.println("individual lengths: " + binary.length);
            
            //convert to grey code
            for (int i = binary.length - 1; i > 0; i--) {
                if (binary[i - 1] == '1') {
                    binary[i] = (binary[i] == '1' ? '0' : '1');
                }
            }
            //System.out.println(integer + " --> " + String.valueOf(binary));
            
            sb.append(String.valueOf(binary));
        }
        
        char[] grey = sb.toString().toCharArray();

        for (int i = 0; i < grey.length; i++) {
            vector.add((grey[i] == '1' ? 1 : 0));
        }
        
//        System.out.println("binary: " + vector.build().size());
        return vector.build();
    }

    public void setDelegate(FunctionOptimisationProblem delegate) {
        this.delegate = delegate;
    }

    public Vector getBinarySolution(Type solution) {
        return getGreyCode((Vector) solution);
    }
    
    @Override
    protected Fitness calculateFitness(Type solution) {
//        System.out.println("in: ");
//        for (Numeric n : (Vector)solution) {
//            System.out.print(n + " ");
//        }
//        System.out.println("");
        
        Vector bitString = getBinarySolution(solution);
        
//        for (Numeric n : bitString) {
//            System.out.print(n + " ");
//        }
        
//        System.out.println("\nlength: " + bitString.size());
//        System.exit(1);
        return delegate.getFitness(bitString);
    }
}
