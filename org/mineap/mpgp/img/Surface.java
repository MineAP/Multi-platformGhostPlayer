package org.mineap.mpgp.img;

import java.awt.image.BufferedImage;
import java.util.Vector;

public class Surface {

	public BufferedImage baseImg = null;
	public Vector<BufferedImage> anime = new Vector<BufferedImage>();
	public Vector<Integer> animationNo = new Vector<Integer>();
	public Vector<Integer> overlay_x = new Vector<Integer>();
	public Vector<Integer> overlay_y = new Vector<Integer>();
	public Vector<Integer> time = new Vector<Integer>();
	public String interval = null;
	public boolean isElement = false;
	public int b_offsetx = 0;
	public int b_offsety = 0;
	public String alignment = "none";
	public Vector<Integer[]> collision = new Vector<Integer[]>();
	public Vector<String> collisionId = new Vector<String>();
	
	
}
