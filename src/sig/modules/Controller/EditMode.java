package sig.modules.Controller;

public enum EditMode {
	DRAGSELECTION, //Drag to set size of button.
	DRAGAXISSELECTION, //Drag to set size of button.
	SELECTION, //Selects a button for editing
	DELETESELECTION, //Delete a button.
	BUTTONSET, //Asks for a controller button to set this button to.
	COLORSET,
	DEFAULT,
	POSITIONSELECTION;
}
