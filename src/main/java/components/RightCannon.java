package components;

import org.lwjgl.opengl.GL11;
import utils.Util;

public final class RightCannon extends Cannon implements Component {
    private final float lowerRightX;
    private final float lowerRightY;

    public RightCannon(float lowerRightX, float lowerRightY) {
        this.lowerRightX = lowerRightX;
        this.lowerRightY = lowerRightY;
    }

    @Override
    protected void drawBarrel() {
        final float cannonBarrelBottom[] = new float[2];
        cannonBarrelBottom[0] = lowerRightX - (float) (cannonBarrelHeight * Math.sin(Math.toRadians(angle)));
        cannonBarrelBottom[1] = lowerRightY;

        final float cannonBarrelRight[] = new float[2];
        cannonBarrelRight[0] = lowerRightX;
        cannonBarrelRight[1] = lowerRightY + (float) (cannonBarrelHeight * Math.cos(Math.toRadians(angle)));

        final float cannonBarrelTop[] = new float[2];
        cannonBarrelTop[0] = lowerRightX - (float) (cannonBarrelLength * Math.cos(Math.toRadians(angle)));
        cannonBarrelTop[1] = lowerRightY +
                (float) (cannonBarrelHeight * Math.cos(Math.toRadians(angle))) +
                (float) (cannonBarrelLength * Math.sin(Math.toRadians(angle))) ;

        final float cannonBarrelLeft[] = new float[2];
        cannonBarrelLeft[0] = lowerRightX -
                (float) (cannonBarrelLength * Math.cos(Math.toRadians(angle))) -
                (float) (cannonBarrelHeight * Math.sin(Math.toRadians(angle))) ;
        cannonBarrelLeft[1] = lowerRightY +
                (float) (cannonBarrelLength * Math.sin(Math.toRadians(angle))) ;

        Util.drawLock.lock();
        try {
            GL11.glColor4fv(cannonBarrelColor);
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
            GL11.glVertex2fv(cannonBarrelBottom);
            GL11.glVertex2fv(cannonBarrelRight);
            GL11.glVertex2fv(cannonBarrelTop);
            GL11.glVertex2fv(cannonBarrelLeft);
            GL11.glEnd();
        } finally {
            Util.drawLock.unlock();
        }
    }

    @Override
    protected void drawWheel() {
        final float width = (float) (cannonBarrelLength * Math.cos(Math.toRadians(angle))) +
                (float) (cannonBarrelHeight * Math.sin(Math.toRadians(angle)));
        final float wheelRadius = width * wheelSizePercentage / 2;
        final float wheelCenter[] = new float[2];
        wheelCenter[0] = lowerRightX - width * wheelOffsetPercentage;
        wheelCenter[1] = lowerRightY + wheelRadius;

        Util.drawLock.lock();
        try {
            GL11.glColor4fv(cannonWheelColor);
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
            for (int i = 0; i < 360; i++) {
                float circlePos[] = new float[2];
                circlePos[0] = (float) (wheelCenter[0] + wheelRadius * Math.sin(Math.toRadians((double) i)));
                circlePos[1] = (float) (wheelCenter[1] + wheelRadius * Math.cos(Math.toRadians((double) i)));
                GL11.glVertex2fv(circlePos);
            }
            GL11.glEnd();
        } finally {
            Util.drawLock.unlock();
        }
    }

    @Override
    protected void drawActive() {
        final float width = (float) (cannonBarrelLength * Math.cos(Math.toRadians(angle))) +
                (float) (cannonBarrelHeight * Math.sin(Math.toRadians(angle)));
        Util.drawLock.lock();
        try {
            GL11.glColor4fv(Util.OpenGLRGB(255, 0, 0, 0));
            GL11.glLineWidth(3.0f);
            GL11.glBegin(GL11.GL_LINES);
            GL11.glVertex2f(lowerRightX, lowerRightY);
            GL11.glVertex2f(lowerRightX - width, lowerRightY);
            GL11.glEnd();
        } finally {
            Util.drawLock.unlock();
        }
    }

    @Override
    public float[] getShootPosition() {
        float coordinate[] = new float[2];

        coordinate[0] = lowerRightX -
                (float) ((cannonBarrelLength + 0.005f) * Math.cos(Math.toRadians(angle))) -
                (float) (cannonBarrelHeight / 2 * Math.sin(Math.toRadians(angle)));
        coordinate[1] = lowerRightY +
                (float) ((cannonBarrelLength + 0.005f) * Math.sin(Math.toRadians(angle))) +
                (float) (cannonBarrelHeight / 2 * Math.cos(Math.toRadians(angle)));

        return coordinate;
    }

    @Override
    public float getShootAngle() {
        return  angle;
    }
}
