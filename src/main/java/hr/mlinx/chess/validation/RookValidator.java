package hr.mlinx.chess.validation;

import hr.mlinx.chess.board.Board;

import java.awt.*;
import java.util.Set;

public class RookValidator {

    private RookValidator() {}

    public static void addValidMoves(int fromRow, int fromCol, Board board, Set<Point> validMoves) {
        MoveValidation.checkDirection(fromRow, fromCol, -1, 0, board, validMoves); // Check left
        MoveValidation.checkDirection(fromRow, fromCol, 1, 0, board, validMoves); // Check right
        MoveValidation.checkDirection(fromRow, fromCol, 0, -1, board, validMoves); // Check up
        MoveValidation.checkDirection(fromRow, fromCol, 0, 1, board, validMoves); // Check down
    }

}
