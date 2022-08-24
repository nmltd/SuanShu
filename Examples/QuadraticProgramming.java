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
import com.numericalmethod.suanshu.analysis.function.rn2r1.QuadraticFunction;
import com.numericalmethod.suanshu.misc.PrecisionUtils;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.constraint.linear.*;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.convex.sdp.socp.qp.QPInfeasible;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.convex.sdp.socp.qp.QPSolution;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.convex.sdp.socp.qp.problem.QPProblem;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.convex.sdp.socp.qp.solver.activeset.QPPrimalActiveSetMinimizer;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.convex.sdp.socp.qp.solver.activeset.QPPrimalActiveSetMinimizer.Solution;
import static java.lang.Math.sqrt;

/**
 * Demonstrates how to solve quadratic programming problems in Java using SuanShu.
 *
 * @author Haksun Li
 */
public final class QuadraticProgramming {

    /**
     * A semi-definite matrix of quadratic coefficients. (x1 - x3)^2 + (x2 - x4)^2
     */
    public static final Matrix H1 = new DenseMatrix(
            new double[][]{
        {1.0, 0, -1, 0},
        {0, 1.0, 0, -1},
        {-1, 0, 1.0, 0},
        {0, -1, 0, 1.0}
    });
    /**
     * A positive-definite matrix of quadratic coefficients obtained by adding x1^2 + x2^2 + x3^2 +
     * x4^2 to the above.
     */
    public static final Matrix H2 = H1.add(H1.ONE());
    /**
     * The linear coefficients of the quadratic function.
     */
    public static final Vector p = new DenseVector(new double[]{0, 0, 0, 0});
    /**
     * The quadratic function used for testing.
     */
    public static final QuadraticFunction F = new QuadraticFunction(H2, p);
    /**
     * The matrix of coefficients on the LHS of the the greater-than constraint inequality.
     */
    public static final Matrix A = new DenseMatrix(
            new double[][]{
        {1, 0, 0, 0},
        {0, 1, 0, 0},
        {-1, -2, 0, 0},
        {0, 0, 0, 1},
        {0, 0, 1, 1},
        {0, 0, -1, -2}
    });
    /**
     * The vector of values on the RHS of the greater-than constraint inequality.
     */
    public static final Vector b = new DenseVector(new double[]{0, 0, -2, 2, 3, -6});
    /**
     * An example greater-than constraint.
     */
    public static final LinearGreaterThanConstraints GREATER =
            new LinearGreaterThanConstraints(A, b);
    /**
     * The matrix of coefficients on the LHS of the equality constraint.
     */
    public static final Matrix E = new DenseMatrix(
            new double[][]{
        {1.0, 1.0, 1.0, 1.0}
    });
    /**
     * The vector of values on the RHS of the equality constraint.
     */
    public static final Vector d = new DenseVector(10.0);
    /**
     * An example equality constraint.
     */
    public static final LinearEqualityConstraints EQUALITY = new LinearEqualityConstraints(E, d);
    /**
     * Initial guesses for a solution. Note that the initial guess must satisfy the constraints!
     */
    public static final Vector INITIAL_GUESSES = new DenseVector(2., 2., 3., 3.);

    private QuadraticProgramming() {
    }

    /**
     * Solves a quadratic function with two different sets of constraints using the primal active
     * set algorithm.
     *
     * @param args not used
     * @throws Exception if an error occurs while solving the quadratic programming problems
     */
    public static void main(String[] args) throws Exception {
        System.out.println("This class demonstrates how to solve quadratic programming problems in "
                           + "Java using SuanShu.");

        System.out.println("Without equality constraints:");
        primalActiveSet(F, null, GREATER, null, INITIAL_GUESSES,
                        sqrt(PrecisionUtils.autoEpsilon(H2)), Integer.MAX_VALUE);

        System.out.println("With equality constraints:");
        primalActiveSet(F, EQUALITY, GREATER, null, INITIAL_GUESSES,
                        sqrt(PrecisionUtils.autoEpsilon(H2)), Integer.MAX_VALUE);
    }

    /**
     * Solves a quadratic programming problem using the primal active set algorithm.
     *
     * @param f             the quadratic functions that is to be solved
     * @param equal         equality constraints on the result
     * @param greater       greater-than constraints on the result
     * @param less          less-than constraints on the result
     * @param initialGuess  an initial guess for the solution
     * @param precision     the precision with which a solution should be found
     * @param maxIterations the maximum number of iterations to find a solution
     * @throws QPInfeasible if the given problem is infeasible
     * @throws Exception    if an error occurs when running the algorithm
     */
    public static void primalActiveSet(
            QuadraticFunction f, LinearEqualityConstraints equal, LinearGreaterThanConstraints greater,
            LinearLessThanConstraints less, Vector initialGuess, double precision, int maxIterations)
            throws QPInfeasible, Exception {
        QPProblem problem = new QPProblem(F, equal, greater, less);
        QPPrimalActiveSetMinimizer instance = new QPPrimalActiveSetMinimizer(precision, maxIterations);
        Solution soln = instance.solve(problem);
        QPSolution qpSoln = soln.search(initialGuess);
        System.out.printf("minimizer: %s%n", qpSoln.minimizer());
    }
}
