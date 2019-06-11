package org.tnmk.robocode.common.gun.predictor.self;

import java.awt.Color;

import org.tnmk.robocode.common.helper.GunHelper;
import org.tnmk.common.math.MathUtils;
import org.tnmk.common.math.Point;
import org.tnmk.robocode.common.gun.predictor.self.model.FirePredictRequest;
import org.tnmk.robocode.common.gun.predictor.self.model.PredictedAimAndFireResult;
import org.tnmk.robocode.common.gun.predictor.self.model.PredictedFirePoint;
import org.tnmk.robocode.common.gun.predictor.self.model.RawEstimateAimResult;

import robocode.Robot;
import robocode.Rules;

public class VeryClosePredictStrategy extends BasePredictStrategy {
	public static int VERY_CLOSE_STEPS = 5;

	public VeryClosePredictStrategy(Robot robot) {
		super(robot);
		setPredictBulletColor(Color.CYAN);
	}

	@Override
	public PredictedAimAndFireResult predictBestFirePoint(FirePredictRequest firePredictRequest) {
		PredictedAimAndFireResult result = super.initResult(firePredictRequest);
		RawEstimateAimResult aimEstimateResult = firePredictRequest.getAimEstimateResult();
		PredictedFirePoint bestFirePoint = null;
		int aimSteps = aimEstimateResult.getAimSteps();

		if (aimSteps <= VERY_CLOSE_STEPS) {
			Point aimedTargetPosition = aimEstimateResult.getAimedTarget().getPosition();
			double distnaceAimedRobots = MathUtils.distance(aimEstimateResult.getAimedSource().getPosition(), aimedTargetPosition);

			int maxFirePower = (int) Rules.MAX_BULLET_POWER;
			for (int firePower = maxFirePower; firePower >= 0; firePower--) {
				int fireSteps = (int) Math.ceil(GunHelper.reckonBulletSteps(distnaceAimedRobots, firePower));
				if (aimSteps + fireSteps <= VERY_CLOSE_STEPS) {
					bestFirePoint = new PredictedFirePoint();
					bestFirePoint.setDistanceToTargetMove(distnaceAimedRobots);
					bestFirePoint.setFirePower(firePower);
					bestFirePoint.setFireSteps(fireSteps);
					bestFirePoint.set(aimedTargetPosition.getX(), aimedTargetPosition.getY());
					break;
				}
			}
		}
		super.setPredictAimResult(firePredictRequest, bestFirePoint, result);
		return result;
	}

}
