package hr.mlinx.chess.ui;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class ImageLoader {

    public Image loadImage(String resourceName) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceName)) {
            if (inputStream != null) {
                return ImageIO.read(inputStream);
            } else {
                throw new IOException(resourceName + " not found");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
