package hr.mlinx.chess.validation;

import hr.mlinx.chess.board.Board;
import hr.mlinx.chess.board.Move;
import hr.mlinx.chess.board.Piece;

import java.util.Set;

public class ValidMovesFilter {

    private final Board board;

    public ValidMovesFilter(Board board) {
        this.board = board;
    }

    public void filter(Set<Move> validMoves) {
        validMoves.removeIf(this::capturesKing);
        validMoves.removeIf(this::putsOwnKingInCheck);
    }

    private boolean capturesKing(Move move) {
        int piece = board.getPieceAt(move.fromRow, move.fromCol);
        int movePiece = board.getPieceAt(move.toRow, move.toCol);

        return Piece.getTypeFromPiece(movePiece) == Piece.KING &&
                Piece.getColorFromPiece(piece) != Piece.getColorFromPiece(movePiece);
    }

    private boolean putsOwnKingInCheck(Move move) {
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

    private int[] findKingPosition(int kingColor, Board simulationBoard) {
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

    private boolean isKingUnderAttack(int kingRow, int kingCol, int kingColor, Board simulationBoard) {
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

    private boolean isPieceAttacked(int fromRow, int fromCol, int toRow, int toCol, Board simulationBoard) {
        int pieceType = Piece.getTypeFromPiece(simulationBoard.getPieceAt(fromRow, fromCol));
        return switch (pieceType) {
            case Piece.PAWN -> PawnValidator.isAttack(fromRow, fromCol, toRow, toCol, simulationBoard);
            case Piece.KNIGHT -> KnightValidator.isAttack(fromRow, fromCol, toRow, toCol, simulationBoard);
            case Piece.BISHOP -> BishopValidator.isAttack(fromRow, fromCol, toRow, toCol, simulationBoard);
            case Piece.ROOK -> RookValidator.isAttack(fromRow, fromCol, toRow, toCol, simulationBoard);
            case Piece.QUEEN -> QueenValidator.isAttack(fromRow, fromCol, toRow, toCol, simulationBoard);
            case Piece.KING -> KingValidator.isAttack(fromRow, fromCol, toRow, toCol);
            default -> false;
        };
    }

}
