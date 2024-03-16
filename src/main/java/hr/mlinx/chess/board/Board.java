package hr.mlinx.chess.board;

import hr.mlinx.chess.util.FENParser;
import hr.mlinx.chess.util.SoundPlayer;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Map;

public class Board {

    private final int[][] chessBoard;
    private LastMove lastMove;
    private final SoundPlayer soundPlayer;
    private final Map<Integer, Image> pieceImagesRegular;

    public Board(SoundPlayer soundPlayer, Map<Integer, Image> pieceImagesRegular) {
        chessBoard = new int[8][8];
        initializeClassicPosition();
        lastMove = new LastMove();
        this.soundPlayer = soundPlayer;
        this.pieceImagesRegular = pieceImagesRegular;
    }

    public Board(int[][] chessBoard) {
        this.chessBoard = chessBoard;
        soundPlayer = null;
        pieceImagesRegular = null;
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

        makeMove(move, false);

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
        makeMove(move, true);
    }

    private void makeMove(Move move, boolean isSimulation) {
        int colorMakingMove = Piece.getColorFromPiece(getPieceAt(move.fromRow, move.fromCol));

        setPieceAt(move.toRow, move.toCol, getPieceAt(move.fromRow, move.fromCol));
        setPieceAt(move.fromRow, move.fromCol, Piece.NONE);

        if (move.getSpecialMove() == SpecialMove.EN_PASSANT) {
            int capturedPawnRow = (colorMakingMove == Piece.WHITE) ? move.toRow + 1 : move.toRow - 1;
            int capturedPawnCol = move.toCol;

            setPieceAt(capturedPawnRow, capturedPawnCol, Piece.NONE);
        }

        if (!isSimulation && move.getSpecialMove() == SpecialMove.PAWN_PROMOTION) {
            int promotedPiece = showPromotionDialog(colorMakingMove);
            setPieceAt(move.toRow, move.toCol, promotedPiece);
        }
    }

    private int showPromotionDialog(int promotingColor) {
        ImageIcon[] options = new ImageIcon[4];
        int pieceTypeIndex = 0;

        for (int pieceType : new int[]{Piece.QUEEN, Piece.ROOK, Piece.BISHOP, Piece.KNIGHT}) {
            options[pieceTypeIndex++] = new ImageIcon(pieceImagesRegular.get(pieceType | promotingColor));
        }

        int n = JOptionPane.showOptionDialog(null,
                "Choose promotion:",
                "Pawn Promotion",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        // Mapping remains the same as before...
        int promotedPieceType = switch (n) {
            case 1 -> Piece.ROOK;
            case 2 -> Piece.BISHOP;
            case 3 -> Piece.KNIGHT;
            default -> Piece.QUEEN;
        };

        return promotedPieceType | promotingColor;
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
