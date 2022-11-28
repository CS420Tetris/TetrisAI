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
public class Tetris extends JFrame implements Runnable{

    private JLabel statusbar;
    private Thread t;
    private String title = "default";

    public Tetris() 
    {
        initUI();
    }

    private void initUI() {

        statusbar = new JLabel(" 0");
        add(statusbar, BorderLayout.SOUTH);

        var board = new Board(this);
        add(board);
        board.start();

        setTitle(title);
        setSize(200, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    JLabel getStatusBar() {

        return statusbar;
    }

    @Override
    public void run() {
        EventQueue.invokeLater(() -> {

            var game = new Tetris();
            game.setVisible(true);
        });
    }

    public void start()
    {
        if (t == null)
        {
        t = new Thread (this, "game");
        }
        t.start();
    }
}
