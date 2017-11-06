package sig.modules.RabiRibi;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import javax.swing.SwingUtilities;

import sig.modules.RabiRibiModule;
import sig.modules.RabiRibi.SmoothObjects.ErinaMarker;

public class Overlay {
	float xcoord=-1f,ycoord=-1f;
	boolean changedRooms=false;
	RabiRibiModule parent;
	public float xpos,ypos,xspd,yspd;
	public float camera_xpos,camera_ypos;
	public List<SmoothObject> objects = new ArrayList<SmoothObject>();
	
	ErinaMarker ERINA_MARKER;
	
	public Overlay(RabiRibiModule parent){
		this.parent = parent;
		/*this.xcoord = (int)(parent.readFloatFromErinaData(MemoryOffset.ERINA_XPOS)/1280);
		this.ycoord = (int)(parent.readFloatFromErinaData(MemoryOffset.ERINA_YPOS)/720);*/
		ERINA_MARKER = new ErinaMarker(0,0,0,0,parent);
		
		objects.add(ERINA_MARKER);
	}
	
	public void run() {
		camera_xpos = parent.readIntFromMemory(MemoryOffset.CAMERA_XPOS);
		camera_ypos = parent.readIntFromMemory(MemoryOffset.CAMERA_YPOS);
		
		float prev_camera_xpos = camera_xpos;
		float prev_camera_ypos = camera_ypos;
		xpos = parent.readFloatFromErinaData(MemoryOffset.ERINA_XPOS);
		ypos = parent.readFloatFromErinaData(MemoryOffset.ERINA_YPOS);
		
		xspd = camera_xpos-prev_camera_xpos;
		yspd = camera_ypos-prev_camera_ypos;
		try {
			for (SmoothObject so : objects) {
				so.run();
			}
		} catch (ConcurrentModificationException e) {
			
		}
		/*int new_xcoord,new_ycoord;
		
		float prev_xpos = xpos;
		float prev_ypos = ypos;
		float prev_camera_xpos = camera_xpos;
		float prev_camera_ypos = camera_ypos;
		
		xpos = parent.readFloatFromErinaData(MemoryOffset.ERINA_XPOS)/1280;
		ypos = parent.readFloatFromErinaData(MemoryOffset.ERINA_YPOS)/720;
		camera_xpos = parent.readIntFromMemory(MemoryOffset.CAMERA_XPOS);
		camera_ypos = parent.readIntFromMemory(MemoryOffset.CAMERA_YPOS);
		
		if (Math.abs(parent.readFloatFromErinaData(MemoryOffset.ERINA_XSPEED))>0.5f) {
			if ((Math.abs(prev_xpos-xpos)>0.005 && xpos%1>0.01 && xpos%1<0.99) || (Math.abs(prev_ypos-ypos)>0.005 && ypos%1>0.01 && ypos%1<0.99)) {
				if (Math.abs(prev_xpos-xpos)>0.005) {
					if (edgeOfScreenX) {
						if (prev_camera_xpos!=camera_xpos) {
							edgeOfScreenX = false;
							//System.out.println("Not on edge of X screen anymore.");
						}
					} else {
						if (prev_camera_xpos==camera_xpos) {
							edgeOfScreenX = true;
							//System.out.println("Now on edge of X screen.");
						}
					}
				}
				if (Math.abs(prev_ypos-ypos)>0.005) {
					if (edgeOfScreenY) {
						if (prev_camera_ypos!=camera_ypos) {
							edgeOfScreenY = false;
							//System.out.println("Not on edge of Y screen anymore.");
						}
					} else {
						if (prev_camera_ypos==camera_ypos) {
							edgeOfScreenY = true; 
							//System.out.println("Now on edge of Y screen.");
						}
					}
				}
			}
		}
		
		
		new_xcoord = (int)(xpos);
		new_ycoord = (int)(ypos);
		int xchange = (int)Math.signum(xcoord-new_xcoord); //-1 = Moving Right (Left edge), 1 = Moving Left(Right edge)
		int ychange = (int)Math.signum(ycoord-new_ycoord); //-1 = Moving Down(Top edge), 1 = Moving Up (Bottom edge)
		if (xchange!=0 || ychange!=0) {
			//Re-orient the camera, there has been a room change.
			float pct_xroom = xpos%1 + ((edgeOfScreenX)?0:0.5f);
			float pct_yroom = ypos%1 + ((edgeOfScreenY)?0:0.5f);
			camera_offset_x = -(pct_xroom*20);
			camera_offset_y = -(pct_yroom*11.25f);
			System.out.println(pct_xroom+"%,"+pct_yroom+"%. Change detected. Camera is offset by ("+camera_offset_x+","+camera_offset_y+")");
			this.xcoord = new_xcoord;
			this.ycoord = new_ycoord;
			camera_x = parent.readFloatFromErinaData(MemoryOffset.ERINA_XPOS) + camera_offset_x*64;
			camera_y = parent.readFloatFromErinaData(MemoryOffset.ERINA_YPOS) + camera_offset_y*64;
			System.out.println("Camera position is ("+camera_x+","+camera_y+")");
			starting_camera_x = camera_xpos;
			starting_camera_y = camera_ypos;
		}*/
		//System.out.println("Objects: "+objects.size());
	}
	
	public Point.Double getScreenPosition(float xpos, float ypos) {
		/*float diffx = xpos-camera_x;
		float diffy = ypos-camera_y;
		
		double screen_blocksize_x = parent.getPosition().getWidth()/20;
		double screen_blocksize_y = parent.getPosition().getHeight()/11.25;
		
		float camera_diffx = starting_camera_x-parent.readIntFromMemory(MemoryOffset.CAMERA_XPOS);
		float camera_diffy = starting_camera_y-parent.readIntFromMemory(MemoryOffset.CAMERA_YPOS);
		
		//System.out.println("Block size is ("+screen_blocksize_x+","+screen_blocksize_y+"). Diff is ("+diffx+","+diffy+").");
		/*System.out.println("Starting Camera: ("+starting_camera_x+","+starting_camera_y+")");
		System.out.println("Camera: ("+camera_diffx+","+camera_diffy+")");
		System.out.println("Block Coords: ("+(camera_diffx/64)+","+(camera_diffy/64)+")");*/

		double screen_blocksize_x = parent.getPosition().getWidth()/20;
		double screen_blocksize_y = parent.getPosition().getHeight()/11.25;
		
		return new Point.Double(((xpos-(-camera_xpos)+xspd*2)/64)*screen_blocksize_x,
				((ypos-(-camera_ypos)+yspd*2)/64)*screen_blocksize_y);
	}

	public void draw(Graphics g) {
		if (parent.readIntFromMemory(MemoryOffset.TRANSITION_COUNTER)<300) {
			try {
				for (SmoothObject so : objects) {
					so.draw(g);
				}
			} catch (ConcurrentModificationException e) {
				
			}
		}
	}
}
