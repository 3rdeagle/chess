package chess;

import shared.DataAccessException;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition startPosition;
    private final  ChessPosition endPosition;
    private final ChessPiece.PieceType promotionPiece;

    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPosition; //We return the start position that is first fed into ChessMove and then saved.
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPosition; //We return the end position that is first fed into ChessMove and then saved.
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece; /* Returning the promotion piece info, which I believe is figured out
                                                    somewhere else that it can be promoted */
    }

    public static ChessPosition convertToCoor(String square) {
        char colChar = Character.toLowerCase(square.charAt(0));
        int col = colChar - 'a' + 1;
        int row = Integer.parseInt(square.substring(1));

        return new ChessPosition(row, col);
    }

    public static ChessMove determineMove (String[] args) throws DataAccessException {
        ChessPosition start = convertToCoor(args[0]);
        ChessPosition end = convertToCoor(args[1]);

        ChessPiece.PieceType promo = null;
        if (args.length == 3) {
            switch (args[2].toUpperCase() ) {
                case "Q": promo = ChessPiece.PieceType.QUEEN;
                break;
                case "R": promo = ChessPiece.PieceType.ROOK;
                break;
                case "N": promo = ChessPiece.PieceType.KNIGHT;
                break;
                case "B": promo = ChessPiece.PieceType.BISHOP;
                break;
                default: throw new DataAccessException("Invalid");
            }
        }
        return new ChessMove(start, end, promo);
    }

    @Override
    public String toString() {
        return "ChessMove{" +
                "startPosition=" + startPosition +
                ", endPosition=" + endPosition +
                ", promotionPiece=" + promotionPiece +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessMove chessMove = (ChessMove) o;
        return Objects.equals(startPosition, chessMove.startPosition) &&
                Objects.equals(endPosition, chessMove.endPosition) && promotionPiece == chessMove.promotionPiece;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPosition, endPosition, promotionPiece);
    }
}
