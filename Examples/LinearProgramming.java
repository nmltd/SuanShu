/*
 * Copyright (c) Numerical Method Inc.
 * http://www.numericalmethod.com/
 * 
 * THIS SOFTWARE IS LICENSED, NOT SOLD.
 * 
 * YOU MAY USE THIS SOFTWARE ONLY AS DESCRIBED IN THE LICENSE.
 * IF YOU ARE NOT AWARE OF AND/OR DO NOT AGREE TO THE TERMS OF THE LICENSE,
 * DO NOT USE THIS SOFTWARE.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITH NO WARRANTY WHATSOEVER,
 * EITHER EXPRESS OR IMPLIED, INCLUDING, WITHOUT LIMITATION,
 * ANY WARRANTIES OF ACCURACY, ACCESSIBILITY, COMPLETENESS,
 * FITNESS FOR A PARTICULAR PURPOSE, MERCHANTABILITY, NON-INFRINGEMENT, 
 * TITLE AND USEFULNESS.
 * 
 * IN NO EVENT AND UNDER NO LEGAL THEORY,
 * WHETHER IN ACTION, CONTRACT, NEGLIGENCE, TORT, OR OTHERWISE,
 * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIMS, DAMAGES OR OTHER LIABILITIES,
 * ARISING AS A RESULT OF USING OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.numericalmethod.suanshu.examples;

import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.Matrix;
import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.matrixtype.dense.DenseMatrix;
import com.numericalmethod.suanshu.algebra.linear.vector.doubles.Vector;
import com.numericalmethod.suanshu.algebra.linear.vector.doubles.dense.DenseVector;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.constraint.linear.*;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.convex.sdp.socp.qp.lp.exception.LPInfeasible;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.convex.sdp.socp.qp.lp.exception.LPUnbounded;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.convex.sdp.socp.qp.lp.problem.*;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.convex.sdp.socp.qp.lp.simplex.solution.LPSimplexSolution;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.convex.sdp.socp.qp.lp.simplex.solution.LPUnboundedMinimizer;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.convex.sdp.socp.qp.lp.simplex.solver.LPCanonicalSolver;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.convex.sdp.socp.qp.lp.simplex.solver.LPTwoPhaseSolver;

/**
 * Demonstrates how to solve linear programming problems in Java using SuanShu.
 *
 * @author Haksun Li
 */
public final class LinearProgramming {

    private LinearProgramming() {
    }

    /**
     * Solves three different types of LP problem and prints the results.
     *
     * @param args not used
     * @throws Exception if an error occurs solving the LP problems
     */
    public static void main(String[] args) throws Exception {
        System.out.println("This class demonstrates how to solve linear programming problems in"
            + " Java using SuanShu.");
        canonicalLPProblem();
        singleConstraintLPProblem();
        multiConstraintLPProblem();
    }

    /**
     * Solves a LP problem given in canonical form.
     *
     * Shown is example 3-3-3 from Linear Programming with MATLAB by Michael C. Ferris, Olvi L.
     * Mangasarian, Stephen J. Wright, which has multiple (6) solutions
     *
     * @throws LPInfeasible if no solution to the problem can be found.
     */
    public static void canonicalLPProblem() throws LPInfeasible {
        DenseVector c = new DenseVector(new double[]{-1, -1, -1});

        Matrix A = new DenseMatrix(new double[][]{
            {1, -1, 1},
            {-1, 1, 1},
            {1, 1, -1},
            {-1, -1, -1}
        });
        DenseVector b = new DenseVector(new double[]{-2, -3, -1, -4});

        LPCanonicalProblem1 problem = new LPCanonicalProblem1(c, A, b);

        LPCanonicalSolver instance = new LPCanonicalSolver();
        LPSimplexSolution soln = instance.solve(problem);
        System.out.printf("minimizer: %s, minimum: %f%n",
                          soln.minimizer(), soln.minimum());
    }

    /**
     * Solves a LP problem with only one constraint. Shown is Exercise 3-3-2 from Linear Programming
     * with MATLAB by Michael C. Ferris, Olvi L. Mangasarian, Stephen J. Wright.
     *
     * @throws LPInfeasible if no solution to the problem is found
     * @throws LPUnbounded  if the problem is unbounded
     * @throws Exception    if there is an error solving the problem
     */
    public static void singleConstraintLPProblem() throws LPInfeasible, LPUnbounded, Exception {
        LinearGreaterThanConstraints greaterThanConstraints =
            new LinearGreaterThanConstraints(new DenseMatrix(new double[][]{
            {0.0, 1.0, -2.0, -1.0},
            {2.0, -1.0, -1.0, 4.0},
            {-1.0, 1.0, 0.0, -2.0}
        }),
                                             new DenseVector(-4.0, -5.0, -3.0));
        LPProblem problem = new LPProblemImpl1(
            new DenseVector(1.0, -2.0, -4.0, 4.0),
            greaterThanConstraints,
            null);

        LPTwoPhaseSolver solver = new LPTwoPhaseSolver();
        LPUnboundedMinimizer minimizer = (LPUnboundedMinimizer) solver.solve(problem).minimizer();
        System.out.printf("minimizer: %s, minimum: %f%n",
                          minimizer.minimizer(), minimizer.minimum());
    }

    /**
     * Solves a LP problem which uses multiple linear constraints.
     *
     * Shown is Example 3-6-13 (c), pp. 84 from Linear Programming with MATLAB by Michael C. Ferris,
     * Olvi L. Mangasarian, Stephen J. Wright.
     *
     * This case is founded unbound during scheme 2.
     *
     * @throws LPInfeasible if no solution to the problem is found
     * @throws LPUnbounded  if the problem is unbounded
     * @throws Exception    if there is an error solving the problem
     */
    public static void multiConstraintLPProblem() throws LPInfeasible, LPUnbounded, Exception {
        Vector c = new DenseVector(2.0, -1.0);

        LinearGreaterThanConstraints greaterThanConstraints =
            new LinearGreaterThanConstraints(new DenseMatrix(new double[][]{
            {1.0, 0.0}
        }), new DenseVector(-6.0));
        LinearLessThanConstraints lessThanConstraints = null;
        LinearEqualityConstraints equalityConstraints =
            new LinearEqualityConstraints(new DenseMatrix(new double[][]{
            {-1.0, 0.0}
        }), new DenseVector(-4.0));
        BoxConstraints boxConstraints =
            new BoxConstraints(
            2,
            new BoxConstraints.Bound(2, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY));
        LPProblem problem = new LPProblemImpl1(
            c,
            greaterThanConstraints,
            lessThanConstraints,
            equalityConstraints,
            boxConstraints);//x2 is free

        LPTwoPhaseSolver solver = new LPTwoPhaseSolver();
        LPUnboundedMinimizer minimizer = (LPUnboundedMinimizer) solver.solve(problem).minimizer();
        System.out.printf("minimum: %f, minimizer: %s, v: %s%n",
                          minimizer.minimum(), minimizer.minimizer(), minimizer.v());
    }
}
