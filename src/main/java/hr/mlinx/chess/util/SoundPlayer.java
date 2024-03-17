package hr.mlinx.chess.util;

import hr.mlinx.chess.board.MoveType;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

public class SoundPlayer {

    private final Map<MoveType, String> moveTypeSounds;
    private final Map<Warning, String> warningSounds;

    public SoundPlayer() {
        moveTypeSounds = new EnumMap<>(MoveType.class);
        moveTypeSounds.put(MoveType.REGULAR, "move.wav");
        moveTypeSounds.put(MoveType.CAPTURE, "capture.wav");
        moveTypeSounds.put(MoveType.CHECK, "move-check.wav");
        moveTypeSounds.put(MoveType.MATE, "game-end.wav");
        moveTypeSounds.put(MoveType.CASTLE, "castle.wav");
        moveTypeSounds.put(MoveType.PROMOTION, "promote.wav");

        warningSounds = new EnumMap<>(Warning.class);
        warningSounds.put(Warning.ILLEGAL_MOVE, "illegal.wav");
        warningSounds.put(Warning.TEN_SECONDS_LEFT, "tenseconds.wav");
    }

    public void playMoveSound(MoveType moveType) {
        play(moveTypeSounds.get(moveType));
    }

    public void playWarningSound(Warning warning) {
        play(warningSounds.get(warning));
    }

    private void play(String soundFilename) {
        try {
            Clip clip = AudioSystem.getClip();
            InputStream is = getClass().getClassLoader().getResourceAsStream(soundFilename);

            if (is == null) {
                throw new IOException(soundFilename + " not found");
            }

            AudioInputStream inputStream = AudioSystem.getAudioInputStream(is);
            clip.open(inputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

}
