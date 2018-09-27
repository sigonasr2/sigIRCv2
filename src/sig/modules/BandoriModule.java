package sig.modules;

import java.awt.Graphics;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import sig.FileManager;
import sig.Module;
import sig.sigIRC;
import sig.utils.SoundUtils;

public class BandoriModule extends Module{
	public static BandoriModule bandori_module;
	public BufferedImage stamp_collection1,stamp_collection2;
	public static HashMap<String,ImageScheme> image_map = new HashMap<String,ImageScheme>();
	public static HashMap<String,List<String>> stamp_map = new HashMap<String,List<String>>();
	static List<Stamp> active_stamps = new ArrayList<Stamp>();
	
	public BandoriModule(Rectangle2D bounds, String moduleName) {
		this(bounds,moduleName,true);
	}

	public BandoriModule(Rectangle2D bounds, String moduleName, boolean enabled) {
		super(bounds, moduleName, enabled);
		Initialize();
	}
	
	void Initialize() {
		BandoriModule.bandori_module = this;
		try {
			stamp_collection1 = ImageIO.read(new File(sigIRC.BASEDIR+"sigIRC/stamps1.png"));
			stamp_collection2 = ImageIO.read(new File(sigIRC.BASEDIR+"sigIRC/stamps2.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		image_map.put("kasumi_gogo", new ImageScheme(stamp_collection1,0));
		image_map.put("tae_letsplay", new ImageScheme(stamp_collection1,1));
		image_map.put("rimi_choco", new ImageScheme(stamp_collection1,2));
		image_map.put("saya_bread", new ImageScheme(stamp_collection1,3));
		image_map.put("arisa_doki", new ImageScheme(stamp_collection1,4));
		image_map.put("ran_same", new ImageScheme(stamp_collection1,5));
		image_map.put("moca_youdidit", new ImageScheme(stamp_collection1,6));
		image_map.put("himari_heyheyhoh", new ImageScheme(stamp_collection1,7));
		image_map.put("tomoe_letsdothis", new ImageScheme(stamp_collection1,8));
		image_map.put("tsugumi_wecandoit", new ImageScheme(stamp_collection1,9));
		image_map.put("kokoro_happylucky", new ImageScheme(stamp_collection1,10));
		image_map.put("kaoru_fleeting", new ImageScheme(stamp_collection1,11));
		image_map.put("hagumi_smileyay", new ImageScheme(stamp_collection1,12));
		image_map.put("kanon_fuee", new ImageScheme(stamp_collection1,13));
		image_map.put("misaki_ready", new ImageScheme(stamp_collection1,14));
		image_map.put("aya_fever", new ImageScheme(stamp_collection1,15));
		image_map.put("hina_fullcombo", new ImageScheme(stamp_collection1,16));
		image_map.put("chisato_planned", new ImageScheme(stamp_collection1,17));
		image_map.put("maya_huhehe", new ImageScheme(stamp_collection1,18));
		image_map.put("eve_bushido", new ImageScheme(stamp_collection1,19));
		image_map.put("yukina_notbad", new ImageScheme(stamp_collection1,20));
		image_map.put("sayo_goodwork", new ImageScheme(stamp_collection1,21));
		image_map.put("lisa_nextonelastone", new ImageScheme(stamp_collection1,22));
		image_map.put("ako_onemoretime", new ImageScheme(stamp_collection1,23));
		image_map.put("rinko_jam", new ImageScheme(stamp_collection2,0));
		image_map.put("marina_yeahyeah", new ImageScheme(stamp_collection2,1)); //Skip 2.
		image_map.put("kokoro_moremore", new ImageScheme(stamp_collection2,3));
		image_map.put("arisa_huh", new ImageScheme(stamp_collection2,4));
		image_map.put("yukina_followmylead", new ImageScheme(stamp_collection2,5));
		image_map.put("kaoru_suchalovelyevening", new ImageScheme(stamp_collection2,6));
		image_map.put("rimi_congrats", new ImageScheme(stamp_collection2,7));
		image_map.put("ran_somethingbigiscoming", new ImageScheme(stamp_collection2,8));
		image_map.put("tsugumi_comeon", new ImageScheme(stamp_collection2,9));
		image_map.put("tae_fufusocute", new ImageScheme(stamp_collection2,10));
		image_map.put("eve_marchintobattle", new ImageScheme(stamp_collection2,11));
		image_map.put("saya_illtry", new ImageScheme(stamp_collection2,12));
		image_map.put("lisa_imsohappy", new ImageScheme(stamp_collection2,13));
		image_map.put("sayo_ohwell", new ImageScheme(stamp_collection2,14));
		image_map.put("ako_areyouokay", new ImageScheme(stamp_collection2,15));
		image_map.put("chisato_thisissomuchfun", new ImageScheme(stamp_collection2,16));
		image_map.put("rinko_theresnoway", new ImageScheme(stamp_collection2,17));
		image_map.put("tae_thisisgreat", new ImageScheme(stamp_collection2,18));
		image_map.put("moca_thisisgettinginteresting", new ImageScheme(stamp_collection2,19));
		image_map.put("kaoru_takemyhand", new ImageScheme(stamp_collection2,20));
		image_map.put("kokoro_letsmaketheworldsmile", new ImageScheme(stamp_collection2,21));
		image_map.put("hina_nowwereboppin", new ImageScheme(stamp_collection2,22));
		
		stamp_map.put("kasumi_gogo",Arrays.asList("gogo"));
		stamp_map.put("tae_letsplay",Arrays.asList("playtogether","wanttoplay","multilive","letsplay"));
		stamp_map.put("rimi_choco",Arrays.asList("choco","cornet"));
		stamp_map.put("saya_bread",Arrays.asList("bread"));
		stamp_map.put("arisa_doki",Arrays.asList("doki","chomama"));
		stamp_map.put("ran_same",Arrays.asList("sameasalways","alwayssame","alwaysthesame"));
		stamp_map.put("moca_youdidit",Arrays.asList("youdidit","congratulations","buns","mocatastic"));
		stamp_map.put("himari_heyheyhoh",Arrays.asList("heyo","heyhey","hihi","hiyo"));
		stamp_map.put("tomoe_letsdothis",Arrays.asList("letsdothis","letsdoit"));
		stamp_map.put("tsugumi_wecandoit",Arrays.asList("wegotthis","wegotit","wecan"));
		stamp_map.put("kokoro_happylucky",Arrays.asList("happy!","lucky"));
		stamp_map.put("kaoru_fleeting",Arrays.asList("fleeting"));
		stamp_map.put("aya_fever",Arrays.asList("fever","ayay"));
		stamp_map.put("hagumi_smileyay",Arrays.asList("smileyay","yay"));
		stamp_map.put("kanon_fuee",Arrays.asList("fue","waa","reee"));
		stamp_map.put("misaki_ready",Arrays.asList("amready","beenready","ready!"));
		stamp_map.put("hina_fullcombo",Arrays.asList("fcd","fullcombo","nomiss","allperfect","notasinglemiss","thefc","anfc","fullperfect"));
		stamp_map.put("chisato_planned",Arrays.asList("justasplanned","allplanned","calculated","thatcoming"));
		stamp_map.put("maya_huhehe",Arrays.asList("hehe","huehe","huehue","shuwashuwa"));
		stamp_map.put("eve_bushido",Arrays.asList("bushido"));
		stamp_map.put("yukina_notbad",Arrays.asList("notbad","veryclose"));
		stamp_map.put("sayo_goodwork",Arrays.asList("goodwork","goodjob","nicejob","welldone","greatwork","greatjob"));
		stamp_map.put("lisa_nextonelastone",Arrays.asList("lastone","mylast"));
		stamp_map.put("ako_onemoretime",Arrays.asList("onemore","goagain","keepgoing","dontstop"));
		stamp_map.put("rinko_jam",Arrays.asList("lovethissong","jam"));
		stamp_map.put("marina_yeahyeah",Arrays.asList("yeahyeah","letsgo"));
		stamp_map.put("kokoro_moremore",Arrays.asList("moremore","iwantmore"));
		stamp_map.put("arisa_huh",Arrays.asList("huh?","hh?","yy?","aat?","aa?","tt?","nani","nand"));
		stamp_map.put("yukina_followmylead",Arrays.asList("followmylead","takethelead","guideyou","fullydevoted"));
		stamp_map.put("kaoru_suchalovelyevening",Arrays.asList("goodevening","lovelyevening","beautifulnight","grandnight","wonderfulevening"));
		stamp_map.put("rimi_congrats",Arrays.asList("grats"));
		stamp_map.put("ran_somethingbigiscoming",Arrays.asList("somethingbig","iscoming"));
		stamp_map.put("tsugumi_comeon",Arrays.asList("comeon","dontbeafraid","dontbeshy","tsugurific"));
		stamp_map.put("tae_fufusocute",Arrays.asList("socute","kawaii","fufu","adorable","cute"));
		stamp_map.put("eve_marchintobattle",Arrays.asList("marchintobattle","chargeintobattle"));
		stamp_map.put("saya_illtry",Arrays.asList("illtry","itachance","itatry","atleastonce"));
		stamp_map.put("lisa_imsohappy",Arrays.asList("ecstatic","sohappy","toohappy"));
		stamp_map.put("sayo_ohwell",Arrays.asList("ohwell","ahwell","youtried"));
		stamp_map.put("ako_areyouokay",Arrays.asList("youok","beok","daijo"));
		stamp_map.put("chisato_thisissomuchfun",Arrays.asList("muchfun","veryfun","reallyfun","extremelyfun","offun"));
		stamp_map.put("rinko_theresnoway",Arrays.asList("noway"));
		stamp_map.put("tae_thisisgreat",Arrays.asList("thisisgreat","thisisawesome","thisiswonderful"));
		stamp_map.put("moca_thisisgettinginteresting",Arrays.asList("gettinginteresting","thingsaregetting","thisisgetting"));
		stamp_map.put("kaoru_takemyhand",Arrays.asList("takemyhand","allowmeto","demonstrate","romeo"));
		stamp_map.put("kokoro_letsmaketheworldsmile",Arrays.asList("hhw","happyworld","hellohappy","worldsmile"));
		stamp_map.put("hina_nowwereboppin",Arrays.asList("bop","nowwere"));
		
		/*for (String s : image_map.keySet()) {
			ImageScheme scheme = image_map.get(s);
			try {
				BufferedImage img = crop(scheme.base,scheme.stamp_index%6*270+4, scheme.stamp_index/6*223+3, 258, 214);
				ImageIO.write(img, "png", new File(sigIRC.BASEDIR + "sigIRC/stamps/"+s+".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
	}
	
	public BufferedImage crop(BufferedImage img, int x, int y, int targetWidth, int targetHeight) throws IOException {
        int height = img.getHeight();
        int width = img.getWidth();

        // Coordinates of the image's middle
        int xc = (width - targetWidth) / 2;
        int yc = (height - targetHeight) / 2;

        // Crop
        BufferedImage croppedImage = img.getSubimage(x, y, targetWidth, targetHeight);
        /*BufferedImage croppedImage = img.getSubimage(
                        xc, 
                        yc,
                        targetWidth, // widht
                        targetHeight // height
        );*/
        return croppedImage;
    }
	
	public void run() {
		for (int i=0;i<active_stamps.size();i++) {
			Stamp s = active_stamps.get(i);
			if (!s.run()) {
				active_stamps.remove(i--);
			}
		}
	}
	
	public static void checkForStamp(String user,String message) {
		boolean foundmatch = false;
		message = message.toLowerCase().replaceAll("[ ]", "");
		if (message.length()>480) {
			return;
		}
		for (String key : image_map.keySet()) {
			for (String message_search : stamp_map.get(key)) {
				String filteredmessage = message;
				filteredmessage = filteredmessage.replaceAll("[^A-Za-z0-9]","");
				//System.out.println(filteredmessage);
				if (message_search.contains("?") || message_search.contains("!")) {
					if (message.contains(message_search)) {
						foundmatch = true;
						CreateStamp(key);
						System.out.println("Stamp "+key+" created by user "+user+" MESSAGE:"+message+".");
						break;
					}
				} else {
					if (filteredmessage.contains(message_search)) {
						foundmatch=true;
						CreateStamp(key);
						System.out.println("Stamp "+key+" created by user "+user+" MESSAGE:"+message+".");
						break;
					}
				}
			}
			if (foundmatch) {
				break;
			}
		}
	}
	
	public static void CreateStamp(String stamp_name) {
		final int STAMP_DURATION = 180;
		String soundName = sigIRC.BASEDIR+"sigIRC/sounds/stamp_sound.wav";    
		FileManager manager = new FileManager("sigIRC/sounds/stamp_sound.wav");
		manager.verifyAndFetchFileFromServer();
		SoundUtils.playSound(soundName);
		active_stamps.add(new Stamp(image_map.get(stamp_name),STAMP_DURATION));
	}
	
	public void ApplyConfigWindowProperties() {
		sigIRC.bandorimodule_X=(int)position.getX();
		sigIRC.bandorimodule_Y=(int)position.getY();
		sigIRC.config.setInteger("BANDORI_module_X", sigIRC.bandorimodule_X);
		sigIRC.config.setInteger("BANDORI_module_Y", sigIRC.bandorimodule_Y);
	}
	
	public void windowClosed(WindowEvent ev) {
		
	}
	
	public void draw(Graphics g) {
		super.draw(g);
		for (Stamp s : active_stamps) {
			//Stamp is 130x107 pixels
			g.drawImage(s.scheme.base, (int)(s.randX+position.getX()), (int)position.getY()+24, (int)(s.randX+130+position.getX()), (int)position.getY()+24+107,
					s.scheme.stamp_index%6*270+4, s.scheme.stamp_index/6*223+3, s.scheme.stamp_index%6*270+260+4, s.scheme.stamp_index/6*223+214+3, sigIRC.panel);
		}
	}
}

class Stamp{
	ImageScheme scheme;
	int timer;
	int randX = (int)(Math.random()*(BandoriModule.bandori_module.position.getWidth()-130));
	Stamp(ImageScheme scheme,int start_timer) {
		this.scheme = scheme;
		this.timer = start_timer;
	}
	public boolean run() {
		return --timer>0;
	}
}

class ImageScheme{
	BufferedImage base;
	int stamp_index; //0-23.
	ImageScheme(BufferedImage base,int index) {
		this.base=base;
		this.stamp_index=index;
	}
}