package sig;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class ConfigFile {
	String basepath;
	Properties properties;
	public static HashMap<String,String> configDefaults = new HashMap<String,String>();
	
	public static void configureDefaultConfiguration() {
		configDefaults.put("server", "irc.chat.twitch.tv");
		configDefaults.put("nickname", "SigoNitori");
		configDefaults.put("channel", "#sigonitori");
		configDefaults.put("dingThreshold", "6");
		configDefaults.put("backgroundColor", Integer.toString(Color.CYAN.getRGB()));
	}
	
	public static void setAllDefaultProperties(ConfigFile conf) {
		for (String key : configDefaults.keySet()) {
			conf.setProperty(key, configDefaults.get(key));
		}
	}
	
	public ConfigFile(String basepath) {
		this.basepath=basepath;
		properties = new Properties();
		try {
			FileReader reader = GetFileReader(this.basepath);
			if (reader!=null) {
				properties.load(reader);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getProperty(String key) {
		String val = properties.getProperty(key);
		if (val==null) {
			if (configDefaults.containsKey(key)) {
				this.setProperty(key, configDefaults.get(key));
				this.saveProperties();
				return properties.getProperty(key);
			} else {
				return null;
			}
		} else {
			return val;
		}
	}
	
	public void setProperty(String key, String value) {
		properties.setProperty(key, value);
	}
	
	public void saveProperties() {
		try {
			properties.store(GetFileWriter(basepath), "Properties file for sigIRCv2\n");
			System.out.println("Properties successfully saved.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private FileReader GetFileReader(String basepath) {
		File file = new File(sigIRC.BASEDIR+basepath);
		if (file.exists()) {
			try {
				return new FileReader(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private FileWriter GetFileWriter(String basepath) {
		File file = new File(sigIRC.BASEDIR+basepath);
		try {
			return new FileWriter(file,false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
