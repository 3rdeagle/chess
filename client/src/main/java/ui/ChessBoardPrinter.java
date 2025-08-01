package ui;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Objects;

public class ChessBoardPrinter {

//    private static char[][] toArray(ChessBoard board)    {
//        char[][] array = new char[8][8];
//        for (int row = 1; row <=8; row++) {
//            for (int col = 1; col <= 8; col++) {
//                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
//                array[8-row][col -1] = (piece != null) ? piece.get() : '·';
//            }
//        }
//        return array;
//    }


    private static final char[][] STARTING_POSITION = {
            // a    b    c    d    e    f    g    h
            {'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r'},  // rank 8
            {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p'},  // rank 7
            {'·', '·', '·', '·', '·', '·', '·', '·'},  // 6
            {'·', '·', '·', '·', '·', '·', '·', '·'},  // 5
            {'·', '·', '·', '·', '·', '·', '·', '·'},  // 4
            {'·', '·', '·', '·', '·', '·', '·', '·'},  // 3
            {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P'},  // rank 2
            {'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'}   // rank 1
    };

    public static void main() {
        printWhiteSideBoard(STARTING_POSITION);
        System.out.println();
        printBlackSideBoard(STARTING_POSITION);
    }

//    public void print(ChessBoard board, String playerColor) {
//
//        if ("white".equalsIgnoreCase(playerColor)) {
//            printWhiteSideBoard(toArray(board));
//        } else {
//            printBlackSideBoard(toArray(board));
//        }
//    }

    public static void printWhiteSideBoard(char[][] board) {
        System.out.print("\u001B[47m\u001B[30m   \u001B[0m");
        for (char fileChar = 'a'; fileChar <= 'h'; fileChar++) {
            System.out.print("\u001B[47m\u001B[30m " + fileChar + " \u001B[0m");
        }

            System.out.print("\u001B[47m\u001B[30m   \u001B[0m");
            System.out.println();

            for (int rank = 0; rank < 8; rank++) {
                System.out.print("\u001B[47m\u001B[30m " + (8 - rank) + " \u001B[0m");

                for (int file = 0; file < 8; file++) {
                    printHelper(board, rank, file);
                }

                System.out.println("\u001B[47m\u001B[30m " + (8 - rank) + " \u001B[0m");
            }

            System.out.print("\u001B[47m\u001B[30m   \u001B[0m");
            for (char fileChar = 'a'; fileChar <= 'h'; fileChar++) {
                System.out.print("\u001B[47m\u001B[30m " + fileChar + " \u001B[0m");
            }

            System.out.print("\u001B[47m\u001B[30m   \u001B[0m");
            System.out.println();
        }

    private static void printHelper(char[][] board, int rank, int file) {
        boolean lightSquare = (rank + file) % 2 == 0;
        String bg = lightSquare ? "\u001B[107m" : "\u001B[40m";    // bright white or black bg
        char piece = board[rank][file];
        char displayChar;
        if (piece == '·') {
            displayChar = ' ';
        } else {
            displayChar = Character.toUpperCase(piece);
        }
        String fgColor;
        if (Character.isUpperCase(piece)) {
            fgColor = "\u001B[31m";             // red text
        } else if (Character.isLowerCase(piece)) {
            fgColor = "\u001B[34m";             // blue text
        } else {
            fgColor = "\u001B[37m";             // light gray for empty
        }
        System.out.print(bg + fgColor + " " + displayChar + " " + "\u001B[0m");
    }

    public static void printBlackSideBoard(char[][] board) {

            System.out.print("\u001B[47m\u001B[30m   \u001B[0m");

            for (char fileChar = 'h'; fileChar >= 'a'; fileChar--) {
                System.out.print("\u001B[47m\u001B[30m " + fileChar + " \u001B[0m");
            }

            System.out.print("\u001B[47m\u001B[30m   \u001B[0m");
            System.out.println();

            for (int rank = 7; rank >= 0; rank--) {
                int label = 1 + (7 - rank);

                System.out.print("\u001B[47m\u001B[30m " + label + " \u001B[0m");


                for (int file = 7; file >= 0; file--) {

                    printHelper(board, rank, file);
                }

                System.out.println("\u001B[47m\u001B[30m " + label + " \u001B[0m");
            }

            System.out.print("\u001B[47m\u001B[30m   \u001B[0m");
            for (char fileChar = 'h'; fileChar >= 'a'; fileChar--) {
                System.out.print("\u001B[47m\u001B[30m " + fileChar + " \u001B[0m");
            }
            System.out.print("\u001B[47m\u001B[30m   \u001B[0m");
            System.out.println();
        }


    }
