package engine;

import java.awt.event.KeyEvent;

public class LevelEditorScene extends Scene {

	private boolean changingScene = false;
	private float timeToChangeScene = 2.0f;

	public LevelEditorScene() {
		System.out.println("LevelEditorScene");
	}

	@Override
	public void update(float dt) {

		if (!changingScene && KeyListener.isKeyPressed(KeyEvent.VK_SPACE)) {
			changingScene = true;
		}

		if (changingScene) {
			if (timeToChangeScene > 0) {
				timeToChangeScene -= dt;
				Window.get().r -= dt * 0.5f;
				Window.get().g -= dt * 0.5f;
				Window.get().b -= dt * 0.5f;
			} else {
				Window.changeScene(1);
			}
		}
	}
}
