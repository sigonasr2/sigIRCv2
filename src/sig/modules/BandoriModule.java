package sig.modules;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import javafx.geometry.Point2D;
import sig.FileManager;
import sig.Module;
import sig.sigIRC;
import sig.modules.Bandori.SongPart;
import sig.utils.DrawUtils;
import sig.utils.SoundUtils;

public class BandoriModule extends Module{
	public static BandoriModule bandori_module;
	public BufferedImage stamp_collection1,stamp_collection2;
	public static HashMap<String,ImageScheme> image_map = new HashMap<String,ImageScheme>();
	public static HashMap<String,List<String>> stamp_map = new HashMap<String,List<String>>();
	static List<Stamp> active_stamps = new ArrayList<Stamp>();
	String songtitle = "";
	List<SongPart> parts = new ArrayList<SongPart>();
	boolean holdControl = false, holdAlt = false;
	JOptionPane pane = new JOptionPane();
	
	
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
			stamp_collection2 = ImageIO.read(new File(sigIRC.BASEDIR+"sigIRC/stamps2_2.png"));
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
		image_map.put("kokoro_what_a_great_idea", new ImageScheme(stamp_collection2,23));
		image_map.put("sayo_im_sorry", new ImageScheme(stamp_collection2,24));
		image_map.put("hagumi_hooray", new ImageScheme(stamp_collection2,25));
		image_map.put("tsugumi_amazing", new ImageScheme(stamp_collection2,26));
		image_map.put("eve_thefruits", new ImageScheme(stamp_collection2,27));
		image_map.put("lisa_imnotready", new ImageScheme(stamp_collection2,28));
		image_map.put("arisa_okayhereigo", new ImageScheme(stamp_collection2,29));
		image_map.put("tomoe_giveityourall", new ImageScheme(stamp_collection2,30));
		image_map.put("kanon_keepgoing", new ImageScheme(stamp_collection2,31));
		
