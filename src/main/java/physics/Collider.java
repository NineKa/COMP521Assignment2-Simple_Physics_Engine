package physics;

import org.javatuples.Pair;

import java.util.List;

public interface Collider {
    List<Pair<Float, Float>> getColliderPolygon();

    void onCollideTerrain(Pair<Float, Float> segLeft, Pair<Float, Float> segRight);
    void onCollideWithEachother(Collider collider, Pair<Float, Float> pos);
}
