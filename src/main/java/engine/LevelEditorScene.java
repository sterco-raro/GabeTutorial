package engine;

import components.Sprite;
import components.SpriteRenderer;
import components.Spritesheet;
import org.joml.Vector2f;
import org.joml.Vector4f;
import renderer.Renderer;
import utils.AssetPool;

public class LevelEditorScene extends Scene {

	private GameObject one, two;
	private Spritesheet spritesheet;

	public LevelEditorScene() {
		System.out.println("LevelEditorScene");
		this.renderer = new Renderer();
	}

	@Override
	public void init() {
		loadResources();

		this.camera = new Camera(new Vector2f(0.0f, 0.0f));
		this.camera.adjustProjection();

		spritesheet = AssetPool.getSpritesheet("assets/graphics/spritesheet.png");

		one = new GameObject("KID", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)));
		one.addComponent(new SpriteRenderer(spritesheet.getSprite(0)));
		this.addGameObjectToScene(one);

		two = new GameObject("CLERK", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)));
		two.addComponent(new SpriteRenderer(spritesheet.getSprite(14)));
		this.addGameObjectToScene(two);
	}

	private int spriteIndex = 0;
	private float spriteFlipTime = 0.2f;
	private float spriteFlipTimeLeft = 0.0f;
	@Override
	public void update(float dt) {
		// TODO FPS counter
		//System.out.println("FPS: " + (1.0f / dt));

		spriteFlipTimeLeft -= dt;
		if (spriteFlipTimeLeft <= 0) {
			spriteFlipTimeLeft = spriteFlipTime;
			spriteIndex++;
			if (spriteIndex > 3) {
				spriteIndex = 1;
			}
			one.getComponent(SpriteRenderer.class).setSprite(spritesheet.getSprite(spriteIndex));
		}

		for (GameObject go :  gameObjects) {
			go.update(dt);
		}

		this.renderer.render();
	}

	private void loadResources() {
		AssetPool.getShader("assets/shaders/default.glsl");
		AssetPool.addSpritesheet(
				"assets/graphics/spritesheet.png",
				new Spritesheet(AssetPool.getTexture(
						"assets/graphics/spritesheet.png"),
						16, 16, 26, 0
				)
		);
	}
}
