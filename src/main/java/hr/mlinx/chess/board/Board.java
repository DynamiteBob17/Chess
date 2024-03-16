package hr.mlinx.chess.board;

import hr.mlinx.chess.util.FENParser;
import hr.mlinx.chess.util.SoundPlayer;

import java.util.Arrays;

public class Board {

    private final int[][] chessBoard;
    private LastMove lastMove;
    private final SoundPlayer soundPlayer;

    public Board(SoundPlayer soundPlayer) {
        chessBoard = new int[8][8];
        initializeClassicPosition();
        lastMove = new LastMove();
        this.soundPlayer = soundPlayer;
    }

    public Board(int[][] chessBoard) {
        this.chessBoard = chessBoard;
        soundPlayer = null;
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

    public void doMove(Move move) {
        int toPiece = getPieceAt(move.toRow, move.toCol);

        makeMove(move);

        if (move.getSpecialMove() == SpecialMove.EN_PASSANT) {
            soundPlayer.playMoveSound(MoveType.CAPTURE);
        } else if (toPiece == Piece.NONE) {
            soundPlayer.playMoveSound(MoveType.REGULAR);
        } else {
            soundPlayer.playMoveSound(MoveType.CAPTURE);
        }

        lastMove.set(move);
    }

    public void doMoveForSimulation(Move move) {
        makeMove(move);
    }

    private void makeMove(Move move) {
        int colorMakingMove = Piece.getColorFromPiece(getPieceAt(move.fromRow, move.fromCol));

        setPieceAt(move.toRow, move.toCol, getPieceAt(move.fromRow, move.fromCol));
        setPieceAt(move.fromRow, move.fromCol, Piece.NONE);

        if (move.getSpecialMove() == SpecialMove.EN_PASSANT) {
            int capturedPawnRow = (colorMakingMove == Piece.WHITE) ? move.toRow + 1 : move.toRow - 1;
            int capturedPawnCol = move.toCol;

            setPieceAt(capturedPawnRow, capturedPawnCol, Piece.NONE);
        }
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

    public LastMove getLastMove() {
        return lastMove;
    }

}
