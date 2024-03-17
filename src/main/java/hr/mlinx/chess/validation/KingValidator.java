package hr.mlinx.chess.validation;

import hr.mlinx.chess.board.Board;
import hr.mlinx.chess.board.Move;
import hr.mlinx.chess.board.Piece;
import hr.mlinx.chess.board.SpecialMove;

import java.util.HashSet;
import java.util.Set;

public class KingValidator {

    private KingValidator() {
    }

    public static Set<Move> getValidMoves(int fromRow, int fromCol, Board board) {
        Set<Move> validMoves = new HashSet<>();

        int pieceColor = Piece.getColorFromPiece(board.getPieceAt(fromRow, fromCol));

        // King can move 1 square in any direction
        for (int row = -1; row <= 1; row++) {
            for (int col = -1; col <= 1; col++) {
                if (row != 0 || col != 0) {
                    int targetRow = fromRow + row;
                    int targetCol = fromCol + col;
                    int targetPiece = board.getPieceAt(targetRow, targetCol);
                    if (targetPiece == Piece.NONE || Piece.getColorFromPiece(targetPiece) != pieceColor) {
                        validMoves.add(new Move(fromRow, fromCol, targetRow, targetCol));
                    }
                }
            }
        }

        // Castling moves
        int kingRow = (pieceColor == Piece.WHITE) ? 7 : 0;
        int kingCol = 4;

        if (fromCol != kingCol) {
            return validMoves;
        }

        if ((pieceColor == Piece.WHITE && fromRow == 7) || (pieceColor == Piece.BLACK && fromRow == 0)) {
            boolean canCastleKingside = (pieceColor == Piece.WHITE) ? board.whiteCanCastleKingside() : board.blackCanCastleKingside();
            boolean canCastleQueenside = (pieceColor == Piece.WHITE) ? board.whiteCanCastleQueenside() : board.blackCanCastleQueenside();

            if (canCastleKingside && checkCastlingPathClear(board, kingRow, kingCol, 7)) {
                validMoves.add(new Move(kingRow, kingCol, kingRow, 6, SpecialMove.SHORT_CASTLE));
            }

            if (canCastleQueenside && checkCastlingPathClear(board, kingRow, kingCol, 0)) {
                validMoves.add(new Move(kingRow, kingCol, kingRow, 2, SpecialMove.LONG_CASTLE));
            }
        }

        return validMoves;
    }

    private static boolean checkCastlingPathClear(Board board, int kingRow, int kingCol, int rookCol) {
        int increment = (rookCol > kingCol) ? 1 : -1;
        for (int col = kingCol + increment; col != rookCol; col += increment) {
            if (board.getPieceAt(kingRow, col) != Piece.NONE) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAttack(int fromRow, int fromCol, int toRow, int toCol) {
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        return (rowDiff <= 1 && colDiff <= 1);
    }

}
