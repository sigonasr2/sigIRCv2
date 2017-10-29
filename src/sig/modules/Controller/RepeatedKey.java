package sig.modules.Controller;

import sig.modules.ControllerModule;
import sig.utils.FileUtils;

public class RepeatedKey {
	int keycode;
	boolean isKeyPressed;
	Thread schedulerThread;
	Runnable action;
	Element element;
	ControllerModule module;
	boolean held_down=false,repeat=false;
	final static int HOLD_DELAY=500;
	final static int REPEAT_DELAY=100;
	
	public RepeatedKey(int keycode, ControllerModule module,
			Runnable action) {
		this.keycode=keycode;
		this.module=module;
		this.action=action;
	}
	
	public int getKeyCode() {
		return keycode;
	}
	
	public boolean isKeyPressed() {
		return this.isKeyPressed;
	}
	
	public void setHeldStatus(boolean heldDown) {
		this.held_down=heldDown;
	}
	
	public void setRepeatStatus(boolean repeat) {
		this.repeat = repeat;
	}
	
	public void setKeyPressed(boolean isPressed) {
		this.isKeyPressed=isPressed;
		if (!held_down) {
			held_down=true;
			schedulerThread = new Thread() {
				public void run() {
					try {
						Thread.sleep(HOLD_DELAY);
						if (!isKeyPressed()) {
							setHeldStatus(false);
						} else {
							setRepeatStatus(true);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		} else {
			if (repeat) {
				schedulerThread = new Thread() {
					public void run() {
						try {
							while (repeat) {
								action.run();
								Thread.sleep(REPEAT_DELAY);
								if (!isKeyPressed()) {
									repeat=false;
									held_down=false;
								}
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				};
			}
		}
	}
}