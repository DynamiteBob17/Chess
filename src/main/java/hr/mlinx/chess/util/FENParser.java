package hr.mlinx.chess.util;

import hr.mlinx.chess.board.Piece;

public class FENParser {

    private FENParser() {}

    public static int[][] parseFEN(String fen) {
        int[][] board = new int[8][8];
        String[] parts = fen.split(" ");

        int row = 0;
        int col = 0;
        for (char c : parts[0].toCharArray()) {
            if (Character.isDigit(c)) {
                col += Character.getNumericValue(c);
            } else if (c == '/') {
                row++;
                col = 0;
            } else {
                int pieceType = getPieceTypeFromChar(c);
                int pieceColor = getPieceColorFromChar(c);
                board[row][col] = pieceType | pieceColor;
                col++;
            }
        }

        // ... (Remaining FEN parsing: active color, castling rights, en passant, etc.)
        return board;
    }

    private static int getPieceTypeFromChar(char c) {
        return switch (Character.toUpperCase(c)) {
            case 'P' -> Piece.PAWN;
            case 'N' -> Piece.KNIGHT;
            case 'B' -> Piece.BISHOP;
            case 'R' -> Piece.ROOK;
            case 'Q' -> Piece.QUEEN;
            case 'K' -> Piece.KING;
            default -> Piece.NONE;
        };
    }

    private static int getPieceColorFromChar(char c) {
        return Character.isUpperCase(c) ? Piece.WHITE : Piece.BLACK;
    }

}
