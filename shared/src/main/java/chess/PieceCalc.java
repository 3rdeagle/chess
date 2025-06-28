package chess;

import java.util.Collection;

public class PieceCalc {

    private final ChessPiece.PieceType type;
    private final ChessPosition position;

    public PieceCalc(ChessPiece.PieceType type, ChessPosition position) {
        this.type = type;
        this.position = position;
    }
    public Collection<ChessMove> calculateMoves(ChessBoard board) {
        if (type == ChessPiece.PieceType.BISHOP) {
            BishopCalc bishopCalc = new BishopCalc(board, position);
//            return bishopCalc.();
        }

    }

}
