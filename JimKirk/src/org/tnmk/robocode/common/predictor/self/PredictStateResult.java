package org.tnmk.robocode.common.predictor.self;

import org.tnmk.robocode.common.math.Point;

public class PredictStateResult {
	private Point point = new Point();
	private double velocity;
	private double heading;
	private double differentHeadingToPreviousStep;
	
	/**
	 * @return heading is only the body angle, but if velocity is negative, the angle is conversed. So we need to re-calculate move angle
	 */
	public double getMoveAngle(){
		if (velocity >= 0){
			return heading;
		}else{
			double rs= heading - 180;
			if (rs < 0){
				rs+=360;
			}
			return rs;
		}
		
	}
	/**
	 * speed is always a positive number, while velocity can be a positive or negative number.
	 * @return
	 */
	public double getSpeed(){
		return Math.abs(velocity);
	}
	
	//Need to predict by more information
	public boolean isStandStill() {
		return (velocity == 0);
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public Point getPoint() {
		return this.point;
	}

	public double getX() {
		return point.x;
	}

	public void setX(double x) {
		point.x = x;
	}

	public double getY() {
		return point.y;
	}

	public void setY(double y) {
		point.y = y;
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

	public double getDifferentHeadingToPreviousStep() {
		return differentHeadingToPreviousStep;
	}

	public void setDifferentHeadingToPreviousStep(double differentHeadingToPreviousStep) {
		this.differentHeadingToPreviousStep = differentHeadingToPreviousStep;
	}

}
