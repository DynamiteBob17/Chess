package hr.mlinx.chess.util;

import java.awt.Color;

public class ColorTinter {

    private ColorTinter() {}

    public static Color tintColorYellow(Color originalColor) {
        int colorAdjustment = 100;

        int red = Math.min(255, Math.max(0, originalColor.getRed() + colorAdjustment));
        int green = Math.min(255, Math.max(0, originalColor.getGreen() + colorAdjustment));
        int blue = Math.min(255, Math.max(0, originalColor.getBlue() - colorAdjustment));

        return new Color(red, green, blue);
    }

    public static Color tintColorBlue(Color originalColor) {
        int colorAdjustment = 33;

        int red = Math.min(255, Math.max(0, originalColor.getRed() - colorAdjustment));
        int green = Math.min(255, Math.max(0, originalColor.getGreen() - colorAdjustment));
        int blue = Math.min(255, Math.max(0, originalColor.getBlue() + colorAdjustment));

        return new Color(red, green, blue);
    }

}
