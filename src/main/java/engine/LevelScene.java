package engine;

public class LevelScene extends Scene {

	public LevelScene() {
		System.out.println("LevelScene");

		Window.get().r = 0.8f;
		Window.get().g = 0.2f;
		Window.get().b = 0.2f;
	}

	@Override
	public void update(float dt) {}
}
