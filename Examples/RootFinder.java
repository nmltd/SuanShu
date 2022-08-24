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

import com.numericalmethod.suanshu.analysis.function.polynomial.Polynomial;
import com.numericalmethod.suanshu.analysis.function.polynomial.root.PolyRoot;
import com.numericalmethod.suanshu.analysis.function.rn2r1.univariate.AbstractUnivariateRealFunction;
import com.numericalmethod.suanshu.analysis.function.rn2r1.univariate.UnivariateRealFunction;
import com.numericalmethod.suanshu.analysis.root.univariate.*;
import java.util.Arrays;
import java.util.List;

/**
 * Demonstrates how to find roots of functions in Java using SuanShu.
 *
 * @author Haksun Li
 */
public final class RootFinder {

    /**
     * A polynomial P(x) = (x-1)(x-2)(x-3)(x-4) = x^4 - 10x^3 + 35x^2 - 50x + 24. Note that a
     * polynomial is also a {@link UnivariateRealFunction} and can hence be solved by both specific
     * and general root finding algorithms.
     */
    public static final Polynomial P = new Polynomial(1, -10, 35, -50, 24);
    /**
     * A function f(x) = log(x).
     */
    public static final UnivariateRealFunction F = new AbstractUnivariateRealFunction() {
        @Override
        public double evaluate(double x) {
            return Math.log(x);
        }
    };
    /**
     * The derivative of {@link #F}.
     */
    public static final UnivariateRealFunction DF = new AbstractUnivariateRealFunction() {
        @Override
        public double evaluate(double x) {
            return 1d / x;
        }
    };

    private RootFinder() {
    }

    /**
     * Demonstrates a few different root finding algorithms.
     *
     * @param args not used
     * @throws NoRootFoundException if an algorithm fails to find a root in the specified number of
     *                              iterations
     */
    public static void main(String[] args) throws NoRootFoundException {
        System.out.println("This class demonstrates how to find roots of functions in Java using "
            + "SuanShu.");
        jenkinsTraub(P);
        brent(P, 1E-9, 10, 1.5, 2.5);
        newton(F, DF, 1E-9, 10, 0.5);

    }

    /**
     * Solves the given polynomial using an appropriate algorithm, depending on the order. For
     * higher order polynomials (>= 5) we use Jenkins-Traub.
     *
     * @param p the polynomial for which to find the roots
     */
    public static void jenkinsTraub(Polynomial p) {
        PolyRoot solver = new PolyRoot();
        List<? extends Number> roots = solver.solve(p);
        System.out.println(Arrays.toString(roots.toArray()));
    }

    /**
     * Finds a root of a {@link UnivariateRealFunction} in the given range using Brent's algorithm.
     *
     * @param f             the function for which to find the root
     * @param tol           the required precision of the result
     * @param maxIterations the maximum number of iterations
     * @param rangeStart    the start of the range in which to look for roots
     * @param rangeEnd      the end of the range in which to look for roots
     */
    public static void brent(
        UnivariateRealFunction f,
        double tol,
        int maxIterations,
        double rangeStart,
        double rangeEnd) {
        BrentRoot solver = new BrentRoot(tol, maxIterations);

        double root = solver.solve(f, rangeStart, rangeEnd);
        double fx = f.evaluate(root);

        System.out.println(String.format("f(%f) = %f", root, fx));
    }

    /**
     * Finds a root of the given function using the Newton-Raphson method.
     *
     * @param f             the function for which to find the root
     * @param df            the derivative of the given function
     * @param tol           the required precision of the result
     * @param maxIterations the maximum number of iterations
     * @param initialGuess  an initial guess for a root
     * @throws NoRootFoundException if no root is found in the given number of iterations
     */
    public static void newton(
        UnivariateRealFunction f,
        UnivariateRealFunction df,
        double tol,
        int maxIterations,
        double initialGuess)
        throws NoRootFoundException {
        NewtonRoot solver = new NewtonRoot(tol, maxIterations);
        double root = solver.solve(
            f, df,
            initialGuess);
        double fx = f.evaluate(root);

        System.out.println(String.format("f(%f) = %f", root, fx));
    }
}
