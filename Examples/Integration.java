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

import com.numericalmethod.suanshu.analysis.function.rn2r1.univariate.AbstractUnivariateRealFunction;
import com.numericalmethod.suanshu.analysis.function.rn2r1.univariate.UnivariateRealFunction;
import com.numericalmethod.suanshu.analysis.integration.univariate.riemann.*;
import com.numericalmethod.suanshu.analysis.integration.univariate.riemann.newtoncotes.NewtonCotes;
import com.numericalmethod.suanshu.analysis.integration.univariate.riemann.newtoncotes.Simpson;
import com.numericalmethod.suanshu.analysis.integration.univariate.riemann.substitution.DoubleExponential;
import com.numericalmethod.suanshu.analysis.integration.univariate.riemann.substitution.PowerLawSingularity;
import static java.lang.Math.*;

/**
 * Demonstrates how to do integration in Java using SuanShu.
 *
 * @author Haksun Li
 */
public class Integration {

    /**
     * exp(2x).
     */
    public static final UnivariateRealFunction EXP_2X = new AbstractUnivariateRealFunction() {
        @Override
        public double evaluate(double x) {
            return exp(2 * x);
        }
    };
    /**
     * x^2.
     */
    public static final UnivariateRealFunction X2 = new AbstractUnivariateRealFunction() {
        @Override
        public double evaluate(double x) {
            return x * x;
        }
    };
    /**
     * log(x) log(1 - x).
     */
    public static final UnivariateRealFunction LOGCLOG = new AbstractUnivariateRealFunction() {
        @Override
        public double evaluate(double x) {
            return log(x) * log(1 - x);
        }
    };
    /**
     * 1 / sqrt(x-1).
     */
    public static final UnivariateRealFunction ONE_OVER_SQRT =
        new AbstractUnivariateRealFunction() {
        @Override
        public double evaluate(double x) {
            return 1 / sqrt(x - 1);
        }
    };
    private static final double PRECISION = 1E-15;
    private static final int MAX_ITERATIONS = 20;

    /**
     * Performs a few example integrations of different functions using different integrators.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        System.out.println("This class demonstrates how to do integration in Java using SuanShu.");
        exp2x();
        x2();
        logclog();
        oneOverSqrt();
    }

    /**
     * Integrates {@link #EXP_2X} from 0 to 1 using a {@link Riemann} integrator.
     */
    public static void exp2x() {
        Integrator integrator = new Riemann();
        double a = 0.0;
        double b = 1.0;
        double result = integrator.integrate(EXP_2X, a, b);

        System.out.printf("integrate exp(2x) from 0 to 1 gives %f%n", result);
    }

    /**
     * Integrates {@link #X2} from 0 to 1 using a {@link Simpson} integrator.
     */
    public static void x2() {
        Integrator integrator = new Simpson(PRECISION, MAX_ITERATIONS);
        double a = 0.0;
        double b = 1.0;
        double result = integrator.integrate(X2, a, b);

        System.out.printf("integrate x^2 from 0 to 1 gives %f%n", result);
    }

    /**
     * Integrates {@link #LOGCLOG} (has a singularity) using {@link NewtonCotes} and a
     * {@link DoubleExponential} substitution.
     */
    public static void logclog() {
        double a = 0.0;
        double b = 1.0;

        /*
         * Use the Euler-Maclaurin integration rule, which converges after only 6 iterations.
         */
        int rate = 2;
        int maxIterations = 6;
        Integrator integrator = new NewtonCotes(
            rate, NewtonCotes.Type.CLOSED, PRECISION, maxIterations);

        // Double exponential substitution.
        Integrator instance = new ChangeOfVariable(new DoubleExponential(LOGCLOG, 1, a, b),
                                                   integrator);

        double result = instance.integrate(
            LOGCLOG, a, b);
        System.out.printf("integrate log(x) * log(1 - x) from 0 to 1 gives %f%n", result);
    }

    /**
     * Integrates {@link #ONE_OVER_SQRT} (has a singularity) using {@link NewtonCotes} and a
     * {@link PowerLawSingularity} substitution.
     */
    public static void oneOverSqrt() {
        double a = 1.0;
        double b = 2.0;
        int rate = 3;
        double substitutionExponential = 0.5;
        NewtonCotes integrator = new NewtonCotes(
            rate, NewtonCotes.Type.OPEN, PRECISION, MAX_ITERATIONS);
        ChangeOfVariable instance = new ChangeOfVariable(
            new PowerLawSingularity(PowerLawSingularity.PowerLawSingularityType.LOWER,
                                    substitutionExponential, a, b),
            integrator);

        double result = instance.integrate(
            ONE_OVER_SQRT,
            a, b);

        System.out.printf("integrate 1 / sqrt(x - 1) from %f to %f gives %f%n",
                          a, b,
                          result);
    }
}
