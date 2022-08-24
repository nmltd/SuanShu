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

import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.Matrix;
import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.matrixtype.dense.DenseMatrix;
import com.numericalmethod.suanshu.algebra.linear.vector.doubles.Vector;
import com.numericalmethod.suanshu.algebra.linear.vector.doubles.dense.DenseVector;
import com.numericalmethod.suanshu.analysis.function.rn2r1.RealScalarFunction;
import com.numericalmethod.suanshu.optimization.IterativeSolution;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.constraint.EqualityConstraints;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.constraint.GreaterThanConstraints;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.constraint.general.GeneralEqualityConstraints;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.constraint.general.GeneralGreaterThanConstraints;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.constraint.linear.LinearGreaterThanConstraints;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.general.sqp.activeset.SQPActiveSetMinimizer;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.general.sqp.activeset.SQPActiveSetMinimizer.Solution;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.general.sqp.activeset.SQPActiveSetOnlyInequalityConstraintMinimizer;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.general.sqp.activeset.equalityconstraint.*;
import com.numericalmethod.suanshu.optimization.multivariate.constrained.problem.ConstrainedOptimProblemImpl1;
import static java.lang.Math.*;

/**
 * Demonstrates how to solve sequential quadratic programming problems in Java using SuanShu.
 *
 * @author Haksun Li
 */
public final class SequentialQuadraticProgramming {

    private SequentialQuadraticProgramming() {
    }

    /**
     * Demonstrates how to solve sequential quadratic programming problems with both or either of
     * inequality and equality constaints.
     *
     * @param args not used
     * @throws Exception if an error occurs solving one of the problems
     */
    public static void main(String[] args) throws Exception {
        System.out.println("This class demonstrates how to solve sequential quadratic programming "
            + "problems in Java using SuanShu.");
        inequalityAndEquality();
        inequalityOnly();
        equalityOnly();
    }

    /**
     * Solves an SQP problem with both inequality and equality constraints using an active-set
     * solver.
     *
     * example 15.4 in Andreas Antoniou, Wu-Sheng Lu
     *
     * @throws Exception if an error occurs solving the problem
     */
    public static void inequalityAndEquality() throws Exception {
        RealScalarFunction f = new RealScalarFunction() {
            @Override
            public Double evaluate(Vector x) {
                double x1 = x.get(1);
                double x2 = x.get(2);

                double fx = x1 * x1 + x2;
                return fx;
            }

            @Override
            public int dimensionOfDomain() {
                return 2;
            }

            @Override
            public int dimensionOfRange() {
                return 1;
            }
        };

        EqualityConstraints equal = new GeneralEqualityConstraints(
            new RealScalarFunction() {
                @Override
                public Double evaluate(Vector x) {
                    double x1 = x.get(1);
                    double x2 = x.get(2);

                    double fx = x1 * x1 + x2 * x2 - 9.;
                    return fx;
                }

                @Override
                public int dimensionOfDomain() {
                    return 2;
                }

                @Override
                public int dimensionOfRange() {
                    return 1;
                }
            });

        LinearGreaterThanConstraints greater = new LinearGreaterThanConstraints(
            new DenseMatrix(new double[][]{
                {1, 0},
                {-1, 0},
                {0, 1},
                {0, -1}
            }),
            new DenseVector(1., -5., 2., -4.));

        SQPActiveSetMinimizer instance = new SQPActiveSetMinimizer(1e-6, 20);
        ConstrainedOptimProblemImpl1 problem = new ConstrainedOptimProblemImpl1(
            f, equal, greater.toLessThanConstraints());

        Vector x;
        double fx;

        Solution soln = instance.solve(problem);
        x = soln.search(new DenseVector(5., 4.),
                        new DenseVector(-1., -1.),
                        new DenseVector(1., 1., 1., 1.));
        fx = f.evaluate(x);
        System.out.println("x = " + x);
        System.out.println("fx = " + fx);
    }

