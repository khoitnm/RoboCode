package org.tnmk.robocode.common.predictor.self.model;

import org.tnmk.robocode.common.model.FullRobotState;

public class PredictedAimAndFireResult {
	/**
	 * The time when prediction begin.
	 */
	private long time;

	private FullRobotState beginSource;
	private FullRobotState beginTarget;

	private RawEstimateAimResult firstAimEstimation = new RawEstimateAimResult();
	private final PredictedAimResult aimResult = new PredictedAimResult();

	private final PredictedFireResult fireResult = new PredictedFireResult();
	/**
	 * This prediction was cancel, we will predict again and fire base on the new prediction. The reason maybe it's too far away from target.
	 */
	private boolean waitForBetterAim = false;

	// CONVENIENT SET-GET
	// ===========================================================================
	public boolean isFoundBestPoint() {
		return getBestFirePoint() != null;
	}

	public PredictedFirePoint getBestFirePoint() {
		if (getFireResult() == null || getFireResult().getFindingBestPointResult() == null)
			return null;
		return getFireResult().getFindingBestPointResult().getBestPoint();
	}

	public long getAimedTime() {
		return time + aimResult.getAimSteps();
	}

	/**
	 * this is the aimed and fired time
	 * 
	 * @return
	 */
	public Long getTotalTime() {
		Integer totalSteps = getTotalSteps();
		if (totalSteps == null)
			return null;
		return time + totalSteps;
	}

	public Integer getTotalSteps() {
		PredictedFirePoint bestFirePoint = getBestFirePoint();
		if (bestFirePoint == null)
			return null;
		return aimResult.getAimSteps() + bestFirePoint.getFireSteps();
	}

	// SET-GET
	// ===========================================================================
	public PredictedAimResult getAimResult() {
		return aimResult;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public boolean isWaitForBetterAim() {
		return waitForBetterAim;
	}

	public void setWaitForBetterAim(boolean waitForBetterAim) {
		this.waitForBetterAim = waitForBetterAim;
	}

	public FullRobotState getBeginTarget() {
		return beginTarget;
	}

	public void setBeginTarget(FullRobotState currentTarget) {
		this.beginTarget = currentTarget;
	}

	public FullRobotState getBeginSource() {
		return beginSource;
	}

	public void setBeginSource(FullRobotState currentSource) {
		this.beginSource = currentSource;
	}

	public PredictedFireResult getFireResult() {
		return fireResult;
	}

	public RawEstimateAimResult getFirstAimEstimation() {
		return firstAimEstimation;
	}

	public void setFirstAimEstimation(RawEstimateAimResult firstAimEstimation) {
		this.firstAimEstimation = firstAimEstimation;
	}
}