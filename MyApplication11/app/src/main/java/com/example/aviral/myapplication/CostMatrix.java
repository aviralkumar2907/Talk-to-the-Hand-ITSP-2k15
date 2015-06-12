package com.example.aviral.myapplication;

import android.util.Log;

/**
 * Created by aviral on 6/8/15.
 */
public class CostMatrix {

    public double[][] costMatrix;
    DTW[] a, b;


    public CostMatrix(DTW[] x, DTW[] y ){
        a = new DTW[x.length];
        b= new DTW[y.length];
        for(int i=0; i<x.length; i++) {
            a[i] = x[i];
        }

        for(int i=0; i<y.length; i++){
            b[i]=y[i];
        }

        //this.b= y;
        costMatrix = new double[a.length][b.length];
        Log.i("Constructor  a length", Integer.toString(a.length));
        Log.i("Constructor  b length", Integer.toString(b.length));
        for(int i=0; i<b.length; i++){

            Log.i("b-values", Integer.toString(b[i].acc[0]));
            Log.i("b-values", Integer.toString(b[i].acc[1]));
            Log.i("b-values", Integer.toString(b[i].acc[2]));
        }
    }

    public void fillCostMatrix(){
        for (int i=0; i<a.length; i++){
            Log.i("TAG", "inside fillCOstMatrix");
            for(int j=0; j<b.length; j++){
                costMatrix[i][j]= a[i].getEulerDistance(b[j]);
                Log.i("TAG-cost", Double.toString(costMatrix[i][j]));

            }
        }
    }

    public boolean findMinCostPath(){
        int i=0, j=0;
        int flag=1;
        //Will have to set warping window here itself
        while(i+1<a.length&&j+1<b.length)                                                         //set min path to zero
        {
            if(costMatrix[i+1][j]<costMatrix[i+1][j+1])
            {
                if(costMatrix[i+1][j]<costMatrix[i][j+1])
                {
                    costMatrix[i][j]=0;
                    i++;
                }
                else
                {
                    costMatrix[i][j]=0;
                    j++;
                }

            }
            else if(costMatrix[i+1][j+1]<costMatrix[i+1][j])
            {
                if(costMatrix[i+1][j+1]<costMatrix[i][j+1])
                {
                    costMatrix[i][j]=0;
                    i++;j++;
                }
                else
                {
                    costMatrix[i][j]=0;
                    j++;
                }
            }
            else
            {
                costMatrix[i][j]=0;
                i++;j++;
            }
        }
        if (flag==1) {
            return true;
        }
        else return false;
    }

}
