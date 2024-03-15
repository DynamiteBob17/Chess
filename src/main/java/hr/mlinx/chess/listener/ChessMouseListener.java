package hr.mlinx.chess.listener;

import hr.mlinx.chess.ChessGUI;
import hr.mlinx.chess.board.Board;
import hr.mlinx.chess.board.Piece;
import hr.mlinx.chess.util.SoundPlayer;
import hr.mlinx.chess.validation.LastMove;
import hr.mlinx.chess.validation.MoveValidation;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static hr.mlinx.chess.ChessGUI.PADDING;
import static hr.mlinx.chess.ChessGUI.SQUARE_SIZE;

public class ChessMouseListener extends MouseAdapter {

    private final ChessGUI chessGUI;
    private final Board board;
    private final MoveValidation moveValidation;
    private final SoundPlayer soundPlayer;

    private Point selectedPiece;
    private Point initialDrag;
    private Point prevMove;
    private Point newMove;

    public ChessMouseListener(
            ChessGUI chessGUI,
            Board board,
            MoveValidation moveValidation) {
        this.chessGUI = chessGUI;
        this.board = board;
        this.moveValidation = moveValidation;
        soundPlayer = new SoundPlayer();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int col = Math.floorDiv(e.getX() - PADDING, SQUARE_SIZE);
        int row = Math.floorDiv(e.getY() - SQUARE_SIZE, SQUARE_SIZE);

        if (Piece.getColorFromPiece(board.getPieceAt(row, col)) == LastMove.color ||
                moveValidation.isInvalidPlacement(row, col) ||
                board.getPieceAt(row, col) == Piece.NONE) {
            return;
        }

        selectedPiece = new Point(col, row);
        initialDrag = new Point(e.getX(), e.getY());
        chessGUI.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int col = Math.floorDiv(e.getX() - PADDING, SQUARE_SIZE);
        int row = Math.floorDiv(e.getY() - SQUARE_SIZE, SQUARE_SIZE);

        if (selectedPiece != null && moveValidation.isValidMoveAndPlacement(selectedPiece.y, selectedPiece.x, row, col)) {
            if (row == selectedPiece.y && col == selectedPiece.x) {
                selectedPiece = null;
                chessGUI.repaint();
                return;
            }

            int fromPiece = board.getPieceAt(selectedPiece.y, selectedPiece.x);
            int toPiece = board.getPieceAt(row, col);

            board.doMove(selectedPiece.y, selectedPiece.x, row, col, fromPiece);
            soundPlayer.playMoveSound(toPiece);

            prevMove = new Point(selectedPiece.x, selectedPiece.y);
            newMove = new Point(col, row);
            LastMove.switchMove();
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
        Point currentSquare = new Point(col, row);
        return selectedPiece != null &&
                moveValidation.getValidMoves(selectedPiece.y, selectedPiece.x).contains(currentSquare);
    }

    public Point getSelectedPiece() {
        return selectedPiece;
    }

    public Point getInitialDrag() {
        return initialDrag;
    }

}
