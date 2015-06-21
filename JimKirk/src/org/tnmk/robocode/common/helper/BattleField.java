package org.tnmk.robocode.common.helper;

import robocode.Robot;

public class BattleField {
	private double height;
	private double width;
	private int sentryBorderSize = 0;

	private double bottom = 0;
	private double left = 0;
	private double right;
	private double top;

	private double safeLeft;
	private double safeRight;
	private double safeTop;
	private double safeBottom;

	public void setHeight(double height) {
		this.height = height;
		reckonBattleField();
	}

	public void setWidth(double width) {
		this.width = width;
		reckonBattleField();
	}

	public void setSentryBorderSize(int sentryBorderSize) {
		this.sentryBorderSize = sentryBorderSize;
		reckonBattleField();
	}

	private void reckonBattleField() {
		int borderSize = this.sentryBorderSize;
		this.top = this.height;
		this.right = this.width;

		this.safeLeft = borderSize;
		this.safeRight = this.width - borderSize;
		this.safeBottom = borderSize;
		this.safeTop = this.height - borderSize;
	}

	public double getHeight() {
		return height;
	}

	public double getWidth() {
		return width;
	}

	public int getSentryBorderSize() {
		return sentryBorderSize;
	}

	public double getBottom() {
		return bottom;
	}

	public double getLeft() {
		return left;
	}

	public double getRight() {
		return right;
	}

	public double getTop() {
		return top;
	}

	public double getSafeLeft() {
		return safeLeft;
	}

	public double getSafeRight() {
		return safeRight;
	}

	public double getSafeTop() {
		return safeTop;
	}

	public double getSafeBottom() {
		return safeBottom;
	}
}
