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

import com.numericalmethod.suanshu.analysis.integration.univariate.riemann.Riemann;
import com.numericalmethod.suanshu.number.DoubleUtils;
import com.numericalmethod.suanshu.stats.descriptive.Statistic;
import com.numericalmethod.suanshu.stats.descriptive.StatisticFactory;
import com.numericalmethod.suanshu.stats.descriptive.moment.Mean;
import com.numericalmethod.suanshu.stats.descriptive.moment.Variance;
import com.numericalmethod.suanshu.stats.random.rng.univariate.RandomNumberGenerator;
import com.numericalmethod.suanshu.stats.random.rng.univariate.exp.Ziggurat2000Exp;
import com.numericalmethod.suanshu.stats.random.rng.univariate.uniform.UniformRNG;
import com.numericalmethod.suanshu.stats.random.sampler.resampler.BootstrapEstimator;
import com.numericalmethod.suanshu.stats.random.sampler.resampler.bootstrap.CaseResamplingReplacement;
import com.numericalmethod.suanshu.stats.random.sampler.resampler.bootstrap.block.PattonPolitisWhite2009;
import com.numericalmethod.suanshu.stats.random.sampler.resampler.bootstrap.block.PattonPolitisWhite2009ForObject;
import java.util.Arrays;

/**
 * Demonstrates how to do bootstrapping in Java using SuanShu.
 *
 * @author Haksun Li
 */
public class Bootstrapping {

    /**
     * Performs a few examples of bootstrapping.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        System.out.println("This class demonstrates how to do bootstrapping in Java using SuanShu.");
        resampledMean();
        resampledWithReplacementMean();
        resampledWithPattonPolitisWhite2009();
    }

    /**
     * Integrates {@link #EXP_2X} from 0 to 1 using a {@link Riemann}
     * integrator.
     */
    public static void resampledMean() {
        double[] sample = new double[]{1., 2., 3., 4., 5.,};
        CaseResamplingReplacement bootstrap = new CaseResamplingReplacement(sample);
        bootstrap.seed(1234567890L);
        int B = 1000;

        BootstrapEstimator estimator = new BootstrapEstimator(
            bootstrap,
            new StatisticFactory() {
            @Override
            public Statistic getStatistic() {
                return new Mean();
            }
        },
            B);

        System.out.printf(
            "bootstrapped mean = %f%n",
            estimator.value());
    }

    public static void resampledWithReplacementMean() {
        double[] sample = new double[]{
            1., 2., 3., 4., 5.,
            1., 2., 3., 4., 5.}; // sample from true population
        CaseResamplingReplacement estimator
            = new CaseResamplingReplacement(sample);
        estimator.seed(1234567890L);

        int B = 100;
        double[] means = new double[B];
        for (int i = 0; i < B; ++i) {
            double[] resample = estimator.newResample();
            means[i] = new Mean(resample).value();
        }

        double mean = new Mean(means).value();// estimator of population mean
        System.out.printf(
            "bootstrapped mean = %f%n",
            mean);

        double var = new Variance(means).value();// variance of estimator; limited by sample size (regardless of how big B is)
        System.out.printf(
            "bootstrapped variance = %f%n",
            var);

    }

    /**
     * Test of resampling series.
     * <p/>
     * Artifically construct a dependent sequence (consisting of 0 or 1) by
     * retaining the last value
     * with probability <i>q</i> while changing the last value with probability
     * <i>1-q</i>.
     * <p/>
     * The simple bootstrapping method {@link CaseResamplingReplacement} will
     * severely overestimate
     * the occurrences of certain pattern, while block bootstrapping method
     * {@link BlockBootstrap}
     * gives a good estimation of the occurrences in the original sample. All
     * estimators over
     * estimate.
     */
    public static void resampledWithPattonPolitisWhite2009() {
        final int N = 10000;
        final double q = 0.70; // the probability of retaining last value

        UniformRNG uniformRNG = new UniformRNG();
        RandomNumberGenerator rlg = new Ziggurat2000Exp();
        uniformRNG.seed(1234567890L);
        rlg.seed(1234567890L);

        Mean mean = new Mean();
        final double[] sample = new double[N];
        sample[0] = uniformRNG.nextDouble() > 0.5 ? 1 : 0;
        for (int i = 1; i < N; ++i) {
            sample[i] = uniformRNG.nextDouble() < q ? sample[i - 1] : 1 - sample[i - 1];
            mean.addData(sample[i]);
        }

        double[] pattern = new double[]{1, 0, 1, 0, 1};
        int B = 10000;

        CaseResamplingReplacement simpleBoot
            = new CaseResamplingReplacement(sample, uniformRNG);
        Mean countInSimpleBootstrap = new Mean();

        PattonPolitisWhite2009 stationaryBlock
            = new PattonPolitisWhite2009(sample, PattonPolitisWhite2009ForObject.Type.STATIONARY, uniformRNG, rlg);
        Mean countInStationaryBlockBootstrap = new Mean();

        PattonPolitisWhite2009 circularBlock
            = new PattonPolitisWhite2009(sample, PattonPolitisWhite2009ForObject.Type.CIRCULAR, uniformRNG, rlg);
        Mean countInCircularBlockBootstrap = new Mean();

        for (int i = 0; i < B; ++i) {
            countInSimpleBootstrap.addData(match(simpleBoot.newResample(), pattern));
            countInStationaryBlockBootstrap.addData(match(stationaryBlock.newResample(), pattern));
            countInCircularBlockBootstrap.addData(match(circularBlock.newResample(), pattern));
        }

        int countInSample = match(sample, pattern);
        System.out.println(
            "matched patterns in sample: "
            + countInSample);
        System.out.println(
            "matched patterns in simple bootstrap: "
            + countInSimpleBootstrap.value());
        System.out.println(
            "matched patterns in stationary block bootstrap: "
            + countInStationaryBlockBootstrap.value());
        System.out.println(
            "matched patterns in circular block bootstrap: "
            + countInCircularBlockBootstrap.value());

    }

    private static int match(double[] seq, double[] pattern) {
        int count = 0;
        for (int i = 0; i < seq.length - pattern.length; ++i) {
            if (seq[i] == pattern[0]) {
                double[] trunc = Arrays.copyOfRange(seq, i, i + pattern.length);
                if (DoubleUtils.equal(trunc, pattern, 1e-7)) {
                    count++;
                }
            }
        }
        return count;
    }

}
