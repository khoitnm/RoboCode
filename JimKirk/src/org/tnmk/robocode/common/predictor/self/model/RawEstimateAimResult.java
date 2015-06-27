package org.tnmk.robocode.common.predictor.self.model;

public class RawEstimateAimResult{
	/**
	 * gun turn right direction (could be negative or positive)
	 */
	private double gunTurnRightDirection;
	/**
	 * Number of steps necessary to turn gun
	 */
	private int aimSteps;
	
	/**
	 * Status of source after aimed, not when bullet hit target
	 */
	private PredictStateResult aimedSource;
	
	/**
	 * This is the state of target when the source finishing aiming. It's not target state when it get fired (hit by bullet)
	 */
	private PredictStateResult aimedTarget;
	
	
	public double getGunTurnRightDirection() {
		return gunTurnRightDirection;
	}
	public void setGunTurnRightDirection(double gunTurnRightDirection) {
		this.gunTurnRightDirection = gunTurnRightDirection;
	}
	public int getAimSteps() {
		return aimSteps;
	}
	public void setAimSteps(int aimSteps) {
		this.aimSteps = aimSteps;
	}
	public PredictStateResult getAimedSource() {
	    return aimedSource;
    }
	public void setAimedSource(PredictStateResult predictedAimedSource) {
	    this.aimedSource = predictedAimedSource;
    }
	public PredictStateResult getAimedTarget() {
	    return aimedTarget;
    }
	public void setAimedTarget(PredictStateResult predictedAimedTarget) {
	    this.aimedTarget = predictedAimedTarget;
    }
	
	
}