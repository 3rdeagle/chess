package chess;


import java.util.Collection;
import java.util.ArrayList;

public class BishopCalc {

    private final ChessBoard board;
    private final ChessPosition position;
    public BishopCalc(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
    }

    public Collection<ChessMove> calcBisMoves() {
        ArrayList<ChessMove> BisMoves = new java.util.ArrayList<>();
        int startCol = position.getColumn(); // starting column
        int startRow = position.getRow(); // starting row
        var startPiece = board.getPiece(new ChessPosition(startRow,startCol)); // Getting the Piece that is stored on the board
        int[][] directions = {{1,1}, {1,-1}, {-1,1}, {-1,-1}}; // potential directions a Bishop can move

        for (int[] dir : directions) {
           int dr = dir[0]; // delta row
           int dc = dir[1]; // delta column
           int row = startRow;
           int col = startCol;
            while (true) { // if no piece in the way or not off the board then its a legal move

                row += dr; // change the row
                col += dc; // change the column have to change both for a Bishop

                if (col < 1 || col > 8 || row < 1 || row > 8) {
                    break;
                }

                ChessPosition newPosition = new ChessPosition(row, col);
                var occupant = board.getPiece(newPosition);
                if (occupant == null) {
                    BisMoves.add(new ChessMove(position, newPosition, null ));
                    System.out.println("Bishop can move to row:" + newPosition.getRow() + ", col:" + newPosition.getColumn());
                    continue;
                }
                if (occupant.getTeamColor() == startPiece.getTeamColor()) {
                    break;  // just break no added move because we can't take our own piece
                }
                if (occupant.getTeamColor() != startPiece.getTeamColor()) {
                    BisMoves.add(new ChessMove(position, newPosition, null));
                    System.out.println("Bishop can move to row:" + newPosition.getRow() + ", col:" + newPosition.getColumn());
                    break; // We can't go any further
                } else {
                    break;
                }

//            if board occupied at spot a few options
//            1. same color can't go
//            2. different color can capture
//            3. if empty, than available move
//            return the collection of Moves possible
            }
        }
        return BisMoves;
    }
}
