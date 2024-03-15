package hr.mlinx.chess.validation;

import hr.mlinx.chess.board.Board;
import hr.mlinx.chess.board.Piece;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class PawnValidator {

    private PawnValidator() {}

    public static Set<Point> getValidMoves(int fromRow, int fromCol, Board board) {
        Set<Point> validMoves = new HashSet<>();

        int pieceColor = Piece.getColorFromPiece(board.getPieceAt(fromRow, fromCol));

        int rowIncrement = (pieceColor == Piece.WHITE) ? -1 : 1;

        // 2 moves up from starting position
        if ((fromRow == 6 && pieceColor == Piece.WHITE) || (fromRow == 1 && pieceColor == Piece.BLACK)
                && (board.getPieceAt(fromRow + 2 * rowIncrement, fromCol) == Piece.NONE)) {
                validMoves.add(new Point(fromCol, fromRow + 2 * rowIncrement));
        }

        // 1 move up
        if (board.getPieceAt(fromRow + rowIncrement, fromCol) == Piece.NONE) {
            validMoves.add(new Point(fromCol, fromRow + rowIncrement));
        }

        // Diagonal moves
        int[] colOffsets = {-1, 1};
        for (int colOffset : colOffsets) {
            int targetRow = fromRow + rowIncrement;
            int targetCol = fromCol + colOffset;
            if (targetRow >= 0 && targetRow < 8 && targetCol >= 0 && targetCol < 8) {
                int targetPiece = board.getPieceAt(targetRow, targetCol);
                if (Piece.getColorFromPiece(targetPiece) != pieceColor && targetPiece != Piece.NONE) {
                    validMoves.add(new Point(targetCol, targetRow));
                }
            }
        }

        return validMoves;
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
