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

import com.numericalmethod.suanshu.algebra.linear.vector.doubles.Vector;
import com.numericalmethod.suanshu.stats.random.rng.RNGUtils;
import com.numericalmethod.suanshu.stats.timeseries.datastructure.univariate.realtime.inttime.IntTimeTimeSeries;
import com.numericalmethod.suanshu.stats.timeseries.datastructure.univariate.realtime.inttime.SimpleTimeSeries;
import com.numericalmethod.suanshu.stats.timeseries.linear.univariate.arima.*;
import com.numericalmethod.suanshu.stats.timeseries.linear.univariate.stationaryprocess.arma.*;
import com.numericalmethod.suanshu.stats.timeseries.linear.univariate.stationaryprocess.garch.*;
import java.util.Arrays;
import java.util.List;

/**
 * Demonstrates how to construct and compute the properties for univariate linear time series in
 * Java using SuanShu.
 *
 * @author Haksun Li
 */
public final class LinearTimeSeries {

    private LinearTimeSeries() {
    }

    /**
     * Prints an ARIMA time series and generates data from a GARCH time series and prints a GARCH
     * model fitted to the generated series.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        System.out.println("This class demonstrates how to construct and compute the properties for"
            + " univariate linear time series in Java using SuanShu.");

        System.out.printf("Example ARIMA time series: %s%n",
                          Arrays.toString(
                              generateARIMA(10, new double[]{0.3, -0.02}, 1, new double[]{0.6})));

        double[] GARCHTimeSeries = generateGARCH(10010, 0.2, new double[]{0.3}, new double[]{0.6});
        System.out.printf("Fitted GARCH model: %s%n", fitGARCH(GARCHTimeSeries, 1, 1));

        System.out.println("Example ARMA time series one-step ahead forecast");
        armaForecastOneStep();

        System.out.println("Example ARMA time series multi-step ahead forecast");
        armaForecastMultiStep();

        System.out.println("Example ARMA time series forecast");
        armaForecast();

        System.out.println("Example ARIMA time series one-step ahead forecast");
        arimaForecast_0010();

        System.out.println("Example ARIMA time series multi-step ahead forecast");
        arimaForecast_0020();

        System.out.println("Example ARIMA time series forecast");
        arimaForecast_0030();

        System.out.println("Example fitting an ARMA and then GARCH on residuals");
        fit_arima_residual_garch();
    }

    /**
     * Generate an ARIMA time series of the given length with the given parameters.
     *
     * @param n  the length of the time series
     * @param AR the autoregressive component coefficients
     * @param d  the order of integration
     * @param MA the moving-average component coefficients
     * @return the generated time series
     */
    public static double[] generateARIMA(int n, double[] AR, int d, double[] MA) {
        ARIMASim sim = new ARIMASim(new ARIMAModel(AR, d, MA));
        return RNGUtils.nextN(sim, n);
    }

    /**
     * Generate a GARCH time series of the given length with the given parameters.
     *
     * @param n  the length of the time series that is to be generated
     * @param a0 the constant term
     * @param a  the ARCH coefficients
     * @param b  the GARCH coefficients
     *
     * @return the generated time series.
     */
    public static double[] generateGARCH(int n, double a0, double[] a, double[] b) {
        GARCHSim sim = new GARCHSim(new GARCHModel(a0, a, b));
        return RNGUtils.nextN(sim, n);
    }

    /**
     * Fits a GARCH model with the given order to the given time series.
     *
     * @param timeSeries the time series to fit a GARCH model to
     * @param p          the order of the ARCH coefficients
     * @param q          the order of the GARCH coefficients
     * @return the fitted model
     */
    public static GARCHModel fitGARCH(double[] timeSeries, int p, int q) {
        GARCHFit instance = new GARCHFit(timeSeries, p, q);
        return instance.getModel();
    }

