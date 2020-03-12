package sig.modules.Twitch;

import java.io.File;
import java.text.DateFormat;

import com.mb3364.twitch.api.models.User;

import sig.modules.TwitchModule;
import sig.utils.FileUtils;

public class Announcement {
	Follower userData;
	long twitchID;
	public Announcement(long twitchID) {
		String userFilePath = TwitchModule.USERDIR+twitchID;
		File userFile = new File(userFilePath);
		if (userFile.exists()) {
			int i=0;
			String[] contents = FileUtils.readFromFile(userFilePath);
			userData = new Follower(twitchID,
					contents[i++],
					"",
					contents[i++],
					contents[i++],
					contents[i++],
					contents[i++],
					"");
		} else {
			System.out.println("WARNING! Could not find user with ID "+twitchID+"!");
		}
		this.twitchID=twitchID;
	}
	
	public Announcement(Follower data) {
		this.userData=data;
		this.twitchID=data.id;
	}
	
	public String toString() {
		return userData.toString();
	}
	
	public Follower getUser() {
		return userData;
	}
}
