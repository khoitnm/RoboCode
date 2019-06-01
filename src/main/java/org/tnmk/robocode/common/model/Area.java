package org.tnmk.robocode.common.model;

import java.io.Serializable;

import org.tnmk.robocode.common.math.LineSegment;
import org.tnmk.robocode.common.math.Point;

/**
 * @author Khoi This class is immutable, so it has only getter methods.
 */
public class Area implements Serializable {
    private static final long serialVersionUID = -8637600309652832198L;
	private Point bottomLeft;
	private Point bottomRight;
	private Point topRight;
	private Point topLeft;

	private double width;
	private double height;

	private double left;
	private double right;
	private double top;
	private double bottom;

	private LineSegment wallLeft;
	private LineSegment wallRight;
	private LineSegment wallBottom;
	private LineSegment wallTop;

	public Area(double bottom, double left, double right, double top) {
		constructArea(bottom, left, right, top);
	}

	public Area(Point root, double width, double height) {
		double bottom = root.getY();
		double left = root.getX();
		double right = left + width;
		double top = bottom + height;

		constructArea(bottom, left, right, top);
	}

	private void constructArea(double bottom, double left, double right, double top) {
		this.left = left;
		this.right = right;
		this.bottom = bottom;
		this.top = top;

		this.width = right - left;
		this.height = top - bottom;

		this.bottomLeft = new Point(left, bottom);
		this.bottomRight = new Point(right, bottom);
		this.topLeft = new Point(left, top);
		this.topRight = new Point(right, top);

		this.wallLeft = new LineSegment(bottomLeft, topLeft);
		this.wallRight = new LineSegment(bottomRight, topRight);
		this.wallBottom = new LineSegment(bottomLeft, bottomRight);
		this.wallTop = new LineSegment(topLeft, topRight);
	}

	public Point getBottomLeft() {
		return bottomLeft;
	}

	public Point getBottomRight() {
		return bottomRight;
	}

	public Point getTopRight() {
		return topRight;
	}

	public Point getTopLeft() {
		return topLeft;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public LineSegment getWallLeft() {
		return wallLeft;
	}

	public LineSegment getWallRight() {
		return wallRight;
	}

	public LineSegment getWallBottom() {
		return wallBottom;
	}

	public LineSegment getWallTop() {
		return wallTop;
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

	public double getBottom() {
		return bottom;
	}

}
