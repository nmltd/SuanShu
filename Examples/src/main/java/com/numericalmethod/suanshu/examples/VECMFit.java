/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.numericalmethod.suanshu.examples;

import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.Matrix;
import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.matrixtype.dense.DenseMatrix;
import com.numericalmethod.suanshu.algebra.linear.vector.doubles.Vector;
import com.numericalmethod.suanshu.algebra.linear.vector.doubles.dense.DenseVector;
import com.numericalmethod.suanshu.stats.timeseries.datastructure.multivariate.realtime.inttime.MultivariateIntTimeTimeSeries;
import com.numericalmethod.suanshu.stats.timeseries.datastructure.multivariate.realtime.inttime.MultivariateSimpleTimeSeries;
import com.numericalmethod.suanshu.stats.timeseries.linear.multivariate.arima.VARIMASim;
import com.numericalmethod.suanshu.stats.timeseries.linear.multivariate.stationaryprocess.arma.VARFit;
import com.numericalmethod.suanshu.stats.timeseries.linear.multivariate.stationaryprocess.arma.VARModel;
import com.numericalmethod.suanshu.stats.timeseries.linear.multivariate.stationaryprocess.arma.VARXModel;
import com.numericalmethod.suanshu.stats.timeseries.linear.multivariate.stationaryprocess.arma.VECMLongrun;

/**
 *
 * @author haksunli
 */
public class VECMFit {

    public static void main(String[] args) {
        System.out.println("This class demonstrates how to fit a multivaraite time series in various forms in Java using SuanShu.");
        fitVECM();
    }

    public static void fitVECM() {
        Vector mu = new DenseVector(new double[]{1., 2.});
        Matrix[] phi = new Matrix[]{
            new DenseMatrix(new double[][]{
                {0.2, 0.3},
                {0., 0.4}}),
            new DenseMatrix(new double[][]{
                {0.1, 0.2},
                {0.3, 0.1}})
        };
        Matrix sigma = new DenseMatrix(new double[][]{
            {0.2, 0.1},
            {0.1, 0.2}});
        VARModel model0 = new VARModel(mu, phi, sigma);

        // generate a sample
        VARIMASim sim = new VARIMASim(model0);
        sim.seed(1234567891L);

        final int N = 5000;
        double[][] ts = new double[N][];
        for (int i = 0; i < N; ++i) {
            ts[i] = sim.nextVector();
        }
        MultivariateIntTimeTimeSeries mts = new MultivariateSimpleTimeSeries(ts);

        // fit a VAR model
        VARModel fitted = new VARFit(mts, 2);
        System.out.println("mu:");
        System.out.println(fitted.mu());
        System.out.println("phi0");
        System.out.println(fitted.AR(1));
        System.out.println("phi1");
        System.out.println(fitted.AR(2));

        // convert a VAR model into a VECM model
        VARXModel varx = new VARXModel(fitted.mu(),
                                       fitted.phi(),
                                       new DenseMatrix(2, 2).ZERO(),
                                       fitted.sigma());
        VECMLongrun vecm = new VECMLongrun(varx);
        System.out.println("vecm:");
        System.out.println(vecm.mu());
        System.out.println(vecm.p());
        System.out.println(vecm.gamma(1));
        System.out.println(vecm.gamma(2));
        System.out.println(vecm.psi());
        System.out.println(vecm.sigma());
    }
}
