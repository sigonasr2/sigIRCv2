package sig;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JColorChooser;
import javax.swing.JPanel;

public class ColorPanel extends JPanel{
		public ColorPanel() {
			
		}
		
		public Color getBackgroundColor() {
			return JColorChooser.showDialog(this, "Background Color Picker", sigIRC.backgroundcol);
		}

	    public Dimension getPreferredSize() {
	        return new Dimension(640,480);
	    }
}
