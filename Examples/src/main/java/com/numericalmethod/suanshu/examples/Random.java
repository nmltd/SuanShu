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

import com.numericalmethod.suanshu.stats.descriptive.moment.*;
import com.numericalmethod.suanshu.stats.distribution.univariate.ExponentialDistribution;
import com.numericalmethod.suanshu.stats.distribution.univariate.ProbabilityDistribution;
import com.numericalmethod.suanshu.stats.random.rng.univariate.InverseTransformSampling;
import com.numericalmethod.suanshu.stats.random.rng.univariate.RandomNumberGenerator;
import com.numericalmethod.suanshu.stats.random.rng.univariate.normal.BoxMuller;
import com.numericalmethod.suanshu.stats.random.rng.univariate.normal.NormalRNG;
import com.numericalmethod.suanshu.stats.random.rng.univariate.uniform.UniformRNG;
import com.numericalmethod.suanshu.stats.random.rng.univariate.uniform.mersennetwister.MersenneTwister;
import java.util.Arrays;

/**
 * Demonstrates how to generate random numbers in Java using SuanShu.
 *
 * @author Haksun Li
 */
public final class Random {

    /**
     * The number of values to print from each random number generator.
     */
    public static final int N = 10;

    private Random() {
    }

    /**
     * Shows a few different algorithms to generate different types of random numbers from different
     * distributions.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        System.out.println("This class demonstrates how to generate random numbers in Java using"
                           + " SuanShu.");
        uniform(N);
        exponetial(0.5, N);
        mersenneTwister(N);
        gaussian(0.5, 1.5, 100000);
    }

    /**
     * Prints the given number of doubles, drawn from a uniform distribution (ranging 0 to 1).
     *
     * @param n the number of random values to generate
     */
    public static void uniform(int n) {
        RandomNumberGenerator rng = new UniformRNG();

        double[] values = new double[n];
        for (int i = 0; i < n; i++) {
            values[i] = rng.nextDouble();
        }
        System.out.printf("Uniformly generated random numbers: %s%n",
                          Arrays.toString(values));
    }

    /**
     * Demonstrates how {@link InverseTransformSampling} can be used to draw random numbers from a
     * probability distribution. In this case we generate the given number of values from an
     * exponential distribution.
     *
     * @param lambda the rate of the exponential distribution
     * @param n      the number of values to generate
     */
    public static void exponetial(double lambda, int n) {
        ProbabilityDistribution F = new ExponentialDistribution(lambda);
        InverseTransformSampling rng = new InverseTransformSampling(F);

        double[] values = new double[n];
        for (int i = 0; i < n; i++) {
            values[i] = rng.nextDouble();
        }
        System.out.printf("Random numbers from exponential distribution: %s%n",
                          Arrays.toString(values));
    }

    /**
     * Prints the given number of longs generated by the popular Mersenne-Twister algorithm. The
     * class can be used to create doubles in the same way as we used {@link UniformRng} above.
     *
     * @param n the number of values to generate
     */
    public static void mersenneTwister(int n) {
        MersenneTwister rng = new MersenneTwister();

        long[] values = new long[n];
        for (int i = 0; i < n; i++) {
            values[i] = rng.nextLong();
        }
        System.out.printf("Random numbers from Mersenne-Twister: %s%n", Arrays.toString(values));
    }

    /**
     * Generates the given number of values from a Gaussian distribution and prints the mean,
     * variance skew and kurtosis of the result. The algorithm used is Box-Muller.
     *
     * @param mu    the mean of the generated values
     * @param sigma the standard deviation of the generated values
     * @param n     the number of values to generate.
     */
    public static void gaussian(double mu, double sigma, int n) {
        Mean mean = new Mean();
        Variance var = new Variance();
        Skewness skew = new Skewness();
        Kurtosis kurtosis = new Kurtosis();

        NormalRNG gaussian = new NormalRNG(mu, sigma, new BoxMuller());
        for (int i = 1; i <= n; ++i) {
            double x = gaussian.nextDouble();
            mean.addData(x);
            var.addData(x);
            skew.addData(x);
            kurtosis.addData(x);
        }
        System.out.printf("Gaussian statistics:%n");
        System.out.printf("expected mean: %f, actual: %f%n", mu, mean.value());
        System.out.printf("expected standard deviation: %f, actual: %f%n",
                          sigma, var.standardDeviation());
        System.out.printf("skewness: %f, kurtosis: %f%n", skew.value(), kurtosis.value());
    }
}
