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
import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.matrixtype.dense.DenseMatrix;
import com.numericalmethod.suanshu.algebra.linear.vector.doubles.Vector;
import com.numericalmethod.suanshu.algebra.linear.vector.doubles.dense.DenseVector;
import com.numericalmethod.suanshu.stats.regression.linear.LMProblem;
import com.numericalmethod.suanshu.stats.regression.linear.glm.GLMProblem;
import com.numericalmethod.suanshu.stats.regression.linear.glm.GeneralizedLinearModel;
import com.numericalmethod.suanshu.stats.regression.linear.glm.distribution.GLMFamily;
import com.numericalmethod.suanshu.stats.regression.linear.glm.distribution.GLMPoisson;
import com.numericalmethod.suanshu.stats.regression.linear.glm.quasi.GeneralizedLinearModelQuasiFamily;
import com.numericalmethod.suanshu.stats.regression.linear.glm.quasi.QuasiGLMProblem;
import com.numericalmethod.suanshu.stats.regression.linear.glm.quasi.family.QuasiFamily;
import com.numericalmethod.suanshu.stats.regression.linear.glm.quasi.family.QuasiGamma;
import com.numericalmethod.suanshu.stats.regression.linear.logistic.LogisticRegression;
import com.numericalmethod.suanshu.stats.regression.linear.ols.*;
import com.numericalmethod.suanshu.stats.regression.linear.residualanalysis.LMDiagnostics;
import com.numericalmethod.suanshu.stats.regression.linear.residualanalysis.LMInformationCriteria;

/**
 * Demonstrates how to do Ordinary Least Square regression in Java using SuanShu.
 *
 * @author Haksun Li
 */
public final class LinearRegression {

    private LinearRegression() {
    }
    /**
     * The matrix of factor values used for OLS.
     */
    public static final Matrix TEST_X1 =
        new DenseMatrix(new double[][]{
        {1.52, 2.23, 4.31},
        {3.22, 6.34, 3.46},
        {4.32, 12.2, 23.1},
        {10.1034, 43.2, 22.3},
        {12.1, 2.12, 3.27}
    });
    /**
     * The vector of dependent variables used for OLS.
     */
    public static final Vector TEST_Y1 =
        new DenseVector(new double[]{2.32, 0.452, 4.53, 12.34, 32.2});
    /**
     * The vector of weights used for testing.
     */
    public static final Vector TEST_W = new DenseVector(new double[]{0.2, 0.4, 0.1, 0.3, 0.1});
    /**
     * The matrix of factor values used for GLM.
     */
    public static final Matrix TEST_X2 =
        new DenseMatrix(new double[][]{
        {1.52, 2.11},
        {3.22, 4.32},
        {4.32, 1.23},
        {10.1034, 8.43},
        {12.1, 7.31}
    });
    /**
     * The vector of dependent variables used for GLM.
     */
    public static final Vector TEST_Y2 = new DenseVector(new double[]{2, 1, 4, 5, 7});

    /**
     * Showcases different types of regressions implemented in SuanShu.
     *
     * @param args not used
     * @throws Exception if an error occurs during a regression
     */
    public static void main(String[] args) throws Exception {
        System.out.println("This class demonstrates how to do Ordinary Least Square regression in"
            + " Java using SuanShu.");

        olsEqualWeight(TEST_X1, TEST_Y1, true);
        System.out.println();
        olsWeighted(TEST_X1, TEST_Y1, TEST_W, true);

        System.out.println();
        glm(TEST_X2, TEST_Y2, true, new GLMFamily(new GLMPoisson()));

        System.out.println();
        quasiGLM(TEST_X2, TEST_Y2, true, new QuasiFamily(
            new com.numericalmethod.suanshu.stats.regression.linear.glm.quasi.family.QuasiPoisson()));
        quasiGLM(new DenseMatrix(new double[][]{{1.52}, {3.22}, {4.32}, {10.1034}, {12.1}}),
                 new DenseVector(new double[]{1.3, 2.4, 0.4, 2, 5.2}),
                 true,
                 new QuasiFamily(new QuasiGamma()));

        System.out.println();
        logistic(new DenseMatrix(new double[][]{
            {1.52},
            {3.22},
            {4.32},
            {10.1034},
            {12.1}
        }), new DenseVector(new double[]{0, 1, 0, 1, 1}), true);
    }

    /**
     * Runs an OLS regression on the given data, where all observations are given equal weight.
     *
     * @param x         the factor values
     * @param y         the dependent variable values
     * @param intercept whether an intercept should be used as part of the regression.
     */
    public static void olsEqualWeight(Matrix x, Vector y, boolean intercept) {
        LMProblem problem = new LMProblem(y, x, intercept);

        printOLSResults(problem);
    }

