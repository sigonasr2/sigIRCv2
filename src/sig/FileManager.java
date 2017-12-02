package sig;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import sig.utils.DebugUtils;

public class FileManager {
	String fileloc;
	final String serverURL = "http://45.33.13.215/sigIRCv2/";
	boolean folder=false;
	
	public FileManager(String location) {
		this.fileloc=location;
		if (fileloc.contains(" ")) {
			System.out.println("!!!!!!!!!!!!!!!!!!");
			System.out.println("!!!!!WARNING!!!!!!");
			System.out.println("There is a space in the global file location "+fileloc+". This is going to cause huge errors!");
			System.out.println("!!!!!!!!!!!!!!!!!!");
			DebugUtils.showStackTrace();
		}
		this.folder=false;
	}
	
	public FileManager(String location, boolean folder) {
		this.fileloc=location;
		this.folder=folder;
	}
	
	public String getRelativeFileLocation() {
		return fileloc;
	}
	
	public boolean verifyAndFetchFileFromServer() {
		if (fileloc.contains("_FAKE_")) {
			return false;
		}
		File file = new File(sigIRC.BASEDIR+fileloc);
		if (folder) {
			if (!file.exists()) {
				System.out.println("Could not find "+file.getAbsolutePath()+", creating Folder "+file.getName()+".");
				if (file.mkdirs()) {
					System.out.println(" >> Successfully created "+file.getAbsolutePath()+".");
					return true;
				}
			} else {
				return true;
			}
		} else {
			if (!file.exists()) {
				System.out.println("Could not find "+file.getAbsolutePath()+", retrieving file online from "+serverURL+file.getName()+".");
				try {
					org.apache.commons.io.FileUtils.copyURLToFile(new URL(serverURL+fileloc),file);
					if (file.exists()) {
						System.out.println(" >> Successfully downloaded "+file.getAbsolutePath()+".");
						return true;
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				return true;
			}
		}
		return false;
	}
}
