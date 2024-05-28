package engine;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import utils.Time;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

	private static Window instance = null;

	private static Scene currentScene = null;

	private int width;

	private int height;

	private String title;

	private long glfwWindow;

	public float r, g, b, a;
	private boolean fadeToBlack = false;

	private Window() {
		this.width = 800;
		this.height = 600;
		this.title = "GabeTutorial";
		r = 0.95f;
		g = 0.95f;
		b = 0.95f;
		a = 1.0f;
	}

	public static Window get() {
		if (Window.instance == null) {
			Window.instance = new Window();
		}

		return Window.instance;
	}

	public static void changeScene(int sceneIndex) {
		if (sceneIndex == 0) {
			currentScene = new LevelEditorScene();
		} else if (sceneIndex == 1) {
			currentScene = new LevelScene();
		} else {
			assert false : String.format("Unknown sceneIndex [%d]", sceneIndex);
		}
		currentScene.init();
		currentScene.start();
	}

	public void run() {
		System.out.println("LWJGL version: " + Version.getVersion());

		init();
		loop();

		// Free allocated memory
		glfwFreeCallbacks(glfwWindow);
		glfwDestroyWindow(glfwWindow);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		GLFWErrorCallback glfwErrorCallback = glfwSetErrorCallback(null);
		if (glfwErrorCallback != null) {
			glfwErrorCallback.free();
		}
	}

	private void init() {
		// Set stderr as error callback
		GLFWErrorCallback.createPrint(System.err).set();

		if (!glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		// Configure GLFW
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
		glfwWindowHint(GLFW_MAXIMIZED, GLFW_FALSE);

		// Create the window
		glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
		if (glfwWindow == NULL) {
			throw new IllegalStateException("Failed to create the GLFW window");
		}

		// Set up input callbacks
		glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
		glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
		glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
		glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);

		glfwMakeContextCurrent(glfwWindow);
		// Enable V-Sync
		glfwSwapInterval(1);

		glfwShowWindow(glfwWindow);

		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		// Enable transparency
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		Window.changeScene(0);
	}

	private void loop() {

		float frameBeginTime = Time.getTime();
		float frameEndTime;
		float dt = -1.0f;

		while (!glfwWindowShouldClose(glfwWindow)) {

			glfwPollEvents();

			// Clear the screen using the clear color defined here
			glClearColor(r, g, b, a);
			glClear(GL_COLOR_BUFFER_BIT);

			if (dt >= 0) {
				currentScene.update(dt);
			}

			// Update screen
			glfwSwapBuffers(glfwWindow);

			// At the end to take into account OS interrupts
			frameEndTime = Time.getTime();
			dt = frameEndTime - frameBeginTime;
			frameBeginTime = frameEndTime;
		}
	}
}
