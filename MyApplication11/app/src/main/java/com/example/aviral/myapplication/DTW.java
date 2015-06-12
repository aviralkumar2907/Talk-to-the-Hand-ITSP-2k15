package com.example.aviral.myapplication;

/**
 * Created by aviral on 6/8/15.
 */
public class DTW {

    public int[] acc;
    //private int[][] costMatrix;

    public DTW(int a, int b, int c) {
        acc = new int[3];
        acc[0] = a + 200;
        acc[1] = b + 200;
        acc[2] = c + 200;
    }

    public double getEulerDistance(DTW a) {
        double sum = 0;
        for (int i = 0; i < 3; i++) {
            sum += (a.acc[i] - this.acc[i]) * (a.acc[i] - this.acc[i]);
        }
        return Math.sqrt(sum);
    }
}
