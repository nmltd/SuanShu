using System;
using System.Runtime.InteropServices;
using Microsoft.Win32;
using Excel = Microsoft.Office.Interop.Excel;

namespace SuanShuAddin
{
    /// <summary>
    /// Uses code from http://code.msdn.microsoft.com/office/CSExcelAutomationAddIn-c46f6956
    /// Helpful explanations: http://blogs.msdn.com/b/gabhan_berry/archive/2008/04/07/writing-custom-excel-worksheet-functions-in-c_2d00_sharp.aspx
    /// </summary>
    [ComVisible(true)]
    public interface ISuanShu
    {
        // Linear Eqn
        object[,] SOLVE_LINEAR(Excel.Range A, Excel.Range b);

        // Matrix
        object[,] MATRIX_INVERSE(Excel.Range x);

        object[,] MATRIX_MULTIPLY(Excel.Range x1, Excel.Range x2);
    }

    [ComDefaultInterface(typeof(ISuanShu)),
    ComVisible(true),
    Guid("0E36A06B-D44B-4CA3-A315-981C962B2A77")]
    public class SuanShu : ISuanShu
    {
        // Linear Eqn
        public object[,] SOLVE_LINEAR(Excel.Range A, Excel.Range b)
        {
            return LinearEqn.SOLVE_LINEAR(A, b);
        }

        // Matrix
        public object[,] MATRIX_INVERSE(Excel.Range x)
        {
            return MatrixOp.MATRIX_INVERSE(x);
        }

        public object[,] MATRIX_MULTIPLY(Excel.Range x1, Excel.Range x2)
        {
            return MatrixOp.MATRIX_MULTIPLY(x1, x2);
        }

        #region Registration of Automation Add-in

        [ComRegisterFunction]
        public static void RegisterFunction(Type type)
        {
            // Add the "Programmable" registry key under CLSID 
            Registry.ClassesRoot.CreateSubKey(
                GetCLSIDSubKeyName(type, "Programmable"));

            // Register the full path to mscoree.dll which makes Excel happier. 
            RegistryKey key = Registry.ClassesRoot.OpenSubKey(
                GetCLSIDSubKeyName(type, "InprocServer32"), true);
            key.SetValue("",
                System.Environment.SystemDirectory + @"\mscoree.dll",
                RegistryValueKind.String);
        }

        [ComUnregisterFunction]
        public static void UnregisterFunction(Type type)
        {
            // Remove the "Programmable" registry key under CLSID 
            Registry.ClassesRoot.DeleteSubKey(
                GetCLSIDSubKeyName(type, "Programmable"), false);
        }

        private static string GetCLSIDSubKeyName(Type type, string subKeyName)
        {
            System.Text.StringBuilder s = new System.Text.StringBuilder();
            s.Append(@"CLSID\{");
            s.Append(type.GUID.ToString().ToUpper());
            s.Append(@"}\");
            s.Append(subKeyName);
            return s.ToString();
        }

        #endregion
    }
}
