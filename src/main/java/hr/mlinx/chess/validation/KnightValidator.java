package hr.mlinx.chess.validation;

import hr.mlinx.chess.board.Board;
import hr.mlinx.chess.board.Piece;

import java.awt.*;
import java.util.Set;

public class KnightValidator {

    private KnightValidator() {
    }

    public static void addValidMoves(int fromRow, int fromCol, Board board, Set<Point> validMoves) {
        int[][] knightMoves = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};

        for (int[] move : knightMoves) {
            int toRow = fromRow + move[0];
            int toCol = fromCol + move[1];

            int pieceAtDestination = board.getPieceAt(toRow, toCol);

            if (pieceAtDestination == Piece.NONE || Piece.getColorFromPiece(pieceAtDestination) != Piece.getColorFromPiece(board.getPieceAt(fromRow, fromCol))) {
                validMoves.add(new Point(toCol, toRow));
            }
        }
    }

}
