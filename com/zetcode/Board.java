package com.zetcode;

import com.Trainer;
import com.zetcode.Shape.Tetrominoe;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Board extends JPanel {

    private final int BOARD_WIDTH = 10;
    private final int BOARD_HEIGHT = 22;
    private final int PERIOD_INTERVAL = 0;

    private Timer timer;
    private boolean isFallingFinished = false;
    private boolean isPaused = false;
    private int numLinesRemoved = 0;
    private int curX = 0;
    private int curY = 0;
    private JLabel statusbar;
    private Shape curPiece;
    private Tetrominoe[] board;
    private Trainer trains;
    private boolean gameStatus = true;
    private boolean display = true;


    public void pausedisplay()
    {
        display = !display;
    }
    // private AI ai;

    public Board(Tetris parent, Double[] weights) 
    {
        initBoard(parent);
        //ai = new AI(1, 1, 1, 1);
        //ai = new AI( -0.510066, 0.760666, -0.184483, -0.35663);
        trains = new Trainer(weights); //= train;
        //trains.generateRandomCandidate();
        //trains.normalize();
        trains.computeFitness();
        
    }

    // public Board(Tetris parent, Double[] parent1, Double[] parent2, int score1, int score2) 
    // {
    //     initBoard(parent);
    //     //ai = new AI(1, 1, 1, 1);
    //     //ai = new AI( -0.510066, 0.760666, -0.184483, -0.35663);
    //     trains = new Trainer(); //= train;        
    //     trains.crossOver(parent1, parent2, score1, score2);
    //     trains.computeFitness();
        
    // }

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
        removeFullLines();

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
            gameStatus = false;
            
            if (!display)
            {
                setVisible(false);
            }
            Thread.currentThread().interrupt();
            //return;
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
    private void dropUp(Shape newPiece, int newX, int newY) {

        //int newY = curY;

        while (newY < 20) {

            if (!aiMove(newPiece, curX, newY + 1)) {

                break;
            }

            newY++;
        }
    }
    private boolean aimovePiece(Shape newPiece, int newX, int newY) {

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

        //curPiece = newPiece;
        //curX = newX;
        //curY = newY;

        //repaint();

        return true;
    }

    private int[] aiMoveTest(Shape currentPiece) 
    {
        //System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        //int currentX = BOARD_WIDTH -1 - currentPiece.minX();
        //int currentY = 10;
        int newX = 0;
        int newY = 10;
        int x = 0;

        boolean inBounds = true;

        int[] xLocations = {0,1,2,3,4,5,6,7,8,9};

        double[] score = new double[40];
        int bestRotate = 0;
        int bestXColumn = 0;
        double maxScore = Double.NEGATIVE_INFINITY;
        int maxIndex = 0;


        Shape testPiece = new Shape();
        testPiece.setShape(currentPiece.getShape());
        Tetrominoe[] test = new Tetrominoe[board.length];
        for (int i = 0; i < board.length; i++)
        {
            test[i] = board[i];
        } 
        
        for(int rotation = 0; rotation < 4; rotation++)
        {
            newX = 0;

            for (int j = 0; j<10; j++)
            {
                newY = 10;
                newX = xLocations[j];
                inBounds = true;
                
                //Increment through segments of Piece
                for (int i = 0; i < 4; i++) 
                {
                    //Set new segment location
                    x = newX + testPiece.x(i);

                    //If out of bounds stop incrementing X
                    if (x >= BOARD_WIDTH || newY >= BOARD_HEIGHT || x < 0) 
                    {
                        inBounds = false;
                    }
                }

                if(inBounds)
                {
                    dropUp(testPiece,newX,newY);
                    //Check if newY hit bottom      MAKE SEPERATE METHOD
                    while (newY > 0) 
                    {

                        if (!aiMove(testPiece, newX, newY - 1)) {
            
                            break;
                        }
        
                        newY--;
                        
                    }

                    for (int i = 0; i < 4; i++) {

                        int xx = newX + testPiece.x(i);
                        int yy = newY - testPiece.y(i);
                        test[(yy * BOARD_WIDTH) + xx] = testPiece.getShape();
                    } 
                    
                    score[rotation + (4*newX)] = trains.getAI().calculateScore(test);
                    //System.out.println("rotates " + rotation + " xCol " + newX + " | " + score[rotation + (4*newX) + " "]);
                    //System.out.println("rotates " + rotation + " xCol " + newX + " | " + score[rotation + (4*newX)] + "\tAH:" + trains.getAI().calculateAggregateHeight(test) + "\tBP: " + trains.getAI().calculateBumpiness(test) + "\tHoles: " + trains.getAI().calculateHoles(test));

                    if (score[rotation + (4*newX)] > maxScore)
                    {
                        bestRotate = rotation;
                        bestXColumn = j;
                        maxScore = score[rotation + (4*newX)];
                        maxIndex = rotation + (4*newX);
                    }


                    for (int i = 0; i < 4; i++) {

                        int xx = newX + testPiece.x(i);
                        int yy = newY - testPiece.y(i);
                        test[(yy * BOARD_WIDTH) + xx ] = Shape.Tetrominoe.NoShape;
                    }
                }
                else
                {
                    //System.out.println("OUT OF BOUNDS");
                }
            }
            
            testPiece = testPiece.rotateRight();
        }

        //System.out.println("Max pos: " + maxIndex + " | " + maxScore + " Using Rotates: " + bestRotate + " BestX: " + bestXColumn);
        return new int[] {bestRotate, bestXColumn};
        //pieceDropped();
        //repaint();
        //return true;
    }

    private void placeBestPiece(Shape currentPiece)
    {
        
        int[] bestMove = aiMoveTest(currentPiece);
        

        for(int i = 0;i<bestMove[0]; i++)
        {
            //movePiece(currentPiece.rotateRight(), curX, curY);//currentPiece.rotateRight();
            currentPiece = currentPiece.rotateRight();
        }
        
        movePiece(currentPiece, bestMove[1], curY);
        dropDown();
    }


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

    private void doGameCycle() 
    {
        update();
        repaint();
    }

    private void update() {

        placeBestPiece(curPiece);
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
                case KeyEvent.VK_ESCAPE -> pausedisplay();
                case KeyEvent.VK_P -> pause();
                case KeyEvent.VK_LEFT -> movePiece(curPiece, curX - 1, curY);
                case KeyEvent.VK_RIGHT -> movePiece(curPiece, curX + 1, curY);
                case KeyEvent.VK_DOWN -> movePiece(curPiece.rotateRight(), curX, curY);
                case KeyEvent.VK_UP -> movePiece(curPiece.rotateLeft(), curX, curY);
                case KeyEvent.VK_SPACE -> {placeBestPiece(curPiece);}
                //case KeyEvent.VK_SPACE -> {dropUp(curPiece, curY);}
                case KeyEvent.VK_D -> oneLineDown();
                case KeyEvent.VK_F -> placeBestPiece(curPiece);
            }
        }
    }

    public int getScore()
    {
        return numLinesRemoved;
    }
    
    public boolean getStatus()
    {
        return gameStatus;
    }

    public Trainer getTrainer()
    {
        return trains;
    }
}
