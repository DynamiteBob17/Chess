package hr.mlinx.chess.ui;

import hr.mlinx.chess.board.Board;
import hr.mlinx.chess.board.MoveType;
import hr.mlinx.chess.board.Piece;
import hr.mlinx.chess.util.SoundPlayer;
import hr.mlinx.chess.util.Warning;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class Clock extends JPanel {

    private double time;
    private double increment;
    private final Board board;

    private double blackTime;
    private double whiteTime;
    private boolean warningSoundPlayedForWhite = false;
    private boolean warningSoundPlayedForBlack = false;

    private final JLabel blackClock;
    private final JLabel whiteClock;

    private boolean isWhiteTurn = true;
    private final Timer timer;
    private static final Color TURN_COLOR = new Color(172, 255, 252);

    public Clock(double time, double increment, Board board, SoundPlayer soundPlayer) {
        setLayout(new GridLayout(2, 1));
        setPreferredSize(new Dimension((int) (160 * Scaling.SCALE), 0));

        this.blackTime = this.whiteTime = this.time = time;
        this.increment = increment;
        this.board = board;

        blackClock = new JLabel();
        whiteClock = new JLabel();
        blackClock.setForeground(Color.BLACK);
        whiteClock.setForeground(TURN_COLOR);
        Font timeFont = blackClock.getFont().deriveFont(Font.BOLD).deriveFont(24f * (float) Scaling.SCALE);
        blackClock.setFont(timeFont);
        whiteClock.setFont(timeFont);
        renderBlackTime();
        renderWhiteTime();

        add(blackClock);
        add(whiteClock);

        timer = new Timer(100, e -> {
            if (isWhiteTurn) {
                whiteTime -= 0.1;
                renderWhiteTime();
                if (whiteTime <= 0) {
                    ChessDialog.showGameOverDialog(Piece.BLACK);
                    board.setGameOver();
                    soundPlayer.playMoveSound(MoveType.GAME_OVER);
                    board.newGame();
                    newGame();
                } else if (whiteTime < 10 && !warningSoundPlayedForWhite) {
                    soundPlayer.playWarningSound(Warning.TEN_SECONDS_LEFT);
                    warningSoundPlayedForWhite = true;
                }
            } else {
                blackTime -= 0.1;
                renderBlackTime();
                if (blackTime <= 0) {
                    ChessDialog.showGameOverDialog(Piece.WHITE);
                    board.setGameOver();
                    soundPlayer.playMoveSound(MoveType.GAME_OVER);
                    board.newGame();
                    newGame();
                }  else if (blackTime < 10 && !warningSoundPlayedForBlack) {
                    soundPlayer.playWarningSound(Warning.TEN_SECONDS_LEFT);
                    warningSoundPlayedForBlack = true;
                }
            }
        });
    }

    private String formatTime(double seconds) {
        int minutes = (int) (seconds / 60);

        String formattedTime;
        if (seconds < 10) {
            formattedTime = String.format("      %2d:%04.1f", minutes, seconds);
            if (isWhiteTurn) {
                whiteClock.setForeground(Color.RED);
            } else {
                blackClock.setForeground(Color.RED);
            }
        } else {
            formattedTime = String.format("      %2d:%02d", minutes, (int) (seconds % 60));
        }

        return formattedTime;
    }

    private void renderBlackTime() {
        blackClock.setText(formatTime(blackTime));
    }

    private void renderWhiteTime() {
        whiteClock.setText(formatTime(whiteTime));
    }

    public void setTimeControls(double time, double increment) {
        this.time = time;
        this.increment = increment;
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        timer.stop();

        if (whiteTime < 0d) {
            whiteTime = 0;
            renderWhiteTime();
        } else if (blackTime < 0d) {
            blackTime = 0;
            renderBlackTime();
        }
    }

    public void switchTurn() {
        if (board.isGameOver()) {
            return;
        }

        if (isWhiteTurn) {
            whiteTime += increment;
            if (whiteTime >= 10) {
                warningSoundPlayedForWhite = false;
            }

            renderWhiteTime();
            renderBlackTime();
            whiteClock.setForeground(Color.WHITE);
            blackClock.setForeground(TURN_COLOR);
        } else {
            blackTime += increment;
            if (blackTime >= 10) {
                warningSoundPlayedForBlack = false;
            }

            renderWhiteTime();
            renderBlackTime();
            whiteClock.setForeground(TURN_COLOR);
            blackClock.setForeground(Color.BLACK);
        }

        isWhiteTurn = !isWhiteTurn;
    }

    public void newGame() {
        timer.stop();
        blackTime = time;
        whiteTime = time;
        warningSoundPlayedForBlack = false;
        warningSoundPlayedForWhite = false;
        renderBlackTime();
        renderWhiteTime();
    }

}
