package sig.modules.RabiRace;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import sig.Module;
import sig.sigIRC;
import sig.modules.RabiRaceModule;

public class JoinButton extends ClickableButton{

	public JoinButton(Rectangle position, String button_label, RabiRaceModule module) {
		super(position,button_label,module);
	}

	public void onClickEvent(MouseEvent ev) {
		if (RabiRaceModule.mySession==null) {
			RabiRaceModule.module.window.setVisible(true);
		} else {
			//RabiRaceModule.module.LeaveSession();
			File file = new File(sigIRC.BASEDIR+"sigIRC/tmp.data");
			try {
				org.apache.commons.io.FileUtils.copyURLToFile(new URL("http://45.33.13.215:8080/rabirace/send.php?key=leavesession&timekey="+RabiRaceModule.CLIENT_SERVER_READTIME+"&name="+RabiRaceModule.module.myProfile.username+"&session="+RabiRaceModule.mySession.id),file);
				RabiRaceModule.mySession=null;
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
