package sig;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JColorChooser;
import javax.swing.JPanel;

public class ColorPanel extends JPanel{
		public ColorPanel() {
		}
		
		public Color getBackgroundColor() {
			return JColorChooser.showDialog(this, "Color Picker", sigIRC.backgroundcol);
		}
		
		public Color getBackgroundColor(Color defaultColor) {
			return JColorChooser.showDialog(this, "Color Picker", defaultColor);
		}

	    public Dimension getPreferredSize() {
	        return new Dimension(640,480);
	    }
}
