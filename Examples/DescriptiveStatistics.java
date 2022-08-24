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
import com.numericalmethod.suanshu.stats.descriptive.correlation.CorrelationMatrix;
import com.numericalmethod.suanshu.stats.descriptive.covariance.SampleCovariance;

/**
 * Demonstrates how to compute descriptive statistics for data sets.
 *
 * @author Haksun Li
 */
public final class DescriptiveStatistics {

    /**
     * The test data for which we compute descriptive statistics in the demo.
     */
    public static final Matrix A = new DenseMatrix(
            new double[][]{
                {1.4022225, -0.04625344, 1.26176112, -1.8394428, 0.7182637},
                {-0.2230975, 0.91561987, 1.17086252, 0.2282348, 0.0690674},
                {0.6939930, 1.94611387, -0.82939259, 1.0905923, 0.1458883},
                {-0.4050039, 0.18818663, -0.29040783, 0.6937185, 0.4664052},
                {0.6587918, -0.10749210, 3.27376532, 0.5141217, 0.7691778},
                {-2.5275280, 0.64942255, 0.07506224, -1.0787524, 1.6217606}
            });

    private DescriptiveStatistics() {
    }

    /**
     * Computes the covariance and correlation matrix for the test data in {@link #A}.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        System.out.println(
                "This class demonstrates how to compute descriptive statistics for data sets.");
        Matrix cov = printCovarianceMatrix(A);
        printCorrelationMatrix(cov);
    }

    /**
     * Compute and print the covariance matrix of the given time series.
     *
     * @param timeSeries the time series for which to compute the covariance matrix
     * @return the covariance matrix of the given time series
     */
    public static Matrix printCovarianceMatrix(Matrix timeSeries) {
        Matrix cov = new SampleCovariance(timeSeries);
        System.out.println("covariance:");
        System.out.println(cov);
        return cov;
    }

    /**
     * Compute and print the correlation matrix of the time series with the given covariance matrix.
     *
     * @param cov the covariance matrix of the time series for which to compute the correlation
     *            matrix
     * @return the correlation matrix from the given covariance matrix
     */
    public static Matrix printCorrelationMatrix(Matrix cov) {
        Matrix cor = new CorrelationMatrix(cov);
        System.out.println("correlation:");
        System.out.println(cor);
        return cor;
    }
}
