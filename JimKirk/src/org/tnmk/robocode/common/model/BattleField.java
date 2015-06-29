package org.tnmk.robocode.common.model;

import org.tnmk.robocode.common.math.Point;

public class BattleField extends Area {
    private static final long serialVersionUID = 5003534200184843218L;

	/**
	 * There is an area around the battlefield which you can get danger from
	 * sentry (guard) robots.
	 */
	private int sentryBorderSize = 0;
	
	private Area safeArea;// Don't have setter because it is calculated from
						  // sendtryBorderSize
	
	public BattleField(double width, double height) {
		super(0, 0, width, height);
		reckonSafeAreaBySentryBorderSize();
	}
	private void reckonSafeAreaBySentryBorderSize(){
		this.safeArea = new Area(sentryBorderSize, sentryBorderSize, getRight() - sentryBorderSize, getTop() - sentryBorderSize);
	}
	public void setSentryBorderSize(int sentryBorderSize) {
		this.sentryBorderSize = sentryBorderSize;
		reckonSafeAreaBySentryBorderSize();
	}
	
	public Area getSafeArea() {
		return safeArea;
	}

	public int getSentryBorderSize() {
		return sentryBorderSize;
	}
	public Point getCenter() {
	   return new Point(getWidth()/2, getHeight()/2);
    }
}
