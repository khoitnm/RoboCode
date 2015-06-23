package org.tnmk.robocode.common.math;

import math.geom2d.Point2D;

public class Point {
	public double x;
	public double y;

	public Point() {
	}

	public Point(Point2D point2D) {
		this.x = point2D.x();
		this.y = point2D.y();
	}

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void set(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public String toString() {
		return String.format("(%.0f,%.0f)", x, y);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

}
