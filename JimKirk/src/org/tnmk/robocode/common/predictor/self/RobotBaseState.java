package org.tnmk.robocode.common.predictor.self;

import org.tnmk.robocode.common.math.Point;

import robocode.Rules;

public class RobotBaseState {
	protected Point position;
	protected double velocity;
	protected double heading;

	/**
	 * @return heading is only the body angle, but if velocity is negative, the
	 *         angle is conversed. So we need to re-calculate move angle
	 */
	public double getMoveAngle() {
		if (velocity >= 0) {
			return heading;
		} else {
			double rs = heading - 180;
			if (rs < 0) {
				rs += 360;
			}
			return rs;
		}

	}

	// CONVENTION SET-GET
	// ====================================================================
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
	public double getSpeed() {
		return Math.abs(velocity);
	}

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
}
