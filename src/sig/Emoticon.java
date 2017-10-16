package sig;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import sig.utils.TextUtils;

public class Emoticon {
	private BufferedImage image=null;
	private String emotename=null;
	private String spacefiller="";
	private String spacefillersmall="";
	
	public Emoticon(String emoteName, URL onlinePath) {
		try {
			String imagePath = sigIRC.BASEDIR+"sigIRC/Emotes/"+emoteName+".png";
			File file = new File(imagePath);
			if (file.exists()) {
				image = ImageIO.read(file);
				emotename = file.getName();
			} else {
				//Download it from online.
				System.out.println("Could not find emote "+emoteName+". Downloading from server...");
				image = ImageIO.read(onlinePath);
				System.out.println("Downloaded "+emoteName+".png");
				ImageIO.write(image, "png", file);
				System.out.println("Saved to "+file.getName()+".");
				emotename = emoteName;
			}
			spacefiller = GetSpaceLength(sigIRC.panel.programFont);
			spacefillersmall = GetSpaceLength(sigIRC.panel.userFont);
			//System.out.println("Space size for "+emotename+" is "+spacefiller.length());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Emoticon(String emoteName, String fileName) {
		try {
			FileManager manager = new FileManager("sigIRC/Emotes/"+fileName+".png");
			manager.verifyAndFetchFileFromServer();
			String imagePath = sigIRC.BASEDIR+"sigIRC/Emotes/"+fileName+".png";
			File file = new File(imagePath);
			if (file.exists()) {
				image = ImageIO.read(file);
				emotename = emoteName;
			}
			spacefiller = GetSpaceLength(sigIRC.panel.programFont);
			spacefillersmall = GetSpaceLength(sigIRC.panel.userFont);
			//System.out.println("Space size for "+emotename+" is "+spacefiller.length());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String GetSpaceLength(Font f) {
		StringBuilder spaces = new StringBuilder();
		while (SpaceFilledIsSmallerThanImageWidth(spaces,f)) {
			spaces.append(" ");
		}
		return spaces.toString();
	}

	public boolean SpaceFilledIsSmallerThanImageWidth(StringBuilder spaces, Font font) {
		return TextUtils.calculateStringBoundsFont(spaces.toString(), font).getWidth()<image.getWidth();
	}

	public String getEmoteName() {
		return emotename.replace(".png", "");
	}

	public BufferedImage getImage() {
		return image;
	}

	public String getSpaceFiller() {
		return spacefiller;
	}

	public String getSmallSpaceFiller() {
		return spacefillersmall;
	}
}
