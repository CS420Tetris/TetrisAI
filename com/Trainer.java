package com;

import com.zetcode.AI;

public class Trainer 
{   
    public Trainer(Double[] bestWeights)
    {
        weights = bestWeights;
        //this.ai =  ai;
        //this.weights = weights;
        //trainAI = new AI(weights[0], weights[1], weights[2] ,weights[3]);
    }
    //private double heightWeight;
    //private double completeLinesWeight;
    //private double bumpinessWeight;
    //private double holeWeight;
    private Double[] weights = new Double[4];
    AI ai; 


    public void generateRandomCandidate()//double[]
    {   
        for (int i = 0; i<4; i++)
        {
            weights[i] = (Math.random() -0.5);
        }
        //return normalize(weights);
    }

    public void crossOver(Double[] parent1weight, Double[] parent2weight, int parent1score, int parent2score){
            
        Double[] child = new Double[4];
        child[0] = parent1weight[0] * parent1score + parent2weight[0] * parent2score;
        child[1] = parent1weight[1] * parent1score + parent2weight[1] * parent2score;
        child[2] = parent1weight[2] * parent1score + parent2weight[2] * parent2score;
        child[3] = parent1weight[3] * parent1score + parent2weight[3] * parent2score;

        this.weights = mutate(child);
        //return mutate(child);
    }

    public Double[] mutate(Double[] child)//(double[] weights)//double[]
    {
        double norm = Math.sqrt(child[0]*child[0] + child[1]*child[1] + child[2]*child[2] + child[3]*child[3]);
        for(int i = 0; i<4; i++)
        {
            child[i] = (child[i] + (Math.random() * 0.4 - 0.2))/norm;
        }
        return child;
        //return weights;
    }

    public void normalize()//(double[] weights)//double[]
    {
        double norm = Math.sqrt(weights[0]*weights[0] + weights[1]*weights[1] + weights[2]*weights[2] + weights[3]*weights[3]);
        for(int i = 0; i<4; i++)
        {
            weights[i] = weights[i]/norm;
        }
        //return weights;
    }
    public void computeFitness()//(double[] weights)
    {
        ai = new AI(weights[0], weights[1], weights[2], weights[3]);
        //System.out.println(weights[0] + "  " + weights[1]+ "  " + weights[2]+ "  " + weights[3]);
    }

    public void train()
    {
        //double[][] trainees = new double[4][16];
        //double[] weight = generateRandomCandidate();

        //computeFitness(weight);

        //Compute fitness
    }

    public AI getAI()
    {
        return ai;
    }

    public Double[] getWeights()
    {
        return weights;
    }
    
}
