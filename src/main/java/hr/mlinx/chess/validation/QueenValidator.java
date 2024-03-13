package hr.mlinx.chess.validation;

import hr.mlinx.chess.board.Board;

import java.awt.*;
import java.util.Set;

public class QueenValidator {

    private QueenValidator() {}

    public static void addValidMoves(int fromRow, int fromCol, Board board, Set<Point> validMoves) {
        BishopValidator.addValidMoves(fromRow, fromCol, board, validMoves);
        RookValidator.addValidMoves(fromRow, fromCol, board, validMoves);
    }

}
