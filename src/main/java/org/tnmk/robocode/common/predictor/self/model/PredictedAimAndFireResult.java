package org.tnmk.robocode.common.predictor.self.model;

import java.io.Serializable;

import org.tnmk.robocode.common.model.BaseRobotState;
import org.tnmk.robocode.common.model.FullRobotState;
import org.tnmk.robocode.common.predictor.self.PredictStrategy;

public class PredictedAimAndFireResult implements Serializable {
	private static final long serialVersionUID = -5769191279934004224L;
	private PredictStrategy predictStrategy;
	/**
	 * The time when prediction begin.
	 */
	private long beginTime;

	private FullRobotState beginSource;
	private BaseRobotState beginTarget;

	private RawEstimateAimResult firstAimEstimation = new RawEstimateAimResult();
	private final PredictedAimResult aimResult = new PredictedAimResult();

	private final PredictedFireResult fireResult = new PredictedFireResult();
	/**
	 * This prediction was cancel, we will predict again and fire base on the new prediction. The reason maybe it's too far away from target.
	 */
	private boolean waitForBetterAim = false;

	// CONVENIENT SET-GET
	// ===========================================================================
	/**
	 * Find best target point to shot or not
	 * 
	 * @return
	 */
	public boolean isFoundBestPoint() {
		return getBestFirePoint() != null;
	}

	public PredictedFirePoint getBestFirePoint() {
		if (getFireResult() == null || getFireResult().getFindingBestPointResult() == null) {
			return null;
		}
		return getFireResult().getFindingBestPointResult().getBestPoint();
	}

	public long getAimedTime() {
		return beginTime + aimResult.getAimSteps();
	}

	/**
	 * this is the aimed and fired time (the time point when bullet hit target)
	 * 
	 * @return
	 */
	public Long getFiredTime() {
		Integer totalSteps = getTotalSteps();
		if (totalSteps == null) {
			return null;
		}
		return beginTime + totalSteps;
	}

	public Integer getTotalSteps() {
		PredictedFirePoint bestFirePoint = getBestFirePoint();
		if (bestFirePoint == null) {
			return null;
		}
		return aimResult.getAimSteps() + bestFirePoint.getFireSteps();
	}

	// SET-GET
	// ===========================================================================
	public PredictedAimResult getAimResult() {
		return aimResult;
	}

	public long getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(long time) {
		this.beginTime = time;
	}

	public boolean isWaitForBetterAim() {
		return waitForBetterAim;
	}

	public void setWaitForBetterAim(boolean waitForBetterAim) {
		this.waitForBetterAim = waitForBetterAim;
	}

	public BaseRobotState getBeginTarget() {
		return beginTarget;
	}

	public void setBeginTarget(BaseRobotState currentTarget) {
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

	public PredictStrategy getPredictStrategy() {
		return predictStrategy;
	}

	public void setPredictStrategy(PredictStrategy predictStrategy) {
		this.predictStrategy = predictStrategy;
	}

}