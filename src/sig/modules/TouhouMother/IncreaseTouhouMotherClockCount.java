package sig.modules.TouhouMother;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import sig.modules.TouhouMotherModule;

public class IncreaseTouhouMotherClockCount implements ActionListener{
	TouhouMotherModule tmm;
	public IncreaseTouhouMotherClockCount(TouhouMotherModule touhouMotherModule) {
		this.tmm=touhouMotherModule;
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		tmm.secondsCount++;
	}
}
