package engine;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class LevelEditorScene extends Scene {

	private String vertexShaderSrc = "#version 330 core\n" +
			"\n" +
			"// aVar = attribute var\n" +
			"// fVar = output var to fragment\n" +
			"\n" +
			"layout (location = 0) in vec3 aPos;\n" +
			"layout (location = 1) in vec4 aColor;\n" +
			"\n" +
			"out vec4 fColor;\n" +
			"\n" +
			"void main() {\n" +
			"    fColor = aColor;\n" +
			"    gl_Position = vec4(aPos, 1.0);\n" +
			"}\n";

	private String fragmentShaderSrc = "#version 330 core\n" +
			"\n" +
			"in vec4 fColor;\n" +
			"\n" +
			"out vec4 color;\n" +
			"\n" +
			"void main() {\n" +
			"    color = fColor;\n" +
			"}\n";

	private int vertexID, fragmentID, shaderProgram;

	private float[] vertexArray = {
			// position 			// color
			0.5f, -0.5f, 0.0f, 		1.0f, 0.0f, 0.0f, 1.0f, // Bottom right 0 red
			-0.5f, 0.5f, 0.0f, 		0.0f, 1.0f, 0.0f, 1.0f, // Top left 	1 green
			0.5f, 0.5f, 0.0f, 		0.0f, 0.0f, 1.0f, 1.0f, // Top right 	2 blue
			-0.5f, -0.5f, 0.0f,		1.0f, 1.0f, 0.0f, 1.0f, // Bottom left 	3 yellow
	};

	// NOTE: Must be in counter-clockwise order
	private int[] elementArray = {
			2, 1, 0, 	// Top right triangle
			0, 1, 3 	// Bottom left triangle
	};

	private int vaoID, vboID, eboID;

	public LevelEditorScene() {
		System.out.println("LevelEditorScene");
	}

	@Override
	public void init() {
		// ============================
		//   Compile and link shaders
		// ============================

		// Load and compile the vertex shader
		vertexID = glCreateShader(GL_VERTEX_SHADER);
		// Pass the shader source to OpenGL
		glShaderSource(vertexID, vertexShaderSrc);
		glCompileShader(vertexID);
		// Check for compilation errors
		int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
		if (success == GL_FALSE) {
			int length = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
			System.out.println("ERROR: 'default.glsl': VERTEX shader compilation failed");
			System.out.println(glGetShaderInfoLog(vertexID, length));
			assert false : "";
		}

		// Load and compile the fragment shader
		fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
		// Pass the shader source to OpenGL
		glShaderSource(fragmentID, fragmentShaderSrc);
		glCompileShader(fragmentID);
		// Check for compilation errors
		success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
		if (success == GL_FALSE) {
			int length = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
			System.out.println("ERROR: 'default.glsl': FRAGMENT shader compilation failed");
			System.out.println(glGetShaderInfoLog(fragmentID, length));
			assert false : "";
		}

		// Link shaders
		shaderProgram = glCreateProgram();
		glAttachShader(shaderProgram, vertexID);
		glAttachShader(shaderProgram, fragmentID);
		glLinkProgram(shaderProgram);
		// Check for link errors
		success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
		if (success == GL_FALSE) {
			int length = glGetShaderi(shaderProgram, GL_INFO_LOG_LENGTH);
			System.out.println("ERROR: 'default.glsl': LINK failed");
			System.out.println(glGetProgramInfoLog(shaderProgram, length));
			assert false : "";
		}

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
		glUseProgram(shaderProgram); 	// Bind shader program
		glBindVertexArray(vaoID); 		// Bind the VAO that we're using
		// Enable the vertex attribute pointers
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);

		glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

		// Unbind everything
		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);
		glUseProgram(0);
	}
}
