package org.tnmk.robocode.common.gun.predictor.self.model;

import org.tnmk.common.math.Point;

/**
 * @author Khoi The point where bullet hit target
 */
public class PredictedFirePoint extends Point {
	private static final long serialVersionUID = -7797298106617128389L;
	/**
	 * This is the distance between this point to the target move line. It's not the distance between source position and target position. Sometimes we don't need to calculate this value to predictFiredPoint.
	 */
	private Double distanceToTargetMove = null;
	/**
	 * This is only the bullet fly steps, not aim & fire (bullet fly) steps
	 */
	private int fireSteps;
	private int firePower;

	public int getFireSteps() {
		return fireSteps;
	}

	public void setFireSteps(int fireSteps) {
		this.fireSteps = fireSteps;
	}

	public int getFirePower() {
		return firePower;
	}

	public void setFirePower(double firePower) {
		this.firePower = (int) Math.round(firePower);
	}

	public Double getDistanceToTargetMove() {
		return distanceToTargetMove;
	}

	public void setDistanceToTargetMove(Double distanceToTargetMove) {
		this.distanceToTargetMove = distanceToTargetMove;
	}
}