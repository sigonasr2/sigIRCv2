package sig.modules.RabiRace;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import sig.Module;
import sig.sigIRC;
import sig.modules.RabiRaceModule;

public class MarkMapButton extends ClickableButton{

	public MarkMapButton(Rectangle position, String button_label, RabiRaceModule module) {
		super(position,button_label,module);
	}

	public void onClickEvent(MouseEvent ev) {
		if (RabiRaceModule.module.mySession!=null) {
			//System.out.println("Mark Map.");
			RabiRaceModule.module.MarkCurrentPosition();
		}
	}
}
