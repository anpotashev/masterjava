package ru.javaops.masterjava.matrix;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        final int poolSize;
        if (executor instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor ex = (ThreadPoolExecutor) executor;
            poolSize = Math.min(ex.getMaximumPoolSize(), 5);
        } else {
            poolSize = 5;
        }
        List<Runnable> runnableList = new ArrayList<>();

        List<Future> futures = new ArrayList<>();
        System.out.println("poolSize = " + poolSize);
        for (int a = 0; a < poolSize; a++) {
            final int a1 = a;
            runnableList.add(new Runnable() {
                @Override
                public void run() {
                    final int[] tmpColumn = new int[matrixSize];
                    int[] tmpRow = new int[matrixSize];
                    for (int j = a1; j < matrixSize; j=j+poolSize) {
                        for (int i = 0; i < matrixSize; i++) {
                            tmpColumn[i] = matrixB[i][j];
                        }
                        for (int i = 0; i < matrixSize; i++) {
                            int sum = 0;
                            tmpRow = matrixA[i];
                            matrixC[i][j] = 0;

                            for (int k = 0; k < matrixSize; k++) {
                                matrixC[i][j] += tmpRow[k] * tmpColumn[k];
                            }
                        }

                    }
                }
            });
        }
        for (Runnable r : runnableList) {
            futures.add(executor.submit(r));
        }
        boolean done = false;
        while (!done) {
            done = true;
            for (Future f : futures)
                done = done & f.isDone();
        }
        return matrixC;
    }

    // TODO optimize by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];
        final int[] tmpColumn = new int[matrixSize];
        int[] tmpRow = new int[matrixSize];
        for (int j = 0; j < matrixSize; j++) {
            for (int i = 0; i < matrixSize; i++) {
                tmpColumn[i] = matrixB[i][j];
            }
            for (int i = 0; i < matrixSize; i++) {
                int sum = 0;
                tmpRow = matrixA[i];
                matrixC[i][j] = 0;

                for (int k = 0; k < matrixSize; k++) {
                    matrixC[i][j] += tmpRow[k] * tmpColumn[k];
                }
            }

        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
