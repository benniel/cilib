/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.functions.continuous.am;

import fj.data.Array;
import net.sourceforge.cilib.functions.ContinuousFunction;
import net.sourceforge.cilib.type.types.Numeric;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 * A bit generating function that wraps a normal function
 * to perform angle modulation.
 */
public class EnsembleBitGeneratingFunction extends AMBitGeneratingFunction {
    protected int ensembleSize; // number of modulation functions to use in ensemble
    protected int coefficients; // number of coefficients of the modulation function

    public EnsembleBitGeneratingFunction() {
        super();
        this.modulationFunction = new SimpleAngleModulationFunction();
        this.coefficients = ((ModulationFunction)this.modulationFunction).coefficients();
    }
    
    @Override
    public String f(Vector input) {
//        for (Numeric p : input) {
//            System.out.print(p + ", ");
//        }
//        System.out.println();
        
        StringBuilder str = new StringBuilder();
        
        int bits = (bitsPerDimension * delegate.getDomain().getDimension()) / ensembleSize;
        //System.out.println(">>>"+input.size()+">>>"+ensembleSize+">>>"+coefficients);
        Numeric[] functionInput;
        for (int f = 0; f < ensembleSize; f++) {
            functionInput = new Numeric[coefficients];
            //System.out.println(f*coefficients + " - " + ((f * coefficients) + coefficients));
            
            int c = 0;
            for (int i = f * coefficients; i < (f * coefficients) + coefficients; i++) {
                functionInput[c++] = input.get(i);
                //System.out.println("done");
            }
            
            Array samplePoints = sampler.getSamplePoints(bits, Vector.of(functionInput));

            Array<Double> sampleValues = samplePoints.map(modulationFunction);

            for (Double d : sampleValues) {
                str.append(d.doubleValue() > 0.0 ? '1' : '0');
            }
            
            //System.out.println(str.toString());
        }
        //System.out.println(str.toString());
//        Vector.Builder vector = Vector.newBuilder();
//        char[] chars = str.toString().toCharArray();
//        for (int i = 0; i < chars.length; i++) {
//            if (Integer.valueOf(chars[i]) == 49) {
//                vector.add(1);
//            } else {
//                vector.add(0);
//            }
//            System.out.print(chars[i]);
//        }
        //System.out.println("");
        //System.out.println(vector.build());
        //System.out.println(getDelegate().getFitness(vector.build()));
        return str.toString();
    }
    
    @Override
    public void setModulationFunction(ContinuousFunction f) {
        this.modulationFunction = f;
        this.coefficients = ((ModulationFunction)f).coefficients();
    }

    public void setEnsembleSize(int ensembleSize) {
        this.ensembleSize = ensembleSize;
    }
}
