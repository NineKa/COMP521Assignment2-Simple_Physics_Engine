package components.goat;

import physics.verlet.Link;
import physics.verlet.PointMass;

public final class GoatNode extends PointMass {
    public GoatNode(float initX, float initY, boolean visible) {
        super(initX, initY, 1.0f, visible);
    }

    public GoatNode(float initX, float initY, boolean visible, float mass) {
        super(initX, initY, mass, visible);
    }

    @Override
    public void updateForce() {
        addForce(0, -2.0f * mass); // Gravity
    }

    void setDrawRadius(float radius) {
        this.printRadius = radius;
    }

    @Override
    public void calculate() {
        super.calculate();
        for (int i = 0; i < 10; i++) {
            for (Link link : verletLinks) link.solve();
        }
    }

    public void clearForce() {
        this.pendingForce.clear();
    }
}
