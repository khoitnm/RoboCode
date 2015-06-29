package org.tnmk.robocode.common.model;

import java.io.Serializable;

import org.tnmk.robocode.common.math.Point;

public class BaseRobotState implements Serializable {
    private static final long serialVersionUID = -9050612832099625440L;
	protected Point position = new Point();
	protected double velocity;
	protected double heading;
	/**
	 * This name is unique in a battle.
	 */
	private String name;

	// CONVENTION SET-GET
	// ====================================================================
	/**
	 * @return heading is only the body angle, but if velocity is negative, the angle is conversed. So we need to re-calculate move angle
	 */
	public double getMoveAngle() {
		if (velocity >= 0) {
			return heading;
		} else {
			double rs = heading - 180;
			return (rs + 360) % 360;// to ensure that 0 <= angle <= 360
		}
	}

	public double getSpeed() {
		return Math.abs(velocity);
	}

	// Need to predict by more information
	public boolean isStandStill() {
		return (velocity == 0);
	}

	public double getX() {
		return position.x;
	}

	public double getY() {
		return position.y;
	}

	public void setX(double x) {
		this.position.x = x;
	}

	public void setY(double y) {
		this.position.y = y;
	}

	// SETTER - GETTER
	// ====================================================================
	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public double getVelocity() {
		return velocity;
	}

	public void setVelocity(double velocity) {
		this.velocity = velocity;
	}

	public double getHeading() {
		return heading;
	}

	public void setHeading(double heading) {
		this.heading = heading;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
