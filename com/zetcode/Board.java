package com.zetcode;

import com.zetcode.Shape.Tetrominoe;

import javax.security.auth.x500.X500Principal;
//import javax.lang.model.util.ElementScanner14;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.security.cert.X509CRL;

public class Board extends JPanel {

    private final int BOARD_WIDTH = 10;
    private final int BOARD_HEIGHT = 22;
    private final int PERIOD_INTERVAL = 1000;

    private Timer timer;
    private boolean isFallingFinished = false;
    private boolean isPaused = false;
    private int numLinesRemoved = 0;
    private int curX = 0;
    private int curY = 0;
    private JLabel statusbar;
    private Shape curPiece;
    private Tetrominoe[] board;
    private AI ai;

    public Board(Tetris parent) 
    {
        initBoard(parent);
        ai = new AI(1, 1, 1, 1);
    }

    private void initBoard(Tetris parent) {

        //GUI stuff
        setFocusable(true);
        statusbar = parent.getStatusBar();

        //User Input
        addKeyListener(new TAdapter());
    }

    private int squareWidth() {

        return (int) getSize().getWidth() / BOARD_WIDTH;
    }

    private int squareHeight() {

        return (int) getSize().getHeight() / BOARD_HEIGHT;
    }

    public Tetrominoe shapeAt(int x, int y) {

        return board[(y * BOARD_WIDTH) + x];
    }

    void start() {

        curPiece = new Shape();
        board = new Tetrominoe[BOARD_WIDTH * BOARD_HEIGHT];

        clearBoard();
        newPiece();

        timer = new Timer(PERIOD_INTERVAL, new GameCycle());
        timer.start();
    }

