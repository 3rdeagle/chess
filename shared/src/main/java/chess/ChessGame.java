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
public class ChessGame implements Cloneable{
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

    public void simpleMove(ChessBoard newBoard, ChessMove move) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);

        if (piece == null) {
            return;
        }

        newBoard.addPiece(start, null); // We essentially pick it up and remove it from the board
        if (move.getPromotionPiece() == null) {
            newBoard.addPiece(end, piece); // We then move the piece to the new spot
        } else {
            ChessPiece promotedPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            newBoard.addPiece(end, promotedPiece);
        }

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
            return new ArrayList<>(); // return an empty list
        }
        // get all of the potential moves from pieceMoves calculator
        Collection<ChessMove> potentialMoves = piece.pieceMoves(board, startPosition);
        List<ChessMove> allowedMoves = new ArrayList<>();
        for (ChessMove move : potentialMoves) {
            ChessGame copy = this.clone(); // create the deep copy of the game
                copy.simpleMove(copy.getBoard(), move);

            if (!copy.isInCheck(copy.currentTurn)) { // if the copy isn't in check from that move then add it
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

        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = board.getPiece(start);


        if (piece == null) {
            throw new InvalidMoveException(STR."No piece at start\{start}");
        }
        if (piece.getTeamColor() != currentTurn) {
            throw new InvalidMoveException("Not right colors turn");
        }

        Collection<ChessMove> allowedMoves = validMoves(start);
        if (allowedMoves == null || !allowedMoves.contains(move)) {
            throw new InvalidMoveException(STR."Illegal move bud\{move}");
        }

        board.addPiece(start, null); // We essentially pick it up and remove it from the board

        if (move.getPromotionPiece() == null) {
            board.addPiece(end, piece); // We then move the piece to the new spot
        }  else {
            ChessPiece promotedPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
            board.addPiece(end, promotedPiece);
        }

        if (currentTurn == TeamColor.WHITE) { // if white make it black or vice versa
            currentTurn = TeamColor.BLACK;
        } else {
            currentTurn = TeamColor.WHITE;
        }



        // figure out which team goes
        // sees the valid moves
        // Make the move happen how you might ask. Great question
        // we remove the piece from its current spot
        // we see if theres a piece in the way to capure, if so remove it
        // we then place the piece at the endposition where it was moving to
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

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col ++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING  &&  piece.getTeamColor() == teamColor) {
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
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition enemyPosition = new ChessPosition(row, col);
                ChessPiece enemyPiece = board.getPiece(enemyPosition);

                if (enemyPiece != null && enemyPiece.getTeamColor() == enemyColor) { //Problems with null potentially??
                    Collection<ChessMove> enemyMoves = enemyPiece.pieceMoves(board, enemyPosition);
                    for (ChessMove move : enemyMoves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
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
    public ChessGame clone() { // copying the board to a deep clone,
        try {
            ChessGame cloneGame = (ChessGame) super.clone();

            cloneGame.board = this.board.clone();
            cloneGame.currentTurn = this.currentTurn;
            return cloneGame;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }



}
