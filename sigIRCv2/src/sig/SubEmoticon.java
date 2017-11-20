package sig;

import java.net.URL;

import com.mb3364.twitch.api.handlers.ChannelResponseHandler;
import com.mb3364.twitch.api.handlers.ChannelSubscriptionResponseHandler;
import com.mb3364.twitch.api.handlers.UserSubscriptionResponseHandler;
import com.mb3364.twitch.api.models.Channel;
import com.mb3364.twitch.api.models.ChannelSubscription;
import com.mb3364.twitch.api.models.UserSubscription;

public class SubEmoticon extends Emoticon{
	String channelName = "";
	static boolean userCanUseEmoticon=false;
	
	public SubEmoticon(String emoteName, String fileName, String channelName) {
		super(emoteName, fileName);
		this.channelName=channelName;
	}
	public SubEmoticon(String emoteName, URL onlinePath, String channelName) {
		super(emoteName,onlinePath);
		this.channelName=channelName;
	}
	
	public boolean canUserUseEmoticon(String username) {
		userCanUseEmoticon=false;
		/*System.out.println("User: "+username+", Channel: "+channelName+"|");
		sigIRC.manager.channels().getSubscription(username.trim(), channelName.trim(), new ChannelSubscriptionResponseHandler() {

			@Override
			public void onFailure(Throwable arg0) {
				System.out.println(arg0.getMessage());
			}

			@Override
			public void onFailure(int arg0, String arg1, String arg2) {
				System.out.println(arg0+","+arg1+","+arg2);
			}

			@Override
			public void onSuccess(ChannelSubscription arg0) {
				System.out.println("User is subscribed to channel "+channelName+"!");
				userCanUseEmoticon=true;
			}
			
		});*/
		/*sigIRC.manager.users().getSubscription( new UserSubscriptionResponseHandler() {

			@Override
			public void onFailure(Throwable arg0) {
				System.out.println(arg0.getMessage());
			}

			@Override
			public void onFailure(int arg0, String arg1, String arg2) {
				System.out.println(arg0+","+arg1+","+arg2);
			}

			@Override
			public void onSuccess(UserSubscription arg0) {
				System.out.println("User is subscribed to channel "+channelName+"!");
				userCanUseEmoticon=true;
			}
		});*/
		return true;
	}
}
