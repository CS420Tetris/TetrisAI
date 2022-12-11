package com.zetcode;

import java.awt.BorderLayout;
//import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;

/*
Java Tetris game clone

Author: Jan Bodnar
Website: https://zetcode.com
 */
public class Tetris extends JFrame {

    private JLabel statusbar;

    public Tetris() {

        initUI();
    }

    private void initUI() {

        statusbar = new JLabel(" 0");
        add(statusbar, BorderLayout.SOUTH);

        var board = new Board(this);
        add(board);
        board.start();

        setTitle("Tetris");
        setSize(200, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    JLabel getStatusBar() {

        return statusbar; 
    }

    public static void main(String[] args) {

        int n = 1; // Number of threads
        for (int i = 0; i < n; i++) {
            MultithreadingDemo object
            = new MultithreadingDemo();
            object.start();
        }
    }
}


class MultithreadingDemo extends Thread {
    public void run() {
        try {
            // Displaying the thread that is running
            var game = new Tetris();
            game.setVisible(true);
        }
        catch (Exception e) {
            // Throwing an exception
            System.out.println("Exception is caught");
        }
    }
}