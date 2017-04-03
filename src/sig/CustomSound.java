package sig;


public class CustomSound {
	final static int SOUNDCOOLDOWN = 108000;
	private int cooldown = 0;
	private String customsound;
	private String username;
	private FileManager manager;
	
	public String getUsername() {
		return username;
	}

	/**
	 * Initializes a <b>Custom Sound</b> object that will listen for a specific username.
	 * @param username The username to listen for playing this sound.
	 * @param customsound Just the filename of the custom sound. Not the absolute path.
	 */
	public CustomSound(String username, String customsound) {
		this.username=username;
		this.customsound=customsound;
		this.manager = new FileManager("sigIRC/sounds/"+customsound);
	}
	
	public boolean isSoundAvailable() {
		return cooldown<=0;
	}
	
	public void decreaseCooldown(int amt) {
		cooldown-=amt;
	}
	
	public void playCustomSound() {
		manager.verifyAndFetchFileFromServer();
		SoundUtils.playSound(sigIRC.BASEDIR+"sounds/"+customsound);
		cooldown = SOUNDCOOLDOWN;
	}
	
	public static CustomSound getCustomSound(String user) {
		for (CustomSound cs : sigIRC.customsounds) {
			if (cs.getUsername().equalsIgnoreCase(user)) {
				return cs;
			}
		}
		return null;
	}
}
