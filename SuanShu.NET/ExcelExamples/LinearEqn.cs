using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.Xml.Linq;
using Microsoft.Office.Tools.Excel;
using Microsoft.VisualStudio.Tools.Applications.Runtime;
using Excel = Microsoft.Office.Interop.Excel;
using Office = Microsoft.Office.Core;
using com.numericalmethod.suanshu.algebra.linear.vector.doubles.dense;
using com.numericalmethod.suanshu.algebra.linear.vector.doubles;
using com.numericalmethod.suanshu.algebra.linear.matrix.doubles;
using com.numericalmethod.suanshu.algebra.linear.matrix.doubles.matrixtype.dense;
using com.numericalmethod.suanshu.algebra.linear.matrix.doubles.operation;
using com.numericalmethod.suanshu.algebra.linear.matrix.doubles.linearsystem;

namespace ExcelExamples
{
    public partial class LinearEqn
    {
        private void Sheet1_Startup(object sender, System.EventArgs e)
        {
        }

        private void Sheet1_Shutdown(object sender, System.EventArgs e)
        {
        }

        #region VSTO Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InternalStartup()
        {
            this.button1.Click += new System.EventHandler(this.button1_Click);
            this.Startup += new System.EventHandler(this.Sheet1_Startup);
            this.Shutdown += new System.EventHandler(this.Sheet1_Shutdown);

        }

        #endregion

        private void button1_Click(object sender, EventArgs e)
        {
            // Read b and A starting at the previously named cells bStart and AStart
            Vector b = SuanShuExcel.ReadVector(bStart.Row, bStart.Column, this);
            Matrix A = SuanShuExcel.ReadMatrix(AStart.Row, AStart.Column, this);

            // Precision parameter
            double epsilon = 1E-15;

            // Solve the linear equation
            LinearSystemSolver solver = new LinearSystemSolver(epsilon);
            LinearSystemSolver.Solution solution = solver.solve(A);            
            Vector x = solution.getParticularSolution(b);

            // Write the result starting at the previously named cell xStart
            SuanShuExcel.WriteVector(x, xStart.Row, xStart.Column, this);
        }
    }
}
