package engine;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;
import utils.Time;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class LevelEditorScene extends Scene {

	private float[] vertexArray = {
			// position 			// color
			100.5f, -100.5f, 0.0f, 		1.0f, 0.0f, 0.0f, 1.0f, // Bottom right 0 red
			-100.5f, 100.5f, 0.0f, 		0.0f, 1.0f, 0.0f, 1.0f, // Top left 	1 green
			100.5f, 100.5f, 0.0f, 		0.0f, 0.0f, 1.0f, 1.0f, // Top right 	2 blue
			-100.5f, -100.5f, 0.0f,		1.0f, 1.0f, 0.0f, 1.0f, // Bottom left 	3 yellow
	};

	// NOTE: Must be in counter-clockwise order
	private int[] elementArray = {
			2, 1, 0, 	// Top right triangle
			0, 1, 3 	// Bottom left triangle
	};

	private int vaoID, vboID, eboID;

	private Shader defaultShader;

	public LevelEditorScene() {
		System.out.println("LevelEditorScene");
	}

	@Override
	public void init() {
		camera = new Camera(new Vector2f(-400.0f, -300.0f));
		camera.adjustProjection();

		defaultShader = new Shader("assets/shaders/default.glsl");
		defaultShader.compile();

		// ============================
		//   VAO, VBO, EBO objects
		// ============================

		// Create VAO
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);

		// Create the vertices buffer
		FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
		vertexBuffer.put(vertexArray).flip(); // NOTE: Must flip otherwise OpenGL will throw an error

		// Create VBO and upload the vertex buffer
		vboID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

		// Create the indices buffer
		IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
		elementBuffer.put(elementArray).flip();

		// Create EBO and upload the element buffer
		eboID = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

		// Add the vertex attribute pointers
		int positionSize 	= 3;
		int colorSize 		= 4;
		int floatSizeBytes 	= 4;
		int vertexSizeBytes = (positionSize + colorSize) * floatSizeBytes;
		glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionSize * floatSizeBytes);
		glEnableVertexAttribArray(1);
	}

	@Override
	public void update(float dt) {
		camera.position.x += 0.05f;
		camera.position.y += 0.05f;

		defaultShader.use();
		defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
		defaultShader.uploadMat4f("uView", camera.getViewMatrix());
		defaultShader.uploadFloat("uTime", Time.getTime());

		// Bind the VAO that we're using
		glBindVertexArray(vaoID);
		// Enable the vertex attribute pointers
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);

		glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

		// Unbind everything
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);
		defaultShader.detach();
	}
}
