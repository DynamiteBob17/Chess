package hr.mlinx.chess.board;

import hr.mlinx.chess.ui.ChessDialog;
import hr.mlinx.chess.util.FENParser;
import hr.mlinx.chess.util.MoveSet;
import hr.mlinx.chess.util.SoundPlayer;
import hr.mlinx.chess.validation.GeneralValidator;
import hr.mlinx.chess.validation.ValidMovesFilter;

import java.awt.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class Board {

    private final int[][] chessBoard;
    private LastMove lastMove;
    private final SoundPlayer soundPlayer;
    private final Map<Integer, Image> pieceImagesRegular;
    private boolean gameOver = false;

    private boolean whiteCanCastleKingside = true;
    private boolean whiteCanCastleQueenside = true;
    private boolean blackCanCastleKingside = true;
    private boolean blackCanCastleQueenside = true;

    private int numOfHalfMoves = 0;
    private int numOfFullMoves = 1;
    private Square enPassantTargetSquare;

    public Board(SoundPlayer soundPlayer, Map<Integer, Image> pieceImagesRegular) {
        chessBoard = new int[8][8];
        initializeClassicPosition();
        lastMove = new LastMove();
        this.soundPlayer = soundPlayer;
        this.pieceImagesRegular = pieceImagesRegular;
    }

    private Board(
            int[][] chessBoard,
            boolean whiteCanCastleKingside,
            boolean whiteCanCastleQueenside,
            boolean blackCanCastleKingside,
            boolean blackCanCastleQueenside
    ) {
        this.chessBoard = chessBoard;
        soundPlayer = null;
        pieceImagesRegular = null;
        this.whiteCanCastleKingside = whiteCanCastleKingside;
        this.whiteCanCastleQueenside = whiteCanCastleQueenside;
        this.blackCanCastleKingside = blackCanCastleKingside;
        this.blackCanCastleQueenside = blackCanCastleQueenside;
    }

    public Board createCopy() {
        int[][] chessBoardCopy = new int[8][8];
        for (int i = 0; i < chessBoard.length; i++) {
            chessBoardCopy[i] = Arrays.copyOf(chessBoard[i], chessBoard[i].length);
        }

        return new Board(
                chessBoardCopy,
                whiteCanCastleKingside,
                whiteCanCastleQueenside,
                blackCanCastleKingside,
                blackCanCastleQueenside
        );
    }

    private void initializeClassicPosition() {
        String initialFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        System.arraycopy(FENParser.parseFEN(initialFen), 0, chessBoard, 0, chessBoard.length);
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
        if (gameOver) {
            return;
        }

        int toPiece = getPieceAt(move.toRow, move.toCol);
        int colorMakingMove = Piece.getColorFromPiece(getPieceAt(move.fromRow, move.fromCol));

        makeMove(move, false);
        playSound(move, toPiece, colorMakingMove);
        lastMove.set(move);
    }

    public void doMoveForSimulation(Move move) {
        if (gameOver) {
            return;
        }

        makeMove(move, true);
    }

    private void makeMove(Move move, boolean isSimulation) {
        int pieceToMove = getPieceAt(move.fromRow, move.fromCol);
        int colorMakingMove = Piece.getColorFromPiece(pieceToMove);
        int toPiece = getPieceAt(move.toRow, move.toCol);

        setPieceAt(move.toRow, move.toCol, pieceToMove);
        setPieceAt(move.fromRow, move.fromCol, Piece.NONE);

        handleEnPassant(move, colorMakingMove);
        handlePawnPromotion(move, colorMakingMove, isSimulation);
        handleDisablingCastlingRights(move, pieceToMove, colorMakingMove);

        handleCastling(move, colorMakingMove);

        if (colorMakingMove == Piece.BLACK) {
            ++numOfFullMoves;
        }

        if (Piece.getTypeFromPiece(pieceToMove) == Piece.PAWN ||
                Piece.getTypeFromPiece(toPiece) != Piece.NONE) {
            numOfHalfMoves = 0;
        } else {
            ++numOfHalfMoves;
        }

        if (Piece.getTypeFromPiece(pieceToMove) == Piece.PAWN &&
                Math.abs(move.fromRow - move.toRow) == 2) {
            enPassantTargetSquare = new Square(
                    colorMakingMove == Piece.WHITE ? move.fromRow - 1 : move.fromRow + 1,
                    move.fromCol
            );
        } else {
            enPassantTargetSquare = null;
        }
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
        if (move.getSpecialMove() != null)
            return;

        int movedPieceType = Piece.getTypeFromPiece(pieceToMove);

        if (movedPieceType == Piece.KING) {
            disableCastlingForKing(colorMakingMove);
        } else if (movedPieceType == Piece.ROOK) {
            disableCastlingForRook(move.fromRow, move.fromCol);
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
        if (row == 7 && col == 7) {
            whiteCanCastleKingside = false;
        } else if (row == 7 && col == 0) {
            whiteCanCastleQueenside = false;
        } else if (row == 0 && col == 7) {
            blackCanCastleKingside = false;
        } else if (row == 0 && col == 0) {
            blackCanCastleQueenside = false;
        }
    }

    private void handleCastling(Move move, int colorMakingMove) {
        if (!(move.getSpecialMove() == SpecialMove.SHORT_CASTLE || move.getSpecialMove() == SpecialMove.LONG_CASTLE)) {
            return;
        }

        int rookRow = (colorMakingMove == Piece.WHITE) ? 7 : 0;

        if (move.getSpecialMove() == SpecialMove.SHORT_CASTLE) {
            setPieceAt(rookRow, 5, Piece.ROOK | colorMakingMove);
            setPieceAt(rookRow, 7, Piece.NONE);
        } else if (move.getSpecialMove() == SpecialMove.LONG_CASTLE) {
            setPieceAt(rookRow, 3, Piece.ROOK | colorMakingMove);
            setPieceAt(rookRow, 0, Piece.NONE);
        }

        if (colorMakingMove == Piece.WHITE) {
            whiteCanCastleKingside = false;
            whiteCanCastleQueenside = false;
        } else {
            blackCanCastleKingside = false;
            blackCanCastleQueenside = false;
        }
    }

    private void playSound(Move move, int toPiece, int colorMakingMove) {
        SpecialMove specialMove = move.getSpecialMove();

        if (isMate(colorMakingMove)) {
            soundPlayer.playMoveSound(MoveType.GAME_OVER);
        } else if (isCheck(colorMakingMove)) {
            soundPlayer.playMoveSound(MoveType.CHECK);
        } else if (specialMove == SpecialMove.PAWN_PROMOTION) {
            soundPlayer.playMoveSound(MoveType.PROMOTION);
        } else if (specialMove == SpecialMove.SHORT_CASTLE || specialMove == SpecialMove.LONG_CASTLE) {
            soundPlayer.playMoveSound(MoveType.CASTLE);
        } else if (specialMove == SpecialMove.EN_PASSANT) {
            soundPlayer.playMoveSound(MoveType.CAPTURE);
        } else if (toPiece == Piece.NONE) {
            soundPlayer.playMoveSound(MoveType.REGULAR);
        } else {
            soundPlayer.playMoveSound(MoveType.CAPTURE);
        }
    }

    private boolean isMate(int colorMakingMove) {
        Set<Move> legalMoves = new MoveSet<>();

        for (int row = 0; row < 8; ++row) {
            for (int col = 0; col < 8; ++col) {
                if (colorMakingMove == Piece.getColorFromPiece(getPieceAt(row, col))) {
                    continue;
                }

                legalMoves.addAll(GeneralValidator.getLegalMoves(row, col, this));

                if (!legalMoves.isEmpty()) {
                    return false;
                }
            }
        }

        gameOver = true;
        return true;
    }

    private boolean isCheck(int colorMakingMove) {
        int kingColor = colorMakingMove == Piece.WHITE ? Piece.BLACK : Piece.WHITE;
        int[] kingPosition = ValidMovesFilter.findKingPosition(kingColor, this);
        return ValidMovesFilter.isKingUnderAttack(kingPosition[0], kingPosition[1], kingColor, this);
    }

    public int[][] getChessBoard() {
        return chessBoard;
    }

    public LastMove getLastMove() {
        return lastMove;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver() {
        gameOver = true;
    }

    public void newGame() {
        initializeClassicPosition();
        lastMove = new LastMove();
        gameOver = false;
        whiteCanCastleKingside = true;
        whiteCanCastleQueenside = true;
        blackCanCastleKingside = true;
        blackCanCastleQueenside = true;
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

    public int getNumOfHalfMoves() {
        return numOfHalfMoves;
    }

    public int getNumOfFullMoves() {
        return numOfFullMoves;
    }

    public Square getEnPassantTargetSquare() {
        return enPassantTargetSquare;
    }

}
