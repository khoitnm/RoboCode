package org.tnmk.robocode.common.predictor.self;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.tnmk.robocode.common.helper.GunHelper;
import org.tnmk.robocode.common.helper.ListUtils;
import org.tnmk.robocode.common.math.Circle;
import org.tnmk.robocode.common.math.LineSegment;
import org.tnmk.robocode.common.math.MathUtils;
import org.tnmk.robocode.common.math.Point;
import org.tnmk.robocode.common.predictor.self.model.FindingBestFirePointResult;
import org.tnmk.robocode.common.predictor.self.model.FirePredictRequest;
import org.tnmk.robocode.common.predictor.self.model.PredictStateResult;
import org.tnmk.robocode.common.predictor.self.model.PredictedAimAndFireResult;
import org.tnmk.robocode.common.predictor.self.model.PredictedFirePoint;
import org.tnmk.robocode.common.predictor.self.model.RawEstimateAimResult;

import robocode.Robot;
import robocode.Rules;

public class LinearPredictStrategy extends BasePredictStrategy {
	/**
	 * The maximum possible value of angle between [current target move direction] and [expected target move direction to get hit by bullet]. Angle in degree
	 */
	private static final int MAX_DIFFERENT_TARGET_MOVE_ANGLE = 89;
	private static final int FILTER_NEAERST_POINTS_COUNT = 5;// The less, the more accurate
	private static final double MAX_DISTANCE_TO_TARGET_MOVE = 30;// The less, the more accurate

	public LinearPredictStrategy(Robot robot) {
		super(robot);
		setPredictBulletColor(Color.YELLOW);
	}

	@Override
	public PredictedAimAndFireResult predictBestFirePoint(FirePredictRequest firePredictRequest) {
		PredictedAimAndFireResult result = initResult(firePredictRequest);

		RawEstimateAimResult aimEstimateResult = firePredictRequest.getAimEstimateResult();
		List<PredictedFirePoint> availabelFireTargetPoints = predictAvailabelFireTargetPoints(aimEstimateResult.getAimedSource(), aimEstimateResult.getAimedTarget());// This
		FindingBestFirePointResult findingBestFirePointResult = findBestPointToTarget(availabelFireTargetPoints, aimEstimateResult.getAimedTarget());// Note:
		
		setPredictAimResult(firePredictRequest, availabelFireTargetPoints, findingBestFirePointResult, result);
		return result;
	}

	private List<PredictedFirePoint> predictAvailabelFireTargetPoints(PredictStateResult predictedAimedSource, PredictStateResult predictedAimedTarget) {
		List<PredictedFirePoint> results = new ArrayList<>();
		double aimedDistance = MathUtils.distance(predictedAimedSource.getPosition(), predictedAimedTarget.getPosition());

		if (predictedAimedTarget.isStandStill()) {
			PredictedFirePoint result = new PredictedFirePoint();
			result.set(predictedAimedTarget.getPosition().x, predictedAimedTarget.getPosition().y);
			double fireDistance = MathUtils.distance(predictedAimedSource.getPosition(), result);
			double firePower = GunHelper.findFirePowerByDistance(fireDistance);
			int fireSteps = (int) Math.ceil(aimedDistance / Rules.getBulletSpeed(firePower));
			result.setFireSteps(fireSteps);
			result.setFirePower(firePower);
			results.add(result);
			return results;
		}
		for (int i = 0; i < Rules.MAX_BULLET_POWER; i++) {
			double firePower = i + 1;
			List<PredictedFirePoint> resultsByFirePower = predictPossibleBulletHitMovingTargetPointsByPower(firePower, aimedDistance, predictedAimedSource, predictedAimedTarget);
			results.addAll(resultsByFirePower);
		}
		return results;
	}

	/**
	 * @param firePower
	 * @param sourceAndTargetDistance
	 * @param predictedAimedSource
	 * @param predictedAimedTarget
	 *            this target is not stand still. If it's stand still, never call this method.
	 * @return
	 */
	private List<PredictedFirePoint> predictPossibleBulletHitMovingTargetPointsByPower(double firePower, double sourceAndTargetDistance, PredictStateResult predictedAimedSource, PredictStateResult predictedAimedTarget) {
		List<PredictedFirePoint> results = new ArrayList<>();
		double bulletSpeed = Rules.getBulletSpeed(firePower);
		double targetSpeed = predictedAimedTarget.getSpeed();
		int minStepsToFire = (int) Math.ceil(sourceAndTargetDistance / (bulletSpeed + targetSpeed));
		int maxStepsToFire = (int) Math.ceil(sourceAndTargetDistance / Math.abs(bulletSpeed - targetSpeed));
		for (int isteps = minStepsToFire; isteps <= maxStepsToFire; isteps++) {
			double targetMoveDistance = isteps * targetSpeed;
			double bulletMoveDistance = isteps * bulletSpeed;
			Circle bulletMoveCircle = new Circle(predictedAimedSource.getPosition(), bulletMoveDistance);
			Circle targetMoveCircle = new Circle(predictedAimedTarget.getPosition(), targetMoveDistance);
			List<Point> possibleHitPoints = MathUtils.intersectCircles(bulletMoveCircle, targetMoveCircle);
			for (Point ipossibleHitPoint : possibleHitPoints) {
				PredictedFirePoint predictedFiredPoint = new PredictedFirePoint();
				predictedFiredPoint.set(ipossibleHitPoint.x, ipossibleHitPoint.y);
				predictedFiredPoint.setFirePower(firePower);
				predictedFiredPoint.setFireSteps(isteps);
				results.add(predictedFiredPoint);
			}
		}
		return results;
	}

