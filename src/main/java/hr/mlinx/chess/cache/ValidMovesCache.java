package hr.mlinx.chess.cache;

import hr.mlinx.chess.board.Board;
import hr.mlinx.chess.board.Move;
import hr.mlinx.chess.util.MoveSet;

import java.util.*;

public class ValidMovesCache {

    private final Board board;
    private final int[][] chessBoard;
    private final Set<Move> validMoves;
    private int fromRow;
    private int fromCol;
    private int piece;

    public ValidMovesCache(Board board) {
        this.board = board;
        chessBoard = new int[8][8];
        copyBoard();
        validMoves = new MoveSet<>();
        fromRow = fromCol = piece = -1;
    }

    private void copyBoard() {
        System.arraycopy(board.getChessBoard(), 0, chessBoard, 0, chessBoard.length);
    }

    public void setNewValidMoves(int fromRow, int fromCol, int piece) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.piece = piece;
        copyBoard();
    }

    public Optional<Set<Move>> getValidMovesOrInvalidateCache(int fromRow, int fromCol, int piece) {
        if (this.fromRow != fromRow ||
                this.fromCol != fromCol ||
                this.piece != piece ||
                !Arrays.deepEquals(chessBoard, board.getChessBoard())) {
            this.fromRow = this.fromCol = this.piece = -1;
            validMoves.clear();
            copyBoard();

            return Optional.empty();
        } else {
            return Optional.of(validMoves);
        }
    }

    public Set<Move> getValidMoves() {
        return validMoves;
    }

}
