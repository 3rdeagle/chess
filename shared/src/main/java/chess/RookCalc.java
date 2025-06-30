package chess;

import java.util.Collection;
import java.util.ArrayList;

public class RookCalc {

    private final ChessBoard board;
    private final ChessPosition position;
    public RookCalc(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
    }

    public Collection<ChessMove> calcRookMoves() {
        ArrayList<ChessMove> RookMoves = new java.util.ArrayList<>();
        int startCol = position.getColumn(); // starting column
        int startRow = position.getRow(); // starting row
        var startPiece = board.getPiece(new ChessPosition(startRow, startCol)); // Getting the Piece that is stored on the board
        int[][] directions = {{1, 0}, {0, -1}, {-1, 0}, {0, 1}}; // potential directions a Rook can move

        for (int[] dir : directions) {
            int dr = dir[0]; // delta row
            int dc = dir[1]; // delta column
            int row = startRow;
            int col = startCol;

            while (true) { // if no piece in the way or not off the board then its a legal move

                row += dr; // change the row
                col += dc; // change the column have to change both for a Bishop

                if (col < 1 || col > 8 || row < 1 || row > 8) { // if it is outside of the boundaries break
                    break;
                }

                ChessPosition newPosition = new ChessPosition(row, col);
                var occupant = board.getPiece(newPosition);              // See what piece is on the new potential spot

                if (occupant == null) {
                    RookMoves.add(new ChessMove(position, newPosition, null));
                    System.out.println("Rook can move to row:" + newPosition.getRow() + ", col:" + newPosition.getColumn());
                }
                else if (occupant.getTeamColor() == startPiece.getTeamColor()) {
                    break;  // just break no added move because we can't take our own piece
                }
                else if (occupant.getTeamColor() != startPiece.getTeamColor()) {
                    RookMoves.add(new ChessMove(position, newPosition, null));
                    System.out.println("Rook can move to row:" + newPosition.getRow() + ", col:" + newPosition.getColumn());
                    break; // We can't go any further
                } else {
                    break;
                }
            }
        }
        return RookMoves;
    }
}