	private FindingBestFirePointResult findBestPointToTarget(List<PredictedFirePoint> possibleBulletHitTargetPoints, PredictStateResult predictedAimedTarget) {
		if (predictedAimedTarget.isStandStill()) {
			FindingBestFirePointResult rs = new FindingBestFirePointResult();
			PredictedFirePoint nearestPoint = findBestPointToTargetStanding(possibleBulletHitTargetPoints, predictedAimedTarget);
			rs.setBestPoint(nearestPoint);
			rs.setImpossiblePoints(new ArrayList<PredictedFirePoint>());
			rs.setNearestPoints(new ArrayList<PredictedFirePoint>());
			return rs;
		} else {
			return findBestPointToTargetMoving(possibleBulletHitTargetPoints, predictedAimedTarget);
		}
	}

	private FindingBestFirePointResult findBestPointToTargetMoving(List<PredictedFirePoint> possibleBulletHitTargetPoints, PredictStateResult predictedAimedTarget) {
		FindingBestFirePointResult nearestPointsResult = findNearestPointsToTargetMovement(possibleBulletHitTargetPoints, predictedAimedTarget);
		nearestPointsResult.setBestPoint(findBestPointToTargetFromNearestPoints(nearestPointsResult.getNearestPoints(), nearestPointsResult.getTargetCurrentMoveLine(), predictedAimedTarget));
		return nearestPointsResult;
	}

	private PredictedFirePoint findBestPointToTargetStanding(List<PredictedFirePoint> possibleBulletHitTargetPoints, PredictStateResult predictedAimedTarget) {
		double nearestDistance = Double.MAX_VALUE;
		PredictedFirePoint nearestPoint = null;
		for (PredictedFirePoint predictedFiredPoint : possibleBulletHitTargetPoints) {
			double distance = MathUtils.distance(predictedFiredPoint, predictedAimedTarget.getPosition());
			if (distance < nearestDistance) {
				nearestDistance = distance;
				nearestPoint = predictedFiredPoint;
			}
		}
		return nearestPoint;
	}

	private FindingBestFirePointResult findNearestPointsToTargetMovement(List<PredictedFirePoint> availableFireTargetPoints, PredictStateResult predictedAimedTarget) {
		FindingBestFirePointResult result = new FindingBestFirePointResult();
		List<PredictedFirePoint> possiblePoints = new ArrayList<>();
		List<PredictedFirePoint> nearestPoints = new ArrayList<>();

		List<PredictedFirePoint> allImpossiblePoints = new ArrayList<>();
		List<PredictedFirePoint> outsideBattlePoints = new ArrayList<>();
		List<PredictedFirePoint> impossibleAnglePoints = new ArrayList<>();
		List<PredictedFirePoint> tooFarPoints = new ArrayList<>();

		LineSegment targetCurrentMoveLine = new LineSegment(predictedAimedTarget.getPosition(), predictedAimedTarget.getMoveAngle(), 1000);
		double targetCurrentMoveAngle = predictedAimedTarget.getMoveAngle();

		for (PredictedFirePoint predictedFiredPoint : availableFireTargetPoints) {
			LineSegment targetMoveToHitFiredLine = new LineSegment(predictedAimedTarget.getPosition(), predictedFiredPoint);
			double targetMoveToHitFiredAngle = targetMoveToHitFiredLine.reckonAngle();
			if (Math.abs(targetCurrentMoveAngle - targetMoveToHitFiredAngle) > MAX_DIFFERENT_TARGET_MOVE_ANGLE && !MathUtils.close(predictedFiredPoint, predictedAimedTarget.getPosition())) {
				impossibleAnglePoints.add(predictedFiredPoint);
				allImpossiblePoints.add(predictedFiredPoint);
				continue;
			}
			;
			if (!isInsideBattleField(predictedFiredPoint)) {
				outsideBattlePoints.add(predictedFiredPoint);
				allImpossiblePoints.add(predictedFiredPoint);
				// Only for debuging
				double testDistance = MathUtils.distance(targetCurrentMoveLine, predictedFiredPoint);
				if (testDistance < 15 && isInsideBattleField(predictedFiredPoint)) {
					System.out.println("Debug somethign wrong");
				}
				predictedFiredPoint.setDistanceToTargetMove(testDistance);

				continue;
			} else {
				predictedFiredPoint.setDistanceToTargetMove(MathUtils.distance(targetCurrentMoveLine, predictedFiredPoint));
				if (predictedFiredPoint.getDistanceToTargetMove() > MAX_DISTANCE_TO_TARGET_MOVE) {
					tooFarPoints.add(predictedFiredPoint);
					allImpossiblePoints.add(predictedFiredPoint);
					continue;
				}
			}

			possiblePoints.add(predictedFiredPoint);

		}
		// Short possiblePoints by distance
		shortByDistance(possiblePoints, targetCurrentMoveLine);
		nearestPoints = ListUtils.firstElements(possiblePoints, FILTER_NEAERST_POINTS_COUNT);

		result.setOutsideBattlePoints(outsideBattlePoints);
		result.setTooFarPoints(tooFarPoints);
		result.setImpossibleAnglePoints(impossibleAnglePoints);
		result.setImpossiblePoints(allImpossiblePoints);

		result.setAvailablePoints(availableFireTargetPoints);
		result.setPossiblePoints(possiblePoints);
		result.setNearestPoints(nearestPoints);

		result.setTargetCurrentMoveAngle(targetCurrentMoveAngle);
		result.setTargetCurrentMoveLine(targetCurrentMoveLine);
		return result;
	}

