package com.zetcode;

public class MultiThreadTetris
{
    public static void main(String[] args) 
    {
        for (int index = 0; index < 6; index++)
        {
            Tetris t1 = new Tetris();
            t1.start();
        }
        
    }
}
