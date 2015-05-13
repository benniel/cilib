/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.problem;

import net.sourceforge.cilib.functions.continuous.am.AMBitGeneratingFunction;
import net.sourceforge.cilib.problem.solution.Fitness;
import net.sourceforge.cilib.type.StringBasedDomainRegistry;
import net.sourceforge.cilib.type.types.Numeric;
import net.sourceforge.cilib.type.types.Type;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 * Angle modulation problem class. 
 */
public class AngleModulationProblem extends AbstractProblem {
    private static final long serialVersionUID = -3492262439415251355L;
    private AMBitGeneratingFunction generatingFunction;
    protected int generatingFunctions;

    public AngleModulationProblem() {
        domainRegistry = new StringBasedDomainRegistry();
        domainRegistry.setDomainString("R(-1.0:1.0)^4");
        this.generatingFunction = new AMBitGeneratingFunction();
        this.generatingFunctions = 1;
    }

    public AngleModulationProblem(AngleModulationProblem copy) {
        this.generatingFunction = copy.generatingFunction.getClone();
        this.domainRegistry = copy.domainRegistry.getClone();
        this.generatingFunctions = copy.generatingFunctions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AngleModulationProblem getClone() {
        return new AngleModulationProblem(this);
    }

    /**
     * @param bits
     * @param bitsPerDimension
     * @return the decoded bit string.
     */
    public Vector decodeBitString(String bits, int bitsPerDimension) {
        Vector.Builder vector = Vector.newBuilder();
        
        for (int i = 0; i < bits.length(); i += bitsPerDimension) {
            double tmp = valueOf(bits, i, i + bitsPerDimension);
            
            if (bitsPerDimension > 1) {
                tmp = transform(tmp);
            }

            vector.add(tmp);
        }

        return vector.build();
    }

    /**
     * Determine the numeric value of the given bitstring.
     *
     * TODO: Move this into a class that will make sense.
     *
     * @param bitString The bitstring as a string
     * @param startIndex The starting index
     * @param endIndex The ending index
     * @return The value of the bitstring
     */
    public double valueOf(String bitString, int startIndex, int endIndex) {
        String substring = bitString.substring(startIndex, endIndex);
        
        return Integer.valueOf(substring, 2).intValue();
    }

    public double valueOf(String bitString, int index) {
        String substring = bitString.substring(index);
        return Integer.valueOf(substring, 2).intValue();
    }

    public double valueOf(String bitString) {
        return Integer.valueOf(bitString, 2).intValue();
    }

    /**
     *
     * @param number
     * @return the transformed number.
     */
    private double transform(double number) {
        double result = number;

        int tmp = 1;
        tmp <<= generatingFunction.getBitsPerDimension() - 1;
        result -= tmp;
        result /= Math.pow(10, generatingFunction.getPrecision());

        return result;
    }
    
    public void setGeneratingFunction(AMBitGeneratingFunction f) {
        this.generatingFunction = f;
        
        StringBuilder builder = new StringBuilder();
        Vector bounds = (Vector) this.domainRegistry.getBuiltRepresentation();
        double lowerBound = bounds.boundsOf(0).getLowerBound();
        double upperBound = bounds.boundsOf(0).getUpperBound();
        int dimensions = domainRegistry.getDimension();
        
        builder.append("R(");
        builder.append(String.valueOf(lowerBound));
        builder.append(":");
        builder.append(String.valueOf(upperBound));
        builder.append(")^");
        builder.append(String.valueOf(dimensions));
        
        domainRegistry.setDomainString(builder.toString());
    }

    public void setGeneratingFunctions(int generatingFunctions) {
        this.generatingFunctions = generatingFunctions;
    }
    
    public AMBitGeneratingFunction getGeneratingFunction() {
        return generatingFunction;
    }
    
    public Vector getBinarySolution(Type solution) {
//        System.out.print("\ninput: ");
//        for (Numeric n : (Vector)solution) {
//            System.out.print(n.doubleValue() + " ");
//        }
//        System.out.println("\nfunctions: " + generatingFunctions);
        
        generatingFunction.setGeneratingFunctions(generatingFunctions);
        StringBuilder sb = new StringBuilder();
        int coefficients = ((Vector)solution).size() / generatingFunctions;
        
//        System.out.println("coefficients: " + coefficients);
        
        for (int g = 0; g < generatingFunctions; g++) {
            Vector.Builder vb = Vector.newBuilder();
            int start = g * coefficients;
//            System.out.println("\nstart: " + start);
            for (int b = start; b < start + coefficients; b++) {
                vb.add(((Vector)solution).doubleValueOf(b));
            }
            
//            System.out.println("vector: " + vb.build());
            
            sb.append(generatingFunction.f(vb.build()));
            
//            System.out.println("g" + g + ": " + sb.toString() + "(" + sb.toString().length() + ")");
        }
        String bitString = sb.toString();
        
//        System.out.println("bitstring: " + bitString);
        
        return decodeBitString(bitString, generatingFunction.getBitsPerDimension());
    }

    @Override
    protected Fitness calculateFitness(Type solution) {

        
//        System.out.println("expandedv: " + expandedVector);
//        System.out.println("fitness: " + generatingFunction.getDelegate().getFitness(expandedVector).getValue());
//        System.exit(1);
        return generatingFunction.getDelegate().getFitness(getBinarySolution(solution));
    }
}
