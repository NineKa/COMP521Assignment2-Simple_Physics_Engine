package components;

import utils.Util;

public abstract class Cannon implements Component {
    protected final float cannonBarrelLength = 0.2f;
    protected final float cannonBarrelHeight = 0.2f / 5;
    protected float angle = 45.0f;

    protected final float wheelOffsetPercentage = 0.5f;
    protected final float wheelSizePercentage = 0.5f;

    protected final float cannonBarrelColor[] = Util.OpenGLRGB(  0,   0,   0,   0);
    protected final float cannonWheelColor[]  = Util.OpenGLRGB(102,  51,   0,   0);

    protected boolean active = false;

    @Override
    public void draw() {
        drawBarrel();
        drawWheel();
        if (active) drawActive();
    }

    protected abstract void drawBarrel();
    protected abstract void drawWheel();
    protected abstract void drawActive();

    public abstract float[] getShootPosition();
    public abstract float   getShootAngle();

    public void setShootAngle(float angle) {
        this.angle = angle;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
