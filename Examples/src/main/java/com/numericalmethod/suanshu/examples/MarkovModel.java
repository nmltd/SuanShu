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
 * FITNESS FOR A0 PARTICULAR PURPOSE, MERCHANTABILITY, NON-INFRINGEMENT,
 * TITLE AND USEFULNESS.
 *
 * IN NO EVENT AND UNDER NO LEGAL THEORY,
 * WHETHER IN ACTION, CONTRACT, NEGLIGENCE, TORT, OR OTHERWISE,
 * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
 * ANY CLAIMS, DAMAGES OR OTHER LIABILITIES,
 * ARISING AS A0 RESULT OF USING OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.numericalmethod.suanshu.examples;

import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.Matrix;
import com.numericalmethod.suanshu.algebra.linear.matrix.doubles.matrixtype.dense.DenseMatrix;
import com.numericalmethod.suanshu.algebra.linear.vector.doubles.Vector;
import com.numericalmethod.suanshu.algebra.linear.vector.doubles.dense.DenseVector;
import com.numericalmethod.suanshu.stats.hmm.HmmInnovation;
import com.numericalmethod.suanshu.stats.hmm.discrete.BaumWelch;
import com.numericalmethod.suanshu.stats.hmm.discrete.DiscreteHMM;
import com.numericalmethod.suanshu.stats.hmm.mixture.MixtureHMMEM;
import com.numericalmethod.suanshu.stats.hmm.mixture.distribution.LogNormalMixtureDistribution;
import com.numericalmethod.suanshu.stats.markovchain.SimpleMC;

/**
 * Demonstrates how to construct and train a hidden Markov model in Java using SuanShu.
 *
 * @author Haksun Li
 */
public final class MarkovModel {

    /**
     * An example transition matrix with three states.
     */
    public static final Matrix A0 = new DenseMatrix(
        new double[][]{
            {1. / 2., 1. / 2., 0.},
            {1. / 3., 1. / 3., 1. / 3.},
            {0., 1. / 2., 1. / 2.}
        });
    /**
     * An example vector of initial probabilities.
     */
    public static final Vector PI = new DenseVector(new double[]{0.2, 0.8});
    /**
     * An example transition matrix with two states.
     */
    public static final Matrix A1 = new DenseMatrix(new double[][]{
        {0.8, 0.2},
        {0.3, 0.7}
    });
    /**
     * An example matrix of probability of observing symbols (column) given states(row).
     */
    public static final Matrix B1 = new DenseMatrix(new double[][]{
        {0.8, 0.1, 0.1},
        {0.1, 0.1, 0.8}
    });

    private MarkovModel() {
    }

    public static void main(String[] args) {
        System.out.println("This class demonstrates how to construct and train a hidden Markov "
            + "model in Java using SuanShu.");
        System.out.printf("Stationary probabilities: %s%n", getStationaryProbabilities(A0));

        rabinerHMM();
        mixedHMM();
    }

    /**
     * Returns the stationary probabilities of the states, given a transition matrix.
     *
     * @param A the transition matrix from which to compute the stationary probabilities
     * @return the stationary probabilities of the states in the transition matrix
     */
    public static Vector getStationaryProbabilities(Matrix A) {
        return SimpleMC.getStationaryProbabilities(A);
    }

    /**
     * Creates a Hidden Markov Model as defined by Rabiner, generates observations from it. We then
     * attempt to train a HMM to match those observations and print the results.
     */
    public static void rabinerHMM() {
        com.numericalmethod.suanshu.stats.hmm.discrete.DiscreteHMM model
            = new com.numericalmethod.suanshu.stats.hmm.discrete.DiscreteHMM(PI, A1, B1);

        int[] observations = simulateHMM(10000, model);

        Vector GUESSED_PI = new DenseVector(
            new double[]{0.5, 0.5});
        Matrix GUESSED_A = new DenseMatrix(
            new double[][]{
                {0.5, 0.5},
                {0.5, 0.5}
            });
        Matrix GUESSED_B = new DenseMatrix(
            new double[][]{
                {0.40, 0.30, 0.30},
                {0.30, 0.30, 0.40}
            });

        DiscreteHMM guessedModel = new DiscreteHMM(GUESSED_PI, GUESSED_A, GUESSED_B);
        DiscreteHMM trainedModel = trainHMM(100, observations, guessedModel);
        System.out.printf("A: %s, %nB: %s%n", trainedModel.A(), trainedModel.B());
    }

