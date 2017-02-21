package components;

import org.javatuples.Pair;
import org.lwjgl.opengl.GL11;
import physics.Rigidbody;
import utils.Util;
import window.GameWindow;

import java.util.concurrent.ThreadLocalRandom;

public final class Wind implements Component {
    private long cumTime = 0L;
    private float wind;

    private final float drawOffsetX;
    private final float drawOffsetY;

    private static final float WIND_MAX = 5.0f;

    public Wind(float drawOffsetX, float drawOffsetY) {
        this.drawOffsetX = drawOffsetX;
        this.drawOffsetY = drawOffsetY;
        wind = 2.0f * ThreadLocalRandom.current().nextFloat() - 1.0f;
    }

    @Override
    public void draw() {
        cumTime = GameWindow.deltaTime + cumTime;
        if (cumTime >= 500L) {
            cumTime = 0L;
            wind = 2.0f * ThreadLocalRandom.current().nextFloat() - 1.0f;
        }
        for (Rigidbody rigidbody : GameWindow.singleton().getRigidbodyCollection()) {
            rigidbody.addForce(wind * 6.f, 0);
        }


        final float windIndicateMax = 0.2f;
        Util.drawLock.lock();
        try {
            GL11.glColor4fv(Util.OpenGLRGB(0, 0, 0, 0));
            GL11.glLineWidth(3.0f);
            GL11.glBegin(GL11.GL_LINES);
            GL11.glVertex2f(drawOffsetX, drawOffsetY);
            GL11.glVertex2f(
                    drawOffsetX + wind * 0.1f,
                    drawOffsetY
            );
            GL11.glEnd();
        } finally {
            Util.drawLock.unlock();
        }

        Util.drawLock.lock();
        try {
            GL11.glColor4fv(Util.OpenGLRGB(0, 0, 0, 0));
            GL11.glLineWidth(1.0f);
            GL11.glBegin(GL11.GL_LINES);
            GL11.glVertex2f(drawOffsetX, drawOffsetY - 0.03f);
            GL11.glVertex2f(drawOffsetX, drawOffsetY + 0.03f);
            GL11.glEnd();
        } finally {
            Util.drawLock.unlock();
        }
    }

}
