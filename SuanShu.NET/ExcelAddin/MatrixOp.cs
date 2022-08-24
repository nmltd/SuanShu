using com.numericalmethod.suanshu.algebra.linear.matrix.doubles;
using com.numericalmethod.suanshu.algebra.linear.matrix.doubles.operation;
using Excel = Microsoft.Office.Interop.Excel;

namespace SuanShuAddin {

    class MatrixOp
    {
        public static object[,] MATRIX_INVERSE(Excel.Range x)
        {
            Matrix X = SuanShuAddin.RangeToMatrix(x);
            Matrix XInv = new Inverse(X);
            return SuanShuAddin.MatrixToObjects(XInv);
        }

        public static object[,] MATRIX_MULTIPLY(Excel.Range x1, Excel.Range x2)
        {
            Matrix X1 = SuanShuAddin.RangeToMatrix(x1);
            Matrix X2 = SuanShuAddin.RangeToMatrix(x2);
            return SuanShuAddin.MatrixToObjects(X1.multiply(X2));
        }
    }


}
