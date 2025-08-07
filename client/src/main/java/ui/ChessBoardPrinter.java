package ui;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class ChessBoardPrinter {

    private static char[][] toArray(ChessBoard board)    {
        char[][] array = new char[8][8];
        for (int row = 1; row <=8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                array[8-row][col -1] = (piece != null) ? piece.getPieceCharacter() : ' ';
            }
        }
        return array;
    }

    public static void main() {
        ChessBoard board = new ChessBoard();
        System.out.println();
        print(board, "black", Set.of());
    }

    public static void print(ChessBoard board, String playerColor, Set<ChessPosition> highlights) {
//        board.resetBoard();
        System.out.println();
        if ("black".equalsIgnoreCase(playerColor)) {
            printBlackSideBoard(toArray(board), highlights);
        } else {
            printWhiteSideBoard(toArray(board), highlights);
        }
    }

    public static void print(ChessBoard board, String playerColor) {
        print(board, playerColor, Collections.emptySet());
    }

    public static void printWhiteSideBoard(char[][] board, Set<ChessPosition> highlights) {
        System.out.print("\u001B[104m\u001B[30m   \u001B[0m");
        for (char fileChar = 'a'; fileChar <= 'h'; fileChar++) {
            System.out.print("\u001B[104m\u001B[30m " + fileChar + " \u001B[0m");
        }

            System.out.print("\u001B[104m\u001B[30m   \u001B[0m");
            System.out.println();

            for (int rank = 0; rank < 8; rank++) {
                System.out.print("\u001B[104m\u001B[30m " + (8 - rank) + " \u001B[0m");

                for (int file = 0; file < 8; file++) {
                    printHelper(board, rank, file, highlights );
                }

                System.out.println("\u001B[104m\u001B[30m " + (8 - rank) + " \u001B[0m");
            }

            System.out.print("\u001B[104m\u001B[30m   \u001B[0m");
            for (char fileChar = 'a'; fileChar <= 'h'; fileChar++) {
                System.out.print("\u001B[104m\u001B[30m " + fileChar + " \u001B[0m");
            }

            System.out.print("\u001B[104m\u001B[30m   \u001B[0m");
            System.out.println();
        }


    public static void printBlackSideBoard(char[][] board, Set<ChessPosition> highlights) {

            System.out.print("\u001B[102m\u001B[30m   \u001B[0m");

            for (char fileChar = 'h'; fileChar >= 'a'; fileChar--) {
                System.out.print("\u001B[102m\u001B[30m " + fileChar + " \u001B[0m");
            }

            System.out.print("\u001B[102m\u001B[30m   \u001B[0m");
            System.out.println();

            for (int rank = 7; rank >= 0; rank--) {
                int label = 1 + (7 - rank);

                System.out.print("\u001B[102m\u001B[30m " + label + " \u001B[0m");


                for (int file = 7; file >= 0; file--) {

                    printHelper(board, rank, file, highlights);
                }

                System.out.println("\u001B[102m\u001B[30m " + label + " \u001B[0m");
            }

            System.out.print("\u001B[102m\u001B[30m   \u001B[0m");
            for (char fileChar = 'h'; fileChar >= 'a'; fileChar--) {
                System.out.print("\u001B[102m\u001B[30m " + fileChar + " \u001B[0m");
            }
            System.out.print("\u001B[102m\u001B[30m   \u001B[0m");
            System.out.println();
        }

    private static void printHelper(char[][] board, int rank, int file, Set<ChessPosition> highlights) {
        ChessPosition checkPosition = new ChessPosition(8 - rank, file + 1);
        String bg;
        if (highlights.contains(checkPosition)) {
            bg = "\u001b[43m";
        } else {
            boolean lightSquare = (rank + file) % 2 == 0;
            bg = lightSquare ? "\u001b[107m" : "\u001b[40m";
        }

        char piece = board[rank][file];
        char displayChar;
        if (piece == ' ') {
            displayChar = ' ';
        } else {
            displayChar = Character.toUpperCase(piece);
        }
        String fgColor;
        if (Character.isUpperCase(piece)) {
            fgColor = "\u001b[31m";
        } else if (Character.isLowerCase(piece)) {
            fgColor = "\u001b[32m";
        } else {
            fgColor = "\u001b[37m";
        }
        System.out.print(bg + fgColor + " " + displayChar + " " + "\u001B[0m");
    }
    }
