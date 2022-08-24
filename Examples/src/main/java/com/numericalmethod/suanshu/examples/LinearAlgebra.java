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

import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.Matrix;
import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.factorization.diagonalization.TriDiagonalization;
import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.factorization.eigen.*;
import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.factorization.eigen.qr.HessenbergDecomposition;
import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.factorization.gaussianelimination.GaussJordanElimination;
import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.factorization.qr.GramSchmidt;
import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.factorization.svd.SVD;
import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.factorization.triangle.Doolittle;
import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.factorization.triangle.cholesky.*;
import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.linearsystem.LinearSystemSolver;
import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.matrixtype.dense.DenseMatrix;
import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.matrixtype.dense.diagonal.DiagonalMatrix;
import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.matrixtype.dense.triangle.*;
import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.operation.*;
import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.operation.householder.HouseholderReflection;
import com.numericalmethod.suanshu.algebra.linear.vector.doubles.Vector;
import com.numericalmethod.suanshu.algebra.linear.vector.doubles.dense.DenseVector;
import com.numericalmethod.suanshu.number.DoubleUtils;
import java.util.*;

/**
 * Demonstrates the linear algebra operations supported in SuanShu.
 *
 * @author Haksun Li
 */
public class LinearAlgebra {

    /**
     * Demonstrates many operations on matrices, printing the results.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        System.out.println(
            "This class demonstrates the linear algebra operations supported in SuanShu.");
        multiplication();
        solveDeterminedSystem();
        solverUnderdeterminedSystem();
        elementaryOperations();
        householderReflection();
        kroneckerProduct();
        inverse();
        pseudoInverse4NonInvertibleMatrix();
        rank();
        determinant();
        cholesky();
        doolittle();
        eigenvalue();
        eigenDecomposition();
        gaussJordanElimination();
        gramSchmidtQR();
        HessenbergDecomposition();
        SVD4TallMatrix();
        tridiagonalization();
    }

    /**
     * Shows how to transpose and multiply matrices with other matrices and vectors.
     */
    public static void multiplication() {
        // create a Matrix
        Matrix A1 = new DenseMatrix(new double[][]{
            {1, 2, 3},
            {4, 5, 6}
        });//2x3
        // copy a Matrix
        Matrix A2 = new DenseMatrix(A1); // 2x3
        // A3 is the transpose of A2
        Matrix A3 = A2.t(); // 3x2
        // matrix multiplication
        Matrix A4 = A3.multiply(A1);

        //convert a Matrix to String
        System.out.printf("%s multiply%n%s =%n%s%n",
                          A3.toString(), A1.toString(), A4.toString());

        // create a Vector
        Vector v1 = new DenseVector(new double[]{1.1, -2.2, 3.3});
        // create a column Matrix from a Vector
        Matrix A5 = new DenseMatrix(v1.toArray(), v1.size(), 1);
        // matrix multiplicaton
        Matrix A6 = A1.multiply(A5);

        // convert a Matrix to String
        System.out.printf("%s multiply%n%s =%n%s%n",
                          A1.toString(), A5.toString(), A6.toString());
    }

    /**
     * Solve a system of linear equations.
     */
    public static void solveDeterminedSystem() {
        Matrix A = new DenseMatrix(new double[][]{
            {1, 2, 3},
            {2, 3, 8},
            {4, 5, 6}
        });
        Vector b = new DenseVector(new double[]{10, 20, 30});

        double precision = 1E-15;
        LinearSystemSolver instance = new LinearSystemSolver(precision);
        LinearSystemSolver.Solution soln = instance.solve(A);

        // solution for Ax = b
        Vector x = soln.getParticularSolution(b);
        System.out.printf("soln for b = %s: %s%n", b, x);
    }

    /**
     * Solve an underdetermined system of linear equations.
     */
    public static void solverUnderdeterminedSystem() {
        Matrix A = new DenseMatrix(new double[][]{
            {0, 1, 2, -1},
            {1, 0, 1, 1},
            {-1, 1, 0, -1},
            {0, 2, 3, -1}
        });
        Vector b = new DenseVector(new double[]{1, 4, 2, 7});

        double precision = 1e-15;
        LinearSystemSolver instance = new LinearSystemSolver(precision);
        LinearSystemSolver.Solution soln = instance.solve(A);

        // solutions for Ax = 0
        List<Vector> soln0 = soln.getHomogeneousSoln();
        System.out.printf("homogenous solutions: %s%n", Arrays.toString(soln0.toArray()));
    }

