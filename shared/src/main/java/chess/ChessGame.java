package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor currentTurn;
    public ChessGame() {
        this.currentTurn = TeamColor.WHITE;
        this.board  = new ChessBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
            this.currentTurn = team;
        }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = getBoard().getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        // get all of the potential moves from pieceMoves calculator
        Collection<ChessMove> potentialMoves = piece.pieceMoves(board, startPosition);
        List<ChessMove> allowedMoves = new ArrayList<>();
        for (ChessMove move : potentialMoves) {
            ChessBoard copy = getBoard().clone(); // create the copy
            copy.makeMove(move); // make a move in the copy for each potential move we have

            if (!copy.isInCheck(currentTurn)) { // if the copy isn't in check from that move then add it
                allowedMoves.add(move);
            }
        }

        return allowedMoves;
        // We then say look is the move gonna put us in check, so somehow do a copy of the board and check all possilb emoves
        // the moves that do put us in check throw them out, and the ones that don't, keep.
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        // figure out which team goes
        // sees the valid moves
        // Make the move happen how you might ask. Great question
        // we remove the piece from its current spot
        // we see if theres a piece in the way to capure, if so remove it
        // we then place the piece at the endposition where it was moving to

        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) { // so how do we know if we are in check???
        /* We need to know what the kings position is
        then if we know where the opponent team pieces are,
        we could check all of their potential moves,

        if any of those moves attack the king then we are in check, if not we free to run
         */

        ChessBoard board = getBoard(); // get the board
        ChessPosition kingPosition = null; // set a position we can fill once we find our teams king

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col ++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece.getPieceType() == ChessPiece.PieceType.KING  &&  piece.getTeamColor() == teamColor) {
                    kingPosition = position;
                    break;
                }
            }
        }

        TeamColor enemyColor;
        if (teamColor == TeamColor.WHITE) { // if our color is White the the enemy color is black or vice versa
            enemyColor = TeamColor.BLACK;
        } else {
            enemyColor = TeamColor.WHITE;
        }

        // Check for all the enemy pieces and see what possible moves they got
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                ChessPosition enemyPosition = new ChessPosition(row, col);
                ChessPiece enemyPiece = board.getPiece(enemyPosition);
                if (enemyPiece.getTeamColor() == enemyColor) { //Problems with null potentially??
                    Collection<ChessMove> moves = enemyPiece.pieceMoves(board, enemyPosition);
                    for (ChessMove move : moves) {
                        return move.getEndPosition() == kingPosition;
                    }
                }
            }
        }


    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public String toString() {
        return "ChessGame{}";
    }


}
