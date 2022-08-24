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
 * FITNESS FOR Jacobian PARTICULAR PURPOSE, MERCHANTABILITY, NON-INFRINGEMENT, 
 * TITLE AND USEFULNESS.
 * 
 * IN NO EVENT AND UNDER NO LEGAL THEORY,
 * WHETHER IN ACTION, CONTRACT, NEGLIGENCE, TORT, OR OTHERWISE,
 * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIMS, DAMAGES OR OTHER LIABILITIES,
 * ARISING AS Jacobian RESULT OF USING OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.numericalmethod.suanshu.examples;

import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.matrixtype.dense.DenseMatrix;
import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.matrixtype.dense.triangle.SymmetricMatrix;
import com.numericalmethod.suanshu.algebra.linear.vector.doubles.Vector;
import com.numericalmethod.suanshu.algebra.linear.vector.doubles.dense.DenseVector;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.convex.sdp.pathfollowing.*;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.convex.sdp.pathfollowing.PrimalDualPathFollowingMinimizer.Solution;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.convex.sdp.problem.SDPDualProblem;

/**
 * Demonstrates how to solve semi-definite programming problems in Java using SuanShu.
 *
 * @author Haksun Li
 */
public final class SemiDefiniteProgramming {

    /**
     * A symmetric matrix defining the constraint on the first index of the result y.
     */
    public static final SymmetricMatrix A1 = new SymmetricMatrix(
            new double[][]{
                {0},
                {1, 0},
                {0, 0, 0}
            });
    /**
     * A symmetric matrix defining the constraint on the second index of the result y.
     */
    public static final SymmetricMatrix A2 = new SymmetricMatrix(
            new double[][]{
                {0},
                {0, 0},
                {1, 0, 0}
            });
    /**
     * A symmetric matrix defining the constraint on the third index of the result y.
     */
    public static final SymmetricMatrix A3 = new SymmetricMatrix(
            new double[][]{
                {0},
                {0, 0},
                {0, 1, 0}
            });
    /**
     * A symmetric matrix defining the constraint on the fourth index of the result y.
     */
    public static final SymmetricMatrix A4 = A3.ONE();
    /**
     * The matrices A, which define the constraints of the semi-definite programming problem.
     */
    public static final SymmetricMatrix[] A = new SymmetricMatrix[]{A1, A2, A3, A4};
    /**
     * The matrix C on the RHS of the constraint equation.
     */
    public static final SymmetricMatrix C = new SymmetricMatrix(
            new double[][]{
                {-2},
                {0.5, -2},
                {0.6, -0.4, -3}
            });
    /**
     * The vector b, which assigns weights to the components of y we are trying to maximize.
     */
    public static final Vector b = new DenseVector(0., 0., 0., 1.);
    /**
     * The dual SDP problem from the above constants.
     */
    public static final SDPDualProblem PROBLEM = new SDPDualProblem(
            b,
            C,
            A);
    /**
     * Our initial guess for the minimizer of the primal problem.
     */
    public static final DenseMatrix X0 = new DenseMatrix(
            new double[][]{
                {1. / 3., 0., 0.},
                {0., 1. / 3., 0.},
                {0., 0., 1. / 3.}
            });
    /**
     * Our initial guess for the minimizer of the dual problem.
     */
    public static final Vector y0 = new DenseVector(0.2, 0.2, 0.2, -4.);
    /**
     * Our initial guess for the auxiliary helper.
     */
    public static final DenseMatrix S0 = new DenseMatrix(
            new double[][]{
                {2, 0.3, 0.4},
                {0.3, 2, -0.6},
                {0.4, -0.6, 1}
            });
    /**
     * Our initial guess for the central path solution.
     */
    public static final CentralPath PATH0 = new CentralPath(X0, y0, S0);

    private SemiDefiniteProgramming() {
    }

    public static void main(String[] args) throws Exception {
        System.out.println("This class demonstrates how to solve semi-definite programming "
                           + "problems in Java using SuanShu.");
        primalDualPathFollowing(PROBLEM, PATH0, 0.9, 1E-3);
        homogeneousPathFollowing(PROBLEM, 1E-9);
    }

    /**
     * Uses Primal-Dual Path-Following to solve the given problem from the given starting point.
     *
     * @param problem the dual SDP problem that is to be solved
     * @param path0   the initial guess for the solution
     * @param gamma0  ensures the next iterates are inside the feasible set; suggested values are
     *                between 0.9 and 0.99
     * @param epsilon the precision of the computed result
     * @throws Exception if an error occurs solving the problem
     */
    public static void primalDualPathFollowing(
            SDPDualProblem problem, CentralPath path0, double gamma0, double epsilon) throws Exception {
        PrimalDualPathFollowingMinimizer pdpf = new PrimalDualPathFollowingMinimizer(gamma0, epsilon);
        Solution soln = pdpf.solve(problem);
        CentralPath path = soln.search(path0);
        printSolution(path);
    }

    private static void printSolution(CentralPath path) {
        System.out.println("X: " + path.X);
        System.out.println("y: " + path.y);
        System.out.println("S: " + path.S);
    }

    /**
     * Uses Homogeneous Path-Following to solve the given problem.
     *
     * @param problem the dual SDP problem that is to be solved
     * @param epsilon the precision of the computed result
     * @throws Exception if an error occurs solving the problem
     */
    public static void homogeneousPathFollowing(
            SDPDualProblem problem, double epsilon) throws Exception {
        HomogeneousPathFollowingMinimizer hsdpf = new HomogeneousPathFollowingMinimizer(epsilon);
        Solution soln = hsdpf.solve(problem);
        CentralPath path = soln.search();
        printSolution(path);
    }
}