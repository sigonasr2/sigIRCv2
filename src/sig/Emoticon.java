package sig;
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
			spacefiller = GetSpaceLength();
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
			spacefiller = GetSpaceLength();
			//System.out.println("Space size for "+emotename+" is "+spacefiller.length());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String GetSpaceLength() {
		StringBuilder spaces = new StringBuilder();
		while (SpaceFilledIsSmallerThanImageWidth(spaces)) {
			spaces.append(" ");
		}
		return spaces.toString();
	}

	public boolean SpaceFilledIsSmallerThanImageWidth(StringBuilder spaces) {
		return TextUtils.calculateStringBoundsFont(spaces.toString(), sigIRC.panel.programFont).getWidth()<image.getWidth();
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
}
