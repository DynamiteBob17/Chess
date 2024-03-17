package hr.mlinx.chess.validation;

import hr.mlinx.chess.board.Board;
import hr.mlinx.chess.board.Move;
import hr.mlinx.chess.board.Piece;
import hr.mlinx.chess.util.MoveSet;

import java.util.Set;

public class KnightValidator {

    private KnightValidator() {
    }

    public static Set<Move> getValidMoves(int fromRow, int fromCol, Board board) {
        Set<Move> validMoves = new MoveSet<>();

        int[][] knightMoves = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};

        for (int[] move : knightMoves) {
            int toRow = fromRow + move[0];
            int toCol = fromCol + move[1];

            int pieceAtDestination = board.getPieceAt(toRow, toCol);

            if (pieceAtDestination == Piece.NONE || Piece.getColorFromPiece(pieceAtDestination) != Piece.getColorFromPiece(board.getPieceAt(fromRow, fromCol))) {
                validMoves.add(new Move(fromRow, fromCol, toRow, toCol));
            }
        }

        return validMoves;
    }

    public static boolean isAttack(int fromRow, int fromCol, int toRow, int toCol) {
        int[][] offsets = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};

        for (int[] offset : offsets) {
            int moveRow = fromRow + offset[0];
            int moveCol = fromCol + offset[1];
            if (moveRow == toRow && moveCol == toCol) {
                return true;
            }
        }

        return false;
    }

}
