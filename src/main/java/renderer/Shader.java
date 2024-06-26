package renderer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {

	private int shaderProgramID;
	private boolean beingUsed = false;

	private String vertexSource;

	private String fragmentSource;

	private String filepath;

	public Shader(String filepath) {
		this.filepath = filepath;
		try {
			String source = new String(Files.readAllBytes(Paths.get(filepath)));
			String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

			int index = source.indexOf("#type") + 6;
			int eol = source.indexOf("\r\n", index);
			String firstPattern = source.substring(index, eol).trim();

			index = source.indexOf("#type", eol) + 6;
			eol = source.indexOf("\r\n", index);
			String secondPattern = source.substring(index, eol).trim();

			if (firstPattern.equals("vertex")) {
				vertexSource = splitString[1];
			} else if (firstPattern.equals("fragment")) {
				fragmentSource = splitString[1];
			} else {
				throw new IOException("Unexpected first token [" + firstPattern + "] in [" + filepath + "]");
			}

			if (secondPattern.equals("vertex")) {
				vertexSource = splitString[2];
			} else if (secondPattern.equals("fragment")) {
				fragmentSource = splitString[2];
			} else {
				throw new IOException("Unexpected second token [" + secondPattern + "] in [" + filepath + "]");
			}
		} catch (IOException ioexcp) {
			ioexcp.printStackTrace();
			assert false : "ERROR: could not open file for shader [" + filepath + "]";
		}
	}

	public void compile() {
		// ============================
		//   Compile and link shaders
		// ============================
		int vertexID, fragmentID;

		// Load and compile the vertex shader
		vertexID = glCreateShader(GL_VERTEX_SHADER);
		// Pass the shader source to OpenGL
		glShaderSource(vertexID, vertexSource);
		glCompileShader(vertexID);
		// Check for compilation errors
		int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);
		if (success == GL_FALSE) {
			int length = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
			System.out.println("ERROR: '" + filepath + "': VERTEX shader compilation failed");
			System.out.println(glGetShaderInfoLog(vertexID, length));
			assert false : "";
		}

		// Load and compile the fragment shader
		fragmentID = glCreateShader(GL_FRAGMENT_SHADER);
		// Pass the shader source to OpenGL
		glShaderSource(fragmentID, fragmentSource);
		glCompileShader(fragmentID);
		// Check for compilation errors
		success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
		if (success == GL_FALSE) {
			int length = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
			System.out.println("ERROR: '" + filepath + "': FRAGMENT shader compilation failed");
			System.out.println(glGetShaderInfoLog(fragmentID, length));
			assert false : "";
		}

		// Link shaders
		shaderProgramID = glCreateProgram();
		glAttachShader(shaderProgramID, vertexID);
		glAttachShader(shaderProgramID, fragmentID);
		glLinkProgram(shaderProgramID);
		// Check for link errors
		success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
		if (success == GL_FALSE) {
			int length = glGetShaderi(shaderProgramID, GL_INFO_LOG_LENGTH);
			System.out.println("ERROR: '" + filepath + "': LINK failed");
			System.out.println(glGetProgramInfoLog(shaderProgramID, length));
			assert false : "";
		}
	}

	public void use() {
		if (!beingUsed) {
			glUseProgram(shaderProgramID);
			beingUsed = true;
		}
	}

	public void detach() {
		glUseProgram(0);
		beingUsed = false;
	}

	public void uploadMat4f(String name, Matrix4f matrix) {
		int location = glGetUniformLocation(shaderProgramID, name);
		// Make sure we're using the shader
		use();
		FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
		matrix.get(matBuffer);
		glUniformMatrix4fv(location, false, matBuffer);
	}

	public void uploadMat3f(String name, Matrix3f matrix) {
		int location = glGetUniformLocation(shaderProgramID, name);
		// Make sure we're using the shader
		use();
		FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
		matrix.get(matBuffer);
		glUniformMatrix3fv(location, false, matBuffer);
	}

	public void uploadVec4f(String name, Vector4f vec) {
		int location = glGetUniformLocation(shaderProgramID, name);
		use();
		glUniform4f(location, vec.x, vec.y, vec.z, vec.w);
	}

	public void uploadVec3f(String name, Vector3f vec) {
		int location = glGetUniformLocation(shaderProgramID, name);
		use();
		glUniform3f(location, vec.x, vec.y, vec.z);
	}

	public void uploadVec2f(String name, Vector2f vec) {
		int location = glGetUniformLocation(shaderProgramID, name);
		use();
		glUniform2f(location, vec.x, vec.y);
	}

	public void uploadFloat(String name, float value) {
		int location = glGetUniformLocation(shaderProgramID, name);
		use();
		glUniform1f(location, value);
	}

	public void uploadInt(String name, int value) {
		int location = glGetUniformLocation(shaderProgramID, name);
		use();
		glUniform1i(location, value);
	}

	public void uploadTexture(String name, int slot) {
		int location = glGetUniformLocation(shaderProgramID, name);
		use();
		glUniform1i(location, slot);
	}

	public void uploadIntArray(String name, int[] array) {
		int location = glGetUniformLocation(shaderProgramID, name);
		use();
		glUniform1iv(location, array);
	}
}