		stamp_map.put("kasumi_gogo",Arrays.asList("gogo"));
		stamp_map.put("tae_letsplay",Arrays.asList("playtogether","wanttoplay","multilive","letsplay"));
		stamp_map.put("rimi_choco",Arrays.asList("choco","cornet","ちょこ","ころね","チョコ","コロネ"));
		stamp_map.put("saya_bread",Arrays.asList("bread","pan","ぱん","パン"));
		stamp_map.put("arisa_doki",Arrays.asList("doki","chomama","baka","ばか","バカ","馬鹿","どき","ドキ"));
		stamp_map.put("ran_same",Arrays.asList("sameasalways","alwayssame","alwaysthesame","itsumodoori","いつも"));
		stamp_map.put("moca_youdidit",Arrays.asList("youdidit","congratulations","buns","mocatastic","やった","yatta","しゃーしたー"));
		stamp_map.put("himari_heyheyhoh",Arrays.asList("heyo","heyhey","hihi","hiyo","えい","おー"));
		stamp_map.put("tomoe_letsdothis",Arrays.asList("letsdothis","letsdoit","やるよ","yarimasyou","やりましょう","yaruyo"));
		stamp_map.put("tsugumi_wecandoit",Arrays.asList("wegotthis","wegotit","wecan","ganbare","がんばれ"));
		stamp_map.put("kokoro_happylucky",Arrays.asList("happy!","lucky","ハッピー","ラッキー","こころん"));
		stamp_map.put("kaoru_fleeting",Arrays.asList("fleeting","はかない","hakanai","儚い"));
		stamp_map.put("aya_fever",Arrays.asList("fever","ayay","あやや","ru","る","ナイス","フィーバー","jinglebells"));
		stamp_map.put("hagumi_smileyay",Arrays.asList("smileyay","yay!","スマイル","イエーイ"));
		stamp_map.put("kanon_fuee",Arrays.asList("fuu","fue","waa","reee","ふええ","わああ"));
		stamp_map.put("misaki_ready",Arrays.asList("amready","beenready","ready!","redii","準備完了"));
		stamp_map.put("hina_fullcombo",Arrays.asList("fcd","fullcombo","nomiss","allperfect","notasinglemiss","thefc","anfc","fullperfect","easyfc","ezfc","フルコンボよゆーでしょ","efsii"));
		stamp_map.put("chisato_planned",Arrays.asList("justasplanned","allplanned","calculated","thatcoming","keikakudoori","計画通り","けいかく","accordingtokeikaku"));
		stamp_map.put("maya_huhehe",Arrays.asList("hehe","huehe","huehue","shuwashuwa","フヘへ","しゅわしゅわ"));
		stamp_map.put("eve_bushido",Arrays.asList("bushido","ブシドー","ぶしど"));
		stamp_map.put("yukina_notbad",Arrays.asList("notbad","veryclose","やるわね","おしい","おしかった","悪くはない"));
		stamp_map.put("sayo_goodwork",Arrays.asList("goodwork","goodjob","nicejob","welldone","greatwork","greatjob","よくやった","お疲れ様でした","おつかれ","otsukare"));
		stamp_map.put("lisa_nextonelastone",Arrays.asList("lastone","mylast","さいご","次はさいご","次はラストねん"));
		stamp_map.put("ako_onemoretime",Arrays.asList("onemore","goagain","keepplaying","dontstop","runit","もういっかい","mouikkai","もう一回"));
		stamp_map.put("rinko_jam",Arrays.asList("lovethissong","jam"/*,"happybirthday"*/),"この歌好き");
		stamp_map.put("marina_yeahyeah",Arrays.asList("yeahyeah","letsgo","運営"));
		stamp_map.put("kokoro_moremore",Arrays.asList("moremore","iwantmore","もっと","motto","もっと欲しい"));
		stamp_map.put("arisa_huh",Arrays.asList("huh?","hh?","yy?","aat?","aa?","tt?","nani","nand","なに","何","はあ","wat"));
		stamp_map.put("yukina_followmylead",Arrays.asList("followmylead","takethelead","guideyou","fullydevoted","私についてきて"));
		stamp_map.put("kaoru_suchalovelyevening",Arrays.asList("goodevening","lovelyevening","beautifulnight","grandnight","wonderfulevening"));
		stamp_map.put("rimi_congrats",Arrays.asList("grats","omedetou","gratz","おめでとう"));
		stamp_map.put("ran_somethingbigiscoming",Arrays.asList("somethingbig","iscoming","thatishowiroll","truecolor","Hajimaru","Hajimeru","何かが始まる"));
		stamp_map.put("tsugumi_comeon",Arrays.asList("comeon","dontbeafraid","dontbeshy","やろうよっ","やるよ","yarouyo","yaruyo"));
		stamp_map.put("tae_fufusocute",Arrays.asList("socute","kawaii","fufu","adorable","cute","ふふ","可愛い","かわいい"));
		stamp_map.put("eve_marchintobattle",Arrays.asList("marchintobattle","chargeintobattle","イクサへ"));
		stamp_map.put("saya_illtry",Arrays.asList("illtry","itachance","itatry","atleastonce","私やってみます"));
		stamp_map.put("lisa_imsohappy",Arrays.asList("ecstatic","sohappy","toohappy","うれしい","嬉しい","ureshii"));
		stamp_map.put("sayo_ohwell",Arrays.asList("ohwell","ahwell","youtried","shikatanai","仕方ないわね"));
		stamp_map.put("ako_areyouokay",Arrays.asList("youok","beok","daijoubu","大丈夫"));
		stamp_map.put("chisato_thisissomuchfun",Arrays.asList("muchfun","veryfun","reallyfun","extremelyfun","offun","楽しいわね","tanoshii"));
		stamp_map.put("rinko_theresnoway",Arrays.asList("noway","絶対ムリ","無理よ"));
		stamp_map.put("tae_thisisgreat",Arrays.asList("thisisgreat","thisisawesome","thisiswonderful","最高だね","saikou"));
		stamp_map.put("moca_thisisgettinginteresting",Arrays.asList("gettinginteresting","thingsaregetting","thisisgetting","omoshiroi","面白くなってきた"));
		stamp_map.put("kaoru_takemyhand",Arrays.asList("takemyhand","allowmeto","demonstrate","romeo","手をとって"));
		stamp_map.put("kokoro_letsmaketheworldsmile",Arrays.asList("hhw","happyworld","hellohappy","worldsmile","世界を笑顔に"));
		stamp_map.put("hina_nowwereboppin",Arrays.asList("bop","nowwere","zap","run","るん","るんってきた"));
		stamp_map.put("kokoro_what_a_great_idea",Arrays.asList("greatidea","goodidea","greatthinking","goodthinking","素敵なアイディアね"));
		stamp_map.put("sayo_im_sorry",Arrays.asList("sorry","gomen","apologize","somethingwrong","forgive","ごめんなさい","gomennasai"));
		stamp_map.put("hagumi_hooray",Arrays.asList("hooray","hiphip","whoo","yahoo","バンザイ","banzai"));
		stamp_map.put("tsugumi_amazing",Arrays.asList("amazing","wow","sugoi","wooo","cool!","tsugurific","すごいですっ","すごい"));
		stamp_map.put("eve_thefruits",Arrays.asList("fruits","labor","hardwork","effort","トックンの成果"));
		stamp_map.put("lisa_imnotready",Arrays.asList("notready","notprep","stopit","holdon","onemin","onemom","noread","wontberead","notrdy","nottime","notime","waitam","waitforme","心の準備が"));
		stamp_map.put("arisa_okayhereigo",Arrays.asList("hereigo","okayilltry","alrightilltry","domybest","trymybest","alrighty","itashot","myturn","domybest","yoshi","よし行くぞ"));
		stamp_map.put("tomoe_giveityourall",Arrays.asList("giveiteverything","allyougot","everythingyougot","tryyourbest","giveityourall","youreverything","yourall","全力で行くぜ"));
		stamp_map.put("kanon_keepgoing",Arrays.asList("keepgoing","dontstop","youcandoit","makeit","gaja","petan","pettan","pengu","susumou","nagaja","進もう","間に合って"));
		
