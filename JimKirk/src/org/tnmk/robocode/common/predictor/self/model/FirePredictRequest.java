package org.tnmk.robocode.common.predictor.self.model;

import org.tnmk.robocode.common.model.FullRobotState;
/**
 * This class contain all information of predicted aimed target.
 * We will use those information to continue predicting fired target.
 * @author Khoi
 *
 */
public class FirePredictRequest {
	private int maxPower;
	/**
	 * The time when prediction begin.
	 */
	private long time;
	private double beginSourceGunHeading;
	private FullRobotState beginSource;
	private FullRobotState beginTarget;

	private RawEstimateAimResult aimEstimateResult;

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public FullRobotState getBeginSource() {
		return beginSource;
	}

	public void setBeginSource(FullRobotState beginSource) {
		this.beginSource = beginSource;
	}

	public FullRobotState getBeginTarget() {
		return beginTarget;
	}

	public void setBeginTarget(FullRobotState beginTarget) {
		this.beginTarget = beginTarget;
	}

	public RawEstimateAimResult getAimEstimateResult() {
		return aimEstimateResult;
	}

	public void setAimEstimateResult(RawEstimateAimResult aimEstimateResult) {
		this.aimEstimateResult = aimEstimateResult;
	}

	public int getMaxPower() {
	    return maxPower;
    }

	public void setMaxPower(int maxPower) {
	    this.maxPower = maxPower;
    }

	public double getBeginSourceGunHeading() {
	    return beginSourceGunHeading;
    }

	public void setBeginSourceGunHeading(double sourceGunHeading) {
	    this.beginSourceGunHeading = sourceGunHeading;
    }
	
	
}
