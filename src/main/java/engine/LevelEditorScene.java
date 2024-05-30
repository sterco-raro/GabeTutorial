package engine;

import components.Sprite;
import components.SpriteRenderer;
import components.Spritesheet;
import org.joml.Vector2f;
import org.joml.Vector4f;
import renderer.Renderer;
import utils.AssetPool;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class LevelEditorScene extends Scene {

	private GameObject one, two, three, four, five, six;
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

		int scale = 128;
		int spacing = scale/2;
		int x = (800 - scale * 4 - spacing * 3) / 2;
		int y = (600 - scale * 2 - spacing) / 2;

		one = new GameObject("1", new Transform(new Vector2f(x, y), new Vector2f(scale, scale)), 0);
		one.addComponent(new SpriteRenderer(spritesheet.getSprite(0)));
		this.addGameObjectToScene(one);

		two = new GameObject("2", new Transform(new Vector2f(x + scale + spacing, y), new Vector2f(scale, scale)), 0);
		two.addComponent(new SpriteRenderer(spritesheet.getSprite(14)));
		this.addGameObjectToScene(two);

		three = new GameObject("3", new Transform(new Vector2f(x + scale * 2 + spacing * 2, y), new Vector2f(scale, scale)), 0);
		three.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture("assets/graphics/blendImage1.png"))));
		this.addGameObjectToScene(three);

		four = new GameObject("4", new Transform(new Vector2f(x + scale * 3 + spacing * 3, y), new Vector2f(scale, scale)), 0);
		four.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture("assets/graphics/blendImage2.png"))));
		this.addGameObjectToScene(four);

		five = new GameObject("5", new Transform(new Vector2f(x + scale + spacing * 2, y + 200), new Vector2f(scale, scale)), 1);
		five.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture("assets/graphics/blendImage1.png"))));
		this.addGameObjectToScene(five);

		six = new GameObject("6", new Transform(new Vector2f(x + scale * 2 + spacing, y + 200), new Vector2f(scale, scale)), -1);
		six.addComponent(new SpriteRenderer(new Sprite(AssetPool.getTexture("assets/graphics/blendImage2.png"))));
		this.addGameObjectToScene(six);
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
		AssetPool.addSpritesheet(
				"assets/graphics/spritesheet.png",
				new Spritesheet(AssetPool.getTexture(
						"assets/graphics/spritesheet.png"),
						16, 16, 26, 0
				)
		);
	}
}
