package appx.extensionsemantics.approximate;

import appx.af.ArgumentationFramework;
import appx.extensionsemantics.exact.SimpleGroundedSemanticsSolver;
import appx.solver.ArgumentSetSolution;
import appx.solver.BinarySolution;
import appx.solver.Solution;
import appx.solver.Solver;
import appx.task.Problem;
import appx.task.Semantics;
import appx.task.Task;

/**
 * This class implements a solver which follows this approach:
 * <ul>
 * <li>arguments in the grounded extension are accepted,</li>
 * <li>arguments attacked by the grounded are rejected,</li>
 * <li>other arguments are considered as accepted if their in-degree is greater or equal to k * out-degree.</li>
 * </ul>
 * .
 *
 */
public class InOutDegreeBasedApproximateSolver extends Solver {
	
	/**
	 * The threshold for considering arguments as accepted
	 */
	private double threshold;
	
	/**
	 * Builds a solver
	 * 
	 * @param thresh 
	 */
	public InOutDegreeBasedApproximateSolver(double thresh) {
		this.threshold = thresh;
	}
	
	/**
	 * Builds a solver without specifying the threshold. 
	 * 
	 */
	public InOutDegreeBasedApproximateSolver() {
		this.threshold = -1;
	}
	
	private void choice_threshold(Task task, ArgumentationFramework af) {
		
		if(task.getProblem() == Problem.DC) {
			switch(task.getSemantics()) {
				case CO:
				case ST:
				case SST:
				case ID:
					this.threshold = af.nb_arguments();
					break;
				case STG:
					this.threshold = 0;
					break;
				default:
					System.out.println("This combination (semantics, problem) is not handled by this solver.");
					System.exit(1);
			}
		}
		
		if(task.getProblem() == Problem.DS) {
			switch(task.getSemantics()) {
				case PR:
				case SST:
				case STG:
					this.threshold = af.nb_arguments();
					break;
				case ST:
					this.threshold = 0.1;
					break;
				default:
					System.out.println("This combination (semantics, problem) is not handled by this solver.");
					System.exit(1);		
			}
			
		}	
	}

	@Override
	public Solution solve(Task task, ArgumentationFramework af) {
		if (task.getProblem() != Problem.DC && task.getProblem() != Problem.DS) {
			throw new UnsupportedOperationException("This solver only supports DC and DS problems.");
		}
		Solver groundedSolver = new SimpleGroundedSemanticsSolver();
		String argumentName = task.getOptionValue("-a");
		if (argumentName == null) {
			throw new UnsupportedOperationException("This solver requires an argument name in the options.");
		}
		int argument = Integer.valueOf(argumentName) - 1;
		
		/* Grounded part */
		
		//long temps_start = System.currentTimeMillis();
		Solution sol = groundedSolver.solve(new Task(Problem.SE, Semantics.GR), af);
		//long temps_end = System.currentTimeMillis();
		//System.out.print((temps_end - temps_start)/1000.+";"); 
		
		ArgumentSetSolution groundedExtension = (ArgumentSetSolution) sol;
		
		if (groundedExtension.containsArgument(argument)) {
			//System.out.print("None;None;"); // To remove
			return new BinarySolution(true);
		}
		for (int attacker : af.get_af_attacker()[argument]) {
			if (groundedExtension.containsArgument(attacker)) {
				//System.out.print("None;None;"); // To remove
				return new BinarySolution(false);
			}
		}
		
		choice_threshold(task,af);
		
		//temps_start = System.currentTimeMillis(); // To remove
		
		int in_degree = af.inDegree(argument);
		int out_degree = af.outDegree(argument);
		boolean res = out_degree >= threshold * in_degree;
		
		//temps_end = System.currentTimeMillis(); // To remove
		
		
		//System.out.print(in_degree+";"); // To remove
		//System.out.print(out_degree+";"); // To remove
		//System.out.print((temps_end - temps_start)/1000.+";"); // To remove
		
		return new BinarySolution(res);
	}

}
