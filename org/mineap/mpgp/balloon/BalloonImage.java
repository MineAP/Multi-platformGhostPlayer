package org.mineap.mpgp.balloon;

import java.awt.image.BufferedImage;
import java.util.Vector;


public class BalloonImage{
	
	public Vector<BufferedImage> balloncGraphics = new Vector<BufferedImage>();
	public Vector<BufferedImage> ballonkGraphics = new Vector<BufferedImage>();
	public Vector<BufferedImage> ballonsGraphics = new Vector<BufferedImage>();
	public Vector<BufferedImage> arrows = new Vector<BufferedImage>();
	public String b_Type;
	public String b_Name;
	public String b_ID;
	public int originX;
	public int originY;
	public int validrectLeft;
	public int validrectTop;
	public int validrectRight;
	public int validrectBottom;
	public int wordwrappointX;
	public int wordwrappointY;
	public int fontHeight;
	public String fontName;
	public int fontCollorR;
	public int fontCollorG;
	public int fontCollorB;
	public int arrow0IndexX;
	public int arrow0IndexY;
	public int arrow1IndexX;
	public int arrow1IndexY;
	
}
