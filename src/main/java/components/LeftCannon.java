package components;

import org.lwjgl.opengl.GL11;
import utils.Util;

public final class LeftCannon extends Cannon implements Component {
    private final float lowerLeftX;
    private final float lowerLeftY;

    public LeftCannon(float lowerLeftX, float lowerLeftY) {
        this.lowerLeftX = lowerLeftX;
        this.lowerLeftY = lowerLeftY;
    }

    @Override
    protected void drawBarrel() {
        final float barrelCoordinatesBottom[] = new float[2];
        barrelCoordinatesBottom[0] = lowerLeftX + (float) Math.sin(Math.toRadians(angle)) * cannonBarrelHeight;
        barrelCoordinatesBottom[1] = lowerLeftY;

        final float barrelCoordinatesLeft[] = new float[2];
        barrelCoordinatesLeft[0] = lowerLeftX;
        barrelCoordinatesLeft[1] = lowerLeftY + (float) Math.cos(Math.toRadians(angle)) * cannonBarrelHeight;

        final float barrelCoordinatesTop[] = new float[2];
        barrelCoordinatesTop[0] = lowerLeftX + (float) Math.cos(Math.toRadians(angle)) * cannonBarrelLength;
        barrelCoordinatesTop[1] = lowerLeftY +
                (float) Math.sin(Math.toRadians(angle)) * cannonBarrelLength +
                (float) Math.cos(Math.toRadians(angle)) * cannonBarrelHeight;

        final float barrelCoordinatesRight[] = new float[2];
        barrelCoordinatesRight[0] = lowerLeftX +
                (float) Math.sin(Math.toRadians(angle)) * cannonBarrelHeight +
                (float) Math.cos(Math.toRadians(angle)) * cannonBarrelLength;
        barrelCoordinatesRight[1] = lowerLeftY +
                (float) Math.sin(Math.toRadians(angle)) * cannonBarrelLength;

        Util.drawLock.lock();
        try {
            GL11.glColor4fv(cannonBarrelColor);
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
            GL11.glVertex2fv(barrelCoordinatesBottom);
            GL11.glVertex2fv(barrelCoordinatesLeft);
            GL11.glVertex2fv(barrelCoordinatesTop);
            GL11.glVertex2fv(barrelCoordinatesRight);
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
        wheelCenter[0] = lowerLeftX + width * wheelOffsetPercentage;
        wheelCenter[1] = lowerLeftY + wheelRadius;

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
            GL11.glVertex2f(lowerLeftX, lowerLeftY);
            GL11.glVertex2f(lowerLeftX + width, lowerLeftY);
            GL11.glEnd();
        } finally {
            Util.drawLock.unlock();
        }
    }

    @Override
    public float[] getShootPosition() {
        float coordinate[] = new float[2];

        coordinate[0] = lowerLeftX +
                (float) (cannonBarrelLength * Math.cos(Math.toRadians(angle))) +
                (float) (cannonBarrelHeight / 2 * Math.sin(Math.toRadians(angle)));
        coordinate[1] = lowerLeftY +
                (float) (cannonBarrelLength * Math.sin(Math.toRadians(angle))) +
                (float) (cannonBarrelHeight / 2 * Math.cos(Math.toRadians(angle)));

        return coordinate;
    }

    @Override
    public float getShootAngle() {
        return 90.0f - angle;
    }
}
