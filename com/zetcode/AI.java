package com.zetcode;

import com.zetcode.Shape.Tetrominoe;

// Decision making component representing an artificial intelligence playing the game
public class AI 
{
    // Priority weights to decide which moves to make
    // Height = aggregate max-height of all columns
    // Complete Lines = Number of rows that will have pieces in all 10 positions of that row
    // Bumpiness = Difference of a columns height compared to its surrounding columns
    // Holes - An empty space such that thre is atleast one tile in the same column above it
    private double heightWeight;
    private double completeLinesWeight;
    private double bumpinessWeight;
    private double holeWeight;

    // Each AI will be given weights
    public AI(double _heightWeight, double _completeLinesWeight, double _bumpinessWeight, double _holeWeight)
    {
        this.heightWeight = _heightWeight;
        this.completeLinesWeight = _completeLinesWeight;
        this.bumpinessWeight = _bumpinessWeight;
        this.holeWeight = _holeWeight;
        //normalize();
    }

    // Returns aggregate height of the board multiplied by its weight
    public double calculateAggregateHeight(Board board)
    {
        int aggregateHeight = 0;
        for (int x = 0; x < 10; x++) 
        {
            for (int y = 21; y >= 0; y--) 
            {

                if (!board.shapeAt(x, y).equals(Shape.Tetrominoe.NoShape)) 
                {
                    aggregateHeight += y + 1;
                    break;
                }
            }
        }
        return aggregateHeight * heightWeight;
    }

    // Returns aggregate height of the board multiplied by its weight
    public double calculateAggregateHeight(Tetrominoe[] board)
    {
        int aggregateHeight = 0;
        for (int x = 0; x < 10; x++) 
        {
            for (int y = 21; y >= 0; y--) 
            {
                //yy * BOARD_WIDTH) + xx 
                if (!board[(y * 10) + x].equals(Shape.Tetrominoe.NoShape))
                {
                    aggregateHeight += y + 1;
                    break;
                }
            }
        }
        // for(int index = 0; index < board.length; index++)
        // {
        //     System.out.println(index + " " + board[index].toString());
        // }
        return aggregateHeight * heightWeight;
    }
    

    // Returns number of complete lines on the board multiplied by its weight
    public double calculateCompleteLines(Board board)
    {
        int completeLines = 0;

        for (int y = 0; y < 22; y++) 
        {

            boolean lineIsFull = true;

            for (int x = 0; x < 10; x++) 
            {

                if (board.shapeAt(x, y).equals(Shape.Tetrominoe.NoShape)) 
                {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) 
            {
                completeLines++;
            }
        }
        return completeLinesWeight * completeLines;
    }

    public double calculateCompleteLines(Tetrominoe[] board)
    {
        int completeLines = 0;

        for (int y = 0; y < 22; y++) 
        {

            boolean lineIsFull = true;

            for (int x = 0; x < 10; x++) 
            {

                if (board[(y * 10) + x].equals(Shape.Tetrominoe.NoShape)) 
                {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) 
            {
                completeLines++;
            }
        }
        return completeLinesWeight * completeLines;
    }

    // Returns bumpiness of the board multiplied by its weight
    public double calculateBumpiness(Board board)
    {
        int aggregateBumpHeights = 0;
        int currentHeight = 0;
        int previousHeight = 0;

        for (int x = 0; x < 10; x++) 
        {
            currentHeight = 0;
            for (int y = 21; y >= 0; y--) 
            {
                if (!board.shapeAt(x, y).equals(Shape.Tetrominoe.NoShape)) 
                {
                    currentHeight = y + 1;
                    break;
                }
            }

            if (x != 0)
            {
                aggregateBumpHeights += Math.abs(currentHeight - previousHeight);
            }
            previousHeight = currentHeight;
        }
        return aggregateBumpHeights * bumpinessWeight;
    }

    // Returns bumpiness of the board multiplied by its weight
    public double calculateBumpiness(Tetrominoe[] board)
    {
        int aggregateBumpHeights = 0;
        int currentHeight = 0;
        int previousHeight = 0;

        for (int x = 0; x < 10; x++) 
        {
            currentHeight = 0;
            for (int y = 21; y >= 0; y--) 
            {
                if (!board[(y * 10) + x].equals(Shape.Tetrominoe.NoShape)) 
                {
                    currentHeight = y + 1;
                    break;
                }
            }

            if (x != 0)
            {
                aggregateBumpHeights += Math.abs(currentHeight - previousHeight);
            }
            previousHeight = currentHeight;
        }
        return aggregateBumpHeights * bumpinessWeight;
    }

    // Returns number of holes on the board multiplied by its weight
    public double calculateHoles(Board board)
    {
        int holes = 0;

        for (int x = 0; x < 10; x++) 
        {
            boolean isHole = false;

            for (int y = 0; y < 22; y++) 
            {
                if (board.shapeAt(x, y).equals(Tetrominoe.NoShape)) 
                {
                    isHole = true;
                }
                else
                {
                    if (isHole == true)
                    {
                        isHole = false;
                        holes++;
                    }
                }
            }
        }
        return holeWeight * holes;
    }

    public double calculateHoles(Tetrominoe[] board)
    {
        int holes = 0;

        for (int x = 0; x < 10; x++) 
        {
            boolean isHole = false;

            for (int y = 0; y < 22; y++) 
            {
                if (board[(y * 10) + x].equals(Shape.Tetrominoe.NoShape))
                {
                    isHole = true;
                }
                else
                {
                    if (isHole == true)
                    {
                        isHole = false;
                        holes++;
                    }
                }
            }
        }
        return holeWeight * holes;
    }

    public double calculateScore(Board board)
    {
        return (calculateAggregateHeight(board) + calculateBumpiness(board) + calculateCompleteLines(board) + calculateHoles(board));
    }
    public double calculateScore(Tetrominoe[] board)
    {
        return (calculateAggregateHeight(board) + calculateBumpiness(board) + calculateCompleteLines(board) + calculateHoles(board));
    }

    public void normalize()
    {
        double norm = Math.sqrt(heightWeight*heightWeight + completeLinesWeight*completeLinesWeight + holeWeight*holeWeight + bumpinessWeight*bumpinessWeight);
        heightWeight = heightWeight / norm;
        completeLinesWeight = completeLinesWeight / norm;
        holeWeight = holeWeight / norm;
        bumpinessWeight = bumpinessWeight / norm;
    }
}
