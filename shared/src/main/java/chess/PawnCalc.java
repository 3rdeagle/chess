package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnCalc {

    private final ChessBoard board;
    private final ChessPosition position;

    public PawnCalc(ChessBoard board, ChessPosition position) {
        this.board = board;
        this.position = position;
    }

    public boolean inbounds(int row, int col) {
        if (col < 1 || col > 8 || row < 1 || row > 8) {
            return false;
        } else {
            return true;
        }
    }

    public void newMove(int row, ChessPosition newPosition, int promoRow, ArrayList<ChessMove> PawnMoves) {
        ChessPiece.PieceType[] promotions = new ChessPiece.PieceType[]{ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK};

        if (row == promoRow){
            for (var promo : promotions) {
                PawnMoves.add(new ChessMove(position, newPosition, promo));
                System.out.println("Pawn can move to row:" + newPosition.getRow() + ", col:" + newPosition.getColumn() + " and be promoted");
            }
        } else {
            PawnMoves.add(new ChessMove(position, newPosition, null));
            System.out.println("Pawn can move to row:" + newPosition.getRow() + ", col:" + newPosition.getColumn());

        }
    }

    public Collection<ChessMove> calcPawnMoves() {
        ArrayList<ChessMove> PawnMoves = new ArrayList<>();
        int startCol = position.getColumn(); // starting column
        int startRow = position.getRow(); // starting row
        var startPiece = board.getPiece(new ChessPosition(startRow, startCol)); // Getting the Piece that is stored on the board
        // if the color is Black and it is on row 7 it can do all 4 directions, but only diagonally on capture
        // if the color is White and it is on row 2 it can do all 4 directions, but only diagonally on capture

        int[] forward;
        int[] twoForward;
        int startSetup;
        int[][] captures;
        int promoRow;


        // Check the color and initiate potential directions
        if (startPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            forward = new int[] {1, 0};
            twoForward = new int[] {2, 0};
            startSetup = 2;
            captures = new int[][] {{1, 1}, {1, -1}};
            promoRow = 8;
        } else {
            forward = new int[] {-1, 0};
            twoForward = new int[] {-2, 0};
            startSetup = 7;
            captures = new int[][] {{-1, 1}, {-1, -1}};
            promoRow = 1;
        }

        //Check 1 step forward
        int dr = forward[0];
        int dc = forward[1];
        int dr2 = twoForward[0];
        int dc2 = twoForward[1];
        int newRow = startRow + dr;
        int newCol = startCol + dc;

        ChessPosition newPosition = new ChessPosition(newRow, newCol);
        var occupant = board.getPiece(newPosition);              // See what piece is on the new potential spot

        if (inbounds(newRow, newCol) && (occupant == null)) {
            newMove(newRow, newPosition, promoRow, PawnMoves);
//            PawnMoves.add(new ChessMove(position, newPosition, null));  // add the one step to it
//            System.out.println("Pawn can move to row:" + newPosition.getRow() + ", col:" + newPosition.getColumn());
            // Check for 2nd step if at setup position
            if (startRow == startSetup) {
                int twoRow = startRow + dr2;
                int twoCol = startCol + dc2;

                ChessPosition newPositionTwo = new ChessPosition(twoRow, twoCol);
                var occupantTwo = board.getPiece(newPositionTwo);              // See what piece is on the new potential spot

                if (occupantTwo == null) { // shouldn't need to check inbounds as we can only do a 2 move from our initial setup position
                    PawnMoves.add(new ChessMove(position, newPositionTwo, null)); // never need to check for promotion only happens at start
                    System.out.println("Pawn can move to row:" + newPositionTwo.getRow() + ", col:" + newPositionTwo.getColumn());
                }
            }
        }

        for (int[] capture : captures) { // can only capture in the two direct diagonals so we just check both
            int drCapture = capture[0];
            int dcCapture = capture[1];
            int captureRow = startRow + drCapture;
            int captureCol = startCol + dcCapture;
            if (inbounds(captureRow, captureCol)) {

                ChessPosition newPositionCapture = new ChessPosition(captureRow, captureCol);
                var occupantCapture = board.getPiece(newPositionCapture);              // See what piece is on the new potential spot

                if (occupantCapture != null && (occupantCapture.getTeamColor() != startPiece.getTeamColor())){
//                    PawnMoves.add(new ChessMove(position, newPositionCapture, null));
//                    System.out.println("Pawn can capture to row:" + newPositionCapture.getRow() + ", col:" + newPositionCapture.getColumn());
                    System.out.println("Pawn can capture");
                    newMove(captureRow, newPositionCapture, promoRow, PawnMoves);
                }
            }
        }
        return PawnMoves;
    }


}
