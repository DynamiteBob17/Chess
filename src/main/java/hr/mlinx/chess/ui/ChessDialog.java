package hr.mlinx.chess.ui;

import hr.mlinx.chess.board.Piece;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ChessDialog {

    private ChessDialog() {}

    public static int showPromotionDialog(int promotingColor, Map<Integer, Image> pieceImagesRegular) {
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

        int promotedPieceType = switch (n) {
            case 1 -> Piece.ROOK;
            case 2 -> Piece.BISHOP;
            case 3 -> Piece.KNIGHT;
            default -> Piece.QUEEN;
        };

        return promotedPieceType | promotingColor;
    }

    public static void showGameOverDialog(int winningColor) {
        String winner = (winningColor == Piece.WHITE) ? "WHITE" : "BLACK";
        new Thread(() -> {
            JOptionPane.showMessageDialog(
                    null,
                    winner + " wins!",
                    "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);
        }).start();
    }

}
