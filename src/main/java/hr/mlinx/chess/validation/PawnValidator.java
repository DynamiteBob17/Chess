package hr.mlinx.chess.validation;

import hr.mlinx.chess.board.Board;
import hr.mlinx.chess.board.Piece;

import java.awt.*;
import java.util.Set;

public class PawnValidator {

    private PawnValidator() {}

    public static void addValidMoves(int fromRow, int fromCol, Board board, Set<Point> validMoves) {
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
    }

}