    /**
     * Generates the given number of observations from the given HMM model.
     *
     * @param n     the number of observations to generate
     * @param model the HMM model from which to generate the observations
     * @return the observations generated by the simulation
     */
    public static int[] simulateHMM(int n, DiscreteHMM model) {
        // simulation
        int[] observations = new int[n];
        for (int i = 0; i < n; ++i) {
            HmmInnovation innovation = model.next();
            observations[i] = (int) innovation.observation();
        }
        return observations;
    }

    /**
     * Trains a HMM using the given observations, starting from the given guessed model.
     *
     * @param n            the number of iterations used by the training algorithm
     * @param observations the observations from which to train the HMMM
     * @param guessed      the guessed model
     * @return the trained HMM model
     */
    public static BaumWelch trainHMM(int n, int[] observations, DiscreteHMM guessed) {
        return new BaumWelch(observations, guessed, n);
    }

    /**
     * Creates a mixture HMM, which features observations drawn from a continuous distribution and
     * generates observations. We then attempt to train a HMM to match those observations and print
     * the results.
     */
    public static void mixedHMM() {
        // Create the model
        Vector PI0 = new DenseVector(new double[]{0., 1., 0.});
        LogNormalMixtureDistribution.Lambda[] lambda0 = new LogNormalMixtureDistribution.Lambda[]{
            new LogNormalMixtureDistribution.Lambda(1., .5),// (mu, sigma)
            new LogNormalMixtureDistribution.Lambda(10., 1.),
            new LogNormalMixtureDistribution.Lambda(3., .5)
        };
        com.numericalmethod.suanshu.stats.hmm.mixture.MixtureHMM model
            = new com.numericalmethod.suanshu.stats.hmm.mixture.MixtureHMM(
                PI0, A0, new LogNormalMixtureDistribution(lambda0));

        // Simulate observations
        double[] observations = simulateContinuousHMM(10000, model);

        // Create our initial guess
        Vector PI1 = new DenseVector(new double[]{1. / 3., 1. / 3., 1. / 3.});
        Matrix A1 = new DenseMatrix(new double[][]{
            {1. / 3., 1. / 3., 1. / 3.},
            {1. / 3., 1. / 3., 1. / 3.},
            {1. / 3., 1. / 3., 1. / 3.},});
        LogNormalMixtureDistribution.Lambda[] lambda1 = new LogNormalMixtureDistribution.Lambda[]{
            new LogNormalMixtureDistribution.Lambda(1., 1.),// (mu, sigma)
            new LogNormalMixtureDistribution.Lambda(3., 1.),
            new LogNormalMixtureDistribution.Lambda(2., 1.)
        };
        com.numericalmethod.suanshu.stats.hmm.mixture.MixtureHMM guessedModel
            = new com.numericalmethod.suanshu.stats.hmm.mixture.MixtureHMM(
                PI1, A1, new LogNormalMixtureDistribution(lambda1));

        // Create the trained HMM model
        com.numericalmethod.suanshu.stats.hmm.HiddenMarkovModel hmm
            = trainContinuousHMM(200, 1E-5, observations, guessedModel);
        System.out.printf("A: %s%n", hmm.A());
    }

    /**
     * Generates the given number of observations for the given HMM model with continuous
     * observations from a log-normal distribution.
     *
     * @param n     the number of observations to generate
     * @param model the model from which to generate the observations
     * @return the generated observations
     */
    public static double[] simulateContinuousHMM(
        int n, com.numericalmethod.suanshu.stats.hmm.mixture.MixtureHMM model) {

        double[] observations = new double[n];
        for (int t = 0; t < n; ++t) {
            observations[t] = model.next().observation();
        }
        return observations;
    }

    /**
     * Trains a HMM using the given observations, starting from the given guessed model.
     *
     * @param n            the maximum number of iterations used by the training algorithm
     * @param epsilon      the desired precision
     * @param observations the observations with which to train the HMM
     * @param guessed      the guessed model
     * @return the trained HMM model
     */
    public static com.numericalmethod.suanshu.stats.hmm.HiddenMarkovModel trainContinuousHMM(
        int n, double epsilon, double[] observations,
        com.numericalmethod.suanshu.stats.hmm.mixture.MixtureHMM guessed) {

        MixtureHMMEM trainedModel = new MixtureHMMEM(observations, guessed, epsilon, n);
        return trainedModel;
    }
}
