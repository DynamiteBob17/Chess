package hr.mlinx.chess.validation;

import hr.mlinx.chess.board.Board;
import hr.mlinx.chess.board.Piece;
import hr.mlinx.chess.cache.ValidMovesCache;

import java.awt.Point;
import java.util.Set;
import java.util.Optional;

public class MoveValidation {

    private final Board board;
    private final ValidMovesCache validMovesCache;

    public MoveValidation(Board board) {
        this.board = board;
        validMovesCache = new ValidMovesCache(board);
    }

    public Set<Point> getValidMoves(int fromRow, int fromCol) {
        if (Piece.getColorFromPiece(board.getPieceAt(fromRow, fromCol)) == LastMove.color) {
            return Set.of();
        }

        Optional<Set<Point>> validMovesOptional = validMovesCache.getValidMovesOrInvalidateCache(
                fromRow,
                fromCol,
                board.getPieceAt(fromRow, fromCol)
        );

        Set<Point> validMoves = validMovesOptional.orElse(null);

        if (validMoves != null) {
            return validMoves;
        }

        validMoves = validMovesCache.getValidMoves();

        switch (Piece.getTypeFromPiece(board.getPieceAt(fromRow, fromCol))) {
            case Piece.PAWN -> PawnValidator.addValidMoves(fromRow, fromCol, board, validMoves);
            case Piece.KNIGHT -> KnightValidator.addValidMoves(fromRow, fromCol, board, validMoves);
            case Piece.BISHOP -> BishopValidator.addValidMoves(fromRow, fromCol, board, validMoves);
            case Piece.ROOK -> RookValidator.addValidMoves(fromRow, fromCol, board, validMoves);
            case Piece.QUEEN -> QueenValidator.addValidMoves(fromRow, fromCol, board, validMoves);
            case Piece.KING -> KingValidator.addValidMoves(fromRow, fromCol, board, validMoves);
            default -> {
                return validMoves;
            }
        }

        ValidMovesFilter.filter(validMoves, fromRow, fromCol);

        validMovesCache.setNewValidMoves(
                fromRow,
                fromCol,
                board.getPieceAt(fromRow, fromCol)
        );

        return validMoves;
    }

    public boolean isValidMove(int fromRow, int fromCol, int toRow, int toCol) {
        return getValidMoves(fromRow, fromCol).contains(new Point(toCol, toRow));
    }

    public boolean isValidMoveAndPlacement(int fromRow, int fromCol, int toRow, int toCol) {
        if (isInvalidPlacement(fromRow, fromCol) || isInvalidPlacement(toRow, toCol)) {
            return false;
        }

        return isValidMove(
                fromRow,
                fromCol,
                toRow,
                toCol
        );
    }

    public boolean isInvalidPlacement(int row, int col) {
        return row < 0 || row >= 8 || col < 0 || col >= 8;
    }

    public static void checkDirection(int fromRow, int fromCol, int rowDir, int colDir, Board board, Set<Point> validMoves) {
        for (int i = fromRow + rowDir, j = fromCol + colDir; i >= 0 && i < 8 && j >= 0 && j < 8; i += rowDir, j += colDir) {
            if (board.getPieceAt(i, j) == Piece.NONE) {
                validMoves.add(new Point(j, i));
            } else {
                if (Piece.getColorFromPiece(board.getPieceAt(i, j)) != Piece.getColorFromPiece(board.getPieceAt(fromRow, fromCol))) {
                    validMoves.add(new Point(j, i));
                }
                break;
            }
        }
    }

}
