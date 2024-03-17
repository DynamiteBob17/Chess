package hr.mlinx.chess.validation;

import hr.mlinx.chess.board.Board;
import hr.mlinx.chess.board.Move;
import hr.mlinx.chess.board.Piece;
import hr.mlinx.chess.board.SpecialMove;

import java.util.Set;

public class ValidMovesFilter {

    private ValidMovesFilter() {}

    public static void filter(Set<Move> validMoves, Board board) {
        validMoves.removeIf(move -> capturesKing(move, board));
        validMoves.removeIf(move -> putsOwnKingInCheck(move, board));
        validMoves.removeIf(move -> isIllegalCastling(move, board));
    }

    private static boolean capturesKing(Move move, Board board) {
        int piece = board.getPieceAt(move.fromRow, move.fromCol);
        int movePiece = board.getPieceAt(move.toRow, move.toCol);

        return Piece.getTypeFromPiece(movePiece) == Piece.KING &&
                Piece.getColorFromPiece(piece) != Piece.getColorFromPiece(movePiece);
    }

    private static boolean putsOwnKingInCheck(Move move, Board board) {
        Board simulationBoard = board.createCopy();

        int simulationMovePieceFrom = simulationBoard.getPieceAt(move.fromRow, move.fromCol);
        int simulationMovePieceTo = simulationBoard.getPieceAt(move.toRow, move.toCol);

        if (simulationMovePieceFrom == -1 || simulationMovePieceTo == -1) {
            return false;
        }

        simulationBoard.doMoveForSimulation(move);

        int kingColor = Piece.getColorFromPiece(board.getPieceAt(move.fromRow, move.fromCol));
        int[] kingPosition = findKingPosition(kingColor, simulationBoard);

        return isKingUnderAttack(kingPosition[0], kingPosition[1], kingColor, simulationBoard);
    }

    public static int[] findKingPosition(int kingColor, Board simulationBoard) {
        int[] kingPosition = new int[2];

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (Piece.getTypeFromPiece(simulationBoard.getPieceAt(row, col)) == Piece.KING &&
                        Piece.getColorFromPiece(simulationBoard.getPieceAt(row, col)) == kingColor) {
                    kingPosition[0] = row;
                    kingPosition[1] = col;
                    return kingPosition;
                }
            }
        }

        // should not happen in a valid chess position
        throw new IllegalStateException("King not found on the board.");
    }

    public static boolean isKingUnderAttack(int kingRow, int kingCol, int kingColor, Board simulationBoard) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (Piece.getTypeFromPiece(simulationBoard.getPieceAt(row, col)) != Piece.NONE &&
                        Piece.getColorFromPiece(simulationBoard.getPieceAt(row, col)) != kingColor &&
                        isPieceAttacked(row, col, kingRow, kingCol, simulationBoard)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isPieceAttacked(int fromRow, int fromCol, int toRow, int toCol, Board simulationBoard) {
        int pieceType = Piece.getTypeFromPiece(simulationBoard.getPieceAt(fromRow, fromCol));

        return switch (pieceType) {
            case Piece.PAWN -> PawnValidator.isAttack(fromRow, fromCol, toRow, toCol, simulationBoard);
            case Piece.KNIGHT -> KnightValidator.isAttack(fromRow, fromCol, toRow, toCol);
            case Piece.BISHOP -> BishopValidator.isAttack(fromRow, fromCol, toRow, toCol, simulationBoard);
            case Piece.ROOK -> RookValidator.isAttack(fromRow, fromCol, toRow, toCol, simulationBoard);
            case Piece.QUEEN -> QueenValidator.isAttack(fromRow, fromCol, toRow, toCol, simulationBoard);
            case Piece.KING -> KingValidator.isAttack(fromRow, fromCol, toRow, toCol);
            default -> false;
        };
    }

    private static boolean isIllegalCastling(Move move, Board board) {
        if (move.getSpecialMove() != SpecialMove.SHORT_CASTLE && move.getSpecialMove() != SpecialMove.LONG_CASTLE) {
            return false;
        }

        int piece = board.getPieceAt(move.fromRow, move.fromCol);
        int color = Piece.getColorFromPiece(piece);
        int kingStartCol = 4;
        int kingDestCol = (move.getSpecialMove() == SpecialMove.SHORT_CASTLE) ? 6 : 2;

        for (int col = Math.min(kingStartCol, kingDestCol); col <= Math.max(kingStartCol, kingDestCol); ++col) {
            if (isKingUnderAttack(move.fromRow, col, color, board)) {
                return true;
            }
        }

        return false;
    }

}
