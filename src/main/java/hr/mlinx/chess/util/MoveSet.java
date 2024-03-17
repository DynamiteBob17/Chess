package hr.mlinx.chess.util;

import hr.mlinx.chess.board.Move;
import hr.mlinx.chess.validation.MoveValidation;

import java.util.HashSet;

public class MoveSet<T> extends HashSet<T> {

    @Override
    public boolean add(T e) {
        if (e instanceof Move move) {
            if (!MoveValidation.isValidMovePlacement(move.fromRow, move.fromCol, move.toRow, move.toCol)) {
                return false;
            }

            return super.add(e);
        }

        return false;
    }

}
