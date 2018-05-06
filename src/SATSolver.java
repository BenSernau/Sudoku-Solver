/**
 * This material is based upon work supported by 
 * the National Science Foundation under Grant No. 1140753.
 */

//package sudoku.util;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

/*
 * Simple class for SAT formulae in DIMACS format based on sat4j (see sat4j.org)
 *  
 * @author Dr. Andrea F. Lobo
 * @author Dr. Ganesh R. Baliga
 * @author Kenneth Janes
 */

public class SATSolver {


    /**
     * Returns literals that satisfy the given formula, if the formula is satisfiable. 
     * 
     * @param cnfFormulaFileName Path name of the file containing the formula
     * @return An array of literal values denoting a satisfying assignment when one exists; null for unsatisfiable formulae.
     * @throws ContradictionException 
     * @throws IOException 
     * @throws ParseFormatException 
     * @throws FileNotFoundException 
     * @throws TimeoutException 
     * @throws Exception 
     */
    public static int [] solve ( String cnfFormulaFileName )  throws 
	FileNotFoundException, ParseFormatException, IOException, ContradictionException, TimeoutException 
    {

	ISolver solver = SolverFactory.newDefault(); 

	Reader reader = new DimacsReader(solver);
	IProblem problem = reader.parseInstance(cnfFormulaFileName); 
	return problem.findModel();

    }

    public static void main(String[] args) {
	// Simple driver
	try {
	    if ( args.length != 1 ) {
		System.err.println("USAGE: java -cp.:org.sat4j.core.jar SATSolver file.cnf");
		System.exit(-1);
	    }
		
	    int [] vars = solve (args[0]);
	    if (vars == null)
		System.out.println ("Formula is unsatisfiable");
	    else {
		System.out.println ("Satisfying assignment: ");

		for (int var : vars)
		    System.out.println ("variable " + Math.abs(var) + " = " + ((var > 0) ? "true" : "false"));
	    }
	}
	catch (Exception e) {
	    e.printStackTrace();
	}

    }
}