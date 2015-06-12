package com.example.aviral.myapplication;

import android.util.Log;

/**
 * Created by aviral on 6/8/15.
 */
public class DTWMatrix extends CostMatrix {
    private double[][] DTWmatrix;

    public DTWMatrix(DTW[] x, DTW[] y ){
        super( x, y);
        //   super.fillCostMatrix();
        DTWmatrix = new double[x.length][y.length];
        Log.i("dtwmatrix", Integer.toString(x.length));
        Log.i("dtwmatrix", Integer.toString(y.length));
    }

    public void fillDTWMatrix(){
        int i, j;
        super.fillCostMatrix();
        DTWmatrix[0][0]=costMatrix[0][0];
        for(i=1;i<a.length;i++)
        {
            DTWmatrix[i][0]=DTWmatrix[i-1][0]+costMatrix[i][0];                                 //set DTWmatrix values
        }
        for(j=1;j<b.length;j++)
        {
            DTWmatrix[0][j]=DTWmatrix[0][j-1]+costMatrix[0][j];
        }

        for(i=1;i<super.a.length;i++)
        {
            for(j=1;j<super.b.length;j++)
            {
                if(costMatrix[i-1][j]<costMatrix[i-1][j-1])
                {
                    if(costMatrix[i-1][j]<costMatrix[i][j-1])
                    {
                        DTWmatrix[i][j]=costMatrix[i][j]+DTWmatrix[i-1][j];
                    }
                    else
                    {
                        DTWmatrix[i][j]=costMatrix[i][j]+DTWmatrix[i][j-1];
                    }

                }
                else if(costMatrix[i-1][j-1]<costMatrix[i-1][j])
                {
                    if(costMatrix[i-1][j-1]<costMatrix[i][j-1])
                    {
                        DTWmatrix[i][j]=costMatrix[i][j]+DTWmatrix[i-1][j-1];
                    }
                    else
                    {
                        DTWmatrix[i][j]=costMatrix[i][j]+DTWmatrix[i][j-1];
                    }
                }
                else
                {
                    DTWmatrix[i][j]=costMatrix[i][j]+DTWmatrix[i-1][j-1];
                }
            }
        }


    }

    public boolean findAndCheckMinPath(){
        int flag=0;
        int i=a.length-1,j=b.length-1;
        while(i>=0&&j>=0)                                                         //set min path to zero
        {
            if(DTWmatrix[i-1][j]<DTWmatrix[i-1][j-1])
            {
                if(DTWmatrix[i-1][j]<DTWmatrix[i][j-1])
                {
                    DTWmatrix[i][j]=0;
                    i--;
                    if ( Math.abs(j-i) > 5)
                        flag=1;
                }
                else
                {
                    DTWmatrix[i][j]=0;
                    j--;

                    if(Math.abs(j-i) > 5)
                        flag=1;
                }

            }
            else if(DTWmatrix[i-1][j-1]<DTWmatrix[i-1][j])
            {
                if(DTWmatrix[i-1][j-1]<DTWmatrix[i][j-1])
                {
                    DTWmatrix[i][j]=0;
                    i--;j--;
                    if(Math.abs(j-i) > 5)
                        flag=1;
                }
                else
                {
                    DTWmatrix[i][j]=0;
                    j--;
                    if(Math.abs(j-i) > 5)
                        flag=1;
                }
            }
            else
            {
                DTWmatrix[i][j]=0;
                i--;j--;
                if(Math.abs(j-i) > 5)
                    flag=1;
            }
        }
        if (flag==1)
            return false;

        else
            return true;
    }

}
