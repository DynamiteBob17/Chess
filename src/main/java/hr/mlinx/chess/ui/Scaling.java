package hr.mlinx.chess.ui;

import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;

public class Scaling {

    private Scaling() {}

    public static final Dimension RES = Toolkit.getDefaultToolkit().getScreenSize();
    public static final double SCALE = RES.getWidth() / 1920.0;

    public static void setUIPresets() {
        float fontSize = 15f * (float) SCALE;
        Font messageFont = new Font("Verdana", Font.PLAIN, (int) fontSize);
        Font componentFont = new Font("Segoe", Font.PLAIN, (int) fontSize);

        UIManager.put("OptionPane.messageFont", messageFont);
        UIManager.put("OptionPane.buttonFont", messageFont);
        UIManager.put("TextField.font", messageFont);
        UIManager.put("ComboBox.font", componentFont);
        UIManager.put("Label.font", componentFont);
        UIManager.put("Button.font", componentFont);

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

}
