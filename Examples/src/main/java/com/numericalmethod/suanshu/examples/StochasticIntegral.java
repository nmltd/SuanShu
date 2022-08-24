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

import com.numericalmethod.suanshu.stats.descriptive.moment.Mean;
import com.numericalmethod.suanshu.stats.descriptive.moment.Variance;
import com.numericalmethod.suanshu.stats.distribution.univariate.EmpiricalDistribution;
import com.numericalmethod.suanshu.stats.stochasticprocess.timegrid.EvenlySpacedGrid;
import com.numericalmethod.suanshu.stats.stochasticprocess.timegrid.TimeGrid;
import com.numericalmethod.suanshu.stats.stochasticprocess.univariate.filtration.Filtration;
import com.numericalmethod.suanshu.stats.stochasticprocess.univariate.filtration.FiltrationFunction;
import com.numericalmethod.suanshu.stats.stochasticprocess.univariate.integration.*;
import com.numericalmethod.suanshu.stats.stochasticprocess.univariate.random.RandomRealizationGenerator;
import com.numericalmethod.suanshu.stats.stochasticprocess.univariate.random.RandomRealizationOfRandomProcess;
import com.numericalmethod.suanshu.stats.stochasticprocess.univariate.sde.discrete.BMSDE;
import com.numericalmethod.suanshu.stats.stochasticprocess.univariate.sde.discrete.DiscreteSDE;

/**
 * Demonstrates how to numerically compute stochastic integrals in Java using SuanShu.
 *
 * @author Haksun Li
 */
public final class StochasticIntegral {

    /**
     * The number of discretizations.
     */
    public static final int N_T = 1000;
    /**
     * The number of simulations.
     */
    public static final int N_SIMS = 1000;
    /**
     * The time of the first point.
     */
    public static final double T_0 = 0;
    /**
     * The time of the last point.
     */
    public static final double T_1 = N_T;

    private StochasticIntegral() {
    }

    /**
     * Computes various stochastic integrals numerically.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        System.out.println("This class demonstrates how to numerically compute stochastic "
            + "integrals in Java using SuanShu.");
        B2dB1_0010();
        B2dB1_0020();
        B1B1dt_0010();
        B1B1dt_0020();
    }

    /**
     * Demonstrates how to calculate the mean and variance of the integral:
     *
     * /1 E( | B2(t) dB1(t) ) /0, where B1, B2 are two different Brownian motions.
     *
     * We calculate the statistics by manually generate filtrations from B1 and evaluate the
     * integral w.r.t. each filtration, obtaining a data point.
     */
    public static void B2dB1_0010() {
        final TimeGrid T = new EvenlySpacedGrid(T_0, T_1, N_T); // discretization
        DiscreteSDE sde = new BMSDE();
        double x0 = 0.0;

        final RandomRealizationGenerator B = new RandomRealizationOfRandomProcess(sde, T, x0);

        Mean mu = new Mean();
        Variance var = new Variance();

        for (int i = 0; i < N_SIMS; ++i) {
            // generate a Brownian filtration
            Filtration Ft = new Filtration(B.nextRealization());

            // a new B2 is created for each iteration, hence a different FT2
            FiltrationFunction B2 = new FiltrationFunction() {
                // generate a different Brownian filtration
                private Filtration Ft2 = new Filtration(B.nextRealization());

                @Override
                public double evaluate(int t) {
                    double B2 = Ft2.B(t);
                    return B2;
                }
            };

            double I = new IntegralDB(B2).value(Ft);

            mu.addData(I);
            var.addData(I);
        }

        /**
         * E = -0.010207 with n = 100000; nSims = 10000, 3:52am, 23/2/2011
         */
        System.out.printf("the mean is %f%n", mu.value());
        System.out.printf("the variance is %f%n", var.value());
    }