		/*for (String s : image_map.keySet()) {
			ImageScheme scheme = image_map.get(s);
			try {
				BufferedImage img = crop(scheme.base,scheme.stamp_index%6*270+4, scheme.stamp_index/6*223+3, 258, 214);
				ImageIO.write(img, "png", new File(sigIRC.BASEDIR + "sigIRC/stamps/"+s+".png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
		for (int i=0;i<4;i++) {
			parts.add(new SongPart("Song Part "+i));
		}
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
		Point2D basepos = new Point2D(this.getPosition().getX(),this.getPosition().getHeight()+this.getPosition().getY()+24);
		double successChance = 1;
		DecimalFormat df = new DecimalFormat("0");
		DecimalFormat df2 = new DecimalFormat("0.0");
		DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, basepos.getX(), basepos.getY()-24, 2, Color.WHITE, Color.BLACK, songtitle);
		for (int i=0;i<parts.size();i++) {
			SongPart s = parts.get(i);
			if (s.getTotal()!=0) {
				double successRate = (double)s.getSuccesses()/s.getTotal();
				successChance *= successRate;
				DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, basepos.getX(), basepos.getY()+i*24, 2, Color.WHITE, Color.BLACK, s.getTitle()+" - "+s.getSuccesses()+"/"+s.getTotal()+" ("+df.format(successRate*100)+"%)");
			}
		}
		DrawUtils.drawOutlineText(g, sigIRC.panel.programFont, basepos.getX(), basepos.getY()+parts.size()*24+8, 2, Color.WHITE, Color.BLACK, "Expected Attempts: "+(df.format(1d/successChance))+"    Success Chance: "+df2.format((successChance)*100)+"%");
	}
	
	public void keypressed(KeyEvent ev) {
		super.keypressed(ev);
		switch (ev.getKeyCode()) {
			case KeyEvent.VK_CONTROL:{
				holdControl=true;
				System.out.println("Pressed Control");
			}break;
			case KeyEvent.VK_ALT:{
				holdAlt=true;
				System.out.println("Pressed Alt");
			}break;
			case KeyEvent.VK_HOME:{
				String input = pane.showInputDialog(sigIRC.window, "Song Title:", songtitle);
				this.songtitle = input;
			}break;
			case KeyEvent.VK_1:{
				if (holdControl) {
					SetupPart(0);
				} else {
					AddtoAllParts(0);
				}
			}break;
			case KeyEvent.VK_2:{
				if (holdControl) {
					SetupPart(1);
				} else {
					AddtoAllParts(1);
				}
			}break;
			case KeyEvent.VK_3:{
				if (holdControl) {
					SetupPart(2);
				} else {
					AddtoAllParts(2);
				}
			}break;
			case KeyEvent.VK_4:{
				if (holdControl) {
					SetupPart(3);
				} else {
					AddtoAllParts(3);
				}
			}break;
		}
	}

	private void AddtoAllParts(int partnumb) {
		for (int i=0;i<=partnumb;i++) {
			parts.get(i).AddtoTrials(holdAlt);
		}
	}

	private void SetupPart(int partnumb) {
		String input = pane.showInputDialog(sigIRC.window,"Name Part 1:",parts.get(partnumb).getTitle());
		String input2 = pane.showInputDialog(sigIRC.window,"Provide Success/Total Count (X/Y Format)",parts.get(partnumb).getSuccesses()+"/"+parts.get(partnumb).getTotal());
		if (input.length()>0) {
			parts.get(partnumb).setTitle(input);
			parts.get(partnumb).setSuccesses(Integer.parseInt(input2.split("/")[0]));
			parts.get(partnumb).setTrials(Integer.parseInt(input2.split("/")[1]));
		}
		holdControl=holdAlt=false;
	}
	
	public void keyreleased(KeyEvent ev) {
		super.keyreleased(ev);
		switch (ev.getKeyCode()) {
			case KeyEvent.VK_CONTROL:{
				holdControl=false;
				System.out.println("Released Control");
			}break;
			case KeyEvent.VK_ALT:{
				holdAlt=false;
				System.out.println("Released Alt");
			}break;
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
