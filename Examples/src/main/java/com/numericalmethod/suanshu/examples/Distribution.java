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

import com.numericalmethod.suanshu.stats.distribution.univariate.*;
import com.numericalmethod.suanshu.stats.test.distribution.kolmogorov.KolmogorovDistribution;

/**
 * Demonstrates the distribution functions in Java using SuanShu.
 *
 * @author Haksun Li
 */
public final class Distribution {

    private Distribution() {
    }

    public static void main(String args[]) {
        System.out.println(
                "This class demonstrates the distribution functions in Java using SuanShu.");
        beta(0.5, 1.5, 0.1);
        chiSquare(5.0, 0.1);
        f(12, 11.5, 0.1);
        kolmogorov(30, 0.1);
    }

    /**
     * Prints the properties of a beta distribution with the given parameters, where the quantile
     * and the density is printed for the given value.
     *
     * @param alpha the shape parameter alpha
     * @param beta  the shape parameter beta
     * @param x     the position at which to evaluate the quantile and the density of the
     *              distribution
     */
    public static void beta(double alpha, double beta, double x) {
        System.out.printf("Beta (alpha=%f, beta=%f):%n", alpha, beta);
        ProbabilityDistribution F = new BetaDistribution(alpha, beta);
        printProperties(F, x);
    }

    /**
     * Prints the properties of the given probability distribution. The quantile and density is
     * printed for the given value.
     *
     * @param F the probability distribution for which to print the properties
     * @param x the value at which to evaluate the quantile and the density of the distribution
     */
    public static void printProperties(ProbabilityDistribution F, double x) {
        System.out.printf("mean: %f, var: %f, skew: %f, kurtosis: %f%n",
                          F.mean(),
                          F.variance(),
                          F.skew(),
                          F.kurtosis());

        System.out.printf("the quantile at %f = %f%n", x, F.quantile(x));
        System.out.printf("the density at %f = %f%n", x, F.density(x));
    }

    /**
     * Prints the properties of a Chi Square distribution with the given degree of freedom, where
     * the quantile and the density is printed for the given value.
     *
     * @param k the degree of freedom of the distribution
     * @param x the value at which to evaluate the quantile and the density of the distribution
     */
    public static void chiSquare(double k, double x) {
        ProbabilityDistribution F = new ChiSquareDistribution(k);
        System.out.printf("Chi Square (k=%f):%n", k);
        printProperties(F, x);

    }

    /**
     * Prints the properties of an F-distribution with the given parameters, where the quantile and
     * the density is printed for the given value.
     *
     * @param df1 the first degree of freedom
     * @param df2 the second degree of freedom
     * @param x   the value at which to evaluate the quantile and the density of the distribution
     */
    public static void f(double df1, double df2, double x) {
        ProbabilityDistribution F = new FDistribution(df1, df2);
        System.out.printf("F (df1=%f, df2=%f)%n", df1, df2);
        printProperties(F, x);
    }

    /**
     * Prints the CDF of a Komogorov distribution with the given sample size at the given value.
     *
     * @param n the sample size
     * @param x the value at which to evaluate the cdf
     */
    public static void kolmogorov(int n, double x) {
        ProbabilityDistribution F = new KolmogorovDistribution(n);
        System.out.printf("the cdf of the Kolmogorov distribution (sample size %d) at %f = %f%n",
                          n, x, F.cdf(x));
    }
}
