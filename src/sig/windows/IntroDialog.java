package sig.windows;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import sig.sigIRC;

public class IntroDialog extends JFrame{
	Font systemFont;
	
	public IntroDialog() {
		this.setLocationByPlatform(true);
		this.setVisible(true);
		this.setTitle("sigIRCv2 v"+sigIRC.VERSION);
		
		InputStream stream = sigIRC.class.getResourceAsStream("/resource/CP_Font.ttf");
		//File font = new File(sigIRC.BASEDIR+"sigIRC/CP_Font.ttf");
		
		try {
			systemFont = Font.createFont(Font.TRUETYPE_FONT,stream);
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(systemFont);
			systemFont = new Font("CP Font",0,16);
		} catch (FontFormatException | IOException e) {
			e.printStackTrace();
		}
		
		JPanel dialogPanel = new JPanel();
		//dialogPanel.setSize(this.getSize()); 
		dialogPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true),"Welcome to sigIRC!",TitledBorder.DEFAULT_JUSTIFICATION,TitledBorder.DEFAULT_POSITION,systemFont));
		
		this.add(dialogPanel);
		
		try {
			this.setIconImage(ImageIO.read(sigIRC.class.getResource("/resource/sigIRCicon.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JEditorPane introText = new JEditorPane();
		try {
			introText.setPage(sigIRC.class.getResource("/resource/text/introText.html"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		dialogPanel.add(introText);
		
		this.setSize(720, 480);
	}
}
