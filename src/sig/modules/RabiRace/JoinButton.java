package sig.modules.RabiRace;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import sig.Module;
import sig.modules.RabiRaceModule;
import sig.modules.Controller.ClickableButton;

public class JoinButton extends ClickableButton{

	public JoinButton(Rectangle position, String button_label, Module parent_module) {
		super(position, button_label, parent_module);
	}

	public void onClickEvent(MouseEvent ev) {
		RabiRaceModule.module.window.setVisible(true);
	}
}
