package hr.mlinx.chess.board;

import java.util.Objects;

public class Move {

    public final int fromRow;
    public final int fromCol;
    public final int toRow;
    public final int toCol;

    private final SpecialMove specialMove;

    public Move(int fromRow, int fromCol, int toRow, int toCol, SpecialMove specialMove) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.specialMove = specialMove;
    }

    public Move(int fromRow, int fromCol, int toRow, int toCol) {
        this(fromRow, fromCol, toRow, toCol, null);
    }

    public SpecialMove getSpecialMove() {
        return specialMove;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof Move otherMove))
            return false;

        return this.fromRow == otherMove.fromRow &&
                this.fromCol == otherMove.fromCol &&
                this.toRow == otherMove.toRow &&
                this.toCol == otherMove.toCol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromRow, fromCol, toRow, toCol);
    }

}
