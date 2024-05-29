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

		GameObject one = new GameObject("KID", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)));
		one.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/graphics/kid-5.png")));
		this.addGameObjectToScene(one);

		GameObject two = new GameObject("CLERK", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)));
		two.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/graphics/shopkeeper.png")));
		this.addGameObjectToScene(two);

		loadResources();
	}

	@Override
	public void update(float dt) {
		// TODO FPS counter
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
