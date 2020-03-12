package sig.modules.RabiRace;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sig.modules.RabiRaceModule;
import sig.utils.ReflectUtils;

public class Session {
	long creationTime = 0;
	long updateTime = 0;
	String name = "";
	int maxPlayers = 0;
	String password = "";
	float difficulty = -1;
	boolean coop = false;
	int gamemode = 0; //0 = Egg Mode, 1 = Item Hunt Mode
	String[] itemHuntData;
	int eggCount = 0;
	int id = 0;
	List<Profile> players = new ArrayList<Profile>();
	
	public Session(String dataString) {
		String[] split = dataString.split(",");
		
		int i=0;
		
		id = Integer.parseInt(split[i++]);
		creationTime = Long.parseLong(split[i++]);
		updateTime = Long.parseLong(split[i++]);
		name = split[i++];
		maxPlayers = Integer.parseInt(split[i++]);
		password = split[i++];
		//System.out.println(this.toString());
		if (split.length>=7) {
			String val = split[i++];
			String[] playerlist = val.split(";");
			//System.out.println(Arrays.toString(playerlist));
			if (playerlist.length>1) {
				for (String s : playerlist) {
					Profile p = new Profile(RabiRaceModule.module,true);
					p.username=s;
					p.avatar = Profile.GetSeededAvatar(p.username);
					//System.out.println("Player "+p.username);
					DownloadAndAddPlayer(p);
				}
			} else {
				Profile p = new Profile(RabiRaceModule.module,true);
				p.username=val;
				p.avatar = Profile.GetSeededAvatar(p.username);
				//System.out.println("Player "+p.username);
				DownloadAndAddPlayer(p);
			}
		}
		if (split.length>=8) {	
			difficulty = Float.parseFloat(split[i++]);
		}
		if (split.length>=9) {	
			gamemode = Integer.parseInt(split[i++]);
			switch (gamemode) {
				case 0:{
					eggCount = Integer.parseInt(split[i++]);
				}break;
				case 1:{
					itemHuntData = split[i++].split(";");
				}break;
			}
		}
		if (split.length>=11) {
			coop = Boolean.parseBoolean(split[i++]);
		}
	}

	private void DownloadAndAddPlayer(Profile p) {
		if (p.downloadProfile()) {
			if (RabiRaceModule.mySession==null && p.username.equalsIgnoreCase(RabiRaceModule.module.myProfile.username)) {
				RabiRaceModule.mySession = this;
				//RetrieveClientAvatar(p);
			}
			//System.out.println("Adding Player "+p);
			players.add(p);
		}
	}

	public int getID() {
		return id;
	}
	
	public List<Profile> getPlayers() {
		return players;
	}
	
	public boolean isCoop() {
		return coop;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getName()+"(");
		boolean first=false;
		for (Field f : this.getClass().getDeclaredFields()) {
			if (!first) {
				try {
					sb.append(f.getName()+"="+f.get(this));
					first=true;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			} else {
				try {
					sb.append(","+f.getName()+"="+f.get(this));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		sb.append(")");
		return sb.toString();
	}
}
