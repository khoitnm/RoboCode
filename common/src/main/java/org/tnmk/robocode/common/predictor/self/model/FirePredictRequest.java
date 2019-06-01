package org.tnmk.robocode.common.predictor.self.model;

import org.tnmk.robocode.common.model.BaseRobotState;
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
	private long beginTime;
	private double beginSourceGunHeading;
	private FullRobotState beginSource;
	private BaseRobotState beginTarget;

	private RawEstimateAimResult aimEstimateResult;

	public long getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(long time) {
		this.beginTime = time;
	}

	public FullRobotState getBeginSource() {
		return beginSource;
	}

	public void setBeginSource(FullRobotState beginSource) {
		this.beginSource = beginSource;
	}

	public BaseRobotState getBeginTarget() {
		return beginTarget;
	}

	public void setBeginTarget(BaseRobotState beginTarget) {
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
