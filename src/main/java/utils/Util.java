package utils;

import java.util.concurrent.locks.ReentrantLock;

public final class Util {
    public static ReentrantLock drawLock = new ReentrantLock();

    public static float[] OpenGLRGB (int red, int green, int blue, int alpha) {
        if (    !(0 <= red   && red   <= 255 ) ||
                !(0 <= green && green <= 255 ) ||
                !(0 <= blue  && blue  <= 255 ) ||
                !(0 <= alpha && alpha <= 255)       ) {
            throw new IllegalArgumentException("invalid RGB value");
        }
        return new float[] {
                (float) red   / 255.0f,
                (float) green / 255.0f,
                (float) blue  / 255.0f,
                (float) alpha / 255.0f
        };
    }
}
