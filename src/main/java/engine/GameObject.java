package engine;

import java.util.ArrayList;
import java.util.List;

public class GameObject {

	private String name;

	private List<Component> components;

	public Transform transform;

	private int zIndex;

	public GameObject(String name) {
		this.name = name;
		this.components = new ArrayList<>();
		this.transform = new Transform();
		this.zIndex = 0;
	}

	public GameObject(String name, Transform transform, int zIndex) {
		this.name = name;
		this.components = new ArrayList<>();
		this.transform = transform;
		this.zIndex = zIndex;
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
		for (Component component : components) {
			component.update(dt);
		}
	}

	public void start() {
		for (Component component : components) {
			component.start();
		}
	}

	public int getzIndex() {
		return zIndex;
	}
}
