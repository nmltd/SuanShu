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
import com.numericalmethod.suanshu.number.DoubleUtils;
import com.numericalmethod.suanshu.stats.descriptive.covariance.Covariance;
import com.numericalmethod.suanshu.stats.descriptive.moment.Variance;
import com.numericalmethod.suanshu.stats.distribution.univariate.EmpiricalDistribution;
import com.numericalmethod.suanshu.stats.stochasticprocess.multivariate.random.*;
import com.numericalmethod.suanshu.stats.stochasticprocess.multivariate.sde.*;
import com.numericalmethod.suanshu.stats.stochasticprocess.multivariate.sde.coefficients.*;
import com.numericalmethod.suanshu.stats.stochasticprocess.multivariate.sde.discrete.MultivariateDiscreteSDE;
import com.numericalmethod.suanshu.stats.stochasticprocess.multivariate.sde.discrete.MultivariateEulerSDE;
import com.numericalmethod.suanshu.stats.stochasticprocess.timegrid.EvenlySpacedGrid;
import com.numericalmethod.suanshu.stats.stochasticprocess.timegrid.TimeGrid;
import com.numericalmethod.suanshu.stats.test.distribution.normality.ShapiroWilk;
import com.numericalmethod.suanshu.stats.test.timeseries.adf.ADFAsymptoticDistribution1.Type;
import com.numericalmethod.suanshu.stats.timeseries.datastructure.multivariate.realtime.MultivariateRealization;
import static java.lang.Math.*;
import java.util.Iterator;

/**
 * Demonstrates how to construct stochastic processes from SDEs in Java using SuanShu.
 *
 * @author Haksun Li
 */
public final class SDEs {

    /**
     * The number of independent driving Brownian motions in this example.
     */
    public static final int N_B = 2;
    /**
     * The standard deviation of the diffusion of the first variable. If we have no drift this will
     * also be the standard deviation of the first derivative of the first variable.
     */
    public static final double SD11 = 1.5;
    /**
     * The standard deviation of the diffusion of the second variable.
     */
    public static final double SD22 = 3.7;
    /**
     * A constant used in the diffusion matrix.
     */
    public static final double RHO = 0.5;
    /**
     * An example diffusion matrix. Note that the standard deviation of diffusion of the first and
     * second variable will be SD11 and SD22 respectively.
     */
    public static final Matrix A = new DenseMatrix(// Cholesky decomposition
        new double[][]{
        {SD11, 0},
        {RHO * SD22, sqrt(1 - RHO * RHO) * SD22}
    });
    /**
     * Constant diffusion computed from the above matrix.
     */
    public static final DiffusionMatrix DIFFUSION = new ConstantSigma1(A);
    /**
     * Zero drift.
     */
    public static final DriftVector DRIFT = new DriftVector() {
        @Override
        public Vector evaluate(MultivariateFt ft) {
            return new DenseVector(N_B, 0.0);
        }
    };

    private SDEs() {
    }

    /**
     * Demonstrates how to simulate three different SDEs and prints some meta-data about the
     * generated series.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        System.out.println("This class demonstrates how to construct stochastic processes from "
            + "SDEs in Java using SuanShu.");
        brownian(3, 5000);
        multiNormal(DRIFT, DIFFUSION, 2500);
        ADF(Type.DICKEY_FULLER, 300, 300);
    }

    /**
     * Creates a Brownian motion with the given number of dimensions and of the given length.
     * Performs a Shapiro-Wilk hypothesis test that the first derivative of each dimension is from a
     * normal distribution and prints the covariance of each pair of first derivatives.
     *
     * @param d the number of dimensions of the Brownian motion
     * @param n the length of the generated Brownian motion
     */
    public static void brownian(int d, int n) {
        MultivariateBrownianRRG Xt = new MultivariateBrownianRRG(d, n);

        MultivariateRealization xt = Xt.nextRealization();
        Iterator<MultivariateRealization.Entry> it = xt.iterator();

        double[][] x = new double[d][xt.size()];

        // Create d x n array containing the brownian motion
        for (int i = 0; it.hasNext(); i++) {
            Vector xi = it.next().getValue();
            for (int j = 0; j < d; j++) {
                x[j][i] = xi.get(j + 1);
            }
        }

        // Hypothesis test that dx are normally distributed
        for (int i = 0; i < d; i++) {
            double[] dxi = DoubleUtils.diff(x[i]);
            ShapiroWilk test = new ShapiroWilk(dxi);
            System.out.printf("Shapiro-Wilk test result for dx%d: %f%n", i, test.pValue());
        }

        // Print covariance for all combinations of dx
        for (int i = 0; i < d; i++) {
            for (int j = i + 1; j < d; j++) {
                Covariance cov = new Covariance(DoubleUtils.diff(x[i]), DoubleUtils.diff(x[j]));
                System.out.printf("Covariance of dx%d and dx%d: %f%n", i, j, cov.value());
            }
        }
    }

