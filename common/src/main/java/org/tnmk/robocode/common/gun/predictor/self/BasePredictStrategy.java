package org.tnmk.robocode.common.gun.predictor.self;

import java.awt.Color;
import java.util.List;

import org.tnmk.common.math.GeoMathUtils;
import org.tnmk.robocode.common.helper.MoveHelper;
import org.tnmk.common.math.Point;
import org.tnmk.robocode.common.model.BattleField;
import org.tnmk.robocode.common.gun.predictor.self.model.FindingBestFirePointResult;
import org.tnmk.robocode.common.gun.predictor.self.model.FirePredictRequest;
import org.tnmk.robocode.common.gun.predictor.self.model.PredictedAimAndFireResult;
import org.tnmk.robocode.common.gun.predictor.self.model.PredictedAimResult;
import org.tnmk.robocode.common.gun.predictor.self.model.PredictedFirePoint;

import robocode.Robot;

public abstract class BasePredictStrategy implements PredictStrategy {

	private Color predictBulletColor = Color.WHITE;

	private final BattleField battleField;
	protected final Robot robot;

	public BasePredictStrategy(Robot robot) {
		this.robot = robot;
		this.battleField = MoveHelper.createBattleField(robot);
	}

	public PredictedAimAndFireResult initResult(FirePredictRequest firePredictRequest) {
		PredictedAimAndFireResult result = new PredictedAimAndFireResult();
		result.setBeginTime(firePredictRequest.getBeginTime());
		result.setBeginSource(firePredictRequest.getBeginSource());
		result.setBeginTarget(firePredictRequest.getBeginTarget());
		result.setFirstAimEstimation(firePredictRequest.getAimEstimateResult());
		result.setPredictStrategy(this);
		return result;
	}

	public void setPredictAimResult(FirePredictRequest firePredictRequest, PredictedFirePoint predictedFirePoint, PredictedAimAndFireResult result) {
		FindingBestFirePointResult findingBestFirePointResult = new FindingBestFirePointResult();
		findingBestFirePointResult.setBestPoint(predictedFirePoint);
		setPredictAimResult(firePredictRequest, null, findingBestFirePointResult, result);
	}

	public void setPredictAimResult(FirePredictRequest firePredictRequest, List<PredictedFirePoint> availabelFireTargetPoints, FindingBestFirePointResult findingBestFirePointResult, PredictedAimAndFireResult result) {
		result.getFireResult().setAvailableFirePoints(availabelFireTargetPoints);
		result.getFireResult().setFindingBestPointResult(findingBestFirePointResult);

		PredictedAimResult aimResult = result.getAimResult();
		aimResult.setAimSteps(result.getFirstAimEstimation().getAimSteps());
		aimResult.setSource(result.getFirstAimEstimation().getAimedSource());
		PredictedFirePoint bestPoint = result.getFireResult().getFindingBestPointResult().getBestPoint();
		if (bestPoint != null) {
			//TODO should we calculate with gunHeading from begin time or from aimed time???
			double gunTurnRightDirection = GeoMathUtils.calculateTurnRightDirectionToTarget(firePredictRequest.getBeginSourceGunHeading(), aimResult.getSource().getX(), aimResult.getSource().getY(), bestPoint.x, bestPoint.y);
			aimResult.setGunTurnRightDirection(gunTurnRightDirection);
			aimResult.setFiredTarget(bestPoint);

			result.setWaitForBetterAim(false);
		} else {
			result.setWaitForBetterAim(true);
		}
	}

	protected boolean isInsideBattleField(Point point) {
		double x = point.getX();
		double y = point.getY();
		return (x >= 0 && x <= battleField.getWidth() && y >= 0 && y <= battleField.getHeight());
	}

	public Robot getRobot() {
		return robot;
	}

	@Override
	public Color getPredictBulletColor() {
		return predictBulletColor;
	}

	public void setPredictBulletColor(Color predictBulletColor) {
		this.predictBulletColor = predictBulletColor;
	}

}
