package org.tnmk.robocode.common.math;

public class LineSegment {
	private Point pointA;
	private Point pointB;
	
	
	public LineSegment(Point pointA, Point pointB){
		this.pointA = pointA;
		this.pointB = pointB;
	}
	/**
	 * @param pointA
	 * @param angleFromY angle from Y-axis, in degree
	 * @param length
	 */
	public LineSegment(Point pointA, double angleFromY, double length){
		this.pointA = pointA;
		this.pointB = new Point();
		this.pointB.x = pointA.x + (length * Math.sin(Math.toRadians(angleFromY)));
		this.pointB.y = pointA.y + (length * Math.cos(Math.toRadians(angleFromY)));
	}
	/**
	 * @return angle compare to Y-Axis, in degree, always positive
	 */
	public double reckonAngle() {
	    return MathUtils.reckonAngle(pointA, pointB);
    }
	public Point getPointA() {
		return pointA;
	}
	public void setPointA(Point pointA) {
		this.pointA = pointA;
	}
	public Point getPointB() {
		return pointB;
	}
	public void setPointB(Point pointB) {
		this.pointB = pointB;
	}
}
