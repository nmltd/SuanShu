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

import com.numericalmethod.suanshu.algebra.linear.vector.doubles.Vector;
import com.numericalmethod.suanshu.algebra.linear.vector.doubles.dense.DenseVector;
import com.numericalmethod.suanshu.analysis.differentiation.Ridders;
import com.numericalmethod.suanshu.analysis.differentiation.multivariate.Gradient;
import com.numericalmethod.suanshu.analysis.differentiation.multivariate.Hessian;
import com.numericalmethod.suanshu.analysis.differentiation.univariate.*;
import com.numericalmethod.suanshu.analysis.function.polynomial.Polynomial;
import com.numericalmethod.suanshu.analysis.function.rn2r1.*;
import com.numericalmethod.suanshu.analysis.function.rn2r1.univariate.AbstractUnivariateRealFunction;
import com.numericalmethod.suanshu.analysis.function.rn2r1.univariate.UnivariateRealFunction;
import com.numericalmethod.suanshu.misc.ArgumentAssertion;
import static java.lang.Math.*;

/**
 * Demonstrates how to do numerical differentiation, both univariate and multivariate, in Java using
 * SuanShu.
 *
 * @author Haksun Li
 */
public final class Differentiation {

    private static final UnivariateRealFunction LOG = new AbstractUnivariateRealFunction() {
        @Override
        public double evaluate(double x) {
            return Math.log(x);
        }
    };
    private static final BivariateRealFunction SINX_COSY = new AbstractBivariateRealFunction() {
        @Override
        public double evaluate(double x, double y) {
            return pow(sin(x), cos(y));
        }
    };
    private static final RealScalarFunction TRIVARIATE = new RealScalarFunction() {
        @Override
        public Double evaluate(Vector x) {
            return evaluate(x.toArray());
        }

        public double evaluate(double... x) {
            return pow(x[0], exp(x[1])) * log(x[2]) + x[0] * x[1] * x[2];
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

    private Differentiation() {
    }

    /**
     * Demonstrates numerical differentiation on uni-, bi- and trivariate functions, as well as some
     * special cases.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        System.out.println("This class demonstrates how to do differentiation, both univariate and"
            + " multivariate, in Java using SuanShu.");
        differentiateUnivariate(LOG, 0.5);
        differentiateBivariate(SINX_COSY, 1.0, 1.0);
        differentiateTrivariate(TRIVARIATE, 1.0, 1.0, 1.0);
        differentiateBeta(0.5, 0.5);
        differentiatePolynomial(new Polynomial(new double[]{3, 2, 1}), 1.0); // 3x^2 + 2x + 1
    }

    /**
     * Computes df/dx and d2f/dx2 for the given function f via the method of finite differences.
     * Then evaluates the obtained derivatives at the given point, printing the results.
     *
     * @param f the function that is to be differentiated
     * @param x the point at which to evaluate the differentiated functions
     */
    public static void differentiateUnivariate(UnivariateRealFunction f, double x) {
        // differentiate f w.r.t. x once
        UnivariateRealFunction df1 = new FiniteDifference(f, 1, FiniteDifference.Type.CENTRAL);
        // evaluate at x
        double dfdx = df1.evaluate(x);
        System.out.printf("df/dx at x = %f is %f%n", x, dfdx);

        // differentiate f w.r.t. x the second time
        FiniteDifference df2 = new FiniteDifference(f, 2, FiniteDifference.Type.CENTRAL);
        double d2fdx2 = df2.evaluate(x);
        System.out.printf("d2f/dx2 at x = %f is %f%n", x, d2fdx2);
    }

    /**
     * Computes the second order derivative d2f/dxdy by first differentiating f w.r.t. x1 and
     * differentiating the result w.r.t. x2 using the method of finite differences.
     *
     * Then evaluates the obtained derivative for the given values and computes the gradient and
     * Hessian at that point.
     *
     * @param f the function that is to be differentiated
     * @param x the first variable of the point at which to evaluate the differentiated function
     * @param y the second variable of the point at which to evaluate the differentiated function
     */
    public static void differentiateBivariate(BivariateRealFunction f, double x, double y) {
        // differentiate f w.r.t. x and then y at x = (1, 1); a 2nd order derivative
        com.numericalmethod.suanshu.analysis.differentiation.multivariate.MultivariateFiniteDifference df =
            new com.numericalmethod.suanshu.analysis.differentiation.multivariate.MultivariateFiniteDifference(
            f, new int[]{1, 2});
        Vector arg = new DenseVector(x, y);
        System.out.printf("d2f/dxdy at x = (1, 1) is %f%n", df.evaluate(arg));

        // compute the gradient of f at (1, 1)
        Gradient g = new Gradient(f, arg);
        System.out.printf("Gradient for f is: %s%n", g);

        // compute the Hessian of f at (1, 1)
        Hessian H = new Hessian(f, arg);
        System.out.printf("Hessian for f is: %s%n", H);
    }

    /**
     * Computes df/dx3, d2f/dx2x3 and d3f/dx1dx2dx3 of a trivariate function using Ridder's method
     * and evaluates them at the given point.
     *
     * @param f  the function that is to be differentiateds
     * @param x1 the first variable of the point at which to evaluate the differentiated function
     * @param x2 the second variable of the point at which to evaluate the differentiated function
     * @param x3 the third variable of the point at which to evaluate the differentiated function
     */
    public static void differentiateTrivariate(
        RealScalarFunction f, double x1, double x2, double x3) {
        ArgumentAssertion.assertTrue(
            f.dimensionOfDomain() == 3,
            "Trivariate function expected but dimension of domain is: %d",
            f.dimensionOfDomain());

        Vector x = new DenseVector(x1, x2, x3);

        // df/dx3
        Ridders dfz = new Ridders(f, new int[]{3});
        System.out.printf("dfz at (1,1,1) = %f%n", dfz.evaluate(x));

        // d2f/dx2x3
        Ridders dfzy = new Ridders(f, new int[]{3, 2});
        System.out.printf("dfzy at (1,1,1) = %f%n", dfzy.evaluate(x));

        // d3f/dx1dx2dx3
        Ridders dfzyx = new Ridders(f, new int[]{3, 2, 1});
        System.out.printf("dfzyx at (1,1,1) = %f%n", dfzyx.evaluate(x));
    }

    /**
     * Evaluates first order derivative function of the Beta function w.r.t <i>x</i>, i.e.,
     * {@code dB(x, y)/dx}.
     *
     * <blockquote><code>
     * dB(x, y)/dx = B(x, y) * (ψ(x) - ψ(x + y)),
     * </code></blockquote>
     *
     * where
     * <code>x > 0, y > 0</code>
     *
     * @param x the first variable of the point at which to evaluate the differentiated function
     * @param y the second variable of the point at which to evaluate the differentiated function
     */
    public static void differentiateBeta(double x, double y) {
        DBeta dBeta = new DBeta();
        System.out.printf("dB(%f, %f) = %f%n", x, y, dBeta.evaluate(x, y));
    }

    /**
     * Differentiates the given polynomial and evaluates the result at the given value.
     *
     * @param p the polynomial that is to be differentiated
     * @param x the point at which to evaluate the differentiated polynomial
     */
    public static void differentiatePolynomial(Polynomial p, double x) {
        Polynomial dp = new DPolynomial(p);
        System.out.printf("dp(%f) = %f%n", x, dp.evaluate(x));
    }
}
