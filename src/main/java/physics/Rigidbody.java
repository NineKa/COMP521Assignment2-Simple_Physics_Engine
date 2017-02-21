package physics;

import org.javatuples.Pair;
import window.GameWindow;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class Rigidbody {
    protected float coordinate[] = new float[2];

    protected float momentum[] = new float[2];
    protected float mass;

    protected List<Pair<Float, Float>> pendingForce = new LinkedList<>();

    public Rigidbody(float initX, float initY, float mass) {
        this.coordinate[0] = initX;
        this.coordinate[1] = initY;
        this.mass = mass;
    }

    public void calculate() {
        updateForce();
        for (Pair<Float, Float> force : pendingForce) {
            momentum[0] = momentum[0] + force.getValue0() * GameWindow.deltaTime * 0.001f;
            momentum[1] = momentum[1] + force.getValue1() * GameWindow.deltaTime * 0.001f;
        }
        coordinate[0] = momentum[0] / mass * GameWindow.deltaTime * 0.001f + coordinate[0];
        coordinate[1] = momentum[1] / mass * GameWindow.deltaTime * 0.001f + coordinate[1];
        pendingForce.clear();
    }

    public void addForce(Pair<Float, Float> force) {
        pendingForce.add(force);
    }

    public void addForce(float x, float y) {
        pendingForce.add(new Pair<>(x, y));
    }

    public void addForce(float[] force) {
        pendingForce.add(new Pair<>(force[0], force[1]));
    }

    public abstract void updateForce();

    public float[] getMomentum() {
        return Arrays.copyOf(momentum, 2);
    }

    public float getMass() {
        return mass;
    }
}