    private void pause() {

        isPaused = !isPaused;

        if (isPaused) {

            statusbar.setText("paused");
        } else {

            statusbar.setText(String.valueOf(numLinesRemoved));
        }

        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        var size = getSize();
        int boardTop = (int) size.getHeight() - BOARD_HEIGHT * squareHeight();

        for (int i = 0; i < BOARD_HEIGHT; i++) {

            for (int j = 0; j < BOARD_WIDTH; j++) {

                Tetrominoe shape = shapeAt(j, BOARD_HEIGHT - i - 1);

                if (shape != Tetrominoe.NoShape) {

                    drawSquare(g, j * squareWidth(),
                            boardTop + i * squareHeight(), shape);
                }
            }
        }

        if (curPiece.getShape() != Tetrominoe.NoShape) {

            for (int i = 0; i < 4; i++) {

                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);

                drawSquare(g, x * squareWidth(),
                        boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(),
                        curPiece.getShape());
            }
        }
    }

    private void dropDown() {

        int newY = curY;

        while (newY > 0) {

            if (!movePiece(curPiece, curX, newY - 1)) {

                break;
            }

            newY--;
        }

        pieceDropped();
    }

    private void oneLineDown() {

        if (!movePiece(curPiece, curX, curY - 1)) {

            pieceDropped();
        }
    }

    private void clearBoard() {

        for (int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH; i++) {

            board[i] = Tetrominoe.NoShape;
        }
    }

    private void pieceDropped() {

        for (int i = 0; i < 4; i++) {

            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * BOARD_WIDTH) + x] = curPiece.getShape();
        }
        //System.out.println("Lines Complete: " + ai.calculateCompleteLines(this));
        removeFullLines();

        //System.out.println("Ag height: " + ai.calculateAggregateHeight(this));
        //System.out.println("AgBump Heights: " + ai.calculateBumpiness(this));
        //System.out.println("Holes: " + ai.calculateHoles(this));
        if (!isFallingFinished) {

            newPiece();
        }
    }

    private void newPiece() {

        curPiece.setRandomShape();
        curX = BOARD_WIDTH / 2 + 1;
        curY = BOARD_HEIGHT - 1 + curPiece.minY();

        if (!movePiece(curPiece, curX, curY)) {

            curPiece.setShape(Tetrominoe.NoShape);
            timer.stop();

            var msg = String.format("Game over. Score: %d", numLinesRemoved);
            statusbar.setText(msg);
        }
    }

    private boolean movePiece(Shape newPiece, int newX, int newY) {

        for (int i = 0; i < 4; i++) {

            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i); //What this do

            if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) {

                return false;
            }

            if (shapeAt(x, y) != Tetrominoe.NoShape) {

                return false;
            }
        }

        curPiece = newPiece;
        curX = newX;
        curY = newY;

        repaint();

        return true;
    }

    private boolean validMove(Shape newPiece, int newX, int newY)
    {
        for (int i = 0; i < 4; i++) {

            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);

            if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) {

                return false;
            }

            if (shapeAt(x, y) != Tetrominoe.NoShape) {

                return false;
            }
        }
        return true;
    }

    private boolean aiMove(Shape newPiece, int newX, int newY) {

        for (int i = 0; i < 4; i++) {

            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i); //What this do

            if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) {

                return false;
            }

            if (shapeAt(x, y) != Tetrominoe.NoShape) {

                return false;
            }
        }

        curPiece = newPiece;
        //curX = newX;
        //curY = newY;

        //repaint();

        return true;
    }

    private void aiMoveTest(Shape currentPiece) 
    {
        int currentX = BOARD_WIDTH -1 + currentPiece.minX();
        int currentY = BOARD_HEIGHT - 1 + currentPiece.minY();
        //int newX = 0;

        double[] score = new double[40];
        //Increment through X
        //rotate
        for(int rotation = 0; rotation < 4; rotation++)
        {
            Shape testPiece = currentPiece;
            testPiece = testPiece.rotateRight();
            for (int x = 0; x < 10; x++) 
            {
                boolean validMove = false;
                //int moves = Math.abs(currentX - x);

                //for(int index = 0; index < moves; )

                while(validMove(testPiece, x, currentY -1))
                {
                    currentY--;
                    validMove = true;
                }  

                if (validMove == false)
                {
                    score[4 * x + rotation] = 0;
                    //System.out.println("X: " + x + " Rot: " + rotation + " Score: " + score[4 * x + rotation]);
                    //System.out.println("invalid pos");
                    //break;
                
                }
                else
                {
                    for (int i = 0; i < 4; i++) 
                    {
                        int xx = x + testPiece.x(i);
                        int yy = currentY - testPiece.y(i);
                        //System.out.println("x-" + xx + " y- " + yy);
                        board[(yy * BOARD_WIDTH) + xx] = testPiece.getShape();
                    }

                    score[4 * x + rotation] = ai.calculateScore(this);
                    //System.out.println("X: " + x + " Rot: " + rotation + " Score: " + score[rotation * x]);
                    //System.out.println("X: " + x + " Rot: " + rotation + " AggScore: " + ai.calculateAggregateHeight(this) + " completedLines " + ai.calculateCompleteLines(this) + " bumpiness " + ai.calculateBumpiness(this) + " holes " + ai.calculateHoles(this));

                    for (int i = 0; i < 4; i++) 
                    {
                        int xx = x + testPiece.x(i);
                        int yy = currentY - testPiece.y(i);
                        board[(yy * BOARD_WIDTH) + xx] = Shape.Tetrominoe.NoShape;
                    }

                }
                currentY = BOARD_HEIGHT - 1 + currentPiece.minY();
                //x = BOARD_WIDTH -1 + currentPiece.minX();
            }
            //x = BOARD_WIDTH -1 + currentPiece.minX();
            //currentY = BOARD_HEIGHT - 1 + currentPiece.minY();

        }
        for(int index = 0; index < 40; index++)
        {
            //System.out.println("X: " + x + " Rot: " + rotation + " AggScore: " + ai.calculateAggregateHeight(this) + " completedLines " + ai.calculateCompleteLines(this) + " bumpiness " + ai.calculateBumpiness(this) + " holes " + ai.calculateHoles(this));
           // System.out.println("X: " + x + " Rot: " + rotation + " AggScore: " + ai.calculateAggregateHeight(this) + " completedLines " + ai.calculateCompleteLines(this) + " bumpiness " + ai.calculateBumpiness(this) + " holes " + ai.calculateHoles(this));
            System.out.println("Pos" + index + " " + score[index]);
        }

        //pieceDropped();
        //repaint();
        //return true;
    }

    /* 
    for (int x = 0; x < 1; x++) 
            {
                while(validMove(testPiece, currentX - 1, currentY) && currentX >= x)
                {
                    currentX--;
                }
                //movePiece(testPiece, x, currentY);
                //System.out.println("test");
                //int newY = curY;

                while (currentY  > 0) {

                    if (!validMove(testPiece, currentX, currentY - 1)) 
                    {
                        //score[rotation * x] = 0;
                        break;
                    }

                    currentY--;
                }

                
                for (int i = 0; i < 4; i++) 
                {
                    int xx = currentX + testPiece.x(i);
                    int yy = currentY - testPiece.y(i);
                    System.out.println("x-" + xx + " y- " + yy);
                    board[(yy * BOARD_WIDTH) + xx] = testPiece.getShape();
                }
                //System.out.println("Lines Complete: " + ai.calculateCompleteLines(this));
              /* 
                while(validMove(testPiece, x, currentY -1))
                {
                    currentY--;
                }  */
                
                /* 
                score[rotation * x] = ai.calculateScore(this);
                System.out.println("X: " + x + " Rot: " + rotation + " AggScore: " + ai.calculateAggregateHeight(this) + " completedLines " + ai.calculateCompleteLines(this) + " bumpiness " + ai.calculateBumpiness(this) + " holes " + ai.calculateHoles(this));

                for (int i = 0; i < 4; i++) {

                    int xx = currentX + testPiece.x(i);
                    int yy = currentY - testPiece.y(i);
                    board[(yy * BOARD_WIDTH) + xx] = Shape.Tetrominoe.NoShape;
                }
                currentY = BOARD_HEIGHT - 1 + currentPiece.minY();
                currentX = BOARD_WIDTH - 1 + currentPiece.minX();
            }*/
    /* 
    private void aiMoveTest(Shape newPiece) 
    {
        int StartY = 0;
        int newX = 0;
        int x = 0;

        //Increment through X
        //rotate
            loop1:
            while (newX<10)
            {   
                
                //Increment through segments of Piece
                for (int i = 0; i < 4; i++) {

                    //Set new segment location
                    x = newX + newPiece.x(i);

                    //If out of bounds stop incrementing X
                    if (x >= BOARD_WIDTH || newY >= BOARD_HEIGHT) {
                        break loop1;
                    }

                    
                }
                
                    
                for (int j = 0; j < 4; j++)
                {  
                    newY = StartY;
                    //Check if newY hit bottom      MAKE SEPERATE METHOD
                    while (newY > 0) {
                        

                        if (!aiMove(newPiece, newX, newY - 1)) {
            
                            break;
                        }
            
                        newY--;
                        
                        
                    }
                    newPiece.rotateRight();
                    System.out.println(newX);
                }
                
                //Check Score
                //curX = newX;
                //curY = newY;
                
                

                //pause();
                newX++;
            }
        

        //repaint();
        //return true;
    }*/

    private void removeFullLines() {

        int numFullLines = 0;

        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {

            boolean lineIsFull = true;

            for (int j = 0; j < BOARD_WIDTH; j++) {

                if (shapeAt(j, i) == Tetrominoe.NoShape) {

                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {

                numFullLines++;

                for (int k = i; k < BOARD_HEIGHT - 1; k++) {
                    for (int j = 0; j < BOARD_WIDTH; j++) {
                        board[(k * BOARD_WIDTH) + j] = shapeAt(j, k + 1);
                    }
                }
            }
        }

        if (numFullLines > 0) {

            numLinesRemoved += numFullLines;

            statusbar.setText(String.valueOf(numLinesRemoved));
            isFallingFinished = true;
            curPiece.setShape(Tetrominoe.NoShape);
        }
    }

    private void drawSquare(Graphics g, int x, int y, Tetrominoe shape) {

        Color colors[] = {new Color(0, 0, 0), new Color(204, 102, 102),
                new Color(102, 204, 102), new Color(102, 102, 204),
                new Color(204, 204, 102), new Color(204, 102, 204),
                new Color(102, 204, 204), new Color(218, 170, 0)
        };

        var color = colors[shape.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + 1);
    }

    private class GameCycle implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            doGameCycle();
        }
    }

    private void doGameCycle() {

        update();
        repaint();
    }

    private void update() {

        if (isPaused) {

            return;
        }

        if (isFallingFinished) {

            isFallingFinished = false;
            newPiece();
        } else {

            oneLineDown();
        }
    }

    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            if (curPiece.getShape() == Tetrominoe.NoShape) {

                return;
            }

            int keycode = e.getKeyCode();

            // Java 12 switch expressions
            switch (keycode) {

                case KeyEvent.VK_P -> pause();
                case KeyEvent.VK_LEFT -> movePiece(curPiece, curX - 1, curY);
                case KeyEvent.VK_RIGHT -> movePiece(curPiece, curX + 1, curY);
                case KeyEvent.VK_DOWN -> movePiece(curPiece.rotateRight(), curX, curY);
                case KeyEvent.VK_UP -> movePiece(curPiece.rotateLeft(), curX, curY);
                case KeyEvent.VK_SPACE -> {aiMoveTest(curPiece);}
                //case KeyEvent.VK_SPACE -> {dropDown();}
                case KeyEvent.VK_D -> oneLineDown();
            }
        }
    }
}