	private void shortByDistance(List<PredictedFirePoint> possiblePoints, final LineSegment targetCurrentMoveLine) {
		Comparator<PredictedFirePoint> comparator = new Comparator<PredictedFirePoint>() {
			@Override
			public int compare(PredictedFirePoint pA, PredictedFirePoint pB) {
				if (pA.getDistanceToTargetMove() == null) {
					pA.setDistanceToTargetMove(MathUtils.distance(targetCurrentMoveLine, pA));
				}
				if (pB.getDistanceToTargetMove() == null) {
					pB.setDistanceToTargetMove(MathUtils.distance(targetCurrentMoveLine, pB));
				}
				if (pA.getDistanceToTargetMove() > pB.getDistanceToTargetMove()) {
					return 1;
				} else if (pA.getDistanceToTargetMove() < pB.getDistanceToTargetMove()) {
					return -1;
				} else {
					return 0;
				}
			}
		};
		Collections.sort(possiblePoints, comparator);
	}

	/**
	 * If cannot found, return null
	 * 
	 * @param nearestPoints
	 * @param targetCurrentMoveLine
	 * @param predictedAimedTarget
	 * @return
	 */
	private PredictedFirePoint findBestPointToTargetFromNearestPoints(List<PredictedFirePoint> nearestPoints, LineSegment targetCurrentMoveLine, PredictStateResult predictedAimedTarget) {
		List<PredictedFirePoint> pointsByPower03 = new ArrayList<>();
		List<PredictedFirePoint> pointsByPower02 = new ArrayList<>();
		List<PredictedFirePoint> pointsByPower01 = new ArrayList<>();
		for (PredictedFirePoint predictedFiredPoint : nearestPoints) {
			if (predictedFiredPoint.getFirePower() == GunHelper.BULLET_POWER_03) {
				pointsByPower03.add(predictedFiredPoint);
			} else if (predictedFiredPoint.getFirePower() == GunHelper.BULLET_POWER_02) {
				pointsByPower02.add(predictedFiredPoint);
			} else {
				pointsByPower01.add(predictedFiredPoint);
			}
		}
		if (pointsByPower03.size() > 0) {
			shortByFireSteps(pointsByPower03);
			PredictedFirePoint bestPointByPower03 = pointsByPower03.get(0);
			if (bestPointByPower03.getFireSteps() > 0 && GunHelper.isShouldFireBySteps(GunHelper.BULLET_POWER_03, bestPointByPower03.getFireSteps())) {
				return bestPointByPower03;
			}
		}
		if (pointsByPower02.size() > 0) {
			shortByFireSteps(pointsByPower02);
			PredictedFirePoint bestPointByPower02 = pointsByPower02.get(0);
			if (bestPointByPower02.getFireSteps() > 0 && GunHelper.isShouldFireBySteps(GunHelper.BULLET_POWER_02, bestPointByPower02.getFireSteps())) {
				return bestPointByPower02;
			}
		}
		if (pointsByPower01.size() > 0) {
			shortByFireSteps(pointsByPower01);
			return pointsByPower01.get(0);
		} else {
			return null;
		}

	}

	private void shortByFireSteps(List<PredictedFirePoint> points) {
		Comparator<PredictedFirePoint> comparator = new Comparator<PredictedFirePoint>() {
			@Override
			public int compare(PredictedFirePoint pA, PredictedFirePoint pB) {
				return pA.getFireSteps() - pB.getFireSteps();
			}
		};
		Collections.sort(points, comparator);
	}
}
