package hr.mlinx.chess.validation;

import hr.mlinx.chess.board.Board;
import hr.mlinx.chess.board.Piece;

import java.awt.*;
import java.util.Set;

public class ValidMovesFilter {

    private final Board board;

    public ValidMovesFilter(Board board) {
        this.board = board;
    }

    public void filter(Set<Point> validMoves, int fromRow, int fromCol) {
        validMoves.removeIf(move -> capturesKing(move, fromRow, fromCol));
        validMoves.removeIf(move -> putsOwnKingInCheck(fromRow, fromCol, move.y, move.x));
    }

    private boolean capturesKing(Point move, int fromRow, int fromCol) {
        int piece = board.getPieceAt(fromRow, fromCol);
        int movePiece = board.getPieceAt(move.y, move.x);

        return Piece.getTypeFromPiece(movePiece) == Piece.KING &&
                Piece.getColorFromPiece(piece) != Piece.getColorFromPiece(movePiece);
    }

    private boolean putsOwnKingInCheck(int fromRow, int fromCol, int toRow, int toCol) {
        Board hypotheticalBoard = board.createCopy();

        int hypotheticalMovePieceFrom = hypotheticalBoard.getPieceAt(fromRow, fromCol);
        int hypotheticalMovePieceTo = hypotheticalBoard.getPieceAt(toRow, toCol);

        if (hypotheticalMovePieceFrom == -1 || hypotheticalMovePieceTo == -1) {
            return false;
        }

        hypotheticalBoard.doMove(fromRow, fromCol, toRow, toCol, hypotheticalBoard.getPieceAt(fromRow, fromCol));

        int kingColor = Piece.getColorFromPiece(board.getPieceAt(fromRow, fromCol));
        int[] kingPosition = findKingPosition(kingColor, hypotheticalBoard);

        return isKingUnderAttack(kingPosition[0], kingPosition[1], kingColor, hypotheticalBoard);
    }

    private int[] findKingPosition(int kingColor, Board hypotheticalBoard) {
        int[] kingPosition = new int[2];

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (Piece.getTypeFromPiece(hypotheticalBoard.getPieceAt(row, col)) == Piece.KING &&
                        Piece.getColorFromPiece(hypotheticalBoard.getPieceAt(row, col)) == kingColor) {
                    kingPosition[0] = row;
                    kingPosition[1] = col;
                    return kingPosition;
                }
            }
        }

        // should not happen in a valid chess position
        throw new IllegalStateException("King not found on the board.");
    }


    private boolean isKingUnderAttack(int kingRow, int kingCol, int kingColor, Board board) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (Piece.getColorFromPiece(board.getPieceAt(row, col)) != kingColor &&
                        isPieceAttacked(row, col, kingRow, kingCol, board)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isPieceAttacked(int fromRow, int fromCol, int toRow, int toCol, Board hypotheticalBoard) {
        int pieceType = Piece.getTypeFromPiece(hypotheticalBoard.getPieceAt(fromRow, fromCol));
        return switch (pieceType) {
            case Piece.PAWN -> PawnValidator.isAttack(fromRow, fromCol, toRow, toCol, hypotheticalBoard);
            case Piece.KNIGHT -> KnightValidator.isAttack(fromRow, fromCol, toRow, toCol, hypotheticalBoard);
            case Piece.BISHOP -> BishopValidator.isAttack(fromRow, fromCol, toRow, toCol, hypotheticalBoard);
            case Piece.ROOK -> RookValidator.isAttack(fromRow, fromCol, toRow, toCol, hypotheticalBoard);
            case Piece.QUEEN -> QueenValidator.isAttack(fromRow, fromCol, toRow, toCol, hypotheticalBoard);
            case Piece.KING -> KingValidator.isAttack(fromRow, fromCol, toRow, toCol);
            default -> false; // Invalid piece type
        };
    }

}
