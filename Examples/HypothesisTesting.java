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

import com.numericalmethod.suanshu.stats.distribution.univariate.NormalDistribution;
import com.numericalmethod.suanshu.stats.test.distribution.kolmogorov.*;

/**
 * Demonstrates how to do hypothesis testing for data sets. Also demonstrates the support for ties
 * in samples, which R doesn't support.
 *
 * @author Haksun Li
 */
public final class HypothesisTesting {

    /**
     * Test sample drawn from a normal distribution.
     */
    public static final double[] SAMPLE1 = new double[]{
        1.2142038235675114, 0.8271665834857130, -2.2786245743283295, 0.8414895245471727,
        -1.4327682855296735, -0.2501807766164897, -1.9512765152306415, 0.6963626117638846,
        0.4741320101265005, -1.2340784297133520
    };
    /**
     * Like the first sample but contains duplicates.
     */
    public static final double[] SAMPLE1_DUPLICATE = new double[]{
        1.2142038235675114, 0.8271665834857130, -2.2786245743283295, 0.8414895245471727,
        -1.4327682855296735, -0.2501807766164897, -1.9512765152306415, 0.6963626117638846,
        0.4741320101265005, 1.2142038235675114
    };
    /**
     * Test sample drawn from a normal distribution.
     */
    public static final double[] SAMPLE2 = new double[]{
        1.7996197748754565, -1.1371109188816089, 0.8179707525071304, 0.3809791236763478,
        0.1644848304811257, 0.3397412780581336, -2.2571685407244795, 0.4137315314876659,
        0.7318687611171864, 0.9905218801425318, -0.4748590846019594, 0.8882674167954235,
        1.0534065683777052, 0.2553123235884622, -2.3172807717538038
    };
    /**
     * Like the second sample but contains a tie with the first sample.
     */
    public static final double[] SAMPLE2_TIE = new double[]{
        -1.2340784297133520,
        1.7996197748754565, -1.1371109188816089, 0.8179707525071304, 0.3809791236763478,
        0.1644848304811257, 0.3397412780581336, -2.2571685407244795, 0.4137315314876659,
        0.7318687611171864, 0.9905218801425318, -0.4748590846019594, 0.8882674167954235,
        1.0534065683777052, 0.2553123235884622, -2.3172807717538038
    };

    private HypothesisTesting() {
    }

    /**
     * Performs Kolmogorov tests of a sample against both a normal distribution and against another
     * sample, with and without duplicates.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        System.out.println("This class demonstrates how to do hypothesis testing for data sets.");
        // test against normal distribution
        kolmogorovSmirnov1Sample(SAMPLE1, KolmogorovSmirnov.Side.TWO_SIDED);
        kolmogorovSmirnov1Sample(SAMPLE1, KolmogorovSmirnov.Side.GREATER);
        kolmogorovSmirnov1Sample(SAMPLE1, KolmogorovSmirnov.Side.LESS);
        kolmogorovSmirnov1Sample(SAMPLE1_DUPLICATE, KolmogorovSmirnov.Side.TWO_SIDED);

        // test against another sample
        kolmogorovSmirnov2Samples(SAMPLE1, SAMPLE2, KolmogorovSmirnov.Side.TWO_SIDED);
        kolmogorovSmirnov2Samples(SAMPLE1, SAMPLE2, KolmogorovSmirnov.Side.GREATER);
        kolmogorovSmirnov2Samples(SAMPLE1, SAMPLE2, KolmogorovSmirnov.Side.LESS);
        kolmogorovSmirnov2Samples(SAMPLE1, SAMPLE2_TIE, KolmogorovSmirnov.Side.TWO_SIDED);
    }

    /**
     * Performs a Kolmogorov-Smirnov test with the given sample against a normal distribution and
     * prints the results.
     *
     * @param sample the sample on which to perform the test
     * @param side   the side for which to perform the test
     */
    public static void kolmogorovSmirnov1Sample(double[] sample, KolmogorovSmirnov.Side side) {
        KolmogorovSmirnov1Sample instance = new KolmogorovSmirnov1Sample(
                sample,
                new NormalDistribution(), side);

        System.out.printf("p-value = %f; test stats = %f; null: %s%n",
                          instance.pValue(),
                          instance.statistics(),
                          instance.getNullHypothesis());
    }

    /**
     * Performs a Kolmogorov-Smirnov test with the given pair of samples.
     *
     * @param sample1 the first sample on which to perform the test
     * @param sample2 the second sample on which to perform the test
     * @param side    the side for which to perform the test
     */
    public static void kolmogorovSmirnov2Samples(
            double[] sample1, double[] sample2, KolmogorovSmirnov.Side side) {
        KolmogorovSmirnov2Samples instance = new KolmogorovSmirnov2Samples(
                sample1, sample2, side);

        System.out.printf("p-value = %f; test stats = %f; null: %s%n",
                          instance.pValue(),
                          instance.statistics(),
                          instance.getNullHypothesis());
    }
}
