package hr.mlinx.chess.board;

public class Piece {

    private Piece() {}

    public static final int NONE = 0;
    public static final int PAWN = 1;
    public static final int KNIGHT = 2;
    public static final int BISHOP = 3;
    public static final int ROOK = 4;
    public static final int QUEEN = 5;
    public static final int KING = 6;
    public static final int WHITE = 8;
    public static final int BLACK = 16;

    public static int getPieceTypeByFile(String filePieceType) {
        return switch (filePieceType) {
            case "p" -> PAWN;
            case "n" -> KNIGHT;
            case "b" -> BISHOP;
            case "r" -> ROOK;
            case "q" -> QUEEN;
            case "k" -> KING;
            default -> NONE;
        };
    }

    public static int getPieceColorByFile(String filePieceColor) {
        return filePieceColor.equals("l") ? WHITE : BLACK;
    }

    public static int getTypeFromPiece(int piece) {
        return piece & 7;
    }

    public static int getColorFromPiece(int piece) {
        if ((piece & WHITE) == WHITE) {
            return WHITE;
        } else if ((piece & BLACK) == BLACK) {
            return BLACK;
        } else {
            return -1;
        }
    }

}
