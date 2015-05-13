/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.functions.continuous.dynamic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import net.sourceforge.cilib.type.types.container.Vector;
import net.sourceforge.cilib.measurement.generic.Iterations;
import net.sourceforge.cilib.problem.FunctionOptimisationProblem;
import net.sourceforge.cilib.problem.objective.Maximise;
import net.sourceforge.cilib.pso.PSO;
import net.sourceforge.cilib.stoppingcondition.Maximum;
import net.sourceforge.cilib.stoppingcondition.MeasuredStoppingCondition;
import org.junit.Test;

/**
 *
 */
public class GeneralisedMovingPeaksTest {

    @Test
    public void generalizedMovingPeaks() {
        int frequency = 1;
        int peaks = 3;
        double widthSeverity = 0.1;
        double heightSeverity = 3.0;
        double shiftSeverity = 2;
        double lambda = 1.0;

        FunctionOptimisationProblem problem = new FunctionOptimisationProblem();
        problem.setObjective(new Maximise());
        problem.setDomain("R(0.0:100.0)^2");
        problem.setFunction(new GeneralisedMovingPeaks(frequency, peaks, widthSeverity, heightSeverity, shiftSeverity, lambda));

        PSO pso = new PSO();
        pso.setOptimisationProblem(problem);
        pso.addStoppingCondition(new MeasuredStoppingCondition(new Iterations(), new Maximum(), 100));

        //pso.initialise();
        pso.run();
    }
    
    @Test
    public void plot() {
        GeneralisedMovingPeaks mp = new GeneralisedMovingPeaks();
        mp.setPeaks(1);
        mp.setMinWidth(0.5);
        mp.setMaxWidth(0.51);
        
        FunctionOptimisationProblem problem = new FunctionOptimisationProblem();
        problem.setObjective(new Maximise());
        problem.setDomain("R(-10:10)^2");
        problem.setFunction(mp);
        
        PSO pso = new PSO();
        pso.setOptimisationProblem(problem);
        pso.addStoppingCondition(new MeasuredStoppingCondition(new Iterations(), new Maximum(), 100));
        
        pso.performInitialisation();
                
        double low = -0.5;
        double high = 0.5;
        double step = 0.001;
        
        File f = new File("/home/bennie/Research/cilib_AM/Convergence/MovingPeaks/plots/landscape.in");
        
        double min = Double.MAX_VALUE;
        
        try {
            FileWriter writer = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(writer);
//                for (double fx = low; fx <= high; fx+=step) {
//                    writer.write(fx + "\t" + problem.getFitness(Vector.of(fx)) + "\n");
//                }
            for (double x = low; x <= high; x += step) {
                for(double y = low; y <= high; y += step) {
                    double fitness = problem.getFitness(Vector.of(x, y)).getValue();
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
