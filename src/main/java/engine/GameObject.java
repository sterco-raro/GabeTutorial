package engine;

import java.util.ArrayList;
import java.util.List;

public class GameObject {

	private String name;

	private List<Component> components;

	public GameObject(String name) {
		this.name = name;
		this.components = new ArrayList<>();
	}

	public <T extends Component> T getComponent(Class<T> componentClass) {
		for (Component c : components) {
			try {
				if (componentClass.isAssignableFrom(c.getClass())) {
					return componentClass.cast(c);
				}
			} catch (ClassCastException excp) {
				excp.printStackTrace();
				assert false : "Error: casting component [" + componentClass.getName() + "]";
			}
		}
		return null;
	}

	public <T extends Component> void removeComponent(Class<T> componentClass) {
		for (int i = 0; i < components.size(); i++) {
			Component c = components.get(i);
			if (componentClass.isAssignableFrom(c.getClass())) {
				components.remove(i);
				break;
			}
		}
	}

	public void addComponent(Component c) {
		this.components.add(c);
		c.gameObject = this;
	}

	public void update(float dt) {
		for (int i = 0; i < components.size(); i++) {
			components.get(i).update(dt);
		}
	}

	public void start() {
		for (int i = 0; i < components.size(); i++) {
			components.get(i).start();
		}
	}
}
