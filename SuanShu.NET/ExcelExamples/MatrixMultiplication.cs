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
using com.numericalmethod.suanshu.algebra.linear.matrix.doubles;

namespace ExcelExamples
{
    public partial class MatrixMultiplication
    {
        private void Sheet2_Startup(object sender, System.EventArgs e)
        {
            // On Startup, register for Change events. Each time the worksheet is changed, Multiply will get called
            this.Change += new Excel.DocEvents_ChangeEventHandler(Multiply);
            Multiply(null);
        }

        private void Sheet2_Shutdown(object sender, System.EventArgs e)
        {
        }

        #region VSTO Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InternalStartup()
        {
            this.Startup += new System.EventHandler(Sheet2_Startup);
            this.Shutdown += new System.EventHandler(Sheet2_Shutdown);
        }

        #endregion

        /// <summary>
        /// Attempts to read the matrices 
        /// </summary>
        /// <param name="Target"></param>
        private void Multiply(Excel.Range Target)
        {
            // We have to disable events to avoid generating another event and recursing infinitely
            Application.EnableEvents = false;
            ClearResult();
            
            // This may fail if the dimensions of X and Y are incompatible, e.g. when the user hasn't finished entering the matrices
            // We use 'try' to ignore exceptions: If we can't multiply the matrices we just clear the result, reenable events and exit
            try
            {      
                Matrix X = SuanShuExcel.ReadMatrix(Mult_XStart.Row, Mult_XStart.Column, this);
                Matrix Y = SuanShuExcel.ReadMatrix(Mult_YStart.Row, Mult_YStart.Column, this);
                Matrix XY = X.multiply(Y);

                SuanShuExcel.WriteMatrix(XY, XYStart.Row, XYStart.Column, this);
            }
            finally
            {
                Application.EnableEvents = true;
            }
        }

        private void ClearResult()
        {
            // this.Cells gets the cell at the given coordinate
            // this.Range gets the range you would obtain by dragging from the first to the second cell
            Excel.Range resultRange = this.Range[
                this.Cells[XYStart.Row, XYStart.Column],
                this.Cells[XYEnd.Row, XYEnd.Column]];
            resultRange.ClearContents();
        }
    }
}
