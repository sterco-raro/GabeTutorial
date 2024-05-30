package renderer;

import components.SpriteRenderer;
import engine.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;
import utils.AssetPool;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch implements Comparable<RenderBatch> {

	//    Vertex
	// ============
	// Current vertex attributes: 9
	// Pos: 2 floats, Color: 4 floats, Texture Coords: 2 floats, Texture ID: 1 float

	private final int POS_SIZE = 2;
	private final int COLOR_SIZE = 4;
	private final int TEX_COORDS_SIZE = 2;
	private final int TEX_ID_SIZE = 1;

	private final int POS_OFFSET = 0;
	private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
	private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
	private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;

	private final int VERTEX_SIZE = 9;
	private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

	private SpriteRenderer[] spriteRenderers;
	private int numSprites;
	private boolean hasRoom;
	private float[] vertices;
	private int[] texSlots = {0, 1, 2, 3, 4, 5, 6, 7};

	private List<Texture> textures;

	private int vaoID, vboID;
	private int maxBatchSize;
	private Shader shader;

	private int zIndex;

	public RenderBatch(int maxBatchSize, int zIndex) {
		this.maxBatchSize = maxBatchSize;
		this.zIndex = zIndex;
		this.numSprites = 0;
		this.hasRoom = true;

		shader = AssetPool.getShader("assets/shaders/default.glsl");

		spriteRenderers = new SpriteRenderer[maxBatchSize];

		// 4 vertices quad
		vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

		textures = new ArrayList<>();
	}

	public void init() {
		// Generate and bind a Vertex Array Object
		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);

		// Allocate space for vertices
		vboID = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboID);
		glBufferData(GL_ARRAY_BUFFER, (long) vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

		// Create and upload indices buffer
		int eboID = glGenBuffers();
		int[] indices = generateIndices();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

		// Enable the buffer attribute pointers
		glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
		glEnableVertexAttribArray(2);
		glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
		glEnableVertexAttribArray(3);
	}

	public void render() {
		boolean rebufferData = false;
		for (int i = 0; i < numSprites; i++) {
			SpriteRenderer spriteRenderer = spriteRenderers[i];
			if (spriteRenderer.isDirty()) {
				loadVertexProperties(i);
				spriteRenderer.setClean();
				rebufferData = true;
			}
		}

		if (rebufferData) {
			glBindBuffer(GL_ARRAY_BUFFER, vboID);
			glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
		}

		shader.use();
		shader.uploadMat4f("uProjection", Window.getScene().getCamera().getProjectionMatrix());
		shader.uploadMat4f("uView", Window.getScene().getCamera().getViewMatrix());

		// Bind textures
		for (int i = 0; i < textures.size(); i++) {
			glActiveTexture(GL_TEXTURE0 + i + 1); // Skip reserved slot 0 (used for simple colored shapes)
			textures.get(i).bind();
		}
		shader.uploadIntArray("uTextures", texSlots);

		glBindVertexArray(vaoID);
		glEnableVertexAttribArray(0);
		glEnableVertexAttribArray(1);

		glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);

		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glBindVertexArray(0);

		for (int i = 0; i < textures.size(); i++) {
			textures.get(i).unbind();
		}

		shader.detach();
	}

	public void addSprite(SpriteRenderer sprite) {
		// Get index and add renderObject
		int index = numSprites;
		this.spriteRenderers[numSprites] = sprite;
		numSprites += 1;

		if (sprite.getTexture() != null) {
			if (!textures.contains(sprite.getTexture())) {
				textures.add(sprite.getTexture());
			}
		}

		// Add properties to local vertices array
		loadVertexProperties(index);

		if (numSprites >= maxBatchSize) {
			hasRoom = false;
		}
	}

	public boolean hasRoom() {
		return hasRoom;
	}

	public boolean hasTextureRoom() {
		return textures.size() < 8;
	}

	public boolean hasTexture(Texture texture) {
		return textures.contains(texture);
	}

	private int[] generateIndices() {
		// 6 indices per quad (3 per triangle)
		int[] elements = new int[maxBatchSize * 6];

		for (int i = 0; i < maxBatchSize; i++) {
			// Create two triangles for each element quad
			loadElementIndices(elements, i);
		}

		return elements;
	}

	private void loadElementIndices(int[] elements, int index) {
		// first quad: 3, 2, 0, 0, 2, 1, second quad: 7, 6, 4, 4, 6, 5, etc.
		int offsetElementValue = 4 * index; 	// the quad first vertex
		int offsetArrayPosition = 6 * index; 	// the quad starting index

		// First triangle indices: 3, 2, 0
		elements[offsetArrayPosition] = offsetElementValue + 3;
		elements[offsetArrayPosition + 1] = offsetElementValue + 2;
		elements[offsetArrayPosition + 2] = offsetElementValue;

		// Second triangle indices: 0, 2, 1
		elements[offsetArrayPosition + 3] = offsetElementValue;
		elements[offsetArrayPosition + 4] = offsetElementValue + 2;
		elements[offsetArrayPosition + 5] = offsetElementValue + 1;
	}

	private void loadVertexProperties(int index) {
		SpriteRenderer sprite = this.spriteRenderers[index];

		Vector4f color = sprite.getColor();
		Vector2f[] texCoords = sprite.getTexCoords();

		int texId = 0;
		if (sprite.getTexture() != null) {
			for (int i = 0; i < textures.size(); i++) {
				if (textures.get(i) == sprite.getTexture()) {
					texId = i + 1; // Skip reserved slot 0 (used for simple colored shapes)
					break;
				}
			}
		}

		// Add vertices with the appropriate properties
		float xAdd = 1.0f; // Normalized vertex X offset
		float yAdd = 1.0f; // Normalized vertex Y offset
		int offset = index * 4 * VERTEX_SIZE; // Vertex array offset (4 vertices per sprite)
		for (int i = 0; i < 4; i++) {
			if (i == 1) {
				yAdd = 0.0f;
			} else if (i == 2) {
				xAdd = 0.0f;
			} else if (i == 3) {
				yAdd = 1.0f;
			}

			// Load position
			vertices[offset] = sprite.gameObject.transform.position.x + (xAdd * sprite.gameObject.transform.scale.x);
			vertices[offset + 1] = sprite.gameObject.transform.position.y + (yAdd * sprite.gameObject.transform.scale.y);

			// Load color
			vertices[offset + 2] = color.x;
			vertices[offset + 3] = color.y;
			vertices[offset + 4] = color.z;
			vertices[offset + 5] = color.w;

			// Load texture coordinates
			vertices[offset + 6] = texCoords[i].x;
			vertices[offset + 7] = texCoords[i].y;

			// Load texture id
			vertices[offset + 8] = texId;

			offset += VERTEX_SIZE;
		}
	}

	public int getzIndex() {
		return zIndex;
	}

	@Override
	public int compareTo(RenderBatch o) {
		return Integer.compare(this.zIndex, o.getzIndex());
	}
}
