package components.goat;

import components.CannonBall;
import components.Component;
import org.javatuples.Pair;
import org.lwjgl.opengl.GL11;
import physics.Collider;
import physics.Rigidbody;
import physics.verlet.Link;
import utils.Util;
import window.GameWindow;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public final class Goat extends Rigidbody implements Component, Collider {
    private GoatNode horn;
    private GoatNode beard;
    private GoatNode eye;

    private GoatNode headTopLeft;
    private GoatNode headBottomLeft, headBottomRight;

    private GoatNode bodyTopLeft, bodyTopRight;
    private GoatNode bodyBottomLeft, bodyBottomRight;

    private GoatNode leftLeg, rightLeg;

    private List<Link> links = new LinkedList<>();

    private final float referenceX, referenceY;
    private final float shootAngle;
    private static final float UNIT_LENGTH = 0.02f;

    public Goat(float referenceX, float referenceY, float angle) {
        super(referenceX, referenceY, 14.0f);
        this.referenceX = referenceX;
        this.referenceY = referenceY;
        this.shootAngle = angle;

        bodyTopRight = new GoatNode(
                referenceX + UNIT_LENGTH * (float) Math.sin(Math.toRadians(angle)),
                referenceY + UNIT_LENGTH * (float) Math.cos(Math.toRadians(angle)),
                true
        );

        bodyBottomRight = new GoatNode(
                referenceX - UNIT_LENGTH * (float) Math.sin(Math.toRadians(angle)),
                referenceY - UNIT_LENGTH * (float) Math.cos(Math.toRadians(angle)),
                true,
                3.0f
        );

        rightLeg = new GoatNode(
                referenceX - 2 * UNIT_LENGTH * (float) Math.sin(Math.toRadians(angle)),
                referenceY - 2 * UNIT_LENGTH * (float) Math.cos(Math.toRadians(angle)),
                true,
                5.0f
        );

        headBottomRight = new GoatNode(
                referenceX - 3 * UNIT_LENGTH * (float) Math.cos(Math.toRadians(angle)),
                referenceY + 3 * UNIT_LENGTH * (float) Math.sin(Math.toRadians(angle)),
                true
        );

        bodyTopLeft = new GoatNode(
                headBottomRight.getCoordinate()[0] + UNIT_LENGTH * (float) Math.sin(Math.toRadians(angle)),
                headBottomRight.getCoordinate()[1] + UNIT_LENGTH * (float) Math.cos(Math.toRadians(angle)),
                true
        );

        bodyBottomLeft = new GoatNode(
                headBottomRight.getCoordinate()[0] - UNIT_LENGTH * (float) Math.sin(Math.toRadians(angle)),
                headBottomRight.getCoordinate()[1] - UNIT_LENGTH * (float) Math.cos(Math.toRadians(angle)),
                true
        );

        leftLeg = new GoatNode(
                headBottomRight.getCoordinate()[0] - 2 * UNIT_LENGTH * (float) Math.sin(Math.toRadians(angle)),
                headBottomRight.getCoordinate()[1] - 2 * UNIT_LENGTH * (float) Math.cos(Math.toRadians(angle)),
                true
        );

        headBottomLeft = new GoatNode(
                headBottomRight.getCoordinate()[0] - UNIT_LENGTH * (float) Math.cos(Math.toRadians(angle)),
                headBottomRight.getCoordinate()[1] + UNIT_LENGTH * (float) Math.sin(Math.toRadians(angle)),
                true
        );


        headTopLeft = new GoatNode(
                bodyTopLeft.getCoordinate()[0] - UNIT_LENGTH * (float) Math.cos(Math.toRadians(angle)),
                bodyTopLeft.getCoordinate()[1] + UNIT_LENGTH * (float) Math.sin(Math.toRadians(angle)),
                true
        );

        float aux1[] = new float[2];
        aux1[0] = headBottomRight.getCoordinate()[0] + 0.5f * UNIT_LENGTH * (float) Math.sin(Math.toRadians(angle));
        aux1[1] = headBottomRight.getCoordinate()[1] + 0.5f * UNIT_LENGTH * (float) Math.cos(Math.toRadians(angle));

        eye = new GoatNode(
                aux1[0] - 0.5f * UNIT_LENGTH * (float) Math.cos(Math.toRadians(angle)),
                aux1[1] + 0.5f * UNIT_LENGTH * (float) Math.sin(Math.toRadians(angle)),
                true
        );
        eye.setDrawRadius(0.005f);

        float aux2[] = new float[2];
        aux2[0] = bodyTopLeft.getCoordinate()[0] + 1.0f * UNIT_LENGTH * (float) Math.sin(Math.toRadians(angle));
        aux2[1] = bodyTopLeft.getCoordinate()[1] + 1.0f * UNIT_LENGTH * (float) Math.cos(Math.toRadians(angle));

        horn = new GoatNode(
                aux2[0] - 0.5f * UNIT_LENGTH * (float) Math.cos(Math.toRadians(angle)),
                aux2[1] + 0.5f * UNIT_LENGTH * (float) Math.sin(Math.toRadians(angle)),
                true
        );

        beard = new GoatNode(
                bodyBottomLeft.getCoordinate()[0] - 0.5f * UNIT_LENGTH * (float) Math.cos(Math.toRadians(angle)),
                bodyBottomLeft.getCoordinate()[1] + 0.5f * UNIT_LENGTH * (float) Math.sin(Math.toRadians(angle)),
                true
        );

        links.add(new Link(bodyTopRight, bodyBottomRight, true));
        links.add(new Link(bodyTopRight, bodyTopLeft, true));
        links.add(new Link(bodyBottomRight, bodyBottomLeft, true));
        links.add(new Link(bodyBottomRight, rightLeg, true));
        links.add(new Link(bodyBottomLeft, leftLeg, true));
        links.add(new Link(headBottomRight, bodyTopLeft, true));
        links.add(new Link(headBottomRight, bodyBottomLeft, true));
        links.add(new Link(headBottomRight, headBottomLeft, true));
        links.add(new Link(headBottomRight, beard, true));
        links.add(new Link(headBottomLeft, beard, true));
        links.add(new Link(headBottomLeft, headTopLeft, true));
        links.add(new Link(headTopLeft, horn, true));
        links.add(new Link(bodyTopLeft, horn, true));
        links.add(new Link(headTopLeft, bodyTopLeft, true));

        links.add(new Link(bodyTopLeft, bodyBottomRight, false));
        links.add(new Link(bodyBottomLeft, bodyTopRight, false));
        links.add(new Link(headTopLeft, headBottomRight, false));
        links.add(new Link(headBottomLeft, bodyTopLeft, false));

        links.add(new Link(headBottomRight, bodyTopRight, false));
        links.add(new Link(headBottomRight, bodyBottomRight, false));

        links.add(new Link(headBottomLeft, bodyTopRight, false));
        links.add(new Link(headBottomLeft, bodyBottomRight, false));
        links.add(new Link(headTopLeft, bodyTopRight, false));
        links.add(new Link(headTopLeft, bodyBottomRight, false));

        links.add(new Link(horn, beard, false));
        links.add(new Link(horn, bodyTopRight, false));
        links.add(new Link(horn, bodyBottomRight, false));
        links.add(new Link(beard, bodyTopRight, false));
        links.add(new Link(beard, bodyBottomRight, false));

        links.add(new Link(eye, headTopLeft, false));
        links.add(new Link(eye, bodyTopLeft, false));
        links.add(new Link(eye, headBottomLeft, false));
        links.add(new Link(eye, headBottomRight, false));
        links.add(new Link(eye, bodyTopRight, false));
        links.add(new Link(eye, bodyBottomRight, false));

        links.add(new Link(leftLeg, bodyBottomRight, false));
        links.add(new Link(rightLeg, bodyBottomLeft, false));
        links.add(new Link(beard, leftLeg, false));
        links.add(new Link(beard, rightLeg, false));
        links.add(new Link(leftLeg, bodyTopRight, false));
        links.add(new Link(rightLeg, bodyTopLeft, false));

        for (Link link : links) {
            link.getMass1().addLink(link);
            link.getMass2().addLink(link);
        }
    }

    private static final float ejectForce = 2000.0f;
    private boolean ejectFlag = false;
    private AtomicBoolean collisionFlag = new AtomicBoolean(false);

    @Override
    public void draw() {
        if (!(-1.0f <= headBottomRight.getCoordinate()[0] && headBottomRight.getCoordinate()[1] <= 1.0f) ||
                !(-1.0f <= headBottomRight.getCoordinate()[1] && headBottomRight.getCoordinate()[1] <= 1.0f)) remove();

        horn.draw();
        beard.draw();
        eye.draw();
        headTopLeft.draw();
        headBottomLeft.draw();
        bodyTopLeft.draw();
        bodyTopRight.draw();
        bodyBottomLeft.draw();
        bodyBottomRight.draw();
        headBottomRight.draw();
        leftLeg.draw();
        rightLeg.draw();
        for (Link link : links) link.draw();

        Util.drawLock.lock();
        try {
            GL11.glColor4fv(Util.OpenGLRGB(0, 0, 0, 0));
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
            GL11.glVertex2f(bodyTopLeft.getCoordinate()[0], bodyTopLeft.getCoordinate()[1]);
            GL11.glVertex2f(bodyTopRight.getCoordinate()[0], bodyTopRight.getCoordinate()[1]);
            GL11.glVertex2f(bodyBottomRight.getCoordinate()[0], bodyBottomRight.getCoordinate()[1]);
            GL11.glVertex2f(bodyBottomLeft.getCoordinate()[0], bodyBottomLeft.getCoordinate()[1]);
            GL11.glEnd();
        } finally {
            Util.drawLock.unlock();
        }

    }

    @Override
    public void calculate() {
        if (leftLeg.getIsPinned() && rightLeg.getIsPinned()) return;
        updateForce();
        horn.calculate();
        beard.calculate();
        eye.calculate();
        headTopLeft.calculate();
        headBottomLeft.calculate();
        headBottomRight.calculate();
        bodyTopLeft.calculate();
        bodyTopRight.calculate();
        bodyBottomLeft.calculate();
        bodyBottomRight.calculate();
        leftLeg.calculate();
        rightLeg.calculate();

        for (int i = 0; i < 10; i++) for (Link link : links) link.solve();
    }

    @Override
    public void updateForce() {
        if (!ejectFlag) {
            beard.addForce(
                    -ejectForce * (float) Math.cos(Math.toRadians(shootAngle)),
                    ejectForce * (float) Math.sin(Math.toRadians(shootAngle))
            );
            ejectFlag = true;
        }
    }

    private void remove() {
        GameWindow.singleton().removeComponent(this);
        GameWindow.singleton().removeRigidbody(this);
        GameWindow.singleton().collisionManager.removeCollider(this);
    }

    @Override
    public void onCollideTerrain(Pair<Float, Float> segLeft, Pair<Float, Float> segRight) {
        if (segLeft.getValue0() <= 2.0f / 3 - 1.0f && segRight.getValue0() <= 2.0f / 3 - 1.0f) {
            remove();
            return;
        }
        if (segLeft.getValue0() >= 2.0f * 2 / 3 - 1.0f && segRight.getValue0() >= 2.0f * 2 / 3 - 1.0f) {
            remove();
            return;
        }
        if (GameWindow.singleton().collisionManager.testCollideTerrian(
                new Pair<>(leftLeg.getCoordinate()[0], leftLeg.getCoordinate()[1])
        )) leftLeg.setPinned(true);
        if (GameWindow.singleton().collisionManager.testCollideTerrian(
                new Pair<>(rightLeg.getCoordinate()[0], rightLeg.getCoordinate()[1])
        )) rightLeg.setPinned(true);

        Consumer<GoatNode> testAndReverse = node -> {
            float X = node.getCoordinate()[0] ;
            float Y = node.getCoordinate()[1] ;
            if (GameWindow.singleton().collisionManager.testCollideTerrian(new Pair<>(X, Y))) {
                float diffX = node.getCoordinate()[0] - node.lastCoordinate[0];
                float diffY = node.getCoordinate()[1] - node.lastCoordinate[1];

                node.lastCoordinate[0] = node.getCoordinate()[0];
                node.lastCoordinate[1] = node.getCoordinate()[1];

                node.clearForce();
                node.addForce(
                        0.0f,
                        30.0f
                );
            }
        };
        testAndReverse.accept(horn);
        testAndReverse.accept(beard);
        testAndReverse.accept(eye);
        testAndReverse.accept(headTopLeft);
        testAndReverse.accept(headBottomRight);
        testAndReverse.accept(headBottomLeft);
        testAndReverse.accept(bodyTopLeft);
        testAndReverse.accept(bodyTopRight);
        testAndReverse.accept(bodyBottomLeft);
        testAndReverse.accept(bodyBottomRight);
    }

    @Override
    public List<Pair<Float, Float>> getColliderPolygon() {
        List<Pair<Float, Float>> retList = new LinkedList<>();
        retList.add(new Pair<>(horn.getCoordinate()[0], horn.getCoordinate()[1]));
        retList.add(new Pair<>(beard.getCoordinate()[0], beard.getCoordinate()[1]));
        retList.add(new Pair<>(eye.getCoordinate()[0], beard.getCoordinate()[1]));
        retList.add(new Pair<>(headTopLeft.getCoordinate()[0], headTopLeft.getCoordinate()[1]));
        retList.add(new Pair<>(headBottomLeft.getCoordinate()[0], headBottomRight.getCoordinate()[1]));
        retList.add(new Pair<>(headBottomRight.getCoordinate()[0], headBottomRight.getCoordinate()[1]));
        retList.add(new Pair<>(bodyTopLeft.getCoordinate()[0], bodyTopLeft.getCoordinate()[1]));
        retList.add(new Pair<>(bodyTopRight.getCoordinate()[0], bodyTopRight.getCoordinate()[1]));
        retList.add(new Pair<>(bodyBottomLeft.getCoordinate()[0], bodyBottomLeft.getCoordinate()[1]));
        retList.add(new Pair<>(bodyBottomRight.getCoordinate()[0], bodyBottomRight.getCoordinate()[1]));
        retList.add(new Pair<>(leftLeg.getCoordinate()[0], leftLeg.getCoordinate()[1]));
        retList.add(new Pair<>(rightLeg.getCoordinate()[0], rightLeg.getCoordinate()[1]));
        return retList;
    }

    @Override
    public void onCollideWithEachother(Collider collider, Pair<Float, Float> pos) {
        this.leftLeg.setPinned(false);
        this.rightLeg.setPinned(false);
        assert collider instanceof CannonBall;

        float momentum[] = ((CannonBall) collider).getMomentum();
        float force[] = new float[2];
        force[0] = momentum[0] / (GameWindow.deltaTime * 0.001f);
        force[1] = momentum[1] / (GameWindow.deltaTime * 0.001f);

        Consumer<GoatNode> checkAndApply = node -> {
            float X = node.getCoordinate()[0] ;
            float Y = node.getCoordinate()[1] ;
            if (X == pos.getValue0() && Y == pos.getValue1()) {
                node.addForce(force[0], force[1]);
            }
        };
        checkAndApply.accept(horn);
        checkAndApply.accept(beard);
        checkAndApply.accept(eye);
        checkAndApply.accept(headTopLeft);
        checkAndApply.accept(headBottomRight);
        checkAndApply.accept(headBottomLeft);
        checkAndApply.accept(bodyTopLeft);
        checkAndApply.accept(bodyTopRight);
        checkAndApply.accept(bodyBottomLeft);
        checkAndApply.accept(bodyBottomRight);
    }
}
