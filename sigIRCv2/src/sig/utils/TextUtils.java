package sig.utils;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import sig.sigIRC;

public class TextUtils {

	public static Rectangle2D calculateStringBoundsFont(String msg, Font font) {
		FontRenderContext frc = sigIRC.panel.getFontMetrics(font).getFontRenderContext();
		return font.getStringBounds(msg, frc);
	}
	
	public static String replaceFirst(String sourcestring, String findstring, String replacestring) {
		int pos = sourcestring.indexOf(findstring);
		if (pos>=0) {
			String piece1 = sourcestring.substring(0,pos);
			String piece2 = sourcestring.substring(pos+findstring.length(),sourcestring.length());
			//basemsg = basemsg.replaceFirst(e.getEmoteName(),e.getSpaceFiller());
			sourcestring = piece1+replacestring+piece2;
		}
		return sourcestring;
	}
	
	public static boolean isAlphanumeric(String str) {
		return str.matches("^[a-zA-Z0-9!\\-.?'\": ]+$");
	}
	
	public static boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
	
	public static boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}
	
	public static String convertSecondsToTimeFormat(int seconds) {
		StringBuilder sb = new StringBuilder();
		int sec = seconds%60;
		int min = (seconds/60)%60;
		int hrs = (seconds/3600)%24;
		if (hrs>0) {
			if (hrs>=10) {
				sb.append(hrs);
			} else {
				sb.append(0);
				sb.append(hrs);
			}
			sb.append(":");
		}
		if (min>=10) {
			sb.append(min);
		} else {
			sb.append(0);
			sb.append(min);
		}
		sb.append(":");
		if (sec>=10) {
			sb.append(sec);
		} else {
			sb.append(0);
			sb.append(sec);
		}
		return sb.toString();
	}
	
	public static String getActualChannelName() {
		return sigIRC.channel.replaceFirst("#", "");
	}
	
	/**
	 * Converts a three CSV value to RGB value.
	 */
	public static Color convertStringToColor(String col) {
		String[] split = col.split(",");
		return new Color(Integer.parseInt(split[0]),Integer.parseInt(split[1]),Integer.parseInt(split[2]));
	}
	
	public static List<String> WrapText(String msg, Font font, double width) {
		List<String> displayMessage = new ArrayList<String>();
		String rawmessage = msg;
		int textWidth = (int)TextUtils.calculateStringBoundsFont(rawmessage, font).getWidth();
		int maxWidth = (int)width;
		do {
			rawmessage = BreakTextAtNextSection(rawmessage+" ",font,displayMessage,maxWidth);
			textWidth = (int)TextUtils.calculateStringBoundsFont(rawmessage, font).getWidth();
		} while (textWidth>maxWidth);
		if (rawmessage.length()>0) {
			displayMessage.add(rawmessage);
		}
		return displayMessage;
		//System.out.println(displayMessage+": "+messageDisplaySize);
	}
	
	private static String BreakTextAtNextSection(String msg, Font font, List<String> list, int maxWidth) {
		int marker = 1;
		int textWidth = (int)TextUtils.calculateStringBoundsFont(msg.substring(0, marker), font).getWidth();
		while (textWidth<maxWidth) {
			if (marker<msg.length()) {
				int tempmarker = msg.indexOf(' ', marker);
				if (tempmarker!=-1) {
					textWidth = (int)TextUtils.calculateStringBoundsFont(msg.substring(0, tempmarker), font).getWidth();
					if (textWidth<maxWidth) {
						marker = tempmarker+1;
					}
					//System.out.println(msg.substring(0, marker)+" | "+textWidth);
				} else {
					marker=msg.length();
					break;
				}
			} else {
				break;
			}
		}
		String currentText = msg.substring(0, marker);
		list.add(currentText);
		return msg.substring(marker, msg.length());
	}
}
