package hr.mlinx.chess.board;

import hr.mlinx.chess.util.FENParser;

import java.util.Arrays;

public class Board {

    private final int[][] chessBoard;

    public Board() {
        chessBoard = new int[8][8];
        initializeClassicPosition();
    }

    public Board(int[][] chessBoard) {
        this.chessBoard = chessBoard;
    }

    public int getPieceAt(int row, int col) {
        try {
            return chessBoard[row][col];
        } catch (ArrayIndexOutOfBoundsException e) {
            return -1;
        }
    }

    public void setPieceAt(int row, int col, int piece) {
        chessBoard[row][col] = piece;
    }

    public void doMove(int fromRow, int fromCol, int toRow, int toCol, int fromPiece) {
        setPieceAt(toRow, toCol, fromPiece);
        setPieceAt(fromRow, fromCol, Piece.NONE);
    }

    public int[][] getChessBoard() {
        return chessBoard;
    }

    public Board createCopy() {
        int[][] chessBoardCopy = new int[8][8];
        for (int i = 0; i < chessBoard.length; i++) {
            chessBoardCopy[i] = Arrays.copyOf(chessBoard[i], chessBoard[i].length);
        }
        return new Board(chessBoardCopy);
    }

    private void initializeClassicPosition(){
        String initialFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        System.arraycopy(FENParser.parseFEN(initialFen), 0, chessBoard, 0, chessBoard.length);
    }

}
