package hr.mlinx.chess.validation;

import hr.mlinx.chess.board.*;
import hr.mlinx.chess.util.MoveSet;

import java.util.Set;

public class PawnValidator {

    private PawnValidator() {
    }

    public static Set<Move> getValidMoves(int fromRow, int fromCol, Board board) {
        Set<Move> validMoves = new MoveSet<>();

        int pieceColor = Piece.getColorFromPiece(board.getPieceAt(fromRow, fromCol));

        int rowIncrement = (pieceColor == Piece.WHITE) ? -1 : 1;

        addTwoMovesUpIfValid(fromRow, fromCol, board, pieceColor, rowIncrement, validMoves);
        addOneMoveUpIfValid(fromRow, fromCol, board, rowIncrement, validMoves);
        addDiagonalMovesIfValid(fromRow, fromCol, board, pieceColor, rowIncrement, validMoves);
        addEnPassantMoveIfValid(fromRow, fromCol, board, rowIncrement, validMoves);

        return validMoves;
    }

    private static void addTwoMovesUpIfValid(int fromRow, int fromCol, Board board, int pieceColor, int rowIncrement, Set<Move> validMoves) {
        if ((fromRow == 6 && pieceColor == Piece.WHITE) || (fromRow == 1 && pieceColor == Piece.BLACK)
                && (board.getPieceAt(fromRow + 2 * rowIncrement, fromCol) == Piece.NONE)) {
            validMoves.add(new Move(fromRow, fromCol, fromRow + rowIncrement * 2, fromCol));
        }
    }

    private static void addOneMoveUpIfValid(int fromRow, int fromCol, Board board, int rowIncrement, Set<Move> validMoves) {
        int toRow = fromRow + rowIncrement;
        if (board.getPieceAt(toRow, fromCol) == Piece.NONE) {
            validMoves.add(new Move(
                    fromRow, fromCol, toRow, fromCol,
                    (toRow == 0 || toRow == 7) ? SpecialMove.PAWN_PROMOTION : null
                    // Check if the pawn reached the end of the board and mark as promotion
            ));
        }
    }

    private static void addDiagonalMovesIfValid(int fromRow, int fromCol, Board board, int pieceColor, int rowIncrement, Set<Move> validMoves) {
        int[] colOffsets = {-1, 1};
        for (int colOffset : colOffsets) {
            int targetRow = fromRow + rowIncrement;
            int targetCol = fromCol + colOffset;
            if (targetRow >= 0 && targetRow < 8 && targetCol >= 0 && targetCol < 8) {
                int targetPiece = board.getPieceAt(targetRow, targetCol);
                if (Piece.getColorFromPiece(targetPiece) != pieceColor && targetPiece != Piece.NONE) {
                    validMoves.add(
                            new Move(
                                    fromRow, fromCol, targetRow, targetCol,
                                    (targetRow == 0 || targetRow == 7) ? SpecialMove.PAWN_PROMOTION : null
                                    // Check if the pawn reached the end of the board and mark as promotion
                            ));
                }
            }
        }
    }

    private static void addEnPassantMoveIfValid(int fromRow, int fromCol, Board board, int rowIncrement, Set<Move> validMoves) {
        LastMove lastMove = board.getLastMove();

        if (lastMove.getMove() == null) {
            return;
        }

        if (Piece.getTypeFromPiece(board.getPieceAt(lastMove.getMove().toRow, lastMove.getMove().toCol)) == Piece.PAWN
                && Math.abs(lastMove.getMove().fromRow - lastMove.getMove().toRow) == 2 // opponent pawn moved two squares
                && lastMove.getMove().toRow == fromRow // opponent pawn is on the same rank
                && Math.abs(lastMove.getMove().toCol - fromCol) == 1) { // opponent pawn is on an adjacent file
            // capture is made on the square the pawn moved over
            validMoves.add(new Move(fromRow, fromCol, fromRow + rowIncrement, lastMove.getMove().toCol, SpecialMove.EN_PASSANT));
        }
    }


    public static boolean isAttack(int fromRow, int fromCol, int toRow, int toCol, Board board) {
        int color = Piece.getColorFromPiece(board.getPieceAt(fromRow, fromCol));
        boolean isWhite = color == Piece.WHITE;

        // Check for normal pawn attack (diagonals)
        if (isWhite) {
            return (toRow == fromRow - 1 && Math.abs(toCol - fromCol) == 1);
        } else {
            return (toRow == fromRow + 1 && Math.abs(toCol - fromCol) == 1);
        }
    }

}
