package org.tnmk.robocode.common.helper;

public class BattleField extends Area {
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
}
