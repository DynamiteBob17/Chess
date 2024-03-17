package hr.mlinx.chess.board;

import hr.mlinx.chess.util.ChessDialog;
import hr.mlinx.chess.util.FENParser;
import hr.mlinx.chess.util.SoundPlayer;

import java.awt.*;
import java.util.Arrays;
import java.util.Map;

public class Board {

    private final int[][] chessBoard;
    private LastMove lastMove;
    private final SoundPlayer soundPlayer;
    private final Map<Integer, Image> pieceImagesRegular;

    private boolean whiteCanCastleKingside = true;
    private boolean whiteCanCastleQueenside = true;
    private boolean blackCanCastleKingside = true;
    private boolean blackCanCastleQueenside = true;

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
        int pieceToMove = getPieceAt(move.fromRow, move.fromCol);
        int colorMakingMove = Piece.getColorFromPiece(pieceToMove);

        setPieceAt(move.toRow, move.toCol, pieceToMove);
        setPieceAt(move.fromRow, move.fromCol, Piece.NONE);

        handleEnPassant(move, colorMakingMove);
        handlePawnPromotion(move, colorMakingMove, isSimulation);
        handleDisablingCastlingRights(move, pieceToMove, colorMakingMove);

        handleCastling(move, colorMakingMove);
    }

    private void handleEnPassant(Move move, int colorMakingMove) {
        if (move.getSpecialMove() == SpecialMove.EN_PASSANT) {
            int capturedPawnRow = (colorMakingMove == Piece.WHITE) ? move.toRow + 1 : move.toRow - 1;
            int capturedPawnCol = move.toCol;
            setPieceAt(capturedPawnRow, capturedPawnCol, Piece.NONE);
        }
    }

    private void handlePawnPromotion(Move move, int colorMakingMove, boolean isSimulation) {
        if (!isSimulation && move.getSpecialMove() == SpecialMove.PAWN_PROMOTION) {
            int promotedPiece = ChessDialog.showPromotionDialog(colorMakingMove, pieceImagesRegular);
            setPieceAt(move.toRow, move.toCol, promotedPiece);
        }
    }

    private void handleDisablingCastlingRights(Move move, int pieceToMove, int colorMakingMove) {
        if (move.getSpecialMove() == null) { // Regular move, not castling
            int movedPieceType = Piece.getTypeFromPiece(pieceToMove);

            if (movedPieceType == Piece.KING) {
                disableCastlingForKing(colorMakingMove);
            } else if (movedPieceType == Piece.ROOK) {
                disableCastlingForRook(move.fromRow, move.fromCol);
            }
        }
    }

    private void disableCastlingForKing(int colorMakingMove) {
        if (colorMakingMove == Piece.WHITE) {
            whiteCanCastleKingside = false;
            whiteCanCastleQueenside = false;
        } else {
            blackCanCastleKingside = false;
            blackCanCastleQueenside = false;
        }
    }

    private void disableCastlingForRook(int row, int col) {
        switch (row * 10 + col) {
            case 70 -> whiteCanCastleQueenside = false;
            case 77 -> whiteCanCastleKingside = false;
            case 0 -> blackCanCastleQueenside = false;
            case 7 -> blackCanCastleKingside = false;
        }
    }

    private void handleCastling(Move move, int colorMakingMove) {
        if (move.getSpecialMove() == SpecialMove.SHORT_CASTLE) {
            if (colorMakingMove == Piece.WHITE) {
                setPieceAt(7, 5, Piece.ROOK | Piece.WHITE);
                setPieceAt(7, 7, Piece.NONE);
            } else {
                setPieceAt(0, 5, Piece.ROOK | Piece.BLACK);
                setPieceAt(0, 7, Piece.NONE);
            }
        } else if (move.getSpecialMove() == SpecialMove.LONG_CASTLE) {
            if (colorMakingMove == Piece.WHITE) {
                setPieceAt(7, 3, Piece.ROOK | Piece.WHITE);
                setPieceAt(7, 0, Piece.NONE);
            } else {
                setPieceAt(0, 3, Piece.ROOK | Piece.BLACK);
                setPieceAt(0, 0, Piece.NONE);
            }
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

    public boolean whiteCanCastleKingside() {
        return whiteCanCastleKingside;
    }

    public boolean whiteCanCastleQueenside() {
        return whiteCanCastleQueenside;
    }

    public boolean blackCanCastleKingside() {
        return blackCanCastleKingside;
    }

    public boolean blackCanCastleQueenside() {
        return blackCanCastleQueenside;
    }

}
