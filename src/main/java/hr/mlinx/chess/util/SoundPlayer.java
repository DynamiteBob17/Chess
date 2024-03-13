package hr.mlinx.chess.util;

import hr.mlinx.chess.board.Piece;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

public class SoundPlayer {

    public void playMoveSound(int toPiece) {
        try {
            Clip clip = AudioSystem.getClip();
            String soundFile = (toPiece == Piece.NONE) ? "move.wav" : "capture.wav";
            InputStream is = getClass().getClassLoader().getResourceAsStream(soundFile);

            if (is == null) {
                throw new IOException(soundFile + " not found");
            }

            AudioInputStream inputStream = AudioSystem.getAudioInputStream(is);
            clip.open(inputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

}
