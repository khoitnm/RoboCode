package org.tnmk.robocode.common.gun.predictor.self.model;

import java.io.Serializable;

/**
 * This class doesn't contain information about target when it get fired (hit by bullet) because those information are stored in {@link PredictedFireResult}
 * 
 * @author Khoi
 */
public class PredictedAimResult implements Serializable {
    private static final long serialVersionUID = -9152614446469196236L;

	private int aimSteps;

	/**
	 * Status of source after aimed, not when bullet hit target
	 */
	private PredictStateResult source;

	/**
	 * Gun turn right direction (could be negative or positive). Note: if cannot find position to fire at target ({@link RawEstimateAimResult#getAimedTarget()}), this value is null
	 */
	private Double gunTurnRightDirection;
	/**
	 * The target point when it get fired (get hit by bullet). It has different meaning from {@link RawEstimateAimResult#getAimedTarget()}. So it will be null if {@link RawEstimateAimResult#getAimedTarget()} is null
	 */
	private PredictedFirePoint firedTarget;

	public int getAimSteps() {
		return aimSteps;
	}

	public void setAimSteps(int aimSteps) {
		this.aimSteps = aimSteps;
	}

	public PredictStateResult getSource() {
		return source;
	}

	public void setSource(PredictStateResult source) {
		this.source = source;
	}

	public double getGunTurnRightDirection() {
		return gunTurnRightDirection;
	}

	public void setGunTurnRightDirection(double gunTurnRightDirection) {
		this.gunTurnRightDirection = gunTurnRightDirection;
	}

	public PredictedFirePoint getFiredTarget() {
		return firedTarget;
	}

	public void setFiredTarget(PredictedFirePoint firedTarget) {
		this.firedTarget = firedTarget;
	}

}
