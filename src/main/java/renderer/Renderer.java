package renderer;

import components.SpriteRenderer;
import engine.GameObject;

import java.util.ArrayList;
import java.util.List;

public class Renderer {

	private final int MAX_BATCH_SIZE = 1000;

	private List<RenderBatch> batches;

	public Renderer() {
		batches = new ArrayList<>();
	}

	public void add(GameObject go) {
		SpriteRenderer sprite = go.getComponent(SpriteRenderer.class);
		if (sprite != null) {
			add(sprite);
		}
	}

	public void add(SpriteRenderer sprite) {
		boolean added = false;
		for (RenderBatch batch : batches) {
			if (batch.hasRoom()) {
				Texture texture = sprite.getTexture();
				if (batch.hasTexture(texture) || batch.hasTextureRoom()) {
					batch.addSprite(sprite);
					added = true;
					break;
				}
			}
		}

		if (!added) {
			RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE);
			newBatch.init();
			batches.add(newBatch);
			newBatch.addSprite(sprite);
		}
	}

	public void render() {
		for (RenderBatch batch : batches) {
			batch.render();
		}
	}
}
