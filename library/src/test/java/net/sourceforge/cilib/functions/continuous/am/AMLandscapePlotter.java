/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.functions.continuous.am;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import net.sourceforge.cilib.functions.discrete.*;
import net.sourceforge.cilib.type.types.container.Vector;
import net.sourceforge.cilib.functions.DiscreteFunction;
import net.sourceforge.cilib.problem.AngleModulationProblem;
import net.sourceforge.cilib.problem.AbstractProblem;
import net.sourceforge.cilib.problem.FunctionOptimisationProblem;
import net.sourceforge.cilib.problem.FunctionOptimisationProblemTest;
import org.junit.Before;
import org.junit.Test;

public class AMLandscapePlotter {
    
    private AbstractProblem am;
    private AMBitGeneratingFunction g;

    @Before
    public void instantiate() {
        FunctionOptimisationProblem p = new FunctionOptimisationProblem();
        LinearQueens df = new LinearQueens();
        df.setBoardSize(4);
        p.setFunction(df);
        p.setDomain("B^16");
        
        g = new AMBitGeneratingFunction();
        //g.setModulationFunction(new UniformSpatialCorrelationFunction());
        g.setDelegate(p);
        
        this.am = new AngleModulationProblem();
        ((AngleModulationProblem)am).setGeneratingFunction(g);
    }

    @Test
    public void testEvaluate() {
        double low = -1;
        double high = 1;
        double step = 0.01;
        
        File f = new File("/home/bennie/Research/cilib_AM/Generating_Function/landscape/landscape.in");
        
        double min = Double.MAX_VALUE;
        
        try {
            FileWriter writer = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(writer);
//                for (double fx = low; fx <= high; fx+=step) {
//                    writer.write(fx + "\t" + am.getFitness(Vector.of(fx)) + "\n");
//                }
            for (double b = low; b <= high; b += step) {
                for(double c = low; c <= high; c += step) {
                    double fitness = am.getFitness(Vector.of(0.6,b,c,-0.5)).getValue();
                    bw.write(b + "\t" + c + "\t" + fitness + "\n");
                    //System.out.println(b + "\t" + c + "\t" + ((b*b) + c));
                    
                    if (fitness < min) {
                        min = fitness;
                    }
                }
            }
            
            
            bw.close();
            writer.close();
        } catch (IOException e) {
            
        } finally {
            //System.out.println(min);
        }
    }
}
