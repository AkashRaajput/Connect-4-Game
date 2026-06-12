package com.akash.connectfour;

/**
 * Connect Four game state: 6 rows x 7 columns.
 * Cell values: 0 = empty, 1 = player one, 2 = player two.
 */
public final class ConnectFourBoard {

    public static final int ROWS = 6;
    public static final int COLS = 7;
    public static final int EMPTY = 0;

    private final int[][] cells = new int[ROWS][COLS];

    public int dropDisc(int column, int player) {
        validateColumn(column);
        validatePlayer(player);

        for (int row = ROWS - 1; row >= 0; row--) {
            if (cells[row][column] == EMPTY) {
                cells[row][column] = player;
                return row;
            }
        }
        return -1;
    }

    public boolean isColumnFull(int column) {
        validateColumn(column);
        return cells[0][column] != EMPTY;
    }

    public boolean hasWinningMove(int row, int column, int player) {
        return countInDirection(row, column, player, 1, 0) >= 4
                || countInDirection(row, column, player, 0, 1) >= 4
                || countInDirection(row, column, player, 1, 1) >= 4
                || countInDirection(row, column, player, 1, -1) >= 4;
    }

    public boolean isBoardFull() {
        for (int column = 0; column < COLS; column++) {
            if (!isColumnFull(column)) {
                return false;
            }
        }
        return true;
    }

    public void reset() {
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLS; column++) {
                cells[row][column] = EMPTY;
            }
        }
    }

    private int countInDirection(int row, int column, int player, int rowStep, int columnStep) {
        int count = 1;
        count += countLine(row, column, player, rowStep, columnStep);
        count += countLine(row, column, player, -rowStep, -columnStep);
        return count;
    }

    private int countLine(int row, int column, int player, int rowStep, int columnStep) {
        int count = 0;
        int nextRow = row + rowStep;
        int nextColumn = column + columnStep;

        while (isInsideBoard(nextRow, nextColumn) && cells[nextRow][nextColumn] == player) {
            count++;
            nextRow += rowStep;
            nextColumn += columnStep;
        }
        return count;
    }

    private static boolean isInsideBoard(int row, int column) {
        return row >= 0 && row < ROWS && column >= 0 && column < COLS;
    }

    private static void validateColumn(int column) {
        if (column < 0 || column >= COLS) {
            throw new IllegalArgumentException("Invalid column: " + column);
        }
    }

    private static void validatePlayer(int player) {
        if (player != 1 && player != 2) {
            throw new IllegalArgumentException("Invalid player: " + player);
        }
    }
}
