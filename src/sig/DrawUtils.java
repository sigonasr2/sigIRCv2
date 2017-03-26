package sig;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;

public class DrawUtils {
	public static void drawOutlineText(Graphics g, Font font, double x, double y, int outline_size, Color text_color, Color shadow_color, String message) {
		AttributedString as = new AttributedString(message);
		as.addAttribute(TextAttribute.FONT, font);
		g.setColor(shadow_color);
		Graphics2D g2 = (Graphics2D) g;
		if (message.length()>200) {
			g2.setColor(shadow_color);
			g2.drawString(as.getIterator(),(int)x+outline_size,(int)y+outline_size);
		} else {
			FontRenderContext frc = g2.getFontMetrics(font).getFontRenderContext();
			GlyphVector gv = font.createGlyphVector(frc, message);
			Rectangle2D box = gv.getVisualBounds();
	        Shape shape = gv.getOutline((int)x,(int)y);
			g2.setClip(shape);
			g2.drawString(as.getIterator(),(int)x,(int)y);
			g2.setClip(null);
			g2.setStroke(new BasicStroke(outline_size*2));
			g2.setColor(shadow_color);
	        g2.setRenderingHint(
	                RenderingHints.KEY_ANTIALIASING,
	                RenderingHints.VALUE_ANTIALIAS_ON);
			g2.draw(shape);
		}
		g2.setColor(text_color);
		g2.drawString(as.getIterator(),(int)x,(int)y);
	}
	public static void drawText(Graphics g, double x, double y, Color color, String message) {
		AttributedString as = new AttributedString(message);
		as.addAttribute(TextAttribute.FONT, MyPanel.programFont);
		g.setColor(color);
		g.drawString(as.getIterator(),(int)x,(int)y);
	}
	public static void drawTextFont(Graphics g, Font font, double x, double y, Color color, String message) {
		AttributedString as = new AttributedString(message);
		as.addAttribute(TextAttribute.FONT, font);
		g.setColor(color);
		g.drawString(as.getIterator(),(int)x,(int)y);
	}
	public static void drawHealthbar(Graphics g, Rectangle bounds, double pct, Color healthbarcol) {
		g.setColor(Color.BLACK);
		g.draw3DRect((int)bounds.getX(), (int)bounds.getY(), (int)bounds.getWidth(), (int)bounds.getHeight(), true);
		g.setColor(healthbarcol);
		g.fill3DRect((int)bounds.getX()+1, (int)bounds.getY()+1, (int)(bounds.getWidth()*pct)-1, (int)bounds.getHeight()-1, true);
	}
}
