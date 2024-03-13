package hr.mlinx.chess.validation;

import hr.mlinx.chess.board.Board;

import java.awt.*;
import java.util.Set;

public class BishopValidator {

    private BishopValidator() {}

    public static void addValidMoves(int fromRow, int fromCol, Board board, Set<Point> validMoves) {
        MoveValidation.checkDirection(fromRow, fromCol, -1, -1, board, validMoves); // Check left-up
        MoveValidation.checkDirection(fromRow, fromCol, -1, 1, board, validMoves); // Check left-down
        MoveValidation.checkDirection(fromRow, fromCol, 1, -1, board, validMoves); // Check right-up
        MoveValidation.checkDirection(fromRow, fromCol, 1, 1, board, validMoves); // Check right-down
    }

}