    /**
     * Computes a realization of a SDE using Euler's method, where the SDE has the given drift and
     * diffusion.
     *
     * @param mu    the drift of the SDE
     * @param sigma the diffusion of the SDE
     * @param nT    the number of grid point in interval [0, 1], i.e. the number of steps to
     *              simulate
     */
    public static void multiNormal(DriftVector mu, DiffusionMatrix sigma, int nT) {
        final int nB = sigma.nB();
        MultivariateSDE sde = new MultivariateSDE(
            mu,
            sigma,
            nB);

        double timeEnd = 1.0;
        TimeGrid t = new EvenlySpacedGrid(0.0, timeEnd, nT);
        double dt = timeEnd / nT;

        MultivariateDiscreteSDE euler = new MultivariateEulerSDE(sde);
        Vector x0 = new DenseVector(nB, 0.0);

        MultivariateRandomRealizationOfRandomProcess Xt = new MultivariateRandomRealizationOfRandomProcess(euler, t, x0);
        MultivariateRealization xt = Xt.nextRealization();
        Iterator<MultivariateRealization.Entry> it = xt.iterator();

        double[][] x = new double[nB][nT];

        for (int i = 0; it.hasNext(); i++) {
            Vector xi = it.next().getValue();
            for (int j = 0; j < nB; j++) {
                x[j][i] = xi.get(j + 1);
            }
        }

        // Hypothesis test that dx are normally distributed and print variance
        for (int i = 0; i < nB; i++) {
            double[] dxi = DoubleUtils.diff(x[i]);
            ShapiroWilk test = new ShapiroWilk(dxi);
            Variance var = new Variance(dxi);
            System.out.printf("Shapiro-Wilk test result for dx%d: %f, Standard Deviation: %f%n",
                              i,
                              test.pValue(),
                              var.standardDeviation() / dt);
        }
    }

    /**
     * This is an implementation of "Wayne A. Fuller. "Introduction to Statistical Time Series".
     * Chapter 10. pp.553, 554, 561, 568." It computes the empirical distribution by Monte Carlo
     * simulation and prints various of its properties.
     *
     * @param type  the types of Dickey-Fuller tests available
     * @param nT    the number of grid point in interval [0, 1]
     * @param nSims the number of simulations
     * @return an empirical distribution of the test statistics
     */
    public static EmpiricalDistribution ADF(Type type, int nT, int nSims) {
        TimeGrid t = new EvenlySpacedGrid(0, 1, nT);
        int nB = 1;

        DriftVector drift = new DriftVector() {
            @Override
            public Vector evaluate(MultivariateFt ft) {
                MultivariateFtWt ftwt = (MultivariateFtWt) ft;

                double t = ftwt.t();
                double Wt = ftwt.Wt().get(1);

                double dG = Wt * Wt; // Fuller, p.553, eq. 10.1.14
                double dT = 0; // Fuller, p.553, eq. 10.1.14
                double dH = Wt; // Fuller, p.561, Theorem 10.1.3
                double dK = 2 * t * Wt - dH; // Fuller, p.568, Theorem 10.1.6

                return new DenseVector(new double[]{dG, dT, dH, dK});
            }
        };
        DiffusionMatrix diffusion = new ConstantSigma1(new DenseMatrix(
            new double[][]{
            {0},
            {1}, // dT; Fuller, p.553, eq. 10.1.14
            {0},
            {0}
        }));
        MultivariateSDE sde = new MultivariateSDE(
            drift,
            diffusion,
            nB) {
            @Override
            public MultivariateFt getFt() {
                return new MultivariateFtWt();
            }
        };

        Vector x0 = new DenseVector(diffusion.dimension(), 0.0);

        MultivariateDiscreteSDE dSDE = new MultivariateEulerSDE(sde);
        MultivariateRandomRealizationGenerator Xt = new MultivariateRandomRealizationOfRandomProcess(dSDE, t, x0);

        double[] stats = new double[nSims];

        /*
         * G, T, H, K are the intermediate variables in computing the asymptotic distribution of ADF
         * test statistics.
         */
        double[] G = new double[nSims]; // Fuller, p.553, eq. 10.1.14
        double[] T = new double[nSims]; // Fuller, p.553, eq. 10.1.14
        double[] H = new double[nSims]; // Fuller, p.561, Theorem 10.1.3
        double[] K = new double[nSims]; // Fuller, p.568, Theorem 10.1.6

        for (int j = 0; j < nSims; ++j) {
            MultivariateRealization xt = Xt.nextRealization();
            Vector GTHK = getLastValue(xt.iterator());
            G[j] = GTHK.get(1);
            T[j] = GTHK.get(2);
            H[j] = GTHK.get(3);
            K[j] = GTHK.get(4);

            switch (type) {
                /*
                 * Fuller, p.554, Corollary 10.1.1.2
                 * DF -> (T^2 - 1) / 2 / sqrt(G)
                 */
                case DICKEY_FULLER:
                    stats[j] = 0.5 * (pow(T[j], 2) - 1) / pow(G[j], 0.5);
                    break;
                /*
                 * Fuller, p.568, Theorem 10.1.6
                 * ADF -> 0.5 * ((T-2H) * (T-6K) - 1) / sqrt(G - H^2 - 3K^2)
                 */
                case AUGMENTED_DICKEY_FULLER:
                    stats[j] = 0.5 * ((T[j] - 2 * H[j]) * (T[j] - 6 * K[j]) - 1) / pow(G[j] - pow(H[j], 2) - 3 * pow(K[j], 2), 0.5);
                    break;
                default:
                    throw new RuntimeException("unreachable");
            }
        }

        EmpiricalDistribution dist = new EmpiricalDistribution(stats);
        System.out.printf("mean: %f, variance: %f, skew: %f, kurtosis: %f%n",
                          dist.mean(), dist.variance(), dist.skew(), dist.kurtosis());
        return dist;
    }

    private static Vector getLastValue(Iterator<MultivariateRealization.Entry> it) {
        Vector lastValue = null;
        while (it.hasNext()) {
            lastValue = it.next().getValue();
        }
        return lastValue;
    }
}
