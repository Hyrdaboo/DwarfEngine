package DwarfEngine.Core;

import DwarfEngine.MathTypes.Mathf;
import DwarfEngine.MathTypes.Vector2;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple polling-based input system that provides methods for handling user
 * input. It provides methods for checking the state of keys, buttons and such.
 */
public final class Input implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
	class KeyEvt {
		Integer keycode;

	}

	private static List<Integer> heldKeys = new ArrayList<>();
	private static Input instance = null;

	private Input() {
	}

	static Input GetInstance() {
		if (instance == null) {
			instance = new Input();
		}
		return instance;
	}

	////// KEYBOARD EVENTS /////////
	static int pressedKey = -1;
	static int releasedKey = -1;

	/**
	 * Checks if a specific key is currently pressed.
	 *
	 * @param e The keycode of the key to check.
	 * @return True if the key is currently pressed, false otherwise.
	 */
	public static boolean OnKeyPressed(Keycode e) {
		if ((e.GetKeyCode() == pressedKey) || (pressedKey != -1 && e == Keycode.AnyKey)) {
			pressedKey = -1;
			return true;
		}
		return false;
	}

	/**
	 * Checks if a specific key is currently released.
	 *
	 * @param e The keycode of the key to check.
	 * @return True if the key is currently released, false otherwise.
	 */
	public static boolean OnKeyReleased(Keycode e) {
		if ((e.GetKeyCode() == releasedKey) || (releasedKey != -1 && e == Keycode.AnyKey)) {
			releasedKey = -1;
			return true;
		}
		return false;
	}

	/**
	 * Checks if a specific key is currently held down.
	 *
	 * @param e The keycode of the key to check.
	 * @return True if the key is currently held down, false otherwise.
	 */
	public static boolean OnKeyHeld(Keycode e) {
		return heldKeys.contains(e.GetKeyCode()) || (e == Keycode.AnyKey && heldKeys.size() > 0);
	}

	static void resetKeyStates() {
		heldKeys.clear();
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	// press and hold
	@Override
	public void keyPressed(KeyEvent e) {
		if (!heldKeys.contains(e.getKeyCode())) {
			heldKeys.add(e.getKeyCode());
			pressedKey = e.getKeyCode();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (heldKeys.contains(e.getKeyCode())) {
			Integer code = e.getKeyCode();
			heldKeys.remove(code);
			releasedKey = e.getKeyCode();
			pressedKey = -1;
		}
	}

	//////// MOUSE EVENTS /////////
	private static final Vector2 mousePosition = Vector2.zero();

	/**
	 * Returns the current position of the mouse cursor.
	 *
	 * @return The current position of the mouse cursor as a Vector2.
	 */
	public static Vector2 getMousePosition() {
		return mousePosition;
	}

	private static int heldButton = -1;
	private static int clickedButton = -1;
	private static int releasedButton = -1;
	static float frameWidth, frameHeight;
	static float windowWidth, windowHeight;
	static float windowX, windowY;

	/**
	 * Checks if a mouse button is currently being held down.
	 *
	 * @param button The index of the mouse button to check. Valid indexes are 1, 2,
	 *               and 3.
	 * @return {@code true} if the specified mouse button is being held down,
	 * {@code false} otherwise.
	 * @throws IllegalArgumentException if an invalid mouse button index is
	 *                                  provided.
	 */
	public static boolean MouseButtonHeld(int button) {
		if (button < 1 || button > 3) {
			throw new IllegalArgumentException("Invalid Mouse button index. Valid indexes are 1, 2, 3");
		}
		return heldButton == button;
	}

	/**
	 * Checks if a mouse button has been clicked.
	 *
	 * @param button The index of the mouse button to check. Valid indexes are 1, 2,
	 *               and 3.
	 * @return {@code true} if the specified mouse button has been clicked,
	 * {@code false} otherwise.
	 * @throws IllegalArgumentException if an invalid mouse button index is
	 *                                  provided.
	 */
	public static boolean MouseButtonClicked(int button) {
		if (button < 1 || button > 3) {
			throw new IllegalArgumentException("Invalid Mouse button index. Valid indexes are 1, 2, 3");
		}
		if (clickedButton == button) {
			clickedButton = -1;
			return true;
		}
		return false;
	}

	/**
	 * Checks if a mouse button has been released.
	 *
	 * @param button The index of the mouse button to check. Valid indexes are 1, 2,
	 *               and 3.
	 * @return {@code true} if the specified mouse button has been released,
	 * {@code false} otherwise.
	 * @throws IllegalArgumentException if an invalid mouse button index is
	 *                                  provided.
	 */
	public static boolean MouseButtonReleased(int button) {
		if (button < 1 || button > 3) {
			throw new IllegalArgumentException("Invalid Mouse button index. Valid indexes are 1, 2, 3");
		}
		if (releasedButton == button) {
			releasedButton = -1;
			return true;
		}
		return false;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		heldButton = e.getButton();
		clickedButton = e.getButton();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		heldButton = -1;
		releasedButton = e.getButton();
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mousePosition.x = (e.getX() / windowWidth) * frameWidth;
		mousePosition.y = (e.getY() / windowHeight) * frameHeight;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mousePosition.x = (e.getX() / windowWidth) * frameWidth;
		mousePosition.y = (e.getY() / windowHeight) * frameHeight;
	}

	private static Vector2 delta = Vector2.zero();
	private static float lastX = -1, lastY = -1;

	static void calculateDelta() {
		Point pos = MouseInfo.getPointerInfo().getLocation();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// prevent the big delta at the start
		lastX = lastX < 0 ? pos.x : lastX;
		lastY = lastY < 0 ? pos.y : lastY;

		delta.x = (pos.x - lastX) / screenSize.width;
		delta.y = (pos.y - lastY) / screenSize.height;
		lastX = pos.x;
		lastY = pos.y;

		if (!mouseConfined)
			return;
		float rightEdge = Mathf.clamp(windowX + windowWidth, 0, screenSize.width - 1);
		float leftEdge = Mathf.clamp(windowX, 0, screenSize.width - 1);
		float bottomEdge = Mathf.clamp(windowY + windowHeight, 0, screenSize.height - 1);
		float topEdge = Mathf.clamp(windowY, 0, screenSize.height - 1);

		if (pos.x >= rightEdge) {
			bot.mouseMove((int) leftEdge + 1, pos.y);
			lastX = leftEdge + 1;
		}
		if (pos.x <= leftEdge) {
			bot.mouseMove((int) rightEdge - 1, pos.y);
			lastX = rightEdge - 1;
		}
		if (pos.y >= bottomEdge) {
			bot.mouseMove(pos.x, (int) topEdge + 1);
			lastY = topEdge + 1;
		}
		if (pos.y <= topEdge) {
			bot.mouseMove(pos.x, (int) bottomEdge - 1);
			lastY = bottomEdge - 1;
		}
	}

	/**
	 * Returns the delta (change) in mouse position since the last frame.
	 *
	 * @return The delta in mouse position as a Vector2.
	 */
	public static Vector2 GetMouseDelta() {
		return delta;
	}

	private static Robot bot;
	private static boolean mouseConfined = false;

	/**
	 * Sets whether the mouse cursor should be confined within the window.
	 *
	 * @param confined true to confine the mouse cursor, false to allow it to move
	 *                 freely.
	 */
	public static void setMouseConfined(boolean confined) {
		mouseConfined = confined;

		try {
			if (bot == null)
				bot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}

	}

	public static boolean isMouseConfined() {
		return mouseConfined;
	}

	private static int scrollDir = 0;

	static void resetScrollDir() {
		scrollDir = 0;
	}

	/**
	 * Returns the direction of the mouse wheel scroll.
	 *
	 * @return An integer representing the direction of the mouse wheel scroll.
	 * Positive value indicates scrolling up, negative value indicates
	 * scrolling down, and 0 indicates no scrolling.
	 */
	public static int getMouseWheel() {
		return scrollDir;
	}

	static float timeSinceLastScroll = 0;

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		timeSinceLastScroll = 0;
		scrollDir = e.getWheelRotation();
	}
}
