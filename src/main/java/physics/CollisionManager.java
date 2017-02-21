package physics;

import components.CannonBall;
import components.Terrain;
import components.goat.Goat;
import org.javatuples.Pair;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class CollisionManager {
    private final Terrain terrain;
    private List<Collider> colliderList = new LinkedList<>();
    private Collection<Collider> colliderRemoveCollection = new HashSet<>();

    public CollisionManager(Terrain terrain) {
        this.terrain = terrain;
    }

    public CollisionManager addCollider(Collider collider) {
        colliderList.add(collider);
        return this;
    }

    public CollisionManager removeCollider(Collider collider) {
        colliderRemoveCollection.add(collider);
        return this;
    }

    public void calculate() {
        for (Collider collider : colliderList) {
            List<Pair<Float, Float>> collidePolygon = collider.getColliderPolygon();
            Pair<Float, Float> segLeft = null;
            Pair<Float, Float> segRight = null;
            boolean collisionFlag = false;
            for (int i = 0; i < terrain.groundVertex.length - 1; i++) {
                for (Pair<Float, Float> vertex : collidePolygon) {
                    if (    (2.0f * (float) i / terrain.groundVertex.length - 1.0f <= vertex.getValue0())           &&
                            (vertex.getValue0() <= 2.0f * (float) (i + 1) / terrain.groundVertex.length - 1.0f )    ) {
                        float expected = (terrain.groundVertex[i + 1] - terrain.groundVertex[i]) /
                                (2.0f * (float) 1 / terrain.groundVertex.length) *
                                (vertex.getValue0() - (2.0f * (float) i / terrain.groundVertex.length - 1.0f)) +
                                terrain.groundVertex[i] - 0.8f;
                        if (vertex.getValue1() <= expected) {
                            collisionFlag = true;
                            segLeft = new Pair<>(
                                    2.0f * (float) i / terrain.groundVertex.length - 1.0f,
                                    terrain.groundVertex[i] - 0.8f
                            );
                            segRight = new Pair<>(
                                    2.0f * (float) (i + 1) / terrain.groundVertex.length - 1.0f,
                                    terrain.groundVertex[i + 1] - 0.8f
                            );
                            break;
                        }
                    }
                }
                if (collisionFlag) break;
            }
            if (collisionFlag) collider.onCollideTerrain(segLeft, segRight);
        }
        colliderList.removeAll(colliderRemoveCollection);
        colliderRemoveCollection.clear();

        for (Collider collider : colliderList) {
            if (collider instanceof CannonBall) {
                for (Collider goat : colliderList) {
                    if (!(goat instanceof Goat)) continue;
                    for (Pair<Float, Float> vertex : goat.getColliderPolygon()) {
                        if (Math.sqrt(Math.pow(vertex.getValue0() - ((CannonBall) collider).coordinate[0], 2) +
                        Math.pow(vertex.getValue1() - ((CannonBall) collider).coordinate[1], 2)) <= CannonBall.radius) {
                            collider.onCollideWithEachother(goat, null);
                            goat.onCollideWithEachother(collider, vertex);
                        }
                    }
                }
            }
        }
        colliderList.removeAll(colliderRemoveCollection);
        colliderRemoveCollection.clear();
    }

    public boolean testCollideTerrian(Pair<Float, Float> vertex) {
        for (int i = 0; i < terrain.groundVertex.length - 1; i++) {
            if ((2.0f * (float) i / terrain.groundVertex.length - 1.0f <= vertex.getValue0()) &&
                    (vertex.getValue0() <= 2.0f * (float) (i + 1) / terrain.groundVertex.length - 1.0f)) {
                float expected = (terrain.groundVertex[i + 1] - terrain.groundVertex[i]) /
                        (2.0f * (float) 1 / terrain.groundVertex.length) *
                        (vertex.getValue0() - (2.0f * (float) i / terrain.groundVertex.length - 1.0f)) +
                        terrain.groundVertex[i] - 0.8f;
                if (vertex.getValue1() <= expected) return true;
            }
        }
        return false;
    }
}
