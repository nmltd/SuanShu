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
import com.numericalmethod.suanshu.analysis.function.rn2r1.AbstractBivariateRealFunction;
import com.numericalmethod.suanshu.analysis.function.rn2r1.RealScalarFunction;
import com.numericalmethod.suanshu.analysis.function.rn2r1.univariate.AbstractUnivariateRealFunction;
import com.numericalmethod.suanshu.analysis.function.rn2r1.univariate.UnivariateRealFunction;
import com.numericalmethod.suanshu.analysis.function.rn2rm.RealVectorFunction;
import com.numericalmethod.suanshu.number.DoubleUtils;
import com.numericalmethod.suanshu.optimization.IterativeSolution;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.constraint.EqualityConstraints;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.constraint.LessThanConstraints;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.constraint.general.GeneralEqualityConstraints;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.constraint.general.GeneralLessThanConstraints;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.general.penaltymethod.PenaltyMethodMinimizer;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.problem.ConstrainedOptimProblemImpl1;
import com.numericalmethod.suanshu.optimization.multivariate.minmax.LeastPth;
import com.numericalmethod.suanshu.optimization.multivariate.minmax.MinMaxProblem;
import com.numericalmethod.suanshu.optimization.multivariate.unconstrained.c2.NelderMeadMinimizer;
import com.numericalmethod.suanshu.optimization.multivariate.unconstrained.c2.quasinewton.BFGSMinimizer;
import com.numericalmethod.suanshu.optimization.problem.*;
import com.numericalmethod.suanshu.optimization.univariate.UnivariateMinimizer.Solution;
import com.numericalmethod.suanshu.optimization.univariate.bracketsearch.BrentMinimizer;
import java.util.Arrays;
import java.util.List;

/**
 * Demonstrates how to optimize a function, both univariate and multivariate, in Java using SuanShu.
 *
 * @author Haksun Li
 */
public final class Optimization {

