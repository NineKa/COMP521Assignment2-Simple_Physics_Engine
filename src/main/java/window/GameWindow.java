package window;

import components.Component;
import components.Terrain;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import physics.CollisionManager;
import physics.Rigidbody;
import physics.verlet.Link;
import physics.verlet.PointMass;

import java.nio.IntBuffer;
import java.util.*;

public class GameWindow {
    private static GameWindow window = null;

    private final long glfwWindowHandle;

    private final int width;
    private final int height;
    private final String title;

    private static List<Component> componentList = new LinkedList<>();
    private static List<Rigidbody> rigidbodyList = new LinkedList<>();

    private static Collection<Component> componentRemoveCollection = new HashSet<>();
    private static Collection<Rigidbody> rigidbodyRemoveCollection = new HashSet<>();

    public static long deltaTime = 15L;

    public final CollisionManager collisionManager;
    public final Terrain terrain;

    public GameWindow(int size, String title, boolean centered, Terrain terrain) {
        if (size <= 0) throw new IllegalArgumentException("size has to be an positive integer");

        this.width = size;
        this.height = size;
        this.title = title;

        GLFWErrorCallback.createPrint(System.err).set();
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("unable to initialize GLFW library");
        }

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE);

        glfwWindowHandle = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        if (glfwWindowHandle == MemoryUtil.NULL) throw new RuntimeException("Unable to create game frame");

        GLFW.glfwSetWindowAspectRatio(glfwWindowHandle, 1, 1);

        if (centered) {
            try (MemoryStack memoryStack = MemoryStack.stackPush()) {
                IntBuffer windowWidth = memoryStack.mallocInt(1);
                IntBuffer windowHeight = memoryStack.mallocInt(1);
                GLFW.glfwGetWindowSize(glfwWindowHandle, windowWidth, windowHeight);
                GLFWVidMode screenMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
                GLFW.glfwSetWindowPos(
                        glfwWindowHandle,
                        (screenMode.width() - windowWidth.get(0)) / 2,
                        (screenMode.height() - windowHeight.get(0)) / 2
                );
            }
        }
        GLFW.glfwMakeContextCurrent(glfwWindowHandle);
        GLFW.glfwSwapInterval(1);
        GLFW.glfwShowWindow(glfwWindowHandle);

        this.collisionManager = new CollisionManager(terrain);
        this.terrain = terrain;
    }

    public static GameWindow init(int size, String title, boolean centered, Terrain terrain) {
        if (window == null) {
            window = new GameWindow(size, title, centered, terrain);
            return window;
        } else {
            throw new IllegalArgumentException("game window has already been initialized");
        }
    }

    public static synchronized GameWindow singleton() {
        if (window == null) throw new IllegalStateException("initialize before retrieving singleton");
        return window;
    }

    public GameWindow setKeyBind(GLFWKeyCallbackI bindCode) {
        GLFW.glfwSetKeyCallback(glfwWindowHandle, bindCode);
        return this;
    }

    public GameWindow addComponent(Component component) {
        synchronized (componentList) {
            componentList.add(component);
        }
        return this;
    }

    public GameWindow removeComponent(Component component) {
        componentRemoveCollection.add(component);
        return this;
    }

    public GameWindow addRigidbody(Rigidbody rigidbody) {
        synchronized (rigidbody) {
            rigidbodyList.add(rigidbody);
        }
        return this;
    }

    public GameWindow removeRigidbody(Rigidbody rigidbody) {
        rigidbodyRemoveCollection.add(rigidbody);
        return this;
    }

    public Collection<Component> getComponentCollection() {
        return Collections.unmodifiableCollection(componentList);
    }

    public Collection<Rigidbody> getRigidbodyCollection() {
        return Collections.unmodifiableCollection(rigidbodyList);
    }

    public void run() {
        GL.createCapabilities();
        while (!GLFW.glfwWindowShouldClose(glfwWindowHandle)) {
            deltaTime = 15 ;
            synchronized (rigidbodyList) {
                for (Rigidbody rigidbody : rigidbodyList) {
                    rigidbody.calculate();
                }
                for (Rigidbody rigidbody : rigidbodyList) {
                    if (rigidbody instanceof PointMass) {
                        for (Link link : ((PointMass) rigidbody).getVerletLinks()) {
                            link.solve();
                        }
                    }
                }
            }
            collisionManager.calculate();
            synchronized (componentList) {
                for (Component component : componentList) {
                    component.draw();
                }
            }
            GLFW.glfwSwapBuffers(glfwWindowHandle);
            GLFW.glfwPollEvents();

            componentList.removeAll(componentRemoveCollection);
            rigidbodyList.removeAll(rigidbodyRemoveCollection);

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {

            }
        }

        Callbacks.glfwFreeCallbacks(glfwWindowHandle);
        GLFW.glfwDestroyWindow(glfwWindowHandle);
        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
    }
}
