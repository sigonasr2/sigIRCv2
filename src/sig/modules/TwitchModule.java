package sig.modules;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;

import com.mb3364.twitch.api.Twitch;
import com.mb3364.twitch.api.handlers.ChannelFollowsResponseHandler;
import com.mb3364.twitch.api.handlers.StreamResponseHandler;
import com.mb3364.twitch.api.models.ChannelFollow;
import com.mb3364.twitch.api.models.Stream;

import sig.Module;
import sig.sigIRC;
import sig.utils.DrawUtils;
import sig.utils.FileUtils;
import sig.utils.TextUtils;

public class TwitchModule extends Module{
	public String console="Twitch module goes here.";
	Twitch manager = new Twitch();
	final static String USERDIR = sigIRC.BASEDIR+"sigIRC/users/"; 
	final static String FOLLOWERQUEUEFILE = USERDIR+"followers.txt";
	public static boolean streamOnline = false;

	public TwitchModule(Rectangle2D bounds, String moduleName) {
		this(bounds,moduleName,true);
	}

	public TwitchModule(Rectangle2D bounds, String moduleName, boolean enabled) {
		super(bounds, moduleName, enabled);
		Initialize();
	}
	
	private void Initialize() {
		boolean firstTime = false;
		firstTime = CreateUserFolder();
		if (firstTime) {
			CreateFollowerQueueLog();
		}
		manager.setClientId("o4c2x0l3e82scgar4hpxg6m5dfjbem");
		getFollowers(firstTime);
		/*manager.streams().get("theduckishot", new StreamResponseHandler() {

			@Override
			public void onFailure(Throwable arg0) {
			}

			@Override
			public void onFailure(int arg0, String arg1, String arg2) {
				System.out.println(arg0+","+arg1+","+arg2);
			}

			@Override
			public void onSuccess(Stream arg0) {
				//System.out.println("Stream data is available! "+arg0);
				if (arg0==null) {
					System.out.println("Stream is offline.");
				} else {
					System.out.println("Stream is online.");
				}
			}
			
		});*/
		/*manager.channels().getFollows(TextUtils.getActualChannelName(), new ChannelFollowsResponseHandler() {
				@Override
				public void onSuccess(int total, java.util.List<ChannelFollow> follows) {
					//System.out.println("Successfully found followers for channel "+sigIRC.channel+". Total: "+total);
					//console = "Last Follower: "+follows.get(0).getUser().getDisplayName();
					follows.get(0).
				}

				@Override
				public void onFailure(Throwable arg0) {
				}

				@Override
				public void onFailure(int arg0, String arg1, String arg2) {
					System.out.println(arg0+","+arg1+","+arg2);
				}
			}
		);*/
	}

	private void CreateFollowerQueueLog() {
		String dir = FOLLOWERQUEUEFILE;
		File filer = new File(dir);
		if (!filer.exists()) {
			try {
				filer.createNewFile();
				System.out.println("Follower Queue Log does not exist. Creating in "+USERDIR+".");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void getFollowers(boolean firstTime) {
		//isStreamOnline();
		manager.channels().getFollows(TextUtils.getActualChannelName(), new ChannelFollowsResponseHandler() {
					@Override
					public void onSuccess(int total, java.util.List<ChannelFollow> follows) {
						//System.out.println("Successfully found followers for channel "+sigIRC.channel+". Total: "+total);
						//console = "Last Follower: "+follows.get(0).getUser().getDisplayName();
						for (ChannelFollow f : follows) {
							addFollower(f,isStreamOnline(),firstTime);
						}
					}
		
					@Override
					public void onFailure(Throwable arg0) {
					}
		
					@Override
					public void onFailure(int arg0, String arg1, String arg2) {
						System.out.println(arg0+","+arg1+","+arg2);
					}
				}
			);
		if (isStreamOnline()) {ClearFollowerAnnouncerQueue();}
	}

	private void ClearFollowerAnnouncerQueue() {
		// TODO Read the file and announce everybody in that queue.
		
	}

	private boolean isStreamOnline() {
		manager.streams().get(TextUtils.getActualChannelName(), new StreamResponseHandler() {

			@Override
			public void onFailure(Throwable arg0) {
				TwitchModule.streamOnline=false;
			}

			@Override
			public void onFailure(int arg0, String arg1, String arg2) {
				System.out.println(arg0+","+arg1+","+arg2);
				TwitchModule.streamOnline=false;
			}

			@Override
			public void onSuccess(Stream arg0) {
				//System.out.println("Stream data is available! "+arg0);
				if (arg0==null) {
					//System.out.println("Stream is offline.");
					TwitchModule.streamOnline=false;
				} else {
					TwitchModule.streamOnline=true;
				}
			}
			
		});
		//return TwitchModule.streamOnline;
		return false;
	}

	protected void addFollower(ChannelFollow f, boolean streamOnline, boolean silent) {
		String filename = USERDIR+f.getUser().getId();
		File userProfile = new File(filename);
		if (!silent) { //If we got in here, this isn't the initial follower setup, so we are good to go with announcing these followers.
			if (!streamOnline) {
				//Save their ID to a queue.
				FileUtils.logToFile(Long.toString(f.getUser().getId()), FOLLOWERQUEUEFILE);
			} else {
				//Announce it now.
				AnnounceFollower(f);
			}
		}
		CreateUserProfile(f, filename, userProfile);
	}

	private void CreateUserProfile(ChannelFollow f, String filename, File userProfile) {
		if (!userProfile.exists()) {
			try {
				userProfile.createNewFile();
				FileUtils.logToFile(DateFormat.getDateInstance().format(f.getCreatedAt()), filename);
				FileUtils.logToFile(f.getUser().getBio(), filename);
				FileUtils.logToFile(f.getUser().getDisplayName(), filename);
				FileUtils.logToFile(f.getUser().getLogo(), filename);
				FileUtils.logToFile(f.getUser().getName(), filename);
				FileUtils.logToFile(f.getUser().getType(), filename);
				FileUtils.logToFile(DateFormat.getDateInstance().format(f.getUser().getUpdatedAt()), filename);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void AnnounceFollower(ChannelFollow f) {
		System.out.println("Thanks for following "+f.getUser().getDisplayName()+"!");
	}

	private boolean CreateUserFolder() {
		File userDir = new File(USERDIR);
		if (!userDir.exists()) {
			userDir.mkdir();
			System.out.println("Could not find Twitch User directory. Creating in "+USERDIR+".");
			return true;
		} else {
			return false;
		}
	}

	public void draw(Graphics g){
		super.draw(g);
		DrawUtils.drawText(g, bounds.getX(), bounds.getY()+24, Color.RED, console);
	}
}
