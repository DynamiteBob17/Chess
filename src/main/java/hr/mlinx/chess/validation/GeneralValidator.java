package hr.mlinx.chess.validation;

import hr.mlinx.chess.board.Board;
import hr.mlinx.chess.board.Move;
import hr.mlinx.chess.board.Piece;

import java.util.Set;

public class GeneralValidator {

    private GeneralValidator() {}

    public static void checkDirection(int fromRow, int fromCol, int rowDir, int colDir, Board board, Set<Move> validMoves) {
        for (int i = fromRow + rowDir, j = fromCol + colDir; i >= 0 && i < 8 && j >= 0 && j < 8; i += rowDir, j += colDir) {
            if (board.getPieceAt(i, j) == Piece.NONE) {
                validMoves.add(new Move(fromRow, fromCol, i, j));
            } else {
                if (Piece.getColorFromPiece(board.getPieceAt(i, j)) != Piece.getColorFromPiece(board.getPieceAt(fromRow, fromCol))) {
                    validMoves.add(new Move(fromRow, fromCol, i, j));
                }
                break;
            }
        }
    }

    enum AttackDirection {
        STRAIGHT, DIAGONAL
    }

    public static boolean isAttack(int fromRow, int fromCol, int toRow, int toCol, Board board, AttackDirection attackDirection) {
        if (attackDirection == AttackDirection.DIAGONAL && Math.abs(fromRow - toRow) != Math.abs(fromCol - toCol)) {
            return false;
        }

        if (attackDirection == AttackDirection.STRAIGHT && fromRow != toRow && fromCol != toCol) {
            return false;
        }

        int rowOffset = Integer.compare(toRow, fromRow);
        int colOffset = Integer.compare(toCol, fromCol);

        int i = fromRow + rowOffset;
        int j = fromCol + colOffset;

        while ((attackDirection == AttackDirection.DIAGONAL && (i != toRow && j != toCol)) ||
                (attackDirection == AttackDirection.STRAIGHT && (i != toRow || j != toCol))
        ) {
            if (board.getPieceAt(i, j) != Piece.NONE) {
                return false; // Obstacle in the path
            }
            i += rowOffset;
            j += colOffset;
        }

        return true;
    }

}
