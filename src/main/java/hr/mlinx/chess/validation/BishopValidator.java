package hr.mlinx.chess.validation;

import hr.mlinx.chess.board.Board;
import hr.mlinx.chess.board.Move;
import hr.mlinx.chess.util.MoveSet;

import java.util.Set;

public class BishopValidator {

    private BishopValidator() {}

    public static Set<Move> getValidMoves(int fromRow, int fromCol, Board board) {
        Set<Move> validMoves = new MoveSet<>();

        GeneralValidator.checkDirection(fromRow, fromCol, -1, -1, board, validMoves); // Check left-up
        GeneralValidator.checkDirection(fromRow, fromCol, -1, 1, board, validMoves); // Check left-down
        GeneralValidator.checkDirection(fromRow, fromCol, 1, -1, board, validMoves); // Check right-up
        GeneralValidator.checkDirection(fromRow, fromCol, 1, 1, board, validMoves); // Check right-down

        return validMoves;
    }

    public static boolean isAttack(int fromRow, int fromCol, int toRow, int toCol, Board board) {
        return GeneralValidator.isAttack(fromRow, fromCol,toRow,toCol, board, GeneralValidator.AttackDirection.DIAGONAL);
    }

}
