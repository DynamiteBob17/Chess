package hr.mlinx.chess.validation;

import hr.mlinx.chess.board.Board;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class RookValidator {

    private RookValidator() {}

    public static Set<Point> getValidMoves(int fromRow, int fromCol, Board board) {
        Set<Point> validMoves = new HashSet<>();

        GeneralValidator.checkDirection(fromRow, fromCol, -1, 0, board, validMoves); // Check left
        GeneralValidator.checkDirection(fromRow, fromCol, 1, 0, board, validMoves); // Check right
        GeneralValidator.checkDirection(fromRow, fromCol, 0, -1, board, validMoves); // Check up
        GeneralValidator.checkDirection(fromRow, fromCol, 0, 1, board, validMoves); // Check down

        return validMoves;
    }

    public static boolean isAttack(int fromRow, int fromCol, int toRow, int toCol, Board board) {
        return GeneralValidator.isAttack(fromRow, fromCol, toRow, toCol, board, GeneralValidator.AttackDirection.STRAIGHT);
    }

}