    /**
     * example 5.3.4, time series - theory and methods, 2nd, Brockwell & Davis
     */
    public static void armaForecastOneStep() {
        ARMAModel arma = new ARMAModel(
            new double[]{1.0, -0.24},
            new double[]{0.4, 0.2, 0.1});
        IntTimeTimeSeries xt = new SimpleTimeSeries(new double[]{1.704, 0.527, 1.041, 0.942, 0.555, -1.002, -0.585, 0.010, -0.638, 0.525});
        ARMAForecastOneStep instance = new ARMAForecastOneStep(xt, arma);

        for (int i = 0; i <= 10; ++i) {
            System.out.printf("x^: %f (%f)%n", instance.xHat(i), instance.var(i));
        }
    }

    /**
     * example 5.3.5, time series - theory and methods, 2nd, Brockwell & Davis
     */
    public static void armaForecastMultiStep() {
        ARMAModel arma = new ARMAModel(
            new double[]{1.0, -0.24},
            new double[]{0.4, 0.2, 0.1});
        IntTimeTimeSeries xt = new SimpleTimeSeries(new double[]{1.704, 0.527, 1.041, 0.942, 0.555, -1.002, -0.585, 0.010, -0.638, 0.525});
        ARMAForecastMultiStep instance = new ARMAForecastMultiStep(xt, arma, 3);
        Vector xHat = instance.allForecasts();
        Vector var = instance.allMSEs();

        System.out.printf("x^= %f (%f)%n", xHat.get(1), var.get(1));
        System.out.printf("x^= %f (%f)%n", xHat.get(2), var.get(2));
        System.out.printf("x^= %f (%f)%n", xHat.get(3), var.get(3));
    }

    /**
     * example 5.3.4, time series - theory and methods, 2nd, Brockwell & Davis
     */
    public static void armaForecast() {
        ARMAModel arma = new ARMAModel(
            new double[]{1.0, -0.24},
            new double[]{0.4, 0.2, 0.1});
        IntTimeTimeSeries xt = new SimpleTimeSeries(new double[]{1.704, 0.527, 1.041, 0.942, 0.555, -1.002, -0.585, 0.010, -0.638, 0.525});
        ARMAForecast instance = new ARMAForecast(xt, arma);

        List<ARIMAForecast.Forecast> forecasts = instance.next(10);
        System.out.printf("forecasts = %s%n", forecasts);
    }
    /**
     * example 9.5.1, time series - theory and methods, 2nd, Brockwell & Davis
     */
    public static double[] x = new double[]{
        -2.533221, -9.075923, -19.16261, -33.12857, -52.81117, -76.74696, -103.2003, -132.419, -165.8976, -204.0274,
        -246.0101, -291.2664, -339.8645, -391.4285, -446.3886, -504.7951, -566.7244, -631.5703, -698.1581, -766.6702,
        -836.0842, -905.3469, -975.1016, -1044.21, -1111.252, -1177.355, -1242.904, -1307.318, -1371.756, -1436.51,
        -1499.991, -1561.489, -1620.846, -1679.025, -1736.917, -1794.468, -1851.077, -1906.859, -1960.592, -2009.976,
        -2055.399, -2097.481, -2136.418, -2173.31, -2210.799, -2250.452, -2290.607, -2328.468, -2363.411, -2398.694,
        -2437.595, -2481.963, -2532.981, -2589.522, -2651.942, -2721.417, -2797.623, -2880.387, -2968.625, -3060.799,
        -3156.102, -3253.448, -3353.284, -3456.878, -3564.321, -3674.987, -3788.705, -3906.614, -4028.158, -4152.984,
        -4281.718, -4412.014, -4542.326, -4673.323, -4805.021, -4937.22, -5068.708, -5197.765, -5323.609, -5444.392,
        -5557.963, -5663.825, -5760.606, -5848.78, -5931.625, -6011.752, -6090.37, -6167.945, -6244.318, -6317.813,
        -6387.302, -6453.514, -6518.92, -6584.648, -6649.018, -6711.818, -6773.328, -6834.459, -6896.037, -6957.059,
        -7015.645, -7073.066, -7131.673, -7193.304, -7259.945, -7332.856, -7411.194, -7495.30, -7586.351, -7683.235,
        -7784.692, -7891.431, -8004.574, -8123.962, -8249.225, -8379.052, -8512.296, -8649.823, -8791.181000000001, -8936.366,
        -9086.25, -9239.602999999999, -9394.603999999999, -9552.022999999999, -9713.290000000001, -9878.415000000001, -10047.16, -10219.03, -10392.09, -10564.01,
        -10733.97, -10903.16, -11071.92, -11238.94, -11401.98, -11560.23, -11713.78, -11863.90, -12011.69, -12157.49,
        -12302.53, -12447.54, -12593.37, -12740.76, -12889.65, -13039.82, -13190.25, -13339.56, -13486.25, -13629.28,
        -13769.90, -13910.10, -14051.69, -14196.29, -14345.89, -14499.74, -14657.51, -14819.80, -14986.36, -15158.30,
        -15335.89, -15518.39, -15704.95, -15895.81, -16091.51, -16290.87, -16492.67, -16697.11, -16904.22, -17113.52,
        -17324.42, -17535.69, -17745.23, -17951.45, -18155.61, -18360.14, -18564.21, -18766.18, -18965.08, -19161.70,
        -19356.27, -19546.96, -19733.54, -19919.49, -20107.26, -20294.51, -20478.04, -20655.74, -20827.69, -20993.73,
        -21154.39, -21310.90, -21463.65, -21613.98, -21762.12, -21908.27, -22053.19, -22195.57, -22335.07, -22474.41
    };

