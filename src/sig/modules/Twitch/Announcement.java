package sig.modules.Twitch;

import java.io.File;
import java.text.DateFormat;

import com.mb3364.twitch.api.models.User;

import sig.modules.TwitchModule;
import sig.utils.FileUtils;

public class Announcement {
	User userData;
	long twitchID;
	public Announcement(long twitchID) {
		String userFilePath = TwitchModule.USERDIR+twitchID;
		File userFile = new File(userFilePath);
		if (userFile.exists()) {
			String[] contents = FileUtils.readFromFile(userFilePath);
			userData = new User();
			int i=1;
			userData.setId(twitchID);
			userData.setBio(contents[i++]);
			userData.setDisplayName(contents[i++]);
			userData.setLogo(contents[i++]);
			userData.setName(contents[i++]);
			userData.setType(contents[i++]);
		} else {
			System.out.println("WARNING! Could not find user with ID "+twitchID+"!");
		}
		this.twitchID=twitchID;
	}
	
	public Announcement(User data) {
		this.userData=data;
		this.twitchID=data.getId();
	}
	
	public String toString() {
		return userData.toString();
	}
	
	public User getUser() {
		return userData;
	}
}
