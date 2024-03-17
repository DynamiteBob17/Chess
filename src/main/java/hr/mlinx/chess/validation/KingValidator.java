package hr.mlinx.chess.validation;

import hr.mlinx.chess.board.Board;
import hr.mlinx.chess.board.Move;
import hr.mlinx.chess.board.Piece;
import hr.mlinx.chess.board.SpecialMove;
import hr.mlinx.chess.util.MoveSet;

import java.util.Set;

public class KingValidator {

    private KingValidator() {
    }

    public static Set<Move> getValidMoves(int fromRow, int fromCol, Board board) {
        Set<Move> validMoves = new MoveSet<>();

        int pieceColor = Piece.getColorFromPiece(board.getPieceAt(fromRow, fromCol));

        addKingMoves(fromRow, fromCol, board, pieceColor, validMoves);
        addCastlingMoves(fromRow, fromCol, board, pieceColor, validMoves);

        return validMoves;
    }

    private static void addKingMoves(int fromRow, int fromCol, Board board, int pieceColor, Set<Move> validMoves) {
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
    }

    private static void addCastlingMoves(int fromRow, int fromCol, Board board, int pieceColor, Set<Move> validMoves) {
        int kingRow = (pieceColor == Piece.WHITE) ? 7 : 0;
        int kingCol = 4;

        if (fromCol != kingCol) {
            return;
        }

        if (isKingOnStartingRow(pieceColor, fromRow) && canCastle(board, pieceColor)) {
            addCastlingMoveIfClear(board, kingRow, kingCol, 7, SpecialMove.SHORT_CASTLE, validMoves);
            addCastlingMoveIfClear(board, kingRow, kingCol, 0, SpecialMove.LONG_CASTLE, validMoves);
        }
    }

    private static boolean isKingOnStartingRow(int pieceColor, int fromRow) {
        return (pieceColor == Piece.WHITE && fromRow == 7) || (pieceColor == Piece.BLACK && fromRow == 0);
    }

    private static boolean canCastle(Board board, int pieceColor) {
        return (pieceColor == Piece.WHITE) ? board.whiteCanCastleKingside() || board.whiteCanCastleQueenside() :
                board.blackCanCastleKingside() || board.blackCanCastleQueenside();
    }

    private static void addCastlingMoveIfClear(Board board, int kingRow, int kingCol, int rookCol, SpecialMove specialMove, Set<Move> validMoves) {
        if (checkCastlingPathClear(board, kingRow, kingCol, rookCol)) {
            validMoves.add(new Move(kingRow, kingCol, kingRow, (rookCol == 7) ? 6 : 2, specialMove));
        }
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
