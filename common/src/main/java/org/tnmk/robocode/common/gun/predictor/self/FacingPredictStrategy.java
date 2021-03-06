package org.tnmk.robocode.common.gun.predictor.self;

import java.awt.Color;

import org.tnmk.common.math.GeoMathUtils;
import org.tnmk.robocode.common.helper.GunHelper;
import org.tnmk.robocode.common.model.BaseRobotState;
import org.tnmk.robocode.common.gun.predictor.self.model.FirePredictRequest;
import org.tnmk.robocode.common.gun.predictor.self.model.PredictStateResult;
import org.tnmk.robocode.common.gun.predictor.self.model.PredictedAimAndFireResult;
import org.tnmk.robocode.common.gun.predictor.self.model.PredictedFirePoint;

import robocode.Robot;
import robocode.Rules;

public class FacingPredictStrategy extends BasePredictStrategy {
	/**
	 * This value should be equals to robot's half size
	 */
	private static final double MIN_DIFFERENT_MOVE_AND_AIM_COS = 0.98;// 10 degree: the bigger, the more accurate (but less than or equals 1, because this is the cos value)

	public FacingPredictStrategy(Robot robot) {
		super(robot);
		setPredictBulletColor(Color.GREEN);
	}

	@Override
	public PredictedAimAndFireResult predictBestFirePoint(FirePredictRequest firePredictRequest) {
		PredictedAimAndFireResult result = initResult(firePredictRequest);
		PredictedFirePoint bestFirePoint = predictPossibleFirePointsWithSmallDifferentAngle(
				firePredictRequest.getMaxPower(), firePredictRequest.getBeginTarget(),
				firePredictRequest.getAimEstimateResult().getAimedSource(),
				firePredictRequest.getAimEstimateResult().getAimedTarget() 
				);

		setPredictAimResult(firePredictRequest, bestFirePoint, result);
		return result;
	}

	private PredictedFirePoint predictPossibleFirePointsWithSmallDifferentAngle(int maxPower, BaseRobotState targetState, PredictStateResult predictedAimedSource, PredictStateResult predictedAimedTarget) {
		double sourceAndTargetDistance = GeoMathUtils.distance(predictedAimedSource.getPosition(), predictedAimedTarget.getPosition());
		double targetMoveAngle = predictedAimedTarget.getMoveAngle();
		double aimAngle = GeoMathUtils.absoluteBearing(predictedAimedSource.getX(), predictedAimedSource.getY(), predictedAimedTarget.getX(), predictedAimedTarget.getY());
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
			correctDistance = GeoMathUtils.distance(predictedAimedSource.getPosition(), predictedFiredTarget.getPosition());
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
