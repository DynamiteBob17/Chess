package hr.mlinx.chess.listener;

import hr.mlinx.chess.ChessGUI;
import hr.mlinx.chess.board.Board;
import hr.mlinx.chess.board.Move;
import hr.mlinx.chess.board.Piece;
import hr.mlinx.chess.ui.Clock;
import hr.mlinx.chess.util.SoundPlayer;
import hr.mlinx.chess.util.Warning;
import hr.mlinx.chess.validation.MoveValidation;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;

import static hr.mlinx.chess.ChessGUI.SQUARE_SIZE;

public class ChessMouseListener extends MouseAdapter {

    private final ChessGUI chessGUI;
    private final Board board;
    private final MoveValidation moveValidation;
    private final SoundPlayer soundPlayer;
    private final Clock clock;

    private Point selectedPiece;
    private Point initialDrag;
    private Point prevMove;
    private Point newMove;

    public ChessMouseListener(
            ChessGUI chessGUI,
            Board board,
            MoveValidation moveValidation,
            SoundPlayer soundPlayer,
            Clock clock) {
        this.chessGUI = chessGUI;
        this.board = board;
        this.moveValidation = moveValidation;
        this.soundPlayer = soundPlayer;
        this.clock = clock;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int col = Math.floorDiv(e.getX(), SQUARE_SIZE);
        int row = Math.floorDiv(e.getY(), SQUARE_SIZE);

        if (Piece.getColorFromPiece(board.getPieceAt(row, col)) == board.getLastMove().getColor() ||
                MoveValidation.isInvalidPlacement(row, col) ||
                board.getPieceAt(row, col) == Piece.NONE) {
            return;
        }

        selectedPiece = new Point(col, row);
        initialDrag = new Point(e.getX(), e.getY());
        chessGUI.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (board.isGameOver()) {
            selectedPiece = null;
            chessGUI.repaint();
            return;
        }

        int col = Math.floorDiv(e.getX(), SQUARE_SIZE);
        int row = Math.floorDiv(e.getY(), SQUARE_SIZE);

        if (selectedPiece != null &&
                MoveValidation.isValidMovePlacement(selectedPiece.y, selectedPiece.x, row, col)) {
            if (row == selectedPiece.y && col == selectedPiece.x) {
                selectedPiece = null;
                chessGUI.repaint();
                return;
            }

            Set<Move> validMoves = moveValidation.getValidMoves(selectedPiece.y, selectedPiece.x);
            Move moveFromMouse = new Move(selectedPiece.y, selectedPiece.x, row, col);
            Move moveToMake = validMoves.stream()
                    .filter(move -> move.equals(moveFromMouse))
                    .findFirst()
                    .orElse(null);

            if (moveToMake != null) {
                board.doMove(moveToMake);

                Move lastMove = board.getLastMove().getMove();

                if (lastMove != null) {
                    clock.start();
                }

                clock.switchTurn();

                prevMove = new Point(selectedPiece.x, selectedPiece.y);
                newMove = new Point(col, row);
            } else {
                soundPlayer.playWarningSound(Warning.ILLEGAL_MOVE);
            }
        }

        if (board.isGameOver()) {
            Clock.ChessDialog.showGameOverDialog(board.getLastMove().getColor());
            clock.stop();
        }

        selectedPiece = null;
        chessGUI.repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (selectedPiece != null) {
            int deltaX = e.getX() - initialDrag.x;
            int deltaY = e.getY() - initialDrag.y;
            initialDrag.setLocation(e.getX(), e.getY());
            selectedPiece.translate(deltaX / SQUARE_SIZE, deltaY / SQUARE_SIZE);
            chessGUI.repaint();
        }
    }

    public boolean isHighlightedSquare(int row, int col) {
        Point currentSquare = new Point(col, row);
        return (selectedPiece != null && selectedPiece.equals(currentSquare)) ||
                (prevMove != null && prevMove.equals(currentSquare)) ||
                (newMove != null && newMove.equals(currentSquare));
    }

    public boolean isPossibleMoveSquare(int row, int col) {
        if (board.isGameOver()) {
            return false;
        }

        return selectedPiece != null &&
                moveValidation
                        .getValidMoves(selectedPiece.y, selectedPiece.x)
                        .contains(new Move(selectedPiece.y, selectedPiece.x, row, col));
    }

    public Point getSelectedPiece() {
        return selectedPiece;
    }

    public Point getInitialDrag() {
        return initialDrag;
    }

}
