import components.*;
import components.goat.Goat;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.Library;
import window.GameWindow;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;


public class Main {
    private static int WINDOW_SIZE = 800;

    public static void main(String argv[]) {
        Library.initialize();

        final AtomicBoolean leftCannonFlag = new AtomicBoolean(true);

        LeftCannon leftCannon = new LeftCannon(-0.9f, -0.8f);
        leftCannon.setActive(true);
        RightCannon rightCannon = new RightCannon(+0.9f, -0.8f);
        rightCannon.setActive(false);
        Wind wind = new Wind(-0.8f, 0.8f);
        Terrain terrain = new Terrain(WINDOW_SIZE);



        GameWindow.init(WINDOW_SIZE, "COMP521-Assignment2", false, terrain)
                .setKeyBind((window, key, scancode, action, mods) -> {
                    if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE)
                        GLFW.glfwSetWindowShouldClose(window, true);
                    if (key == GLFW.GLFW_KEY_SPACE && action == GLFW.GLFW_RELEASE && leftCannonFlag.get()) {
                        CannonBall cannonBall = new CannonBall(
                                leftCannon.getShootPosition()[0],
                                leftCannon.getShootPosition()[1],
                                leftCannon.getShootAngle()
                        );
                        GameWindow.singleton().addRigidbody(cannonBall);
                        GameWindow.singleton().addComponent(cannonBall);
                        GameWindow.singleton().collisionManager.addCollider(cannonBall);

                        float randomAngle = (85.0f - 15.0f) * ThreadLocalRandom.current().nextFloat() + 15.0f;
                        leftCannon.setShootAngle(randomAngle);
                    }
                    if (key == GLFW.GLFW_KEY_SPACE && action == GLFW.GLFW_RELEASE && !leftCannonFlag.get()) {
                        Goat goat = new Goat(
                                rightCannon.getShootPosition()[0],
                                rightCannon.getShootPosition()[1],
                                rightCannon.getShootAngle()
                        );
                        GameWindow.singleton().addRigidbody(goat);
                        GameWindow.singleton().addComponent(goat);
                        GameWindow.singleton().collisionManager.addCollider(goat);

                        float randomAngle = (70.0f - 45.0f) * ThreadLocalRandom.current().nextFloat() + 45.0f;
                        rightCannon.setShootAngle(randomAngle);
                    }
                    if (key == GLFW.GLFW_KEY_TAB && action == GLFW.GLFW_RELEASE) {
                        leftCannonFlag.set(! leftCannonFlag.get());
                        if (leftCannonFlag.get()) {
                            leftCannon.setActive(true);
                            rightCannon.setActive(false);
                        } else {
                            leftCannon.setActive(false);
                            rightCannon.setActive(true);
                        }
                    }
                })
                .addComponent(terrain)
                .addComponent(leftCannon)
                .addComponent(rightCannon)
                .addComponent(wind)
                .run();
    }
}
