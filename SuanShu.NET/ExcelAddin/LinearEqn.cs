using com.numericalmethod.suanshu.algebra.linear.matrix.doubles;
using com.numericalmethod.suanshu.algebra.linear.vector.doubles;
using com.numericalmethod.suanshu.algebra.linear.matrix.doubles.linearsystem;
using Excel = Microsoft.Office.Interop.Excel;

namespace SuanShuAddin
{
    class LinearEqn
    {
        public static object[,] SOLVE_LINEAR(Excel.Range A, Excel.Range b)
        {
            Matrix AMatrix = SuanShuAddin.RangeToMatrix(A);
            Vector bVector = SuanShuAddin.RangeToColumnVector(b);

            LinearSystemSolver solver = new LinearSystemSolver(1E-15);
            Vector x = solver.solve(AMatrix).getParticularSolution(bVector);

            return SuanShuAddin.VectorToColumn(x);
        }
    }

}