    /**
     * An example univariate function.
     */
    public static final UnivariateRealFunction F = new AbstractUnivariateRealFunction() {
        @Override
        public double evaluate(double x) {
            return (x - 1) * (x - 1);
        }
    };
    /**
     * An example multivariate function.
     */
    public static final RealScalarFunction G = new AbstractBivariateRealFunction() {
        @Override
        public double evaluate(double x, double y) {
            return x * x - 4 * x + y * y - y - x * y;
        }
    };
    /**
     * Initial guesses for minima of {@link #G}.
     */
    public static final Vector[] G_GUESSES = new Vector[]{
        new DenseVector(0., 0.),
        new DenseVector(1.2, 0.),
        new DenseVector(0., 0.8)
    };
    /**
     * An example bivariate function for constrained optimization.
     */
    public static final RealScalarFunction H = new AbstractBivariateRealFunction() {
        @Override
        public double evaluate(double x, double y) {
            return Math.pow(x + 1, 2) + Math.pow(y + 1, 2);
        }
    };
    /**
     * An equality constraint for constrained optimization.
     */
    public static final EqualityConstraints C1 = new GeneralEqualityConstraints(
        new AbstractBivariateRealFunction() {
            @Override
            public double evaluate(double x, double y) {
                return y; // y = 0
            }
        });
    /**
     * A less than constraint for constrained optimization.
     */
    public static final LessThanConstraints C2 = new GeneralLessThanConstraints(
        new AbstractBivariateRealFunction() {
            @Override
            public double evaluate(double x, double y) {
                return 1 - x; // x >= 1
            }
        });
    /**
     * Coefficient matrix for the example {@link #MINMAX_PROBLEM}.
     */
    public static final Matrix A = new DenseMatrix(
        new double[][]{
            {3, -4, 2, -1},
            {-2, 3, 6, -2},
            {1, 2, 5, 1},
            {-3, 1, -2, 2},
            {7, -2, 4, 3},
            {10, -1, 8, 5}
        });
    /**
     * Coefficient vector for the example {@link #MINMAX_PROBLEM}.
     */
    public static final DenseMatrix B = new DenseMatrix(
        new double[][]{
            {-17.4},
            {-1.2},
            {7.35},
            {9.41},
            {4.1},
            {12.3}
        });
    /**
     * An example minmax problem taken from Example 8.1 Practical Optimization: Algorithms and
     * Engineering Applications by Andreas Antoniou, Wu-Sheng Lu.
     */
    public static final MinMaxProblem<Double> MINMAX_PROBLEM = new MinMaxProblem<Double>() {
        @Override
        public RealScalarFunction error(Double omega) {
            final int row = omega.intValue();

            return new RealScalarFunction() {
                @Override
                public Double evaluate(Vector x) {
                    Matrix X = new DenseMatrix(x);
                    Matrix AX = A.multiply(X);//6x1
                    double diff = AX.get(row, 1);
                    diff -= B.get(row, 1);
                    return diff;
                }

                @Override
                public int dimensionOfDomain() {
                    return 4;
                }

                @Override
                public int dimensionOfRange() {
                    return 1;
                }
            };
        }

        @Override
        public RealVectorFunction gradient(Double omega) {
            final int row = omega.intValue();

            RealVectorFunction g = new RealVectorFunction() {
                @Override
                public Vector evaluate(Vector x) {
                    Vector gradient = A.getRow(row);

                    if (gradient.innerProduct(new DenseVector(x)) - B.get(row, 1) < 0) {
                        gradient = gradient.scaled(-1);
                    }

                    return gradient;
                }

                @Override
                public int dimensionOfDomain() {
                    return 4;
                }

                @Override
                public int dimensionOfRange() {
                    return 4;
                }
            };

            return g;
        }

        @Override
        public List<Double> getOmega() {
            return Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0, 6.0);
        }
    };
    /**
     * The initial guesses of solutions for the example minmax problem.
     */
    public static final Vector INITIAL_GUESSES
        = new DenseVector(new double[]{0.6902, 3.6824, -0.7793, 3.1150});

    private Optimization() {
    }

    /**
     * Demonstrates a number of optimization algorithms on different problems.
     *
     * @param args not used
     * @throws Exception thrown if an error occurs during execution
     */
    public static void main(String[] args) throws Exception {
        System.out.println("This class demonstrates how to optimize a function, both univariate "
            + "and multivariate, in Java using SuanShu.");
        brent(F);
        nelderMead(G, G_GUESSES);
        constrainedBFGS(G, C1, C2);
        minMax(MINMAX_PROBLEM, INITIAL_GUESSES);
    }

    /**
     * Optimizes a univariate function using Brent's method and prints the result.
     *
     * @param f the function that is to be optimized
     * @throws Exception if an error occurs during the optimization
     */
    public static void brent(UnivariateRealFunction f) throws Exception {
        double epsilon = 1e-15;
        int maxIterations = 20;
        BrentMinimizer instance = new BrentMinimizer(epsilon, maxIterations);
        Solution soln = instance.solve(f);

        double xmin = soln.minimizer();

        double fmin = F.evaluate(xmin);
        System.out.println(String.format("f(%f) = %f", xmin, fmin));
    }

    /**
     * Optimizes a multivariate function using Nelder-Mead's method and prints the result.
     *
     * @param g              the function that is to be optimized
     * @param initialGuesses initial guesses for the minima
     */
    public static void nelderMead(RealScalarFunction g, Vector[] initialGuesses) {
        C2OptimProblem problem = new C2OptimProblemImpl(g);

        double epsilon = 0.0;
        int maxIterations = 20;
        NelderMeadMinimizer nm = new NelderMeadMinimizer(epsilon, maxIterations);
        NelderMeadMinimizer.Solution soln = nm.solve(problem);
        Vector nmmin = soln.search(initialGuesses);
        double fmin = g.evaluate(nmmin);
        System.out.println(String.format("f(%s) = %f", nmmin.toString(), fmin));
    }

    /**
     * Solves a constrained general optimization MINMAX_PROBLEM using BFGS.
     *
     * @param f                   the function that is to be optimized
     * @param equalityConstraints the equality constraints on the function arguments
     * @param lessThanConstraints the less-than constraints on the function arguments
     * @throws Exception thrown if an error occurs during the optimization
     */
    public static void constrainedBFGS(
        RealScalarFunction f,
        EqualityConstraints equalityConstraints,
        LessThanConstraints lessThanConstraints) throws Exception {
        ConstrainedOptimProblemImpl1 problem = new ConstrainedOptimProblemImpl1(f, C1, C2);

        double gamma = 1e30;
        double epsilon = 1e-8;
        int maxIterations = 200;

        PenaltyMethodMinimizer optim = new PenaltyMethodMinimizer(
            PenaltyMethodMinimizer.DEFAULT_PENALTY_FUNCTION_FACTORY,
            gamma,
            new BFGSMinimizer(false, epsilon, maxIterations));

        IterativeSolution<Vector> minimizer = optim.solve(problem);
        Vector xmin = minimizer.search(new DenseVector(0., 0.));
        double fxmin = f.evaluate(xmin);

        System.out.println(String.format("f(%s) = %f",
                                         DoubleUtils.toString(xmin.toArray()), fxmin));
    }

    /**
     * Solves the given minmax problem and prints the result. We attempt to find the vector for
     * which the maximum error function, parameterized by omega, is minimized.
     *
     * @param problem        the minmax problem that is to be solved
     * @param initialGuesses initial guesses for a solution of the minmax problem
     * @throws Exception thrown if an exception occurs when solving the minmax problem
     */
    public static void minMax(MinMaxProblem<Double> problem, Vector initialGuesses)
        throws Exception {
        double precision = 1e-6;
        int maxIterations = 15;

        LeastPth<Double> instance = new LeastPth<Double>(precision, maxIterations);
        IterativeSolution<Vector> soln = instance.solve(problem);
        Vector xmin = soln.search(initialGuesses);
        System.out.printf("minmax soln: %s%n", xmin);
    }
}
