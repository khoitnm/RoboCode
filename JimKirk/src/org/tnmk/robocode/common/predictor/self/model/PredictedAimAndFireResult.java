package org.tnmk.robocode.common.predictor.self.model;

import java.util.List;

import org.tnmk.robocode.common.model.FullRobotState;

public class PredictedAimAndFireResult {
	
	
	// BEGIN STATS
	// ---------------------------------------------------------------------------
	/**
	 * The time when prediction begin.
	 */
	private long time;

	private FullRobotState beginSource;
	private FullRobotState beginTarget;
	
	// AIMED STATS
	// ---------------------------------------------------------------------------
	private PredictedAimResult aimResult;


	// FIRED STATS
	// ---------------------------------------------------------------------------
	private FindingBestFirePointResult findNearestPointToTargetMovementResult;

	/**
	 * This prediction was cancel, we will predict again and fire base on the
	 * new prediction. The reason maybe it's too far away from target.
	 */
	private boolean waitForBetterAim = false;
	private List<PredictedFiredPoint> possibleBulletHitTargetPoints;

	private PredictedFiredPoint bestPredictPoint;

	/**
	 * Status of target when it's hit by bullet.
	 */
	private PredictStateResult predictedFiredTarget;

	// CONVENIENT SET-GET
	// ===========================================================================
	public long getFinishAimTime() {
		return time + aimResult.getAimSteps();
	}

	public long getFinishAllTime() {
		return time + getTotalSteps();
	}

	public Integer getTotalSteps() {
	    if (bestPredictPoint == null) return null;
	    return aimResult.getAimSteps() + bestPredictPoint.getFireSteps();
    }
	// SET-GET
	// ===========================================================================
	public PredictedAimResult getAimResult() {
		return aimResult;
	}

	public void setAimResult(PredictedAimResult aimAndFire) {
		this.aimResult = aimAndFire;
	}


	public PredictStateResult getPredictedFiredTarget() {
		return predictedFiredTarget;
	}

	public void setPredictedFiredTarget(PredictStateResult predictedFiredTarget) {
		this.predictedFiredTarget = predictedFiredTarget;
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


	public List<PredictedFiredPoint> getPossibleBulletHitTargetPoints() {
		return possibleBulletHitTargetPoints;
	}

	public void setPossibleBulletHitTargetPoints(List<PredictedFiredPoint> possibleBulletHitTargetPoints) {
		this.possibleBulletHitTargetPoints = possibleBulletHitTargetPoints;
	}

	public PredictedFiredPoint getBestPredictPoint() {
		return bestPredictPoint;
	}

	public void setBestPredictPoint(PredictedFiredPoint bestPredictPoint) {
		this.bestPredictPoint = bestPredictPoint;
	}

	public FindingBestFirePointResult getFindNearestPointToTargetMovementResult() {
		return findNearestPointToTargetMovementResult;
	}

	public void setFindNearestPointToTargetMovementResult(FindingBestFirePointResult findNearestPointToTargetMovementResult) {
		this.findNearestPointToTargetMovementResult = findNearestPointToTargetMovementResult;
	}

}