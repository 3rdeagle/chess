package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingCalc {

    private final ChessBoard board;
    private final ChessPosition position;

    public KingCalc(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
    }

    public Collection<ChessMove> calcKingMoves() {
        ArrayList<ChessMove> KingMoves = new java.util.ArrayList<>();
        int startCol = position.getColumn(); // starting column
        int startRow = position.getRow(); // starting row
        var startPiece = board.getPiece(new ChessPosition(startRow, startCol)); // Getting the Piece that is stored on the board
        int[][] directions = {{1, 0}, {0, -1}, {-1, 0}, {0, 1}, {1,1}, {1,-1}, {-1,1}, {-1,-1}}; // potential directions a King can move

        for (int[] dir : directions) {
            int dr = dir[0]; // delta row
            int dc = dir[1]; // delta column
            int row = startRow;
            int col = startCol;

            row += dr; // change the row
            col += dc; // change the column have to change both for a King

            if (col < 1 || col > 8 || row < 1 || row > 8) { // if it is outside of the boundaries break
                continue; // continues on to the next direction
            }

            ChessPosition newPosition = new ChessPosition(row, col);
            var occupant = board.getPiece(newPosition);              // See what piece is on the new potential spot

            if (occupant == null) {
                KingMoves.add(new ChessMove(position, newPosition, null));
                System.out.println("King can move to row:" + newPosition.getRow() + ", col:" + newPosition.getColumn());
            }
            else if (occupant.getTeamColor() == startPiece.getTeamColor()) {
                continue;  // just break no added move because we can't take our own piece
            }
            else if (occupant.getTeamColor() != startPiece.getTeamColor()) {
                KingMoves.add(new ChessMove(position, newPosition, null));
                System.out.println("King can move to row:" + newPosition.getRow() + ", col:" + newPosition.getColumn());
            }
        }
        return KingMoves;
    }
}