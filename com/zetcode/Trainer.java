package com.zetcode;

public class Trainer 
{
    //private double heightWeight;
    //private double completeLinesWeight;
    //private double bumpinessWeight;
    //private double holeWeight;

    public double[] generateRandomCandidate()
    {   
        double [] weights = {Math.random() -0.5,Math.random() -0.5,Math.random() -0.5,Math.random() -0.5};
        return normalize(weights);
    }

    public double[] normalize(double[] weights)
    {
        double norm = Math.sqrt(weights[0]*weights[0] + weights[1]*weights[1] + weights[2]*weights[2] + weights[3]*weights[3]);
        for(int i = 0; i<4; i++)
        {
            weights[i] = weights[i]/norm;
        }
        return weights;
    }

    private void train()
    {
        //double[][] trainees = new double[4][16];
        double[] weight;
        

        
        generateRandomCandidate();

        //Compute fitness
    }
    
}
