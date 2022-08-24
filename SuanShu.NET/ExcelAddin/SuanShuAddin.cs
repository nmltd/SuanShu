using com.numericalmethod.suanshu.algebra.linear.matrix.doubles;
using com.numericalmethod.suanshu.algebra.linear.matrix.doubles.matrixtype.dense;
using com.numericalmethod.suanshu.algebra.linear.vector.doubles;
using com.numericalmethod.suanshu.algebra.linear.vector.doubles.dense;
using Excel = Microsoft.Office.Interop.Excel;

namespace SuanShuAddin
{
    /// <summary>
    /// Provides conversions between SuanShu datatypes and the datatypes accepted by the COM interfaces.
    /// </summary>
    public static class SuanShuAddin
    {
        # region Matrix conversions

        /// <summary>
        /// Creates a matrix from the values of the cells in the given range.
        /// </summary>
        /// <param name="x">The range containing the cells that are to be converted to a Matrix.</param>
        /// <returns>A Matrix from the values of the cells in the given range.</returns>
        public static Matrix RangeToMatrix(Excel.Range x)
        {
            object[,] objects = x.Value2;

            int rows = objects.GetLength(0);
            int cols = objects.GetLength(1);
           

            Matrix matrix = new DenseMatrix(rows, cols);

            for (int i = 1; i <= rows; i++)
            {
                for (int j = 1; j <= cols; j++)
                {
                     matrix.set(i, j, (double)objects[i, j]);
                }
            }

            return new DenseMatrix(matrix);
        }

        /// <summary>
        /// Converts the given matrix to a 2D object array, which can be returned by a COM interface.
        /// </summary>
        /// <param name="X">The matrix that is to be converted to an array.</param>
        /// <returns>A 2D object array containing the numbers in the matrix.</returns>
        public static object[,] MatrixToObjects(Matrix X)
        {
            object[,] objects = new object[X.nRows(), X.nCols()];
            for (int i = 0; i < X.nRows(); i++)
            {
                for (int j = 0; j < X.nCols(); j++)
                {
                    objects[i, j] = X.get(i + 1, j + 1);
                }
            }
            return objects;
        }

        # endregion

        # region Vector conversions

        /// <summary>
        /// Creates a vector from the values of the cells in the first column of the given range.
        /// </summary>
        /// <param name="x">The range from which to create the vector.</param>
        /// <returns>A vector containing the same values as the first colum in the given range.</returns>
        public static Vector RangeToColumnVector(Excel.Range x)
        {
            object[,] objects = x.Value2;

            int rows = objects.GetLength(0);

            Vector vector = new DenseVector(rows);

            for (int i = 1; i <= rows; i++)
            {
                vector.set(i, (double)objects[i, 1]);
            }

            return vector;
        }

        /// <summary>
        /// Creates a vector from the values of the cells in the first row of the given range.
        /// </summary>
        /// <param name="x">The range from which to create the vector.</param>
        /// <returns>A vector containing the same values as the first row in the given range.</returns>
        public static Vector RangeToRowVector(Excel.Range x)
        {
            object[,] objects = x.Value2;

            int cols = objects.GetLength(1);

            Vector vector = new DenseVector(cols);

            for (int i = 1; i <= cols; i++)
            {
                vector.set(i, (double)objects[1, i]);
            }

            return vector;
        }

        /// <summary>
        /// Converts the given vector to a 2D object array, which can be returned by a COM interface.
        /// 
        /// The vector is stored in the array as one row.
        /// </summary>
        /// <param name="x">The vector that is to be converted to a object array.</param>
        /// <returns>An object array that contains the values of the given vector.</returns>
        public static object[,] VectorToRow(Vector x)
        {
            object[,] objects = new object[1, x.size()];

            for (int i = 0; i < x.size(); i++)
            {
                objects[0, i] = x.get(i+1);
            }
            return objects;
        }

        /// <summary>
        /// Converts the given vector to a 2D object array, which can be returned by a COM interface.
        /// 
        /// The vector is stored in the array as one column.
        /// </summary>
        /// <param name="x">The vector that is to be converted to a object array.</param>
        /// <returns>An object array that contains the values of the given vector.</returns>
        public static object[,] VectorToColumn(Vector x)
        {
            object[,] objects = new object[x.size(), 1];

            for (int i = 0; i < x.size(); i++)
            {
                objects[i, 0] = x.get(i+1);
            }
            return objects;
        }

        # endregion
    }
}