    /**
     * Solves an SQP problem with only inequality constraints using an active-set solver.
     *
     * example 15.2 in Andreas Antoniou, Wu-Sheng Lu
     *
     * @throws Exception if an error occurs solving the problem
     */
    public static void inequalityOnly() throws Exception {
        RealScalarFunction f = new RealScalarFunction() {
            @Override
            public Double evaluate(Vector x) {
                double x1 = x.get(1);
                double x2 = x.get(2);
                double x3 = x.get(3);
                double x4 = x.get(4);

                double fx = (x1 - x3) * (x1 - x3);
                fx += (x2 - x4) * (x2 - x4);
                fx /= 2;

                return fx;
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

        GreaterThanConstraints greater = new GeneralGreaterThanConstraints(
            new RealScalarFunction() {
                @Override
                public Double evaluate(Vector x) {
                    double x1 = x.get(1);
                    double x2 = x.get(2);
                    double x3 = x.get(3);
                    double x4 = x.get(4);

                    Matrix v = new DenseMatrix(new double[]{x1, x2}, 2, 1);

                    Matrix A = new DenseMatrix(new double[][]{
                        {0.25, 0},
                        {0, 1}
                    });
                    Matrix B = new DenseMatrix(new double[]{0.5, 0}, 2, 1);

                    Matrix FX = v.t().multiply(A).multiply(v);
                    FX = FX.scaled(-1);
                    FX = FX.add(v.t().multiply(B));

                    double fx = FX.get(1, 1);
                    fx += 0.75;

                    return fx;
                }

                @Override
                public int dimensionOfDomain() {
                    return 4;
                }

                @Override
                public int dimensionOfRange() {
                    return 1;
                }
            },
            new RealScalarFunction() {
                @Override
                public Double evaluate(Vector x) {
                    double x3 = x.get(3);
                    double x4 = x.get(4);

                    Matrix v = new DenseMatrix(new double[]{x3, x4}, 2, 1);

                    Matrix A = new DenseMatrix(new double[][]{
                        {5, 3},
                        {3, 5}
                    });
                    Matrix B = new DenseMatrix(new double[]{11. / 2, 13. / 2}, 2, 1);

                    Matrix FX = v.t().multiply(A).multiply(v);
                    FX = FX.scaled(-1. / 8);
                    FX = FX.add(v.t().multiply(B));

                    double fx = FX.get(1, 1);
                    fx += -35. / 2;

                    return fx;
                }

                @Override
                public int dimensionOfDomain() {
                    return 4;
                }

                @Override
                public int dimensionOfRange() {
                    return 1;
                }
            });

        SQPActiveSetOnlyInequalityConstraintMinimizer instance
            = new SQPActiveSetOnlyInequalityConstraintMinimizer(1e-7, 300);
        IterativeSolution<Vector> minimizer = instance.solve(f, greater);
        Vector x = minimizer.search(new DenseVector(1., 0.5, 2., 3.), new DenseVector(1., 1.));
        double fx = f.evaluate(x);
        System.out.println("x = " + x);
        System.out.println("fx = " + fx);
    }

    /**
     * Solves and SQP problem with only equality constraints using an active-set solver.
     *
     * example 15.1 in Andreas Antoniou, Wu-Sheng Lu
     *
     * @throws Exception if an error occurs solving the problem
     */
    public static void equalityOnly() throws Exception {
        RealScalarFunction f = new RealScalarFunction() {
            @Override
            public Double evaluate(Vector x) {
                double x1 = x.get(1);
                double x2 = x.get(2);
                double x3 = x.get(3);

                double fx = -pow(x1, 4.);
                fx -= 2. * pow(x2, 4.);
                fx -= pow(x3, 4.);
                fx -= pow(x1 * x2, 2.);
                fx -= pow(x1 * x3, 2.);

                return fx;
            }

            @Override
            public int dimensionOfDomain() {
                return 3;
            }

            @Override
            public int dimensionOfRange() {
                return 1;
            }
        };

        EqualityConstraints equal = new GeneralEqualityConstraints(
            new RealScalarFunction() {
                @Override
                public Double evaluate(Vector x) {
                    double x1 = x.get(1);
                    double x2 = x.get(2);
                    double x3 = x.get(3);

                    double fx = pow(x1, 4.);
                    fx += pow(x2, 4.);
                    fx += pow(x3, 4.);
                    fx -= 25.;

                    return fx;
                }

                @Override
                public int dimensionOfDomain() {
                    return 3;
                }

                @Override
                public int dimensionOfRange() {
                    return 1;
                }
            },
            new RealScalarFunction() {
                @Override
                public Double evaluate(Vector x) {
                    double x1 = x.get(1);
                    double x2 = x.get(2);
                    double x3 = x.get(3);

                    double fx = 8. * pow(x1, 2.);
                    fx += 14. * pow(x2, 2.);
                    fx += 7. * pow(x3, 2.);
                    fx -= 56.;

                    return fx;
                }

                @Override
                public int dimensionOfDomain() {
                    return 3;
                }

                @Override
                public int dimensionOfRange() {
                    return 1;
                }
            });

        SQPActiveSetOnlyEqualityConstraint1Minimizer instance
            = new SQPActiveSetOnlyEqualityConstraint1Minimizer(
                new SQPActiveSetOnlyEqualityConstraint1Minimizer.VariationFactory() {
                    @Override
                    public SQPASEVariation newVariation(
                        RealScalarFunction f, EqualityConstraints equal) {
                            SQPASEVariation2 impl = new SQPASEVariation2(100., 0.01, 10);
                            impl.set(f, equal);
                            return impl;
                        }
                },
                1e-10,
                200);
        IterativeSolution<Vector> minimizer = instance.solve(f, equal);
        Vector x = minimizer.search(new DenseVector(100.6, 37.3, -23.95),
                                    new DenseVector(-100., -1.));
        double fx = f.evaluate(x);
        System.out.println("x = " + x);
        System.out.println("fx = " + fx);
    }
}