    /**
     * Apply elementary operations on a matrix.
     */
    public static void elementaryOperations() {
        Matrix original = new DenseMatrix(new double[][]{
            {1.0, 0.0, 0.0},
            {0.0, 1.0, 0.0},
            {0.0, 0.0, 1.0}
        });
        ElementaryOperation modified = new ElementaryOperation(original);
        modified.swapRow(2, 3);
        modified.scaleRow(2, 2);
        modified.addRow(2, 3, -1);
        System.out.printf("matrix after first set of elementary operations: %s%n", modified);

        modified.swapColumn(2, 3);
        modified.scaleColumn(2, 2);
        modified.addColumn(2, 3, -1);
        System.out.printf("matrix after second set of elementary operations: %s%n", modified);
    }

    /**
     * Householder reflection.
     */
    public static void householderReflection() {
        Vector v = new DenseVector(new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
        HouseholderReflection householder = new HouseholderReflection(v);

        Matrix x = new DenseMatrix(DoubleUtils.seq(1, 81, 1d), 9, 9);

        Matrix result = householder.reflect(x);
        System.out.printf("housholder reflection result: %s%n", result);
    }

    /**
     * Compute the Kronecker Product of two matrices.
     */
    public static void kroneckerProduct() {
        Matrix A1 = new DenseMatrix(new double[][]{
            {1, 2},
            {3, 4}
        });
        Matrix A2 = new DenseMatrix(new double[][]{
            {5, 6},
            {7, 8}
        });

        Matrix A1A2 = new KroneckerProduct(A1, A2);
        System.out.printf("kronecker product: %s%n", A1A2);
    }

    /**
     * Compute the inverse of an invertible matrix.
     */
    public static void inverse() {
        Matrix A = new DenseMatrix(new double[][]{
            {5, 4, 4, 1, 5, 4, 2, 4, 1, 1},
            {4, 5, 2, 2, 1, 2, 4, 5, 5, 2},
            {5, 5, 3, 3, 5, 2, 3, 4, 1, 3},
            {1, 2, 5, 5, 3, 1, 4, 3, 3, 3},
            {2, 2, 4, 2, 3, 1, 3, 5, 4, 4},
            {5, 4, 5, 1, 1, 3, 2, 3, 3, 4},
            {3, 4, 4, 3, 4, 3, 2, 5, 5, 5},
            {3, 4, 3, 3, 2, 1, 4, 2, 2, 1},
            {4, 1, 1, 1, 1, 4, 4, 2, 2, 1},
            {1, 5, 5, 5, 1, 1, 2, 4, 1, 4}
        });

        Matrix Ainv = new Inverse(A);
        System.out.printf("inverse: %s%n", Ainv);
    }

    /**
     * Compute the pseudo inverse of a non-invertible matrix.
     */
    public static void pseudoInverse4NonInvertibleMatrix() {
        Matrix A = new DenseMatrix(new double[][]{
            {2, 4},
            {3, 6}
        });

        PseudoInverse Ainv = new PseudoInverse(A);
        System.out.printf("pseudo inverse: %s%n", Ainv);
    }

    /**
     * Compute the rank of a matrix.
     */
    public static void rank() {
        Matrix A = new DenseMatrix(new double[][]{
            {3, 5},
            {7, 4}
        });

        double precision = 1e-15;
        int rank = MatrixMeasure.rank(A, precision);
        System.out.printf("rank: %d%n", rank);
    }

    /**
     * Compute the determinant of a matrix.
     */
    public static void determinant() {
        Matrix A = new DenseMatrix(new double[][]{
            {3, 5},
            {7, 4}
        });

        double det = MatrixMeasure.det(A);
        System.out.printf("determinant: %f%n", det);
    }

    /**
     * Cholesky decomposition.
     */
    public static void cholesky() {
        Matrix A1 = new DenseMatrix(new double[][]{
            {2, 1, 1},
            {1, 2, 1},
            {1, 1, 2}
        });
        Cholesky cholesky = new Chol(A1);
        System.out.printf("cholesky decomposition: L: %s%n", cholesky.L());
    }

    /**
     * LU decomposition by the Doolittle process.
     */
    public static void doolittle() {
        Matrix A1 = new DenseMatrix(new double[][]{
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        });
        Doolittle instance = new Doolittle(A1);
        LowerTriangularMatrix L = instance.L();
        UpperTriangularMatrix U = instance.U();

        System.out.printf("doolitle LU decomposition:%nL: %s,%nU: %s%n", L, U);
    }

    /**
     * Compute the eigenvalues and eigenvectors. The eigenvectors are stored in the Property
     * associated with each eigenvalues.
     */
    public static void eigenvalue() {
        Matrix A = new DenseMatrix(new double[][]{
            {1.5, 0.0, 1.0},
            {-0.5, 0.5, -0.5},
            {-0.5, 0.0, 0.0}
        });
        Eigen eigen = new Eigen(A, Eigen.Method.QR, 0);

        System.out.println("eigenvalues:");
        for (int i = 0; i < eigen.size(); ++i) {
            Number eigenvalue = eigen.getEigenvalue(i);
            EigenProperty property = eigen.getProperty(eigenvalue);
            Vector eigenVector = property.eigenVector();
            System.out.printf("%s, eigenvector: %s%n", eigenvalue, eigenVector);
        }
    }

    /**
     * Eigen decomposition of a symmetric matrix.
     */
    public static void eigenDecomposition() {
        DenseMatrix A = new DenseMatrix(
            new SymmetricMatrix(new double[][]{
            {1},
            {2, 3},
            {4, 5, 6},
            {7, 8, 9, 10}
        }));
        double precision = 1e-4;
        EigenDecomposition decomp = new EigenDecomposition(A, precision);

        Matrix D = decomp.D();//the diagonal matrix containing the getEigenvalues
        System.out.printf("eigen decomposition result: %s%n", D);
    }

    /**
     * Gauss-Jordan elimination of a square matrix.
     */
    public static void gaussJordanElimination() {
        Matrix A = new DenseMatrix(new double[][]{
            {2, 1, 1},
            {2, 2, -1},
            {4, -1, 6}
        });
        double precision = 1e-7;
        GaussJordanElimination instance = new GaussJordanElimination(A, true, precision);

        Matrix U = instance.U();
        Matrix T = instance.T();

        System.out.printf("gauss-jordan elimination:%nU: %s,%nT: %s%n", U, T);
    }

    /**
     * QR decomposition by the Gram-Schmidt process.
     */
    public static void gramSchmidtQR() {
        Matrix A1 = new DenseMatrix(new double[][]{
            {3, 2},
            {1, 2}});
        GramSchmidt instance = new GramSchmidt(A1, true, 0);

        Matrix Q = instance.Q();
        UpperTriangularMatrix R = instance.R();

        System.out.printf("gram-schmidt QR decomposition:%nQ: %s,%nT: %s%n", Q, R);
    }

    /**
     * Hessenberg decomposition of a square matrix.
     */
    public static void HessenbergDecomposition() {
        Matrix A1 = new DenseMatrix(new double[][]{
            {1, 5, 7},
            {3, 0, 6},
            {4, 3, 1}
        });
        HessenbergDecomposition instance = new HessenbergDecomposition(A1);

        Matrix H = instance.H();
        Matrix Q = instance.Q();

        System.out.printf("hessenberg decomposition:%nH: %s,%nQ: %s%n", H, Q);

    }

    /**
     * SVD decomposition of a tall matrix.
     */
    public static void SVD4TallMatrix() {
        Matrix A1 = new DenseMatrix(new double[][]{
            {1.5, 2, 4.96, 6.37, 36.01},
            {3, 4, 99, 12.24, 369.6},
            {73, 63.4, 0.19, 24, 99.39},
            {25.36, 9.31, 66.78, 88.12, 36.03},
            {15.51, 63.25, 36.1, 45.68, 111},
            {55, 66, 12.3, 79, 121},
            {55.5, 66.9, 12, 79.98, 97.001},
            {55, 66, 12, 79, 97}
        });

        SVD instance = new SVD(A1, true);

        DiagonalMatrix D1 = instance.D();
        Matrix U = instance.U();
        Matrix V = instance.V();

        System.out.printf("SVD decomposition:%nU: %s,%nD: %s,%nV: %s%n", U, D1, V);
    }

    /**
     * Tridiagonalization of a matrix.
     */
    public static void tridiagonalization() {
        Matrix S = new DenseMatrix(new double[][]{
            {1, 5, 7},
            {5, 0, 6},
            {7, 6, 1}
        });
        TriDiagonalization instance = new TriDiagonalization(S);

        Matrix T = instance.T();
        Matrix Q = instance.Q();
        System.out.printf("tridiagonalization: T: %s,%nQ: %s%n", T, Q);
    }
}
