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
import com.numericalmethod.suanshu.stats.cointegration.*;
import com.numericalmethod.suanshu.stats.timeseries.datastructure.multivariate.realtime.inttime.MultivariateSimpleTimeSeries;

/**
 * Demonstrates how to compute the cointegrating factors (beta) and adjustment factors (alpha) for a
 * system of multivariate time series.
 *
 * @author Haksun Li
 */
public final class Cointegration {

    /**
     * We use the example from Søren Johansen. Likelihood-Based Inference in Cointegrated Vector
     * Autoregressive Models. Oxford University Press, USA. February 1, 1996.
     *
     * This values are obtained from R output of the following script:
     *
     * library(urca) data(finland) sjf &lt;- finland summary(ca.jo(sjf, ecdet = "none",
     * type="eigen", K=2))
     */
    public static final MultivariateSimpleTimeSeries FINLAND =
            new MultivariateSimpleTimeSeries(new double[][]{
                {2.848664, 3.836539, 0.1731126, 0.01481509},
                {2.82801, 3.902982, 0.1012019, 0},
                {2.908186, 3.89688, 0.1320803, 0},
                {2.934326, 3.847045, 0.06999237, 0},
                {2.986127, 3.916454, 0.06531947, 0},
                {3.006543, 3.986216, 0.06531947, 0.0145988},
                {3.016235, 3.984211, 0.06531947, 0.01438874},
                {2.95403, 3.956458, 0.06531947, 0.01418463},
                {3.00275, 3.990079, 0.1612681, 0},
                {3.041873, 4.048979, 0.243495, 0.01398624},
                {3.033884, 4.053306, 0.1633086, 0},
                {3.058968, 4.066436, 0.1101092, 0},
                {3.03522, 4.088541, 0.1224831, 0},
                {3.087247, 4.146216, 0.1173385, 0},
                {3.100822, 4.125302, 0.1476438, 0.02739897},
                {3.05858, 4.108145, 0.1752969, 0},
                {3.039497, 4.095878, 0.2291256, 0.02666825},
                {3.07537, 4.157463, 0.242397, 0},
                {3.107436, 4.159411, 0.2403546, 0.01307208},
                {3.137721, 4.100841, 0.210909, 0.0129034},
                {3.127759, 4.169761, 0.1712606, 0.01273903},
                {3.128404, 4.199699, 0.1891316, 0.01257878},
                {3.199932, 4.197109, 0.1706706, 0.01242252},
                {3.068053, 4.178529, 0.1367138, 0.05989814},
                {3.072799, 4.178418, 0.1686451, 0.02298952},
                {3.084895, 4.192901, 0.1779788, 0.01129956},
                {3.135006, 4.260383, 0.1972102, 0},
                {3.099091, 4.222265, 0.1833211, 0.0111733},
                {3.090548, 4.219907, 0.1354046, 0.02197891},
                {3.078614, 4.276832, 0.06765865, 0},
                {3.110883, 4.293151, 0.06765865, 0.01081092},
                {3.024568, 4.199678, 0.06765865, 0.01069529},
                {3.083357, 4.276213, 0.1153802, 0.01058211},
                {3.04108, 4.299289, 0.2200187, 0.02083409},
                {3.117137, 4.341603, 0.2944591, 0.0102565},
                {3.026565, 4.293609, 0.06765865, 0.01015237},
                {3.046901, 4.315433, 0.1218636, 0.01005034},
                {2.99375, 4.32215, 0.09184965, 0.009950331},
                {3.006729, 4.312939, 0.1208893, 0.02927038},
                {2.991557, 4.30584, 0.09166719, 0.03774033},
                {3.047116, 4.333206, 0.06765865, 0.01834914},
                {3.073535, 4.380844, 0.06765865, 0},
                {3.180678, 4.3923, 0.06765865, 0.009049836},
                {3.135102, 4.420402, 0.06765865, 0},
                {3.193558, 4.4441, 0.06765865, 0.00896867},
                {3.196119, 4.489679, 0.06765865, 0},
                {3.333479, 4.518776, 0.0719485, 0},
                {3.411379, 4.490783, 0.08231704, 0.01769958},
                {3.459963, 4.536882, 0.1513468, 0},
                {3.451505, 4.561972, 0.2311117, 0.00873368},
                {3.530157, 4.619116, 0.1854001, 0.008658063},
                {3.297392, 4.51013, 0.07927318, 0.0255333},
                {3.280233, 4.575117, 0.1179608, 0.02489755},
                {3.307619, 4.569593, 0.09285262, 0.02429269},
                {3.451498, 4.631627, 0.08157999, 0.00796817},
                {3.437284, 4.60271, 0.07464354, 0.00790518},
                {3.485342, 4.62328, 0.07464354, 0.03101024},
                {3.500827, 4.64374, 0.07464354, 0.01515181},
                {3.606705, 4.715684, 0.07464354, 0.01492565},
                {3.508857, 4.689491, 0.07464354, 0.02919915},
                {3.580815, 4.691851, 0.07464354, 0.03533937},
                {3.494112, 4.703741, 0.09558287, 0.05406722},
                {3.66848, 4.80992, 0.2311117, 0.02597549},
                {3.49595, 4.745103, 0.1684761, 0.04389419},
                {3.553317, 4.809414, 0.1478164, 0.03614851},
                {3.536008, 4.751507, 0.08993211, 0.05748709},
                {3.689291, 4.880777, 0.1377599, 0.01662088},
                {3.64695, 4.762205, 0.1298873, 0.0586835},
                {3.66369, 4.81644, 0.1754648, 0.03562718},
                {3.565684, 4.757928, 0.1619488, 0.04401689},
                {3.783132, 4.807621, 0.2462349, 0.02830378},
                {3.641322, 4.695385, 0.1785645, 0.04546237},
                {3.6362, 4.77279, 0.1779788, 0.01324523},
                {3.580232, 4.766532, 0.1593941, 0.03871451},
                {3.647325, 4.842663, 0.1674617, 0.02087759},
                {3.629197, 4.695384, 0.1413259, 0.04049136},
                {3.64542, 4.732926, 0.1524635, 0.03509132},
                {3.585797, 4.729782, 0.1655144, 0.02646657},
                {3.595339, 4.801802, 0.18057, 0.01113184},
                {3.599353, 4.698796, 0.1707549, 0.01465228},
                {3.693067, 4.762504, 0.09148469, 0.01801851},
                {3.66311, 4.739455, 0.08709471, 0.01418463},
                {3.693763, 4.84324, 0.09285262, 0.007017573},
                {3.661466, 4.771751, 0.08056565, 0.02758796},
                {3.789403, 4.84193, 0.08038111, 0.02020271},
                {3.752624, 4.81652, 0.08111905, 0.0165293},
                {3.813215, 4.890468, 0.1118991, 0.01948114},
                {3.72502, 4.785139, 0.1160927, 0.03475863},
                {3.770843, 4.837455, 0.139066, 0.04255961},
                {3.713289, 4.86724, 0.1483338, 0.02643326},
                {3.745108, 4.922232, 0.1531502, 0.0257525},
                {3.722089, 4.799556, 0.1633086, 0.03060044},
                {3.733176, 4.875043, 0.1217751, 0.03234783},
                {3.73251, 4.868998, 0.1244276, 0.01839737},
                {3.788271, 4.936803, 0.1403705, 0.01293679},
                {3.705715, 4.802846, 0.1309405, 0.03287275},
                {3.813328, 4.890625, 0.1293602, 0.02457126},
                {3.821703, 4.891206, 0.1282173, 0.007255171},
                {3.849595, 4.96843, 0.1410654, 0.02145494},
                {3.80396, 4.834531, 0.1339188, 0.01938634},
                {3.863014, 4.923077, 0.1397619, 0.03723472},
                {3.852309, 4.93591, 0.1445332, 0.01262335},
                {3.826078, 4.980688, 0.1551216, 0.02735139},
                {3.782735, 4.866376, 0.1612681, 0.004273511},
                {3.830368, 4.959666, 0.1544364, 0.01900796},
                {3.825409, 4.95232, 0.1516905, 0.01453816}
            });

