using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using com.numericalmethod.suanshu.algebra.linear.matrix.doubles;
using com.numericalmethod.suanshu.algebra.linear.matrix.doubles.matrixtype.dense;
namespace SuanShu.NET
{
    /// <summary>
    /// Example program that does matrix multiplication.
    /// 
    /// Please ensure that the license file is in the same folder as the compiler output (bin\Debug and bin\Release).
    /// </summary>
    class MatrixMultiplication
    {
        static void Main(string[] args)
        {
            Matrix A = new DenseMatrix(new double[][] {
                new double[] { 1.0, 2.0, 3.0 },
                new double[] { 3.0, 5.0, 6.0 },
                new double[] { 7.0, 8.0, 9.0 }
            });

            Matrix B = new DenseMatrix(new double[][] {
                new double[] { 2.0, 0.0, 0.0 },
                new double[] { 0.0, 2.0, 0.0 },
                new double[] { 0.0, 0.0, 2.0 }
            });

            Matrix AB = A.multiply(B);
            Console.Write(AB.ToString());
        }
    }
}
