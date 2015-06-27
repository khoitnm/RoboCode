package org.tnmk.robocode.common.predictor.self;

import org.tnmk.robocode.common.helper.GunHelper;
import org.tnmk.robocode.common.math.MathUtils;
import org.tnmk.robocode.common.model.FullRobotState;
import org.tnmk.robocode.common.predictor.self.model.FirePredictRequest;
import org.tnmk.robocode.common.predictor.self.model.PredictStateResult;
import org.tnmk.robocode.common.predictor.self.model.PredictedAimAndFireResult;
import org.tnmk.robocode.common.predictor.self.model.PredictedFirePoint;

import robocode.Robot;
import robocode.Rules;

public class FacingPredictStrategy extends BasePredictStrategy {
	/**
	 * This value should be equals to robot's half size
	 */
	private static final double MIN_DIFFERENT_MOVE_AND_AIM_COS = 0.98;// 10 degree: the bigger, the more accurate (but less than or equals 1, because this is the cos value)

	public FacingPredictStrategy(Robot robot) {
		super(robot);
	}

	@Override
	public PredictedAimAndFireResult predictBestFirePoint(FirePredictRequest firePredictRequest) {
		PredictedAimAndFireResult result = initResult(firePredictRequest);
		PredictedFirePoint bestFirePoint = predictPossibleFirePointsWithSmallDifferentAngle(
				firePredictRequest.getMaxPower(), firePredictRequest.getBeginTarget(), 
				firePredictRequest.getAimEstimateResult().getAimedTarget(), 
				firePredictRequest.getAimEstimateResult().getAimedSource());

		setPredictAimResult(firePredictRequest, bestFirePoint, result);
		return result;
	}

	private PredictedFirePoint predictPossibleFirePointsWithSmallDifferentAngle(int maxPower, FullRobotState targetState, PredictStateResult predictedAimedSource, PredictStateResult predictedAimedTarget) {
		double sourceAndTargetDistance = MathUtils.distance(predictedAimedSource.getPosition(), predictedAimedTarget.getPosition());
		double targetMoveAngle = predictedAimedTarget.getMoveAngle();
		double aimAngle = MathUtils.absoluteBearing(predictedAimedSource.getX(), predictedAimedSource.getY(), predictedAimedTarget.getX(), predictedAimedTarget.getY());
		double differentAngle = Math.abs(aimAngle - targetMoveAngle) % 180;// It's not important which A - B or B - A, negative or positive is not important.
		double absCosDifferentAngle = Math.abs(Math.cos(Math.toRadians(differentAngle)));
		if (absCosDifferentAngle < MIN_DIFFERENT_MOVE_AND_AIM_COS) {
			return null;
		}

		for (int firePower = maxPower; firePower >= GunHelper.BULLET_POWER_02; firePower--) {
			PredictedFirePoint predictPoint = predictPossibleFirePointsByPowerWithSmallDifferentAngle(firePower, sourceAndTargetDistance, predictedAimedSource, predictedAimedTarget);
			if (GunHelper.isShouldFireBySteps(firePower, predictPoint.getFireSteps())) {
				return predictPoint;
			}
		}
		return predictPossibleFirePointsByPowerWithSmallDifferentAngle(GunHelper.BULLET_POWER_01, sourceAndTargetDistance, predictedAimedSource, predictedAimedTarget);
	}

	/**
	 * If target's moving direction and our robot aiming direction is very the same, use this method
	 * 
	 * @param firePower
	 * @param sourceAndTargetDistance
	 *            raw estimate source and target distance. It's usally the distance of aimedSource and aimedTarget (target position when source is aimed, not target when it will get fired)
	 * @param predictedAimedSource
	 * @param predictedAimedTarget
	 * @return
	 */
	private PredictedFirePoint predictPossibleFirePointsByPowerWithSmallDifferentAngle(double firePower, double sourceAndTargetDistance, PredictStateResult predictedAimedSource, PredictStateResult predictedAimedTarget) {
		// aim to absolutely by linear target point
		double bulletSpeed = Rules.getBulletSpeed(firePower);
		int estimateFireSteps = (int) Math.ceil(sourceAndTargetDistance / bulletSpeed);

		PredictStateResult predictedFiredTarget = PredictWrapper.predictTargetPosition(estimateFireSteps, predictedAimedTarget);
		double correctDistance;
		int correctFireSteps;
		if (isInsideBattleField(predictedFiredTarget.getPosition())) {
			correctDistance = MathUtils.distance(predictedAimedSource.getPosition(), predictedFiredTarget.getPosition());
			correctFireSteps = (int) Math.ceil(correctDistance / bulletSpeed);
		} else {
			predictedFiredTarget = predictedAimedTarget;
			correctDistance = sourceAndTargetDistance;
			correctFireSteps = estimateFireSteps;
		}

		PredictedFirePoint predictedFiredPoint = new PredictedFirePoint();
		predictedFiredPoint.set(predictedFiredTarget.getX(), predictedFiredTarget.getY());
		predictedFiredPoint.setFirePower(firePower);
		predictedFiredPoint.setFireSteps(correctFireSteps);
		return predictedFiredPoint;
	}
}
