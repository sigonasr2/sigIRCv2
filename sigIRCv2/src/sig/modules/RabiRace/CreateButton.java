package sig.modules.RabiRace;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import sig.Module;
import sig.modules.RabiRaceModule;

public class CreateButton extends ClickableButton{

	public CreateButton(Rectangle position, String button_label, RabiRaceModule module) {
		super(position, button_label, module);
	}

	public void onClickEvent(MouseEvent ev) {
		RabiRaceModule.createwindow.setVisible(true);
	}
}
