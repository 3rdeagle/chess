package chess;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;

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
        if (type == ChessPiece.PieceType.ROOK) {
            RookCalc rookCalcMoves = new RookCalc(board, position);
            return rookCalcMoves.calcRookMoves();
        }
        if (type == ChessPiece.PieceType.QUEEN) {
            QueenCalc queenCalcMoves = new QueenCalc(board, position);
            return queenCalcMoves.calcQueenMoves();
        }
        if (type == ChessPiece.PieceType.KING) {
            KingCalc kingCalcMoves = new KingCalc(board, position);
            return kingCalcMoves.calcKingMoves();
        }
        if (type == ChessPiece.PieceType.KNIGHT) {
            KnightCalc knightCalcMoves = new KnightCalc(board, position);
            return knightCalcMoves.calcKnightMoves();
        }
        if (type == ChessPiece.PieceType.PAWN) {
            PawnCalc pawnCalcMoves = new PawnCalc(board,position);
            return pawnCalcMoves.calcPawnMoves();
        }



    return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PieceCalc pieceCalc = (PieceCalc) o;
        return Objects.equals(position, pieceCalc.position);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(position);
    }

    @Override
    public String toString() {
        return "PieceCalc{" +
                "position=" + position +
                '}';
    }
}
