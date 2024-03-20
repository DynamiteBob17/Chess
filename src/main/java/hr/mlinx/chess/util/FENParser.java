package hr.mlinx.chess.util;

import hr.mlinx.chess.board.Board;
import hr.mlinx.chess.board.Piece;
import hr.mlinx.chess.board.Square;

import java.util.Arrays;

public class FENParser {

    private FENParser() {}

    private static final String[] FILES = {"a", "b", "c", "d", "e", "f", "g", "h"};

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

        // ... (Remaining FEN parsing: active color, castling rights, en passant, etc. - not necessary for now)
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

    private static String getChessSquare(int row, int col) {
        int rank = 8 - row;
        return FILES[col] + rank;
    }

    public static Square getSquare(String chessSquare) {
        int col = Arrays.asList(FILES).indexOf(String.valueOf(chessSquare.charAt(0)));
        int row = 8 - Integer.parseInt(chessSquare.substring(1));
        return new Square(row, col);
    }

    public static String boardToFEN(Board board) {
        StringBuilder fen = new StringBuilder();

        fen.append(getPiecePlacementData(board));

        fen.append(" ");
        fen.append(board.getLastMove().getColor() == Piece.BLACK ? "w" : "b");

        fen.append(" ");
        fen.append(getCastlingAvailability(board));

        // En passant target square
        fen.append(" ");
        Square enPassantTargetSquare = board.getEnPassantTargetSquare();
        if (enPassantTargetSquare != null) {
            fen.append(getChessSquare(enPassantTargetSquare.row(), enPassantTargetSquare.col()));
        } else {
            fen.append("-");
        }

        fen.append(" ");
        fen.append(board.getNumOfHalfMoves());
        fen.append(" ");
        fen.append(board.getNumOfFullMoves());

        return fen.toString();
    }

    private static String getPiecePlacementData(Board board) {
        StringBuilder piecePlacementData = new StringBuilder();
        for (int row = 0; row < 8; row++) {
            int emptyCount = 0;
            for (int col = 0; col < 8; col++) {
                int piece = board.getPieceAt(row, col);
                if (piece == Piece.NONE) {
                    emptyCount++;
                } else {
                    if (emptyCount > 0) {
                        piecePlacementData.append(emptyCount);
                        emptyCount = 0;
                    }
                    piecePlacementData.append(getFENPiece(piece));
                }
            }
            if (emptyCount > 0) {
                piecePlacementData.append(emptyCount);
            }
            if (row < 7) {
                piecePlacementData.append("/");
            }
        }
        return piecePlacementData.toString();
    }

    private static String getCastlingAvailability(Board board) {
        StringBuilder castlingAvailability = new StringBuilder();
        if (!board.whiteCanCastleKingside() && !board.whiteCanCastleQueenside() &&
                !board.blackCanCastleKingside() && !board.blackCanCastleQueenside()) {
            castlingAvailability.append("-");
        } else {
            if (board.whiteCanCastleKingside()) castlingAvailability.append("K");
            if (board.whiteCanCastleQueenside()) castlingAvailability.append("Q");
            if (board.blackCanCastleKingside()) castlingAvailability.append("k");
            if (board.blackCanCastleQueenside()) castlingAvailability.append("q");
        }
        return castlingAvailability.toString();
    }

    private static char getFENPiece(int piece) {
        int type = Piece.getTypeFromPiece(piece);
        int color = Piece.getColorFromPiece(piece);
        char c = switch (type) {
            case Piece.PAWN -> 'P';
            case Piece.KNIGHT -> 'N';
            case Piece.BISHOP -> 'B';
            case Piece.ROOK -> 'R';
            case Piece.QUEEN -> 'Q';
            case Piece.KING -> 'K';
            default -> throw new IllegalArgumentException("Invalid piece type");
        };
        return color == Piece.WHITE ? Character.toUpperCase(c) : Character.toLowerCase(c);
    }

}
