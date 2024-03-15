package hr.mlinx.chess.validation;

import hr.mlinx.chess.board.Board;
import hr.mlinx.chess.board.Piece;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class KingValidator {

    private KingValidator() {
    }

    public static Set<Point> getValidMoves(int fromRow, int fromCol, Board board) {
        Set<Point> validMoves = new HashSet<>();

        int pieceColor = Piece.getColorFromPiece(board.getPieceAt(fromRow, fromCol));

        // King can move 1 square in any direction
        for (int row = -1; row <= 1; row++) {
            for (int col = -1; col <= 1; col++) {
                if (row != 0 || col != 0) {
                    int targetRow = fromRow + row;
                    int targetCol = fromCol + col;
                    int targetPiece = board.getPieceAt(targetRow, targetCol);
                    if (targetPiece == Piece.NONE || Piece.getColorFromPiece(targetPiece) != pieceColor) {
                        validMoves.add(new Point(targetCol, targetRow));
                    }
                }
            }
        }

        return validMoves;
    }

    public static boolean isAttack(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        return (rowDiff <= 1 && colDiff <= 1);
    }

}
