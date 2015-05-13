/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.functions.continuous.unconstrained;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import net.sourceforge.cilib.functions.ContinuousFunction;
import net.sourceforge.cilib.type.types.container.Vector;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class SinglePeakTest {

    private ContinuousFunction function;

    @Before
    public void instantiate() {
        this.function = new SinglePeak();
    }

    /**
     * Test of evaluate method, of class {@link Ackley}.
     */
    @Test
    public void plot() {
        double low = -10;
        double high = 10;
        double step = 0.1;
        
        File f = new File("/home/bennie/Research/cilib_AM/Convergence/MovingPeaks/plots/singlePeakLandscape.in");
        
        double min = Double.MAX_VALUE;
        
        try {
            FileWriter writer = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(writer);
//                for (double fx = low; fx <= high; fx+=step) {
//                    writer.write(fx + "\t" + problem.getFitness(Vector.of(fx)) + "\n");
//                }
            for (double x = low; x <= high; x += step) {
                for(double y = low; y <= high; y += step) {
                    double fitness = function.f(Vector.of(x, y));
                    bw.write(x + "\t" + y + "\t" + fitness + "\n");
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
