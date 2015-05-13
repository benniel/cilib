/**           __  __
 *    _____ _/ /_/ /_    Computational Intelligence Library (CIlib)
 *   / ___/ / / / __ \   (c) CIRG @ UP
 *  / /__/ / / / /_/ /   http://cilib.net
 *  \___/_/_/_/_.___/
 */
package net.sourceforge.cilib.functions.discrete;

import net.sourceforge.cilib.functions.ContinuousFunction;
import net.sourceforge.cilib.type.types.container.Vector;

/**
 * Implementation of the a function to return the fitness of the Queens problem.
 * The function class only determines the fitness of the solution by determining
 * the number of conflicts and penalises the fitness if there are.
 *
 */
public class NQueens extends ContinuousFunction {

    private static final long serialVersionUID = 8900436160526532438L;
    private final double[] xMoves = {1, 1,  1,  0, -1, -1, -1, 0};
    private final double[] yMoves = {1, 0, -1, -1, -1,  0,  1, 1};
    private int boardSize;

    /**
     * Create a new instance of this function. The default board size is 8.
     */
    public NQueens() {
        this.boardSize = 8;
    }

    /**
     * Create a copy of the provided instance.
     * @param copy The instance to copy.
     */
    public NQueens(NQueens copy) {
        this.boardSize = copy.boardSize;
//        setDomain("B^" + boardSize * boardSize);
    }

    /**
     * Get the matrix and determine if there are any conflicts. If there are no
     * conflicts, then a suitable solution has been found.
     *
     */
    @Override
    public Double f(Vector input) {
        boolean[][] board = initialiseBoard(input);
        
        int conflicts = 0;
        int missingQueens = boardSize - numberOfQueens(board);
        int tn = boardSize - 1;
        int triangleNumber = (tn * (tn + 1)) / 2;
        int columnsTotal = 0;

        for (int col = 0; col < boardSize; col++) {
            for (int row = 0; row < boardSize; row++) {
                boolean isQueen = board[row][col];

                if (isQueen) {
                    columnsTotal += row;
                    
                    for (int move = 0; move < xMoves.length; move++) {
                        conflicts += determineConflicts(move, row, col, board);
                    }
                }
            }
        }
        
        //System.out.print("(" + conflicts + " + " + missingQueens + ") / (|" + triangleNumber + " - " + columnsTotal + "| + 1) = ");

        //double val = (double)(conflicts + missingQueens) / (double)(Math.abs(triangleNumber - columnsTotal) + 1);
        double val = (conflicts + (missingQueens * missingQueens)) * (Math.abs(triangleNumber - columnsTotal) + 1);
        
        //System.out.println(val);
//        if (val == 0) {
//            System.out.print("\n>>>");
//            for (Numeric n : input) {
//                System.out.print(n.intValue());
//            }
//            System.out.println();
//        }
        //System.exit(0);
        return val;
    }

    /**
     * Determine the number of conflicts, based on the current direction that
     * the queen is headed in.
     * @param move The integer determining the move of the queen.
     * @param row The current {@code row}.
     * @param col The current {@code column}.
     * @param board The current game state.
     * @return The value of the number of conflicts.
     */
    private double determineConflicts(int move, int row, int col, boolean[][] board) {
        double conflicts = 0;
        int newRow = row;
        int newCol = col;

        newRow += xMoves[move];
        newCol += yMoves[move];

        while (insideBoard(newRow, newCol)) {
            if (board[newRow][newCol]) {
                conflicts++;
            }

            newRow += xMoves[move];
            newCol += yMoves[move];
        }

        return conflicts;
    }

    /**
     * Initialise the current game state with the provided {@code Vector}.
     *
     * @param x The {@code Vector} to base the initialisation on.
     */
    public boolean[][] initialiseBoard(Vector x) {
        boolean[][] board = new boolean[boardSize][boardSize];
        int bitsPerCol = (int) Math.ceil(Math.log(boardSize)/Math.log(2));
        
//        for (Numeric n : x) {
//            System.out.print(n.intValue());
//        }
//        System.out.println("\nsize: " + boardSize);
        
        for (int col = 0; col < boardSize; col++) {
            int start = col * bitsPerCol;
            int end = start + bitsPerCol;
            
            char[] gray = new char[bitsPerCol];
            char[] bin = new char[bitsPerCol];
            int counter = 0;
            
            //get gray code for current column
            for (int bit = start; bit < end; bit++) {
                gray[counter++] = (x.intValueOf(bit) == 1 ? '1' : '0');
            }
            
            //System.out.println("gc: " + String.valueOf(gray));
            
            //gray to binary
            bin[0] = gray[0];
            for (int i = 1; i < bitsPerCol; i++) {
                bin[i] = (bin[i - 1] == '0' ? gray[i] : (gray[i] == '0' ? '1' : '0'));
            }
            
            //get integer position of queen in current column
            int pos = Integer.parseInt(String.valueOf(bin), 2);
            
            //set queen
            if (pos < boardSize) {
                board[pos][col] = true;
            }
        }
        
//        for (int i = 0; i < boardSize; i++) {
//            for (int j = 0; j < boardSize; j++) {
//                System.out.print((board[i][j] ? "1" : "0"));
//            }
//            System.out.println("");
//        }

        return board;
    }

    /**
     * Get the size of the currently set board.
     * @return Returns the boardSize.
     */
    public int getBoardSize() {
        return boardSize;
    }

    /**
     * Set the size of the board.
     * @param boardSize The boardSize to set.
     */
    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
//        this.setDomain("B^" + boardSize * boardSize);
    }

    /**
     * Determine if the current {@code row} and {@code col} values are within
     * the bounds of the chess board.
     * @param row The row value in the range: {@code [0..boardSize-1]}
     * @param col The column value in the range:  {@code [0..boardSize-1]}
     * @return {@code true} if the position is inside the board, {@code false} otherwise.
     */
    private boolean insideBoard(int row, int col) {
        return (row >= 0 && row < boardSize) && (col >= 0 && col < boardSize);
    }

    /**
     * Obtain the number of queen's that are currently on the chess board.
     * @param board The board to inspect.
     * @return The number of queens.
     */
    public int numberOfQueens(boolean[][] board) {
        int count = 0;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j]) {
                    count++;
                }
            }
        }

        return count;
    }
//
//    @Override
//    public String getDomain() {
//        return "B^" + boardSize * boardSize;
//    }
}
