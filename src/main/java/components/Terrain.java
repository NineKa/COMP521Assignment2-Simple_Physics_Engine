package components;

import org.lwjgl.opengl.GL11;
import utils.Util;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public final class Terrain implements Component {
    public final float[] groundVertex;
    private final int numSlice;

    private float mountHeight = 0.0f;
    private float leftSlopePercentage = 0.0f;
    private float rightSlopePercentage = 0.0f;

    public Terrain(int numSlice) {
        this.numSlice = numSlice;
        this.groundVertex = new float[numSlice];
        Arrays.fill(this.groundVertex, 0.0f);

        final int oneThirdPoint = numSlice / 3;
        final int twoThirdPoint = 2 * numSlice / 3;

        while (!((0.4 < mountHeight) && (mountHeight < 0.5))) mountHeight = ThreadLocalRandom.current().nextFloat();
        while (!(   leftSlopePercentage + rightSlopePercentage <= 0.7f  &&
                    leftSlopePercentage >= 0.3f                         &&
                    rightSlopePercentage >= 0.3f                            )) {
            leftSlopePercentage = ThreadLocalRandom.current().nextFloat();
            rightSlopePercentage = ThreadLocalRandom.current().nextFloat();
        }

        final int mountTopLeft  = (int) ((twoThirdPoint - oneThirdPoint) * leftSlopePercentage) + oneThirdPoint;
        final int mountTopRight = twoThirdPoint - (int) ((twoThirdPoint - oneThirdPoint) * rightSlopePercentage);
        final int mountBottomLeft = oneThirdPoint;
        final int mountBottomRight = twoThirdPoint;

        final int leftSlopeLength = Math.abs(mountTopLeft - mountBottomLeft);
        final int rightSlopeLength = Math.abs(mountTopRight - mountBottomRight);

        final float[] decorateLeftSlope  = bisectionDecorate(0.0f, mountHeight, leftSlopeLength);
        final float[] decorateRightSlope = bisectionDecorate(mountHeight, 0.0f, rightSlopeLength);

        for (int i = 0; i < groundVertex.length; i++) {
            if (i < mountBottomLeft) {
                groundVertex[i] = 0.0f;
            } else if (i < mountTopLeft) {
                groundVertex[i] = decorateLeftSlope[i - mountBottomLeft];
            } else if (i < mountTopRight) {
                groundVertex[i] = mountHeight;
            } else if (i < mountBottomRight) {
                groundVertex[i] = decorateRightSlope[i - mountTopRight];
            } else if (i < groundVertex.length) {
                groundVertex[i] = 0.0f;
            }
        }
    }

    private static float[] bisectionDecorate(float leftHeight, float rightHeight, int width) {
        if (width <= 0) throw new IllegalArgumentException("decoration width has to be greater than zero");
        float[] deltaArray = new float[width];
        if (width <= 10) {
            for (int i = 0; i < width; i++) {
                deltaArray[i] = (rightHeight - leftHeight) * ((float) i / width) + leftHeight;
            }
            return deltaArray;
        }
        float percentage = 0.0f;
        while (!(0.3f <= percentage && percentage <= 0.7f)) percentage = ThreadLocalRandom.current().nextFloat();

        final float midPointHeight = (rightHeight - leftHeight) * percentage + leftHeight;
        final float[] lhsArray = bisectionDecorate(leftHeight, midPointHeight, width / 2);
        final float[] rhsArray = bisectionDecorate(midPointHeight, rightHeight, width / 2);

        for (int i = 0; i < width; i++) {
            if (i < width / 2) {
                deltaArray[i] = lhsArray[i];
            } else if (i > width / 2) {
                deltaArray[i] = rhsArray[i - width / 2 - 1];
            } else if (i == width / 2) {
                deltaArray[i] = midPointHeight;
            }
        }
        return deltaArray;
    }

    @Override
    public void draw() {
        GL11.glClearColor(204.0f / 255, 230.0f / 255, 255.0f / 255, 0.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        drawTerrain(Util.OpenGLRGB(  0, 128,  43, 0), -0.8f);
        drawTerrain(Util.OpenGLRGB(134,  89,  45, 0), -0.9f);
    }

    private void drawTerrain(float[] color, float terrainBaseline) {
        Util.drawLock.lock();
        GL11.glColor4fv(color);
        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex2f(+0.0f, -1.0f);
        GL11.glVertex2f(-1.0f, -1.0f);
        GL11.glVertex2f(-1.0f, terrainBaseline);
        for (int i = 0; i < numSlice; i++) {
            GL11.glVertex2f(-1.0f + 2 * ((float) (i + 1)) / numSlice, terrainBaseline + groundVertex[i]);
        }
        GL11.glVertex2f(+1.0f, -1.0f);
        GL11.glEnd();
        Util.drawLock.unlock();
    }

    @Override
    public String toString() {
        return String.format(
                "[components.Terrain@%x, mountHeight=%.2f, leftSlopePercentage=%.2f, rightSlopePercentage=%.2f]",
                this.hashCode(),
                this.mountHeight,
                this.leftSlopePercentage,
                this.rightSlopePercentage
        );
    }
}
