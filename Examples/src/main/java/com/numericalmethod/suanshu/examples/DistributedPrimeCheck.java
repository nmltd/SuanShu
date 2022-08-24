///*
// * Copyright (c) Numerical Method Inc.
// * http://www.numericalmethod.com/
// * 
// * THIS SOFTWARE IS LICENSED, NOT SOLD.
// * 
// * YOU MAY USE THIS SOFTWARE ONLY AS DESCRIBED IN THE LICENSE.
// * IF YOU ARE NOT AWARE OF AND/OR DO NOT AGREE TO THE TERMS OF THE LICENSE,
// * DO NOT USE THIS SOFTWARE.
// * 
// * THE SOFTWARE IS PROVIDED "AS IS", WITH NO WARRANTY WHATSOEVER,
// * EITHER EXPRESS OR IMPLIED, INCLUDING, WITHOUT LIMITATION,
// * ANY WARRANTIES OF ACCURACY, ACCESSIBILITY, COMPLETENESS,
// * FITNESS FOR A PARTICULAR PURPOSE, MERCHANTABILITY, NON-INFRINGEMENT, 
// * TITLE AND USEFULNESS.
// * 
// * IN NO EVENT AND UNDER NO LEGAL THEORY,
// * WHETHER IN ACTION, CONTRACT, NEGLIGENCE, TORT, OR OTHERWISE,
// * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR
// * ANY CLAIMS, DAMAGES OR OTHER LIABILITIES,
// * ARISING AS A RESULT OF USING OR OTHER DEALINGS IN THE SOFTWARE.
// */
//package com.numericalmethod.suanshu.examples;
//
//import com.numericalmethod.suanshu.analysis.function.Function;
//import com.numericalmethod.suanshu.grid.executor.DefaultGridExecutorFactory;
//import com.numericalmethod.suanshu.grid.executor.GridExecutor;
//import com.numericalmethod.suanshu.number.DoubleUtils;
//import java.util.Iterator;
//import java.util.List;
//
///**
// * Demonstrates how grid API is used to determine whether each of the given
// * integers is prime or not.
// *
// * @author Johannes Lehmann
// */
//public final class DistributedPrimeCheck {
//
//    /**
//     * Determines whether a number is prime or not.
//     */
//    public static class IsPrime implements Function<Integer, Boolean> {
//
//        private static final long serialVersionUID = -6362919406091841135L;
//
//        @Override
//        public Boolean evaluate(Integer in) {
//            int n = Math.abs(in);
//            int sqrtN = (int) Math.floor(Math.sqrt(n));
//
//            for (int i = 2; i <= sqrtN; i++) {
//                if (n % i == 0) {
//                    return false;
//                }
//            }
//            return true;
//        }
//
//        @Override
//        public int dimensionOfDomain() {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        public int dimensionOfRange() {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        }
//
//    }
//    private static final Function<Integer, Boolean> F = new IsPrime();
//
//    private DistributedPrimeCheck() {
//    }
//
//    /**
//     * Runs the program.
//     *
//     * @param args not used
//     */
//    public static void main(String[] args) {
//        GridExecutor executor
//                = DefaultGridExecutorFactory.getInstance().newExecutor();
//
//        List<Integer> numbers = DoubleUtils.intArray2List(DoubleUtils.seq(10, 20));
//
//        List<Boolean> areNumbersPrime = executor.map(numbers, F);
//
//        System.out.println("results: " + areNumbersPrime.toString());
//        printResults(numbers, areNumbersPrime);
//    }
//
//    private static void printResults(
//            List<Integer> numbers,
//            List<Boolean> areNumbersPrime) {
//
//        Iterator<Boolean> isPrimeItr = areNumbersPrime.iterator();
//
//        for (Integer number : numbers) {
//            System.out.printf("%d %s prime%n",
//                              number,
//                              isPrimeItr.next() ? "is" : "is not");
//        }
//    }
//}
