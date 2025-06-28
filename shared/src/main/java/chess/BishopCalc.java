package chess;


import java.util.Collection;

public class BishopCalc {

    private final ChessBoard board;
    private final ChessPosition position;
    public BishopCalc(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
    }

    public Collection<ChessMove> calcBisMoves() {
        Collection<ChessMove> BisMoves;
        int startCol = position.getColumn();
        int startRow = position.getRow();

        int[][] directions = {{1,1}, {1,-1}, {-1,1}, {-1,-1}};
        for (int[] dir : directions) {
            int dr = dir[0];
            int dc = dir[1];
            int newRow = dr + startRow; // change the row
            int newCol = dc + startCol; // change the column have to change both for a Bishop
//            if not (inbounds)
//                false;
//            if board occupied at spot a few options
//            1. same color can't go
//            2. different color can capture
//            3. if empty than available move
//            return the collection of Moves possible


        }
    }

}
