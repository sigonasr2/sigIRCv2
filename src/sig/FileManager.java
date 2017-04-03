package sig;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class FileManager {
	String fileloc;
	final String serverURL = "http://45.33.13.215/sigIRCv2/";
	boolean folder=false;
	
	public FileManager(String location) {
		this.fileloc=location;
		this.folder=false;
	}
	
	public FileManager(String location, boolean folder) {
		this.fileloc=location;
		this.folder=folder;
	}
	
	public String getRelativeFileLocation() {
		return fileloc;
	}
	
	public void verifyAndFetchFileFromServer() {
		File file = new File(sigIRC.BASEDIR+fileloc);
		if (folder) {
			if (!file.exists()) {
				System.out.println("Could not find "+file.getAbsolutePath()+", creating Folder "+file.getName()+".");
				if (file.mkdirs()) {
					System.out.println(" >> Successfully created "+file.getAbsolutePath()+".");
				}
			}
		} else {
			if (!file.exists()) {
				System.out.println("Could not find "+file.getAbsolutePath()+", retrieving file online from "+serverURL+file.getName()+".");
				try {
					org.apache.commons.io.FileUtils.copyURLToFile(new URL(serverURL+fileloc),file);
					if (file.exists()) {
						System.out.println(" >> Successfully downloaded "+file.getAbsolutePath()+".");
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
