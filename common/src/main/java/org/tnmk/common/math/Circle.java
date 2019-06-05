package org.tnmk.common.math;

import java.io.Serializable;

/**
 * @author Khoi http://2000clicks.com/mathhelp/GeometryConicSectionCircleIntersection.aspx (x−centerX)^2+(y−centerY)^2 =radius^2,
 */
public class Circle implements Serializable {
    private static final long serialVersionUID = 4130389969030089660L;
	private Point center;
	private double radius;

	public Circle(Point center, double radius) {
		this.center = center;
		this.radius = radius;
	}

	public Circle(double centerX, double centerY, double radius) {
		this(new Point(centerX, centerY), radius);
	}

	public Point getCenter() {
		return center;
	}

	public void setCenter(Point center) {
		this.center = center;
	}

	public double getCenterX() {
		return this.center.x;
	}

	public void setCenterX(double centerX) {
		this.center.x = centerX;
	}

	public double getCenterY() {
		return this.center.y;
	}

	public void setCenterY(double centerY) {
		this.center.y = centerY;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}

}
