package hr.mlinx.chess.validation;

import hr.mlinx.chess.board.Board;
import hr.mlinx.chess.board.Move;
import hr.mlinx.chess.board.Piece;
import hr.mlinx.chess.cache.ValidMovesCache;

import java.util.Set;
import java.util.Optional;

public class MoveValidation {

    private final Board board;
    private final ValidMovesCache validMovesCache;
    private final ValidMovesFilter validMovesFilter;

    public MoveValidation(Board board) {
        this.board = board;
        validMovesCache = new ValidMovesCache(board);
        validMovesFilter = new ValidMovesFilter(board);
    }

    public Set<Move> getValidMoves(int fromRow, int fromCol) {
        if (Piece.getColorFromPiece(board.getPieceAt(fromRow, fromCol)) == board.getLastMove().getColor()) {
            return Set.of();
        }

        Optional<Set<Move>> validMovesOptional = validMovesCache.getValidMovesOrInvalidateCache(
                fromRow,
                fromCol,
                board.getPieceAt(fromRow, fromCol)
        );
        Set<Move> cachedValidMoves = validMovesOptional.orElse(null);

        if (cachedValidMoves != null) {
            return cachedValidMoves;
        }

        Set<Move> validMoves = validMovesCache.getValidMoves();
        validMoves.addAll(calculateValidMoves(fromRow, fromCol, board));

        if (!validMoves.isEmpty()) {
            validMovesFilter.filter(validMoves);
        }

        validMovesCache.setNewValidMoves(
                fromRow,
                fromCol,
                board.getPieceAt(fromRow, fromCol)
        );

        return validMoves;
    }

    public static Set<Move> calculateValidMoves(int fromRow, int fromCol, Board board) {
        return switch (Piece.getTypeFromPiece(board.getPieceAt(fromRow, fromCol))) {
            case Piece.PAWN -> PawnValidator.getValidMoves(fromRow, fromCol, board);
            case Piece.KNIGHT -> KnightValidator.getValidMoves(fromRow, fromCol, board);
            case Piece.BISHOP -> BishopValidator.getValidMoves(fromRow, fromCol, board);
            case Piece.ROOK -> RookValidator.getValidMoves(fromRow, fromCol, board);
            case Piece.QUEEN -> QueenValidator.getValidMoves(fromRow, fromCol, board);
            case Piece.KING -> KingValidator.getValidMoves(fromRow, fromCol, board);
            default -> Set.of();
        };
    }

    public boolean isValidMovePlacement(int fromRow, int fromCol, int toRow, int toCol) {
        return !(isInvalidPlacement(fromRow, fromCol) || isInvalidPlacement(toRow, toCol));
    }

    public boolean isInvalidPlacement(int row, int col) {
        return row < 0 || row >= 8 || col < 0 || col >= 8;
    }

}
