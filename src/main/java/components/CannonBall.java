package components;

import components.goat.Goat;
import org.javatuples.Pair;
import org.lwjgl.opengl.GL11;
import physics.Collider;
import physics.Rigidbody;
import utils.Util;
import window.GameWindow;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class CannonBall extends Rigidbody implements Component, Collider {
    private final float ejectForce = 1100.0f;
    private final float gravityForce = -20.0f;
    private final float[] cannonBallColor = Util.OpenGLRGB(51, 51, 0, 0);

    public static final float radius = 0.2f / 8;

    private boolean ejectFlag = false;
    private final float ejectAngle;

    public CannonBall(float initX, float initY, float ejectAngle) {
        super(initX, initY, 10.0f);
        this.ejectAngle = ejectAngle;
    }

    @Override
    public void draw() {
        if (!(-1.0 <= coordinate[0] && coordinate[0] <= 1.0) ||
                !(-1.0 <= coordinate[1] && coordinate[1] <= 1.0)) {
            GameWindow.singleton().removeComponent(this);
            GameWindow.singleton().removeRigidbody(this);
            GameWindow.singleton().collisionManager.removeCollider(this);
        }

        Util.drawLock.lock();
        try {
            GL11.glColor4fv(cannonBallColor);
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
            GL11.glVertex2fv(coordinate);
            for (int i = 0; i < 365; i++) {
                GL11.glVertex2f(
                        coordinate[0] + (float) (radius * Math.cos(Math.toRadians((double) i))),
                        coordinate[1] + (float) (radius * Math.sin(Math.toRadians((double) i)))
                );
            }
            GL11.glEnd();
        } finally {
            Util.drawLock.unlock();
        }
    }

    @Override
    public void updateForce() {
        if (!ejectFlag) {
            this.addForce(
                    (float) (ejectForce * Math.sin(Math.toRadians(ejectAngle))),
                    (float) (ejectForce * Math.cos(Math.toRadians(ejectAngle)))
            );
            ejectFlag = true;
        }

        this.addForce(0.0f, gravityForce);

        final float velocity = (float) Math.sqrt(Math.pow(momentum[0] / mass, 2) + Math.pow(momentum[1] / mass, 2));
        if (velocity != 0.0f) {
            final float airDrag = (float) (-0.1 * Math.pow(velocity, 2));
            Pair<Float, Float> airResistance = new Pair<>(
                    momentum[0] / (mass * velocity) * airDrag,
                    momentum[1] / (mass * velocity) * airDrag
            );
            this.addForce(airResistance);
        }
    }

    @Override
    public List<Pair<Float, Float>> getColliderPolygon() {
        List<Pair<Float, Float>> polygonVertexList = new LinkedList<>();
        Pair<Float, Float> future = new Pair<>(
                momentum[0] / mass * GameWindow.deltaTime * 0.5f * 0.001f,
                momentum[1] / mass * GameWindow.deltaTime * 0.5f * 0.001f
        );
        for (int i = 0; i < 360; i++) {
            polygonVertexList.add(new Pair<>(
                    coordinate[0] + (float) (Math.sin(Math.toRadians((double) i)) * radius) + future.getValue0(),
                    coordinate[1] + (float) (Math.cos(Math.toRadians((double) i)) * radius) + future.getValue1()
            ));
        }
        return Collections.unmodifiableList(polygonVertexList);
    }

    @Override
    public void onCollideTerrain(Pair<Float, Float> segLeft, Pair<Float, Float> segRight) {
        if (segLeft.getValue0() <= 2.0f / 3 - 1.0f && segRight.getValue0() <= 2.0f / 3 - 1.0f) {
            GameWindow.singleton().removeComponent(this);
            GameWindow.singleton().removeRigidbody(this);
            GameWindow.singleton().collisionManager.removeCollider(this);
        }
        if (segLeft.getValue0() >= 2.0f * 2 / 3 - 1.0f && segRight.getValue0() >= 2.0f * 2 / 3 - 1.0f) {
            GameWindow.singleton().removeComponent(this);
            GameWindow.singleton().removeRigidbody(this);
            GameWindow.singleton().collisionManager.removeCollider(this);
        }

        if (segLeft.getValue1() - segRight.getValue1() != 0) {
            //momentum[0] = +momentum[0] * 0.7f;
            //momentum[1] = -momentum[1] * 0.7f;

            float d = (segRight.getValue1() - segLeft.getValue1()) / (segRight.getValue0() - segRight.getValue0());
            float dPrime = -1 / d;

            Pair<Float, Float> verVector = new Pair<>(1.0f, dPrime);
            Pair<Float, Float> inVector = new Pair<>(momentum[0], momentum[1]);

            double cosTheta = (verVector.getValue0() * inVector.getValue0() + verVector.getValue1() * inVector.getValue1()) /
                    (Math.sqrt(Math.pow(verVector.getValue0(), 2) + Math.pow(verVector.getValue1(), 2)) +
                            Math.sqrt(Math.pow(inVector.getValue0(), 2) + Math.pow(inVector.getValue1(), 2))
                    );
            double theta = Math.acos(cosTheta);

            Pair<Float, Float> v1 = new Pair<>(
                    (float) (-inVector.getValue0() * Math.cos(theta)),
                    (float) (-inVector.getValue1() * Math.cos(theta))
            );
            Pair<Float, Float> v2 = new Pair<>(
                    (float) (-inVector.getValue0() * Math.sin(theta)),
                    (float) (-inVector.getValue1() * Math.sin(theta))
            );

            momentum[0] = (v1.getValue0() + v2.getValue0()) * 0.5f;
            momentum[1] = (v1.getValue1() + v2.getValue1()) * 0.5f;
        } else {
            momentum[0] = +momentum[0] * 0.5f;
            momentum[1] = -momentum[1] * 0.5f;
        }

        if (Math.sqrt(Math.pow(momentum[0], 2) + Math.pow(momentum[1], 2)) <= 0.15f) {
            GameWindow.singleton().removeComponent(this);
            GameWindow.singleton().removeRigidbody(this);
            GameWindow.singleton().collisionManager.removeCollider(this);
        }
    }

    @Override
    public String toString() {
        return String.format("[CannonBall@%x, X:%.2f, Y:%.2f]", this.hashCode(), coordinate[0], coordinate[1]);
    }

    @Override
    public void onCollideWithEachother(Collider collider, Pair<Float, Float> pos) {
        assert collider instanceof Goat;
        GameWindow.singleton().removeComponent(this);
        GameWindow.singleton().removeRigidbody(this);
        GameWindow.singleton().collisionManager.removeCollider(this);
    }
}
