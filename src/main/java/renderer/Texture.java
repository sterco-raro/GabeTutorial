package renderer;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {

	private String filepath;

	private int textureID;

	private int width, height;

	public Texture(String filepath) {
		this.filepath = filepath;

		// Generate texture on GPU
		textureID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureID);

		// Set texture parameters
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT); // Repeat image in the X direciton (or U)
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT); // Repeat image in the Y direciton (or V)
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST); // Pixelate when stretching
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST); // Pixelate when shrinking

		// Load the image data
		IntBuffer width = BufferUtils.createIntBuffer(1);
		IntBuffer height = BufferUtils.createIntBuffer(1);
		IntBuffer channels = BufferUtils.createIntBuffer(1);
		stbi_set_flip_vertically_on_load(true);
		ByteBuffer image = stbi_load(filepath, width, height, channels, 0);

		if (image != null) {
			this.width = width.get(0);
			this.height = height.get(0);

			int imageChannels = GL_RGB;
			if (channels.get(0) == 3) {
				imageChannels = GL_RGB;
			} else if (channels.get(0) == 4) {
				imageChannels = GL_RGBA;
			} else {
				assert false : "Error: Unknown number of channels [" + channels.get(0) + "] in texture [" + filepath + "]";
			}
			glTexImage2D(GL_TEXTURE_2D, 0, imageChannels, width.get(0), height.get(0), 0, imageChannels, GL_UNSIGNED_BYTE, image);
		} else {
			assert false : "Error: Could not load Texture image '" + filepath + "'";
		}

		stbi_image_free(image);
	}

	public void bind() {
		glBindTexture(GL_TEXTURE_2D, textureID);
	}

	public void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
