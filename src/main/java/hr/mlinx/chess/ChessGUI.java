package hr.mlinx.chess;

import com.formdev.flatlaf.FlatDarkLaf;
import hr.mlinx.chess.board.Board;
import hr.mlinx.chess.board.Piece;
import hr.mlinx.chess.listener.ChessMouseListener;
import hr.mlinx.chess.util.ColorTinter;
import hr.mlinx.chess.util.ImageLoader;
import hr.mlinx.chess.util.SoundPlayer;
import hr.mlinx.chess.validation.MoveValidation;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

// TODO: adjust sizes for different resolutions

// TODO: MAYBE make each piece an object and store all the pieces that are on the board
//  to not have to do a double 8x8 for loop when checking for attacks on the king or if there is a mate and other checks,
//  but it's not THAT big of a deal since it's only ever 64 loop iterations, but still, consider it!

public class ChessGUI extends JPanel {

    public static final int SQUARE_SIZE = 80;
    public static final int PADDING = 300;
    private static final Color WHITE_SQUARE_COLOR = new Color(238, 238, 210);
    private static final Color BLACK_SQUARE_COLOR = new Color(118, 150, 86);

    private final transient SoundPlayer soundPlayer;
    private final transient Board board;
    private final transient ChessMouseListener chessMouseListener;
    private final transient Map<Integer, Image> pieceImagesRegular;
    private final transient Map<Integer, Image> pieceImagesSelected;

    public ChessGUI() {
        pieceImagesRegular = new HashMap<>();
        pieceImagesSelected = new HashMap<>();
        String[] pieceTypes = {"p", "n", "b", "r", "q", "k"};
        String[] pieceColors = {"l", "d"};
        ImageLoader imageLoader = new ImageLoader();

        for (String pieceColor : pieceColors) {
            for (String pieceType : pieceTypes) {
                Image originalImage = imageLoader.loadImage(String.format("Chess_%s%st45.png", pieceType, pieceColor));
                Image scaledImage = originalImage.getScaledInstance(SQUARE_SIZE - 16, SQUARE_SIZE - 16, Image.SCALE_SMOOTH);
                pieceImagesRegular.put(
                        Piece.getPieceTypeByFile(pieceType) | Piece.getPieceColorByFile(pieceColor),
                        scaledImage
                );
            }
        }

        for (String pieceColor : pieceColors) {
            for (String pieceType : pieceTypes) {
                Image originalImage = imageLoader.loadImage(String.format("Chess_%s%st45.png", pieceType, pieceColor));
                Image scaledImage = originalImage.getScaledInstance(SQUARE_SIZE, SQUARE_SIZE, Image.SCALE_SMOOTH);
                pieceImagesSelected.put(
                        Piece.getPieceTypeByFile(pieceType) | Piece.getPieceColorByFile(pieceColor),
                        scaledImage
                );
            }
        }

        soundPlayer = new SoundPlayer();
        board = new Board(soundPlayer, pieceImagesRegular);
        MoveValidation moveValidation = new MoveValidation(board);
        chessMouseListener = new ChessMouseListener(
                this,
                board,
                moveValidation,
                soundPlayer
        );

        setBackground(Color.DARK_GRAY);
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        this.addMouseListener(chessMouseListener);
        this.addMouseMotionListener(chessMouseListener);

        JFrame frame = new JFrame("Chess Game");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(SQUARE_SIZE * 8 + PADDING * 2, SQUARE_SIZE * 8 + SQUARE_SIZE * 2);
        frame.setResizable(false);
        frame.add(this);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        setRenderingHints(g2d);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Color squareColor = getSquareColor(row, col);

                if (chessMouseListener.isHighlightedSquare(row, col)) {
                    squareColor = ColorTinter.tintSquareYellow(getSquareColor(row, col));
                }

                if (chessMouseListener.isPossibleMoveSquare(row, col)) {
                    squareColor = ColorTinter.tintSquareBlue(getSquareColor(row, col));
                }

                drawSquare(g2d, row, col, squareColor);
                drawPiece(g2d, row, col);
            }
        }

        drawSelectedPiece(g2d);
    }

    private void setRenderingHints(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    private Color getSquareColor(int row, int col) {
        return ((row + col) % 2 == 0) ? WHITE_SQUARE_COLOR : BLACK_SQUARE_COLOR;
    }

    private void drawSquare(Graphics2D g2d, int row, int col, Color squareColor) {
        int x = col * SQUARE_SIZE + PADDING;
        int y = row * SQUARE_SIZE + SQUARE_SIZE;
        g2d.setColor(squareColor);
        g2d.fillRect(x, y, SQUARE_SIZE, SQUARE_SIZE);
    }

    private void drawPiece(Graphics2D g2d, int row, int col) {
        int piece = board.getPieceAt(row, col);
        Point currentSquare = new Point(col, row);

        if (piece != Piece.NONE &&
                (chessMouseListener.getSelectedPiece() == null || !chessMouseListener.getSelectedPiece().equals(currentSquare))) {
            Image pieceImage = pieceImagesRegular.get(piece);
            int x = col * SQUARE_SIZE + PADDING + 8;
            int y = row * SQUARE_SIZE + SQUARE_SIZE + 8;
            g2d.drawImage(pieceImage, x, y, SQUARE_SIZE - 16, SQUARE_SIZE - 16, this);
        }
    }

    private void drawSelectedPiece(Graphics2D g2d) {
        if (chessMouseListener.getSelectedPiece() != null &&
                !MoveValidation.isInvalidPlacement(chessMouseListener.getSelectedPiece().y, chessMouseListener.getSelectedPiece().x)) {
            Image selectedPieceImage = pieceImagesSelected.get(board.getPieceAt(chessMouseListener.getSelectedPiece().y, chessMouseListener.getSelectedPiece().x));
            int x = chessMouseListener.getInitialDrag().x - SQUARE_SIZE / 2;
            int y = chessMouseListener.getInitialDrag().y - SQUARE_SIZE / 2;
            g2d.drawImage(selectedPieceImage, x, y, SQUARE_SIZE, SQUARE_SIZE, this);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChessGUI::new);
    }

}