    private Cointegration() {
    }

    /**
     * Runs cointegration on the data in {@link #FINLAND}.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        System.out.println("This class demonstrates how to compute the cointegrating factors (beta)"
                           + " and adjustment factors (alpha) for a system of multivariate time series.");
        cointegration(FINLAND);
    }

    /**
     * Computes the cointegration of a system of multivariate time series and prints out the
     * eigenvalues, cointegrating factors, speeds of adjustment and Johansen test statistics.
     *
     * @param timeSeries the multivariate time series for which to compute the cointegration
     */
    public static void cointegration(MultivariateSimpleTimeSeries timeSeries) {
        CointegrationMLE coint = new CointegrationMLE(timeSeries, true, 2);

        Vector eigenvalues = coint.getEigenvalues();
        System.out.println("eigenvalues:");
        System.out.println(eigenvalues);
        System.out.println("cointegrating factors");
        System.out.println(coint.beta());
        System.out.println("speeds of adjustment");
        System.out.println(coint.alpha());

        System.out.println("test statistics");
        JohansenTest test = new JohansenTest(
                JohansenAsymptoticDistribution.Test.EIGEN,
                JohansenAsymptoticDistribution.TrendType.CONSTANT,
                eigenvalues.size());
        System.out.println(test.getStats(coint));
    }
}
