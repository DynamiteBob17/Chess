package hr.mlinx.chess.validation;

import hr.mlinx.chess.board.Board;

import java.awt.*;
import java.util.Set;

public class QueenValidator {

    private QueenValidator() {
    }

    public static Set<Point> getValidMoves(int fromRow, int fromCol, Board board) {
        Set<Point> validMoves = BishopValidator.getValidMoves(fromRow, fromCol, board);
        validMoves.addAll(RookValidator.getValidMoves(fromRow, fromCol, board));

        return validMoves;
    }

    public static boolean isAttack(int fromRow, int fromCol, int toRow, int toCol, Board board) {
        return RookValidator.isAttack(fromRow, fromCol, toRow, toCol, board) ||
                BishopValidator.isAttack(fromRow, fromCol, toRow, toCol, board);
    }

}
