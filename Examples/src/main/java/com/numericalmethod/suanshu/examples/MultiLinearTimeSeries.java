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
import com.numericalmethod.suanshu.misc.ArgumentAssertion;
import com.numericalmethod.suanshu.stats.timeseries.datastructure.multivariate.realtime.inttime.MultivariateIntTimeTimeSeries;
import com.numericalmethod.suanshu.stats.timeseries.datastructure.multivariate.realtime.inttime.MultivariateSimpleTimeSeries;
import com.numericalmethod.suanshu.stats.timeseries.linear.multivariate.stationaryprocess.arma.*;

/**
 * Demonstrates how to construct and compute the properties for multivariate linear time series in
 * Java using SuanShu.
 *
 * @author Haksun Li
 */
public final class MultiLinearTimeSeries {

    /**
     * The VARMA model used to demonstrate how to compute the auto-covariance.
     */
    public static final VARMAModel MODEL1 = getExampleVARMAModel();
    /**
     * The number of lags used by the auto-covariance demonstration.
     */
    public static final int N_LAGS = 10;
    /**
     * The AR components of an example model.
     */
    public static final Matrix PHI2 = new DenseMatrix(
        new double[][]{
        {0.7, 0},
        {0, 0.6}
    });
    /**
     * The MA components of an example model.
     */
    public static final Matrix THETA2 = new DenseMatrix(
        new double[][]{
        {0.5, 0.6},
        {-0.7, 0.8}
    });
    /**
     * The covariance matrix of an example model.
     */
    public static final Matrix SIGMA2 = new DenseMatrix(
        new double[][]{
        {1, 0.71},
        {0.71, 2}
    });
    /**
     * An example model.
     */
    public static final VARMAModel MODEL2 = new VARMAModel(
        new Matrix[]{PHI2},
        new Matrix[]{THETA2});
    /**
     * An example bivariate time-series.
     */
    public static final MultivariateIntTimeTimeSeries X_T = new MultivariateSimpleTimeSeries(
        new double[][]{
        {-1.875, 1.693},
        {-2.518, -0.03},
        {-3.002, -1.057},
        {-2.454, -1.038},
        {-1.119, -1.086},
        {-0.72, -0.455},
        {-2.738, 0.962},
        {-2.565, 1.992},
        {-4.603, 2.434},
        {-2.689, 2.118}
    });
    /**
     * The AR components for an example model.
     */
    public static final Matrix PHI3 = new DenseMatrix(
        new double[][]{
        {0.5, 0.5},
        {0, 0.5}
    });
    /**
     * The MA components for an example model.
     */
    public static final Matrix THETA3 = PHI3.t();
    /**
     * An example model.
     */
    public static final VARMAModel MODEL3 = new VARMAModel( // unit covariance (identity matrix)
        new Matrix[]{PHI3},
        new Matrix[]{THETA3});

    private MultiLinearTimeSeries() {
    }

    private static VARMAModel getExampleVARMAModel() {
        Matrix[] PHI = new Matrix[4];
        PHI[0] = new DenseMatrix(new DenseVector(new double[]{0.3}));
        PHI[1] = new DenseMatrix(new DenseVector(new double[]{-0.2}));
        PHI[2] = new DenseMatrix(new DenseVector(new double[]{0.05}));
        PHI[3] = new DenseMatrix(new DenseVector(new double[]{0.04}));

        Matrix[] THETA = new Matrix[2];
        THETA[0] = new DenseMatrix(new DenseVector(new double[]{0.2}));
        THETA[1] = new DenseMatrix(new DenseVector(new double[]{0.5}));

        Matrix sigma = new DenseMatrix(new double[][]{{1}});//the covariance matrix

        return new VARMAModel(PHI, THETA, sigma);
    }

    /**
     * Demonstrates some multivariate time series functionality by computing the auto-covariance, a
     * next-step predictor and a linear representation of different time series.
     *
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("This class demonstrates how to construct and compute the properties "
            + "for multivariate linear time series in Java using SuanShu.");
        autoCovariance(MODEL1, 10);
        innovationAlgorithm(X_T, MODEL2);
        linearRepresentation(MODEL3);
    }

    /**
     * Prints the auto-covariance of the given model with the given model. The model must have a 1x1
     * covariance matrix, to make the output easier to read for testing purposes (SuanShu supports
     * arbitrary dimensional covariance matrices).
     *
     * @param model the model for which to compute the auto-covariance
     * @param nLags the number of lags
     */
    public static void autoCovariance(VARMAModel model, int nLags) {
        ArgumentAssertion.assertTrue(model.sigma().nCols() == 1 && model.sigma().nRows() == 1,
                                     "Covariance matrix must be 1x1");

        VARMAAutoCovariance instance = new VARMAAutoCovariance(model, nLags);

        System.out.printf("Auto covariance: ");
        for (int i = 0; i < nLags; i++) {
            System.out.printf("%f, ", instance.evaluate(i).get(1, 1));
        }
    }

    /**
     * Computes a one step least squares predictor for the given time series from the innovations
     * algorithm and prints the predictor and the covariance matrix for prediction errors for each
     * step.
     *
     * @param timeSeries the time series for which to compute the predictor
     * @param model      the model to use to compute the predictor
     */
    public static void innovationAlgorithm(
        MultivariateIntTimeTimeSeries timeSeries,
        VARMAModel model) {
        VARMAForecastOneStep instance = new VARMAForecastOneStep(X_T, model);

        int T = timeSeries.size();
        for (int t = 0; t <= T; t++) {
            Vector xTHat = instance.xHat(t + 1);
            Matrix V = instance.covariance(t);
            System.out.printf("Predictor at time %d: %s, covariance of errors: %s%n",
                              t, xTHat, V);
        }
    }

    /**
     * Prints the coefficients for the linear representation of the given VARMA model, which is a
     * (truncated) infinite sum of AR terms.
     *
     * @param model the model for which to print the linear representation
     */
    public static void linearRepresentation(VARMAModel model) {
        VARLinearRepresentation instance = new VARLinearRepresentation(
            MODEL2);
        for (int i = 0; i < instance.p(); i++) {
            System.out.println(instance.AR(i));
        }
    }
}