    /**
     * example 9.5.1, time series - theory and methods, 2nd, Brockwell & Davis
     */
    public static void arimaForecast_0010() {
        ARIMAModel arima = new ARIMAModel(0, new double[]{0.9}, 2, new double[]{0.8});
        ARIMAForecastMultiStep instance = new ARIMAForecastMultiStep(new SimpleTimeSeries(x), arima, 1);

        System.out.printf("the differenced series y[1] = %f%n", instance.y(1));
        System.out.printf("the differenced series y[1] = %f%n", instance.y(2));
        System.out.printf("the differenced series y[1] = %f%n", instance.y(198));

        System.out.printf("x^ = %f (%f)%n", instance.xHat(), instance.var());
    }

    /**
     * example 9.5.1, time series - theory and methods, 2nd, Brockwell & Davis
     */
    public static void arimaForecast_0020() {
        ARIMAModel arima = new ARIMAModel(0, new double[]{0.9}, 2, new double[]{0.8});
        ARIMAForecastMultiStep instance = new ARIMAForecastMultiStep(new SimpleTimeSeries(x), arima, 2);

        System.out.printf("x^ = %f (%f)%n", instance.xHat(), instance.var());
    }

    /**
     * example 9.5.1, time series - theory and methods, 2nd, Brockwell & Davis
     */
    public static void arimaForecast_0030() {
        ARIMAModel arima = new ARIMAModel(0, new double[]{0.9}, 2, new double[]{0.8});
        ARIMAForecast instance = new ARIMAForecast(
            new SimpleTimeSeries(x),
            arima);

        List<ARIMAForecast.Forecast> forecasts = instance.next(5);
        System.out.printf("forecasts = %s%n", forecasts);
    }

    /**
     * This example first fit a time series to an ARMA(1, 1) model. It then computes the residual
     * time series.
     * Finally, it fits the residuals to a GARCH(1, 1) model.
     */
    public static void fit_arima_residual_garch() {
        // the time series
        IntTimeTimeSeries x = new SimpleTimeSeries(new double[]{-1.1, 0.514, 0.116, -0.845, 0.872, -0.467, -0.977, -1.699, -1.228, -1.093});
        double[] xt = x.toArray();

        // fit an ARMA(1,1) model
        ConditionalSumOfSquares fit1 = new ConditionalSumOfSquares(
            xt,
            1, 0, 1);
        ARMAModel arma = fit1.getARMAModel();
        System.out.printf("the ARMA(1,1) model: %s%n", arma);

        // residual time series        
        ARMAForecastOneStep xt_hat = new ARMAForecastOneStep(x, arma);
        double[] residuals = new double[xt.length];
        for (int t = 0; t < xt.length; ++t) {
            residuals[t] = xt[t] - xt_hat.xHat(t);
        }

        // fit a GARCH(1, 1) model
        GARCHFit fit2 = new GARCHFit(residuals, 1, 1);
        GARCHModel garch = fit2.getModel();
        System.out.printf("the GARCH(1,1) model: %s%n", garch);
    }
}
