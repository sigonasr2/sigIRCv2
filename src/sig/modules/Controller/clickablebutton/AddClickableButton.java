package sig.modules.Controller.clickablebutton;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import sig.modules.ControllerModule;
import sig.modules.Controller.ClickableButton;
import sig.modules.Controller.ControlConfigurationWindow;
import sig.modules.Controller.DialogType;
import sig.modules.Controller.EditMode;

public class AddClickableButton extends ClickableButton{

	public AddClickableButton(Rectangle position, String button_label, ControllerModule parent_module) {
		super(position, button_label, parent_module);
	}
	
	public void onClickEvent(MouseEvent ev) {
		super.onClickEvent(ev);
		/*if (mouseInsideBounds(ev)) {
			module.setMode(EditMode.DRAGSELECTION); 
			module.resetDragPoints();
		}*/
		if (mouseInsideBounds(ev)) {
			new ControlConfigurationWindow(DialogType.CREATE_CONTROL,module);
			module.resetDragPoints();
		}
	}
}
