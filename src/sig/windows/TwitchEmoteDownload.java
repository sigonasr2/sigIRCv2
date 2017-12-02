package sig.windows;

import java.net.MalformedURLException;
import java.net.URL;

import sig.Emoticon;
import sig.sigIRC;

public class TwitchEmoteDownload {
	String name;
	int id;
	
	public TwitchEmoteDownload(String name,int id) {
		this.name=name;
		this.id=id;
	}
	
	public void download() throws MalformedURLException {
		sigIRC.emoticons.add(new Emoticon(name,new URL(sigIRC.TWITCHEMOTEURL+id+"/1.0")));
	}
}
