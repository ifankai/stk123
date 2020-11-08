package com.stk123.tool.util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DrawLineUtils {
	
	//点的位置，在线中，线上，还是线下
	public enum POINT_POSITION {
		EQUALS_WITH_LINE,
		HIGHER_THAN_LINE,
		LOWER_THAN_LINE
	}

	public static void main(String[] args) {
		List<Point> ps = DrawLineUtils.getPointsByX(0, 1376, 8, 1277, 4);
		System.out.println(ps);
		System.out.println(DrawLineUtils.compare(ps, 1330));
	}
	
	public static POINT_POSITION getPosition(int highX1,int highY1,int highX2, int highY2, int todayX,int todayY){
		List<Point> ps = DrawLineUtils.getPointsByX(highX1, highY1, todayX, todayY, highX2);
		if(ps.size() > 0){
			return DrawLineUtils.compare(ps, highY2);
		}
		return null;
	}
	
	public static POINT_POSITION compare(List<Point> ps, int highY2){
		Point maxP = Collections.max(ps, new Comparator<Point>(){
			public int compare(Point arg0, Point arg1) {
				return (int)(arg0.getY() - arg1.getY());
			}
		});
		if(maxP.getY() < highY2){
			return POINT_POSITION.LOWER_THAN_LINE;
		}
		Point minP = Collections.max(ps, new Comparator<Point>(){
			public int compare(Point arg0, Point arg1) {
				return (int)(arg1.getY() - arg0.getY());
			}
		});
		if(minP.getY() > highY2){
			return POINT_POSITION.HIGHER_THAN_LINE;
		}
		return POINT_POSITION.EQUALS_WITH_LINE;
	}
	/**
	 * 假设有a，b两点，看c点是不是在a，b两点连线上，由于不知道怎么计算a，b两点的延长线，
	 * 所以，就看a，c两点的连线是不是经过b点
	 * @param highX1 a点x轴
	 * @param highY1 a点y轴
	 * @param todayX c点x轴
	 * @param todayY c点y轴
	 * @param highX2 b点x轴
	 * @return b点y轴的集合
	 */
	public static List<Point> getPointsByX(int highX1,int highY1,int todayX,int todayY, int highX2){
		List<Point> points = DrawLineUtils.getPoints(highX1, highY1, todayX, todayY);
		List<Point> ps = new ArrayList<Point>();
		for(Point point : points){
			if(point.getX() == highX2){
				ps.add(point);
			}
		}
		return ps;
	}
	
	public static List<Point> getPoints(int x1,int y1,int x2,int y2){
		BufferedImage img = new BufferedImage(Math.max(x1,x2), Math.max(y1, y2), BufferedImage.TYPE_INT_ARGB);
	    Graphics g = img.getGraphics();
	    Color color = Color.BLACK;
	    g.setColor(color);
	    g.drawLine(x1, y1, x2, y2);
	    int[] rgbArray = new int[img.getWidth()];
	    List<Point> list = new ArrayList<Point>();
	    for (int y = 0; y < img.getHeight(); y++) {
	      img.getRGB(0, y, img.getWidth(), 1, rgbArray, 0, img.getWidth());
	      for (int x = 0; x < rgbArray.length; x++) {
	        if (rgbArray[x] == color.getRGB()) {
	          list.add(new Point(x, y));
	        }
	      }
	    }
	    return list;
	}

}
