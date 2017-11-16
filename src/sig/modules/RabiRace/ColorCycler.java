package sig.modules.RabiRace;

import java.awt.Color;

public class ColorCycler {
	int r = 0;
	int g = 0;
	int b = 0;
	int a = 255;
	int cyclespd = 0;
	int stage = 1; //1 = Green+, 2 = Red-, 3 = Blue+, 4 = Green-, 5 = Red+, 6 = Blue-
	
	public ColorCycler(Color startingColor, int cyclespd) {
		this.r = startingColor.getRed();
		this.g = startingColor.getGreen();
		this.b = startingColor.getBlue();
		this.a = startingColor.getAlpha();
		this.cyclespd=cyclespd;
	}
	
	public void run() {
		switch (stage) {
			case 1:{
				if (g<255) {
					g=Math.min(255, g+cyclespd);
				} else {
					stage++;
				}
			}break;
			case 2:{
				if (r>0) {
					r=Math.max(0, r-cyclespd);
				} else {
					stage++;
				}
			}break;
			case 3:{
				if (b<255) {
					b=Math.min(255, b+cyclespd);
				} else {
					stage++;
				}
			}break;
			case 4:{
				if (g>0) {
					g=Math.max(0, g-cyclespd);
				} else {
					stage++;
				}
			}break;
			case 5:{
				if (r<255) {
					r=Math.min(255, r+cyclespd);
				} else {
					stage++;
				}
			}break;
			case 6:{
				if (b>0) {
					b=Math.max(0, b-cyclespd);
				} else {
					stage=1;
				}
			}break;
		}
	}
	
	public Color getCycleColor() {
		return new Color(r,g,b,a);
	}
}
