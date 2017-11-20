package sig.modules.Controller;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;

import org.lwjgl.glfw.GLFW;


public class Controller {
	int identifier;
	float[] axes;
	byte[] buttons;
	
	public Controller(int identifier) {
		this.identifier=identifier;
		FloatBuffer axisBuffer = GLFW.glfwGetJoystickAxes(identifier);
		axes = new float[axisBuffer.limit()];
		axisBuffer.get(axes);
		ByteBuffer buttonBuffer = GLFW.glfwGetJoystickButtons(identifier);
		buttons = new byte[buttonBuffer.limit()];
		buttonBuffer.get(buttons);
	}
	
	public String outputAxes() {
		return Arrays.toString(axes);
	}
	
	public String outputButtons() {
		return Arrays.toString(buttons);
	}
	
	public float[] getAxes() {
		return axes;
	}

	public byte[] getButtons() {
		return buttons;
	}
	
	public float getAxisValue(int axisNumber) {
		return axes[axisNumber];
	}
	
	public byte getButtonValue(int buttonNumber) {
		return buttons[buttonNumber];
	}

	@Deprecated
	public Type getType() {
		return null;
	}

	@Deprecated
	public String getName() {
		return null;
	}

	public void poll() {
		//System.out.println(Glfw.glfwGetJoystickParam(identifier, 1));
		FloatBuffer axisBuffer = GLFW.glfwGetJoystickAxes(identifier);
		axes = new float[axisBuffer.limit()];
		axisBuffer.get(axes);
		ByteBuffer buttonBuffer = GLFW.glfwGetJoystickButtons(identifier);
		buttons = new byte[buttonBuffer.limit()];
		buttonBuffer.get(buttons);
		//System.out.println(outputAxes()+","+outputButtons());
	}

	@Deprecated
	public Component[] getComponents() {
		return new Component[]{};
	}

	@Deprecated
	public Component getComponent(Identifier identifier2) {
		return null;
	}

}
