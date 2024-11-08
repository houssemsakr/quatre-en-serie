package com.example.mynewapp;

public class GameBoard {
    private int[][] board;
    private static final int ROWS = 7;
    private static final int COLS = 6;

    public GameBoard() {
        board = new int[ROWS][COLS];
    }

    public boolean makeMove(int playerId, int row, int col) {
        if (board[row][col] == 0) {
            board[row][col] = playerId + 1;
            return checkWin(row, col, playerId + 1);
        }
        return false;
    }

    private boolean checkWin(int row, int col, int playerVal) {
        return checkDirection(row, col, playerVal, 1, 0) ||  // Vertical
                checkDirection(row, col, playerVal, 0, 1) ||  // Horizontal
                checkDirection(row, col, playerVal, 1, 1) ||  // Diagonal \
                checkDirection(row, col, playerVal, 1, -1);   // Diagonal /
    }

    private boolean checkDirection(int row, int col, int playerVal, int rowDir, int colDir) {
        int count = 0;
        for (int i = -3; i <= 3; i++) {
            int r = row + i * rowDir;
            int c = col + i * colDir;
            if (r >= 0 && r < ROWS && c >= 0 && c < COLS && board[r][c] == playerVal) {
                count++;
                if (count == 4) return true;
            } else {
                count = 0;
            }
        }
        return false;
    }

    public int[][] getBoard() {
        return board;
    }
}
