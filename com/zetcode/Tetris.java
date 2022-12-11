package com.zetcode;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;

/*
Java Tetris game clone

Author: Jan Bodnar
Website: https://zetcode.com
 */
public class Tetris extends JFrame {

    private JLabel statusbar;

    public Tetris(int x, int y) {
        initUI(x, y);
    }

    private void initUI(int x, int y) {

        statusbar = new JLabel(" 0");
        add(statusbar, BorderLayout.SOUTH);

        var board = new Board(this);
        add(board);
        board.start();

        setTitle("Tetris");
        setSize(200, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(x*200,y);
    }

    JLabel getStatusBar() {

        return statusbar; 
    }

    public static void main(String[] args) {

        int n = 16; // Number of threads
        for (int i = 0; i < n; i++) {
            if(i>=8){
                MultithreadingDemo object
                = new MultithreadingDemo(i-8, 0);
                object.start();

            }else{
                MultithreadingDemo object
                = new MultithreadingDemo(i, 400);
                object.start();
            }
        }
    }
}


class MultithreadingDemo extends Thread {
    public MultithreadingDemo(int x, int y) {
        try {
            // Displaying the thread that is running
            var game = new Tetris(x, y);
            game.setVisible(true);
        }
        catch (Exception e) {
            // Throwing an exception
            System.out.println("Exception is caught");
        }
    }
}