package hr.mlinx.chess.validation;

import hr.mlinx.chess.board.Board;
import hr.mlinx.chess.board.Piece;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class KnightValidator {

    private KnightValidator() {
    }

    public static Set<Point> getValidMoves(int fromRow, int fromCol, Board board) {
        Set<Point> validMoves = new HashSet<>();

        int[][] knightMoves = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};

        for (int[] move : knightMoves) {
            int toRow = fromRow + move[0];
            int toCol = fromCol + move[1];

            int pieceAtDestination = board.getPieceAt(toRow, toCol);

            if (pieceAtDestination == Piece.NONE || Piece.getColorFromPiece(pieceAtDestination) != Piece.getColorFromPiece(board.getPieceAt(fromRow, fromCol))) {
                validMoves.add(new Point(toCol, toRow));
            }
        }

        return validMoves;
    }

    public static boolean isAttack(int fromRow, int fromCol, int toRow, int toCol, Board board) {
        int[][] offsets = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};
        int toPiece = board.getPieceAt(toRow, toCol);

        for (int[] offset : offsets) {
            int movePiece = board.getPieceAt(fromRow + offset[0], fromCol + offset[1]);
            if (toPiece == movePiece) {
                return true;
            }
        }
        return false;
    }

}
