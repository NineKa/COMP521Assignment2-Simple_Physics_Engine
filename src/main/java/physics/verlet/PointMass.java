package physics.verlet;

import components.Component;
import org.javatuples.Pair;
import org.lwjgl.opengl.GL11;
import physics.Rigidbody;
import utils.Util;
import window.GameWindow;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class PointMass extends Rigidbody implements Component {
    public float lastCoordinate[] = new float[2];
    private float acceleration[] = new float[2];

    protected List<Link> verletLinks = new LinkedList<>();

    private final boolean visible;
    protected float printRadius = 0.002f;
    private boolean pinned = false;
    private boolean hasPinned = false;

    public PointMass(float xPos, float yPos, float mass, boolean visible) {
        super(xPos, yPos, mass);
        lastCoordinate[0] = coordinate[0];
        lastCoordinate[1] = coordinate[1];
        acceleration[0] = 0.0f;
        acceleration[1] = 0.0f;
        this.visible = visible;
    }

    public abstract void updateForce();

    @Override
    public void calculate() {
        updateForce();
        if (!pinned) {
            for (Pair<Float, Float> force : this.pendingForce) {
                acceleration[0] = acceleration[0] + force.getValue0() / (mass);
                acceleration[1] = acceleration[1] + force.getValue1() / (mass);
            }
            pendingForce.clear();
            acceleration[0] *= 0.015f;
            acceleration[1] *= 0.015f;

            float velocityX = coordinate[0] - lastCoordinate[0];
            float velocityY = coordinate[1] - lastCoordinate[1];

            //velocityX *= 0.9;
            //velocityY *= 0.9;

            final float nextX = coordinate[0] + velocityX + 0.5f * acceleration[0] * (GameWindow.deltaTime * 0.001f);
            final float nextY = coordinate[1] + velocityY + 0.5f * acceleration[1] * (GameWindow.deltaTime * 0.001f);

            lastCoordinate[0] = coordinate[0];
            lastCoordinate[1] = coordinate[1];
            coordinate[0] = nextX;
            coordinate[1] = nextY;
            acceleration[0] = 0.0f;
            acceleration[0] = 0.0f;
        } else {
            pendingForce.clear();
        }
    }

    public float[] getCoordinate() {
        return Arrays.copyOf(coordinate, 2);
    }

    public void setCoordinate(float x, float y) {
        coordinate[0] = x;
        coordinate[1] = y;
    }

    public void  addLink(Link link) {
        verletLinks.add(link);
    }

    public void removeLink(Link link) {
        throw new AssertionError();
    }

    public List<Link> getVerletLinks() {
        return Collections.unmodifiableList(verletLinks);
    }

    @Override
    public void draw() {
        if (visible) {
            Util.drawLock.lock();
            try {
                GL11.glColor4fv(Util.OpenGLRGB(0, 0, 0, 0));
                GL11.glBegin(GL11.GL_TRIANGLE_FAN);
                GL11.glVertex2f(coordinate[0], coordinate[1]);
                for (int i = 0; i < 361; i++) {
                    GL11.glVertex2f(
                            coordinate[0] + (float) (Math.sin(Math.toRadians((double) i)) * printRadius),
                            coordinate[1] + (float) (Math.cos(Math.toRadians((double) i)) * printRadius)
                    );
                }
                GL11.glEnd();
            } finally {
                Util.drawLock.unlock();
            }
        }
    }

    public void setPinned(boolean pinned) {
        if (pinned && !hasPinned) {
            this.pinned = true;
            hasPinned = true;
        } else if (!pinned) {
            this.pinned = false;
        }
    }

    public boolean getIsPinned() {
        return this.pinned;
    }
}