    /**
     * Demonstrates a simpler way to compute:
     *
     *    /1 E( | B2(t) dB1(t) ) /0, where B1, B2 are two different Brownian motions.
     *
     * Instead of the process above, we use {@link Exception}.
     */
    public static void B2dB1_0020() {
        TimeGrid T = new EvenlySpacedGrid(T_0, T_1, N_T); // discretization
        DiscreteSDE sde = new BMSDE();
        double x0 = 0.0;
        final RandomRealizationGenerator B = new RandomRealizationOfRandomProcess(sde, T, x0);

        FiltrationFunction B2 = new FiltrationFunction() {
            private Filtration Ft2;

            @Override
            public void setFt(Filtration Ft) {
                // FT is not used in this function
                super.setFt(Ft);
                Ft2 = new Filtration(B.nextRealization());
            }

            @Override
            public double evaluate(int t) {
                double B2 = Ft2.B(t);
                return B2;
            }
        };
        Integral I = new IntegralDB(B2);

        IntegralExpectation E = new IntegralExpectation(I,
                                                        T_0, T_1, // [0, 1]
                                                        N_T, // discretization
                                                        N_SIMS); // number of simulations

        /**
         * mean = -0.003524; var = 0.501905; with n = 100000; nSims = 10000, 4:01am, 23/2/2011
         */
        System.out.printf("the mean is %f%n", E.mean());
        System.out.printf("the variance is %f%n", E.variance());
    }

    /**
     * Computes
     *
     *    /1 E( | [B1(t) * B2(t)] dt ) /0, where B1, B2 are two different Brownian motions.
     *
     *
     */
    public static void B1B1dt_0010() {
        TimeGrid T = new EvenlySpacedGrid(T_0, T_1, N_T); // discretization
        DiscreteSDE sde = new BMSDE();
        double x0 = 0.0;
        final RandomRealizationGenerator B = new RandomRealizationOfRandomProcess(sde, T, x0);

        FiltrationFunction B1B2 = new FiltrationFunction() {
            private Filtration Ft1;
            private Filtration Ft2;

            @Override
            public void setFt(Filtration Ft) {
                // FT is not used in this function
                super.setFt(Ft);
                // generate a filtration for each new simulation
                Ft1 = new Filtration(B.nextRealization());
                Ft2 = new Filtration(B.nextRealization());
            }

            @Override
            public double evaluate(int t) {
                double B1 = Ft1.B(t);
                double B2 = Ft2.B(t);
                return B1 * B2;
            }
        };
        Integral I = new IntegralDt(B1B2);

        IntegralExpectation E = new IntegralExpectation(I,
                                                        T_0, T_1, // [0, 1]
                                                        N_T, // discretization
                                                        N_SIMS); // number of simulations

        System.out.printf("the mean is %f%n", E.mean());
        System.out.printf("the variance is %f%n", E.variance());
    }

    /**
     * Compute the (empirical) distribution for
     *
     * /1 | [B1(t) * B2(t)] dt /0
     *
     * B1, B2 are two different Brownian motions.
     */
    public static void B1B1dt_0020() {

        TimeGrid T = new EvenlySpacedGrid(T_0, T_1, N_T); // discretization
        DiscreteSDE sde = new BMSDE();
        double x0 = 0.0;
        final RandomRealizationGenerator B = new RandomRealizationOfRandomProcess(sde, T, x0);

        FiltrationFunction B1B2 = new FiltrationFunction() {
            private Filtration Ft1;
            private Filtration Ft2;

            @Override
            public void setFt(Filtration Ft) {
                // FT is not used in this function
                super.setFt(Ft);
                // generate a filtration for each new simulation
                Ft1 = new Filtration(B.nextRealization());
                Ft2 = new Filtration(B.nextRealization());
            }

            @Override
            public double evaluate(int t) {
                double B1 = Ft1.B(t);
                double B2 = Ft2.B(t);
                return B1 * B2;
            }
        };

        double[] stats = new double[N_SIMS];
        for (int i = 0; i < N_SIMS; ++i) {
            double I = new IntegralDt(B1B2).value(new Filtration(B.nextRealization()));
            stats[i] = I;
        }

        // construct an empirical distribution
        EmpiricalDistribution dist = new EmpiricalDistribution(stats);
        double x = 0.1;
        double cdf = dist.cdf(x);
        System.out.printf("F(%.1f) = %f%n", x, cdf);
    }
}
