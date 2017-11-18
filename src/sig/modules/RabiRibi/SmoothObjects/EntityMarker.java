package sig.modules.RabiRibi.SmoothObjects;

import java.awt.Color;
import java.awt.Graphics;

import sig.modules.RabiRibiModule;
import sig.modules.RabiRibi.Entity;
import sig.modules.RabiRibi.MemoryOffset;
import sig.modules.RabiRibi.SmoothObject;

public class EntityMarker extends SmoothObject{
	Entity ent;

	public EntityMarker(int x, int y, int targetx, int targety, Entity ent, RabiRibiModule parent) {
		super(x, y, targetx, targety, parent);
		this.ent=ent;
	}
	
	public void draw(Graphics g) {
		super.draw(g);
		int alphaval = (ent.getLastHitTime()>parent.readIntFromMemory(MemoryOffset.PLAYTIME)-180)?255:110;
		float pct = ent.getHealth()/(float)ent.getMaxHealth();
		if (pct>=0.66) {
			g.setColor(new Color(64,255,64,alphaval));
		} else
		if (pct>=0.33) {
			g.setColor(new Color(255,255,64,alphaval));
		} else {
			g.setColor(new Color(255,64,64,alphaval));
		}
		g.fillRect(x, y-56, (int)(48*pct), 16);
		g.setColor(new Color(0,0,0,alphaval));
		g.drawRect(x, y-56, 48, 16);
		g.setColor(Color.BLACK);
	}
	
}
