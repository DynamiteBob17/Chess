package hr.mlinx.chess.validation;

import hr.mlinx.chess.board.Piece;

public class LastMove {

    private LastMove() {}

    public static int color = Piece.BLACK;

    public static void switchMove() {
        color = color == Piece.WHITE
                ? Piece.BLACK
                : Piece.WHITE;
    }

}
