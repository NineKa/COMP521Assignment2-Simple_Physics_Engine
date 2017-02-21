package physics.verlet;

import components.Component;
import org.lwjgl.opengl.GL11;
import utils.Util;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Link implements Component {
    private final float restingDistance;
    private final float stiffness;
    private final float tearSensitivity;

    private final PointMass mass1;
    private final PointMass mass2;

    private final boolean visible;

    public Link(PointMass mass1, PointMass mass2, float restingDistance,
                float stiffness, float tearSensitivity, boolean visible) {
        this.restingDistance = restingDistance;
        this.stiffness = stiffness;
        this.tearSensitivity = tearSensitivity;
        this.mass1 = mass1;
        this.mass2 = mass2;
        this.visible = visible;
    }

    public Link(PointMass mass1, PointMass mass2, boolean visible) {
        this.restingDistance = (float) Math.sqrt(Math.pow(mass1.getCoordinate()[0] - mass2.getCoordinate()[0], 2) +
                Math.pow(mass1.getCoordinate()[1] - mass2.getCoordinate()[1], 2));
        this.stiffness = 1.0f;
        this.tearSensitivity = 3.0f;
        this.mass1 = mass1;
        this.mass2 = mass2;
        this.visible = visible;
    }

    public void solve() {
        final float coordinate1[] = mass1.getCoordinate();
        final float coordinate2[] = mass2.getCoordinate();

        final float diffX = coordinate1[0] - coordinate2[0];
        final float diffY = coordinate1[1] - coordinate2[1];
        final float absDiff = (float) Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));

        final float ratio = (restingDistance - absDiff) / absDiff;
        if (absDiff > tearSensitivity) mass1.removeLink(this);
        float inverse1 = 1 / mass1.getMass();
        float inverse2 = 1 / mass2.getMass();
        float scalar1 = (inverse1 / (inverse1 + inverse2)) * stiffness;
        float scalar2 = stiffness - scalar1;

        if (!mass1.getIsPinned() && !mass2.getIsPinned()) {
            mass1.setCoordinate(
                    coordinate1[0] + diffX * scalar1 * ratio,
                    coordinate1[1] + diffY * scalar1 * ratio
            );
            mass2.setCoordinate(
                    coordinate2[0] - diffX * scalar2 * ratio,
                    coordinate2[1] - diffY * scalar2 * ratio
            );
        } else if (mass1.getIsPinned() && !mass2.getIsPinned()) {
            mass2.setCoordinate(
                    coordinate2[0] - diffX * ratio,
                    coordinate2[1] - diffY * ratio
            );
        } else if (!mass1.getIsPinned() && mass2.getIsPinned()) {
            mass1.setCoordinate(
                    coordinate1[0] + diffX * ratio,
                    coordinate1[1] + diffY * ratio
            );
        } else {
            /* ignore */
        }
    }

    @Override
    public void draw() {
        if (visible) {
            Util.drawLock.lock();
            try {
                GL11.glColor4fv(Util.OpenGLRGB(0, 0, 0, 0));
                GL11.glLineWidth(1.5f);
                GL11.glBegin(GL11.GL_LINES);
                GL11.glVertex2f(mass1.getCoordinate()[0], mass1.getCoordinate()[1]);
                GL11.glVertex2f(mass2.getCoordinate()[0], mass2.getCoordinate()[1]);
                GL11.glEnd();
            } finally {
                Util.drawLock.unlock();
            }
        }
    }

    public PointMass getMass1() {
        return mass1;
    }

    public PointMass getMass2() {
        return mass2;
    }
}