    /**
     * Performs an OLS regression on the given problem and prints the results, as well as various
     * statistics.
     *
     * @param problem the OLS problem on which to perform the regression
     */
    public static void printOLSResults(LMProblem problem) {
        OLSRegression ols = new OLSRegression(problem);
        OLSResiduals olsResiduals = ols.residuals();

        System.out.printf("beta hat: %s,%nstderr: %s,%nt: %s,%nresiduals: %s%n",
                          ols.beta().betaHat(), ols.beta().stderr(), ols.beta().t(),
                          olsResiduals.residuals());

        System.out.printf("R2: %f, AR2: %f, standard error: %f, f: %f%n",
                          olsResiduals.R2(), olsResiduals.AR2(), olsResiduals.stderr(),
                          olsResiduals.Fstat());

        System.out.printf("fitted values: %s%n", olsResiduals.fitted());
        System.out.printf("leverage: %s%n", olsResiduals.leverage());
        System.out.printf("standardized residuals: %s%n", olsResiduals.standardized());
        System.out.printf("studentized residuals: %s%n", olsResiduals.studentized());

        System.out.printf("sum of squared residuals: %s%n", olsResiduals.RSS());
        System.out.printf("total sum of squares: %f%n", olsResiduals.TSS());

        System.out.println();
        LMDiagnostics olsDiagnostics = ols.diagnostics();

        System.out.printf("DFFITS (Welsch and Kuh measure): %s%n", olsDiagnostics.DFFITS());
        System.out.printf("Hadi: %s%n", olsDiagnostics.Hadi());
        System.out.printf("Cook distance: %s%n", olsDiagnostics.cookDistances());


        LMInformationCriteria olsInformationCriteria = ols.informationCriteria();

        System.out.printf("Akaike information criterion: %f%n", olsInformationCriteria.AIC());
        System.out.printf("Bayesian information criterion: %f%n", olsInformationCriteria.BIC());
    }

    /**
     * Runs an OLS regression on the given data, where observations are given the weight specified
     * in the given vector.
     *
     * @param x         the factor values
     * @param y         the dependent variable values
     * @param w         the weight given to the observations
     * @param intercept whether an intercept should be used as part of the regression.
     */
    public static void olsWeighted(Matrix x, Vector y, Vector w, boolean intercept) {
        LMProblem problem = new LMProblem(y, x, intercept, w);
        printOLSResults(problem);
    }

    /**
     * Runs a GLM regression on the given data, with the given link function (family).
     *
     * @param x         the factor values
     * @param y         the dependent variable values
     * @param intercept whether an intercept should be used as part of the regression
     * @param family    the link function
     */
    public static void glm(Matrix x, Vector y, boolean intercept, GLMFamily family) {
        GLMProblem problem = new GLMProblem(y, x, true, family);
        GeneralizedLinearModel glm = new GeneralizedLinearModel(problem);


        System.out.printf("beta hat: %s,%nstderr: %s,%nt: %s%n",
                          glm.beta().betaHat(), glm.beta().stderr(), glm.beta().t());

        System.out.printf("fitted values: %s%n", glm.residuals().fitted());
        System.out.printf("deviance residuals: %s%n", glm.residuals().devianceResiduals());
        System.out.printf("deviance: %f, overdispersion: %f, AIC: %f%n",
                          glm.residuals().deviance(), glm.residuals().overdispersion(), glm.AIC());
    }

    /**
     * Runs a GLM regression on the given data, estimating beta with quasi-likelihood estimators
     * from the given family.
     *
     * @param x         the factor values
     * @param y         the dependent variable values
     * @param intercept whether an intercept should be used as part of the regression
     * @param family    the family
     */
    public static void quasiGLM(Matrix x, Vector y, boolean intercept, QuasiFamily family) {
        GeneralizedLinearModelQuasiFamily quasi = new GeneralizedLinearModelQuasiFamily(
            new QuasiGLMProblem(y, x, intercept, family));
        System.out.printf("beta hat: %s,%nstderr: %s,%nt: %s%n",
                          quasi.beta().betaHat(), quasi.beta().stderr(), quasi.beta().t());

        System.out.printf("fitted values: %s%n", quasi.residuals().fitted());
        System.out.printf("deviance residuals: %s%n", quasi.residuals().devianceResiduals());
        System.out.printf("deviance: %f, overdispersion: %f%n",
                          quasi.residuals().deviance(), quasi.residuals().overdispersion());
    }

    /**
     * Runs a logistic regression on the given data.
     *
     * @param x         the factor values
     * @param y         the dependent variable values
     * @param intercept whether an intercept should be used as part of the regression
     * @throws Exception thrown if an error occurs during the regression
     */
    public static void logistic(DenseMatrix x, DenseVector y, boolean intercept) throws Exception {
        com.numericalmethod.suanshu.stats.regression.linear.LMProblem problem =
            new com.numericalmethod.suanshu.stats.regression.linear.LMProblem(y, x, true);
        LogisticRegression logistic = new LogisticRegression(problem);
        System.out.printf("beta hat: %s,%nstderr: %s,%nt: %s%n",
                          logistic.beta().betaHat(), logistic.beta().stderr(), logistic.beta().t());

        System.out.printf("fitted values: %s%n", logistic.residuals().fitted());
    }
}
