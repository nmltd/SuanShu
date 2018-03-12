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
package com.numericalmethod.suanshu.stats.descriptive;

import com.numericalmethod.suanshu.matrix.doubles.AreMatrices;
import com.numericalmethod.suanshu.matrix.doubles.Matrix;
import com.numericalmethod.suanshu.matrix.doubles.matrixtype.dense.DenseMatrix;
import org.junit.Test;

/**
 *
 * @author Haksun Li
 */
public class CorrelationMatrixTest {

    @Test
    public void test_0010() {
        DenseMatrix A = new DenseMatrix(new double[][]{
                    {1.4022225, -0.04625344, 1.26176112, -1.8394428, 0.7182637},
                    {-0.2230975, 0.91561987, 1.17086252, 0.2282348, 0.0690674},
                    {0.6939930, 1.94611387, -0.82939259, 1.0905923, 0.1458883},
                    {-0.4050039, 0.18818663, -0.29040783, 0.6937185, 0.4664052},
                    {0.6587918, -0.10749210, 3.27376532, 0.5141217, 0.7691778},
                    {-2.5275280, 0.64942255, 0.07506224, -1.0787524, 1.6217606}
                });

        CovarianceMatrix cov = new CovarianceMatrix(A);

        DenseMatrix covExpected = new DenseMatrix(new double[][]{
                    {1.8914619605066914, -0.0940529762770646, 0.6656689707202647, 0.1769623013404954, -0.4870227666639277},
                    {-0.0940529762770646, 0.6002733224811770, -0.7425837392715091, 0.4045117724259264, -0.1735474428635912},
                    {0.6656689707202647, -0.7425837392715091, 2.1673062607416513, -0.2506723639944475, 0.0850986167152383},
                    {0.1769623013404954, 0.4045117724259264, -0.2506723639944475, 1.3017513547658062, -0.3858916139755872},
                    {-0.4870227666639277, -0.1735474428635912, 0.0850986167152383, -0.3858916139755872, 0.3173008375647300}
                });

        AreMatrices.equal(covExpected, cov, 1e-14);

        Matrix cor = new CorrelationMatrix(cov);

        DenseMatrix corExpected = new DenseMatrix(new double[][]{
                    {1.0000000000000000, -0.0882671727437672, 0.3287754378466710, 0.1127763235190793, -0.6286585817897393},
                    {-0.0882671727437672, 1.0000000000000000, -0.6510446447124169, 0.4576069637074221, -0.3976565023793239},
                    {0.3287754378466710, -0.6510446447124169, 1.0000000000000000, -0.1492389877846141, 0.1026187567166916},
                    {0.1127763235190793, 0.4576069637074221, -0.1492389877846141, 1.0000000000000000, -0.6004346026136406},
                    {-0.6286585817897393, -0.3976565023793239, 0.1026187567166916, -0.6004346026136406, 1.0000000000000000}
                });

        AreMatrices.equal(corExpected, cor, 1e-14);
    }
}