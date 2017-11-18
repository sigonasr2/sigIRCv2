package sig.utils;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.text.AttributedString;

import sig.MyPanel;

public class DrawUtils {
	public static void drawOutlineText(Graphics g, Font font, double x, double y, int outline_size, Color text_color, Color shadow_color, String message) {
		drawOutlineText(g,font,x,y,0,0,outline_size,text_color,shadow_color,message);
	}
	static void drawOutlineText(Graphics g, Font font, double x, double y, double xoffset, double yoffset, int outline_size, Color text_color, Color shadow_color, String message) {
		AttributedString as = new AttributedString(message);
		as.addAttribute(TextAttribute.FONT, font);
		g.setColor(shadow_color);
		Graphics2D g2 = (Graphics2D) g;
		if (message.length()>200) {
			g2.setColor(shadow_color);
			g2.drawString(as.getIterator(),(int)(x+outline_size+xoffset),(int)(y+outline_size+yoffset));
		} else {
			FontRenderContext frc = g2.getFontMetrics(font).getFontRenderContext();
			GlyphVector gv = font.createGlyphVector(frc, message);
			Rectangle2D box = gv.getVisualBounds();
	        Shape shape = gv.getOutline((int)(x+xoffset),(int)(y+yoffset));
			g2.setClip(shape);
			g2.drawString(as.getIterator(),(int)(x+xoffset),(int)(y+yoffset));
			g2.setClip(null);
			g2.setStroke(new BasicStroke(outline_size*2));
			g2.setColor(shadow_color);
	        g2.setRenderingHint(
	                RenderingHints.KEY_ANTIALIASING,
	                RenderingHints.VALUE_ANTIALIAS_ON);
			g2.draw(shape);
		}
		g2.setColor(text_color);
		g2.drawString(as.getIterator(),(int)(x+xoffset),(int)(y+yoffset));
	}
	public static void drawCenteredOutlineText(Graphics g, Font font, double x, double y, int outline_size, Color text_color, Color shadow_color, String message) {
		Rectangle2D textBounds = TextUtils.calculateStringBoundsFont(message, font);
		drawOutlineText(g,font,x,y,-textBounds.getWidth()/2,-textBounds.getHeight()/2,outline_size,text_color,shadow_color,message);
	}
	public static void drawText(Graphics g, double x, double y, Color color, String message) {
		if (message.length()>0) {
			AttributedString as = new AttributedString(message);
			as.addAttribute(TextAttribute.FONT, MyPanel.programFont);
			g.setColor(color);
			g.drawString(as.getIterator(),(int)x,(int)y);
		}
	}
	public static void drawTextFont(Graphics g, Font font, double x, double y, Color color, String message) {
		if (message.length()>0) {
			AttributedString as = new AttributedString(message);
			as.addAttribute(TextAttribute.FONT, font);
			g.setColor(color);
			g.drawString(as.getIterator(),(int)x,(int)y);
		}
	}
	public static void drawHealthbar(Graphics g, Rectangle bounds, double pct, Color healthbarcol) {
		g.setColor(Color.BLACK);
		g.draw3DRect((int)bounds.getX(), (int)bounds.getY(), (int)bounds.getWidth(), (int)bounds.getHeight(), true);
		g.setColor(healthbarcol);
		g.fill3DRect((int)bounds.getX()+1, (int)bounds.getY()+1, (int)(bounds.getWidth()*pct)-1, (int)bounds.getHeight()-1, true);
	}
	/**
	 * Centers the text along the X Axis.
	 */
	public static void drawCenteredText(Graphics g, Font font, int x, int y, Color color, String text) {
		AttributedString as = new AttributedString(text);
		as.addAttribute(TextAttribute.FONT, font);
		g.setColor(color);
		Rectangle2D textBounds = TextUtils.calculateStringBoundsFont(text, font);
		g.drawString(as.getIterator(),(int)(x-textBounds.getWidth()/2),(int)(y+textBounds.getHeight()));
	}
	
	public static Color convertStringToColor(String s) {
		String[] split = s.split(",");
		if (split.length==3) {
			return new Color(
					Math.min(Math.abs(Integer.parseInt(split[0])),255),
					Math.min(Math.abs(Integer.parseInt(split[1])),255),
					Math.min(Math.abs(Integer.parseInt(split[2])),255));
		} else 
		if (split.length==4) {
			return new Color(
					Math.min(Math.abs(Integer.parseInt(split[0])),255),
					Math.min(Math.abs(Integer.parseInt(split[1])),255),
					Math.min(Math.abs(Integer.parseInt(split[2])),255),
					Math.min(Math.abs(Integer.parseInt(split[3])),255));
		} else {
			System.out.println("WARNING! Invalid Color string specified ("+s+").");
			return null;
		}
	}
	
	public static void drawImage(Graphics g, Image img, double x, double y, Color blend_col, ImageObserver source) {
		BufferedImage tmp = new BufferedImage(img.getWidth(source),img.getHeight(source),BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = tmp.createGraphics();
		g2.drawImage(img, 0, 0, null);
		g2.setComposite(AlphaComposite.SrcAtop);
		g2.setColor(blend_col);
		g2.fillRect(0, 0, img.getWidth(source), img.getHeight(source));
		g2.dispose();
		g.drawImage(tmp,(int)x,(int)y,source);
	}
	
	public static Color invertColor(Color c) {
		return new Color(255-c.getRed(),255-c.getGreen(),255-c.getBlue(),255);
	}
}
