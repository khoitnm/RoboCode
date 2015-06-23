package org.tnmk.robocode.common.model;

public class AimAndFireResult{
	/**
	 * gun turn right angle
	 */
	private double turnRightAngle;
	/**
	 * distance from aimed position to fired position.
	 */
	private double distance;
	private int aimSteps;
	private int fireSteps;
	
	
	public int getAimSteps() {
		return aimSteps;
	}

	public void setAimSteps(int aimSteps) {
		this.aimSteps = aimSteps;
	}

	public int getFireSteps() {
		return fireSteps;
	}

	public void setFireSteps(int fireSteps) {
		this.fireSteps = fireSteps;
	}

	public int getTotalSteps() {
		return aimSteps + fireSteps;
	}
	
	public double getTurnRightAngle() {
		return turnRightAngle;
	}
	public void setTurnRightAngle(double turnRightAngle) {
		this.turnRightAngle = turnRightAngle;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
}
