package com.zetcode;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;

//import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.lang.Math;

import com.Trainer;
import java.util.Arrays;
/*
Java Tetris game clone

Author: Jan Bodnar
Website: https://zetcode.com
 */
public class Tetris extends JFrame {

    private JLabel statusbar;
    private Board board;
    private Double[] weights;

    public Tetris(int x, int y, Double[] bestWeights) {
        weights = bestWeights;
        initUI(x, y);
    }

    // public Tetris(int x, int y, Double[] parent1, Double[] parent2, int score1, int score2) {
    //     initUI(x, y, parent1, parent2, score1, score2);
    // }

    private void initUI(int x, int y) {

        statusbar = new JLabel(" 0");
        add(statusbar, BorderLayout.SOUTH);

        board = new Board(this,weights);
        add(board);
        board.start();
        board.getStatus();
        board.getScore();

        setTitle("Tetris");
        setSize(200, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(x*200,y);
    }

    // private void initUI(int x, int y, Double[] parent1, Double[] parent2, int score1, int score2) {

    //     statusbar = new JLabel(" 0");
    //     add(statusbar, BorderLayout.SOUTH);

    //     board = new Board(this,parent1, parent2, score1, score2);
    //     add(board);
    //     board.start();
    //     board.getStatus();
    //     board.getScore();

    //     setTitle("Tetris");
    //     setSize(200, 400);
    //     setDefaultCloseOperation(EXIT_ON_CLOSE);
    //     setLocation(x*200,y);
    // }

    public Board getBoard()
    {
        return board;
    }

    JLabel getStatusBar() {

        return statusbar; 
    }

    public static void main(String[] args) {

        int n = 16; // Number of threads
        MultithreadingDemo[] games = new MultithreadingDemo[n];
        Double[] parent1 = new Double[4];
        Double[] parent2 = new Double[4];
        Double[] bestWeights = new Double[4];
        Double[] currentWeights = new Double[4];
        int gens = 500;

        int gameid1 = 0;
        int gameid2 = 0;

        int prevMax1 = -1;
        int prevMax2 = -1;

        int topscore = 0;


        for (int g = 0; g < gens; g++)
        {
            
            int maxScore1 = -1;
            int maxScore2 = -1;
            for (int i = 0; i < n; i++) {
                MultithreadingDemo object;
                double mutation = Math.random();
                int mutateweight = -1;


                //Mutation chance of 5%
                if (mutation>0.75)
                {
                    mutateweight = (int)Math.floor(Math.random()*5);
                    System.out.println("MUTATION: " + mutateweight);
                }
                
                // If first generation
                if (g == 0)
                {
                    for (int j = 0; j<4; j++)
                    {
                        currentWeights[j] = (Math.random() * 0.4 - 0.2);
                    }
                }
                else
                {
                    double norm = Math.sqrt(bestWeights[0]*bestWeights[0] + bestWeights[1]*bestWeights[1] + bestWeights[2]*bestWeights[2] + bestWeights[3]*bestWeights[3]);
                    
                    for (int j = 0; j<4; j++)
                    {
                        if (j != mutateweight)
                        {
                            currentWeights[j] = bestWeights[j]/norm;
                        }
                        else
                        {
                            currentWeights[j] = (bestWeights[j] + (Math.random() * 0.4 - 0.2))/norm;
                        }
                    }

                    
                
                }

                int windowsPerRow = 12;
                int Yoffset = 400 * (int)Math.floor(i/windowsPerRow);
                int Xoffset = i%windowsPerRow;

                object = new MultithreadingDemo(Xoffset, Yoffset, currentWeights);
                games[i] = object;
                object.start();
                // for (int k = 0; k < n; k++)
                // {
                //     games[i].getGame().dispose();
                // }
            }
            System.out.println("Generation: " + g + "\tBest Score: " + topscore );
            System.out.println( "Children Weights" + "\tAH: " + currentWeights[0] + "\tCL: " + currentWeights[1] + "\tBP: " + currentWeights[2] + "\tHole: " + currentWeights[3]);
    
            boolean continueGen = true;
            while(continueGen)
            {
                continueGen = false;
                for (int i = 0; i<n; i++)
                {
                    if (games[i].getGame().getBoard().getStatus())
                    {
                        continueGen = true;
                    }
                }
            }
            

            // search games for the highest score
            for (int i = 0; i < n; i++)
            {
                int curScore = games[i].getGame().getBoard().getScore();
                if (curScore > maxScore1)
                {
                    maxScore1 = curScore;
                    gameid1 = i;
                }
                else if (curScore > maxScore2)
                {
                    maxScore2 = curScore;
                    gameid2 = i;
                }
            }
            parent1 = games[gameid1].getGame().getBoard().getTrainer().getWeights();
            parent2 = games[gameid2].getGame().getBoard().getTrainer().getWeights();
            
            // int parent1score = prevMax1;
            // int parent2score = prevMax2;

            // if (maxScore1 > prevMax1 || maxScore1 > prevMax2)
            // {
            //     System.out.println("Did better: Adjusting Weights");
            //     if (maxScore1 > prevMax1)
            //     {
            //         parent1score = maxScore1;
            //         parent1 = games[gameid1].getGame().getBoard().getTrainer().getWeights();
            //     }
            //     if (maxScore1 > prevMax2)
            //     {
            //         parent2score = maxScore2;
            //         parent2 = games[gameid2].getGame().getBoard().getTrainer().getWeights();
            //     }
            //     if (maxScore2 > prevMax2)
            //     {
            //         parent2score = maxScore2;
            //         parent2 = games[gameid2].getGame().getBoard().getTrainer().getWeights();
            //     }
                
                
            //     System.out.println("AH: " + bestWeights[0] + "\tCL: " + bestWeights[1] + "\tBP: " + bestWeights[2] + "\tHole: " + bestWeights[3] + "\n");
            //     bestWeights[0] = parent1[0] * parent1score + parent2[0] * parent2score;
            //     bestWeights[1] = parent1[1] * parent1score + parent2[1] * parent2score;
            //     bestWeights[2] = parent1[2] * parent1score + parent2[2] * parent2score;
            //     bestWeights[3] = parent1[3] * parent1score + parent2[3] * parent2score;
                
            //     prevMax1 = parent1score;
            //     prevMax2 = parent2score;
            // }

                bestWeights[0] = parent1[0] * maxScore1 + parent2[0] * maxScore2;
                bestWeights[1] = parent1[1] * maxScore1 + parent2[1] * maxScore2;
                bestWeights[2] = parent1[2] * maxScore1 + parent2[2] * maxScore2;
                bestWeights[3] = parent1[3] * maxScore1 + parent2[3] * maxScore2;

                topscore = maxScore1;

            for (int i = 0; i < n; i++)
            {
                games[i].getGame().dispose();
            }
        }
    }
}


class MultithreadingDemo extends Thread {
    private Tetris game;
    public MultithreadingDemo(int x, int y, Double[] weights) {
        try {
            // Displaying the thread that is running
            game = new Tetris(x, y, weights);
            game.setVisible(true);
            
            
        }
        catch (Exception e) {
            // Throwing an exception
            System.out.println("Exception is caught");
        }
    }

    // public MultithreadingDemo(int x, int y, Double[] parent1, Double[] parent2, int score1, int score2) {
    //     try {
    //         // Displaying the thread that is running
    //         game = new Tetris(x, y, parent1, parent2, score1, score2);
    //         game.setVisible(true);
            
            
    //     }
    //     catch (Exception e) {
    //         // Throwing an exception
    //         System.out.println("Exception is caught");
    //     }
    // }
    

    public Tetris getGame()
    {
        return game;
    }
}

