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

/// <summary>
/// Contains methods that perform reading and writing of SuanShu datatype from and to Excel.
/// 
/// In particular it contains methods for reading SuanShu datatypes from the worksheet and writing them to the worksheet.
/// 
/// Please note that row and column indices start at 1 in Excel.
/// </summary>
public static class SuanShuExcel {
    /// <summary>
    /// Reads a scalar at the given coordinates, returning NaN if there is no scalar in that cell.
    /// </summary>
    /// <param name="row">The index of the row in which to read the scalar.</param>
    /// <param name="col">The index of the column in which to read the scalar.</param>
    /// <param name="worksheet">The worksheet from which to read the scalar.</param>
    /// <returns>The scalar at the given coordinates, or NaN if none is found.</returns>
    public static double ReadScalar(int row, int col, WorksheetBase worksheet)
    {
        var cell = worksheet.Cells[row, col].Value2;
        if (cell == null || !cell.GetType().IsAssignableFrom(typeof(double)))
        {
            return Double.NaN; // The cell we found does not contain a number.
        }
        return (double)cell;
    }

    /// <summary>
    /// Sets the value of the cell at the given coordinates to the given scalar.
    /// </summary>
    /// <param name="x">The scalar value to which to set the cell.</param>
    /// <param name="row">The row of the cell of which to set the value.</param>
    /// <param name="col">The column of the cell of which to set the value.</param>
    /// <param name="worksheet">The worksheet to which to write the scalar.</param>
    public static void WriteScalar(double x, int row, int col, WorksheetBase worksheet)
    {
        worksheet.Cells[row, col].Value2 = x;
    }

    /// <summary>
    /// Reads a vector top-to-bottom, starting from the given coordinates in the given cells.
    /// 
    /// The method will stop reading when an empty cell is encountered.
    /// </summary>
    /// <param name="startRow">The row (indices starting from 1) in which to start reading the vector.</param>
    /// <param name="col">The column in which to read the vector.</param>
    /// <param name="cells">The worksheet from which to read the vector.</param>
    /// <returns>The vector obtained by reading downwards from the cell at the given coordinates.</returns>
    public static Vector ReadVector(int startRow, int col, WorksheetBase worksheet)
    {
        List<double> values = new List<double>();
        for (int row = startRow; ; row++)
        {
            double value = ReadScalar(row, col, worksheet);
            if (Double.IsNaN(value)) // Have reached the end of the vector.
            {
                return new DenseVector(values.ToArray());
            }
            values.Add(value);
        }
    }

    /// <summary>
    /// Writes a vector to the given worksheet, top-to-bottom starting at the given coordinates.
    /// </summary>
    /// <param name="x">The vector that is to be written.</param>
    /// <param name="startRow">The row in which to start writing the vector.</param>
    /// <param name="col">The column in which to write the vector.</param>
    /// <param name="worksheet">The worksheet to which to write the vector.</param>
    public static void WriteVector(Vector x, int startRow, int col, WorksheetBase worksheet)
    {
        for (int i = 1; i <= x.size(); i++)
        {
            WriteScalar(x.get(i), startRow + i - 1, col, worksheet);
        }
    }

    /// <summary>
    /// Reads a matrix top-to-bottom, left-to-right, starting from the given coordinates in the given cells.
    /// 
    /// The method will stop when a column is encountered that is empty or has a different size than previous columns.
    /// </summary>
    /// <param name="startRow">The coordinate of the first row of the matrix.</param>
    /// <param name="startCol">The coordinate of the first column of the matrix</param>
    /// <param name="cells">The worksheet from which to read the matrix.</param>
    /// <returns>The matrix read from the given cells starting at the given coordinates.</returns>
    public static Matrix ReadMatrix(int startRow, int startCol, WorksheetBase worksheet)
    {
        List<Vector> columns = new List<Vector>();
        for (int col = startCol; ; col++)
        {
            Vector columnVector = ReadVector(startRow, col, worksheet);
            if (columnVector.size() == 0 || (columns.Count > 0 && columnVector.size() != columns[columns.Count - 1].size()))
            {
                // Have reached the end of the matrix.
                return MatrixFactory.cbind(columns.ToArray());
            }
            columns.Add(columnVector);
        }

    }

    /// <summary>
    /// Writes a matrix to the given worksheet.
    /// </summary>
    /// <param name="x">The matrix that is to be written.</param>
    /// <param name="startRow">The row in which to start writing the matrix.</param>
    /// <param name="startCol">The column in which to start writing the matrix.</param>
    /// <param name="worksheet">The worksheet to which to write the matrix.</param>
    public static void WriteMatrix(Matrix x, int startRow, int startCol, WorksheetBase worksheet)
    {
        for (int i = 0; i < x.nCols(); i++)
        {
            WriteVector(x.getColumn(i + 1), startRow, startCol + i, worksheet);
        }
    }
}
