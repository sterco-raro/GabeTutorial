package engine;

import components.SpriteRenderer;
import org.joml.Vector2f;
import org.joml.Vector4f;
import renderer.Renderer;
import utils.AssetPool;

public class LevelEditorScene extends Scene {

	public LevelEditorScene() {
		System.out.println("LevelEditorScene");
		this.renderer = new Renderer();
	}

	@Override
	public void init() {
		this.camera = new Camera(new Vector2f(0.0f, 0.0f));
		this.camera.adjustProjection();

		int offset = 20;
		float totalWidth = (float) (800 - offset * 2);
		float totalHeight = (float) (600 - offset * 2);
		float sizeX = totalWidth / 100.0f;
		float sizeY = totalHeight / 100.0f;

		for (int x = 0; x < 100; x++) {
			for (int y = 0; y < 100; y++) {
				float xPos = offset + (x * sizeX);
				float yPos = offset + (y * sizeY);

				GameObject go = new GameObject(
						String.format("Object #%d at [%d, %d]", x + y, x, y),
						new Transform(new Vector2f(xPos, yPos), new Vector2f(sizeX, sizeY))
				);
				go.addComponent(new SpriteRenderer(new Vector4f(xPos / totalWidth, yPos / totalHeight, 0, 1)));
				this.addGameObjectToScene(go);
			}
		}

		loadResources();
	}

	@Override
	public void update(float dt) {
		//System.out.println("FPS: " + (1.0f / dt));

		for (GameObject go :  gameObjects) {
			go.update(dt);
		}

		this.renderer.render();
	}

	private void loadResources() {
		AssetPool.getShader("assets/shaders/default.glsl");
		AssetPool.getTexture("assets/graphics/test.png");
	}
}
