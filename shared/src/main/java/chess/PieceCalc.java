package chess;

import java.util.Collection;
import java.util.ArrayList;

public class PieceCalc {

    private final ChessPosition position;

    public PieceCalc(ChessBoard board, ChessPosition position) {
        this.position = position;
    }

    public Collection<ChessMove> calculateMoves(ChessBoard board) {

        ChessPiece pieceAtSpot = board.getPiece(position);
        var type = pieceAtSpot.getPieceType();

        if (type == ChessPiece.PieceType.BISHOP) {
            BishopCalc bishopCalcMoves = new BishopCalc(board, position);
            return bishopCalcMoves.calcBisMoves();
        }
    return null;
    }

}
