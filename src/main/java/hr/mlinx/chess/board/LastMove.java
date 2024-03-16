package hr.mlinx.chess.board;

public class LastMove {

    private Move move;

    private int color;

    public LastMove() {
        color = Piece.BLACK;
    }

    public void set(Move move) {
        this.move = move;
        switchMove();
    }

    public Move getMove() {
        return move;
    }

    public int getColor() {
        return color;
    }

    private void switchMove() {
        color = color == Piece.WHITE
                ? Piece.BLACK
                : Piece.WHITE;
    }

}
