package org.tnmk.robocode.common.predictor.self;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.tnmk.robocode.common.helper.BattleField;
import org.tnmk.robocode.common.helper.GunHelper;
import org.tnmk.robocode.common.helper.MoveHelper;
import org.tnmk.robocode.common.math.Circle;
import org.tnmk.robocode.common.math.LineSegment;
import org.tnmk.robocode.common.math.MathUtils;
import org.tnmk.robocode.common.math.Point;
import org.tnmk.robocode.common.model.AimAndFireResult;
import org.tnmk.robocode.common.model.FullRobotState;
import org.tnmk.robocode.common.predictor.self.model.FindingBestFirePointResult;
import org.tnmk.robocode.common.predictor.self.model.PredictStateResult;
import org.tnmk.robocode.common.predictor.self.model.PredictedAimAndFireResult;
import org.tnmk.robocode.common.predictor.self.model.PredictedFiredPoint;

import robocode.Robot;
import robocode.Rules;

public class PredictHelper {
	/**
	 * The maximum possible value of angle between [current target move direction] and [expected target move direction to get hit by bullet].
	 * Angle in degree
	 */
	private static final int MAX_DIFFERENT_TARGET_MOVE_ANGLE = 89;
	private static final int FILTER_NEAERST_POINTS_COUNT = 5;//The less, the more accurate
	private static final double MAX_DISTANCE_TO_TARGET_MOVE = 30;//The less, the more accurate
	/**
	 * This value should be equals to robot's half size
	 */
	private static final double MIN_DIFFERENT_MOVE_AND_AIM_COS = 0.98;//10 degree: the bigger, the more accurate (but less than or equals 1, because this is the cos value)
	private BattleField battleField;
	private Robot robot;
	public PredictHelper(Robot robot) {
		this.robot = robot;
		this.battleField = MoveHelper.createBattleField(robot);
	}

	public PredictedAimAndFireResult predictBestStepsToAimAndFire(double gunCoolRate, double sourceGunHeat, double maxPower, double sourceGunHeading, FullRobotState sourceState, FullRobotState targetState) {
		// Robot will aim while waiting for the gun cool down. So the steps to
		// aim is equals to the steps to cool gun down.
		int gunCoolTime = (int) Math.ceil(sourceGunHeat / gunCoolRate) + 1;
		double estimatedGunTurnRightAngle = MathUtils.calculateTurnRightAngleToTarget(sourceGunHeading, sourceState.getX(), sourceState.getY(), targetState.getX(), targetState.getY());
		int estimatedAimSteps = (int)Math.ceil(Math.abs(estimatedGunTurnRightAngle)/Rules.GUN_TURN_RATE)+2;
		// if the steps to cool gun down is too short, robot won't have enough steps to aim, so we must calculate aimSteps base on the estimated gunTurnAngle.
		int aimSteps = Math.max(gunCoolTime, estimatedAimSteps);

		PredictStateResult predictedAimedSource = PredictWrapper.predict(aimSteps, sourceState, battleField);
		PredictStateResult predictedAimedTarget = PredictWrapper.predictTargetPosition(aimSteps, targetState);
		
		PredictedAimAndFireResult result = new PredictedAimAndFireResult();
		PredictedFiredPoint bestPoint = predictPossibleFirePointsWithSmallDifferentAngle(maxPower, aimSteps, targetState, predictedAimedSource, predictedAimedTarget);
		if (bestPoint != null){
			FindingBestFirePointResult findResult = new FindingBestFirePointResult();
			result.setFindNearestPointToTargetMovementResult(findResult);
		}else{
			List<PredictedFiredPoint> possibleBulletHitTargetPoints = predictPossibleBulletHitTargetPoints(predictedAimedSource, predictedAimedTarget);// This
			FindingBestFirePointResult findResult = findBestPointToTarget(possibleBulletHitTargetPoints, predictedAimedTarget);// Note:
			result.setPossibleBulletHitTargetPoints(possibleBulletHitTargetPoints);
			result.setFindNearestPointToTargetMovementResult(findResult);
			bestPoint = findResult.getBestPoint();
		}
		
		result.setAimResult(new AimAndFireResult());
		result.getAimResult().setAimSteps(aimSteps);
		result.setSource(predictedAimedSource);
		result.setTarget(predictedAimedTarget);
		result.setPredictedFiredTarget(PredictWrapper.predict(result.getAimResult().getTotalSteps(), targetState, battleField));
		
		if (bestPoint != null){
			result.getAimResult().setFireSteps(bestPoint.getFireSteps());
			double predictedDistance = MathUtils.distance(predictedAimedSource.getPosition(), bestPoint);
			result.getAimResult().setDistance(predictedDistance);
			double turnRightAngle = MathUtils.calculateTurnRightAngleToTarget(sourceGunHeading, predictedAimedSource.getX(), predictedAimedSource.getY(), bestPoint.x, bestPoint.y);
			result.getAimResult().setTurnRightAngle(turnRightAngle);
			result.setBestPredictPoint(bestPoint);
			result.setWaitForBetterAim(false);
		}else{
			result.setWaitForBetterAim(true);
		}
		return result;
	}


	private FindingBestFirePointResult findBestPointToTarget(List<PredictedFiredPoint> possibleBulletHitTargetPoints, PredictStateResult predictedAimedTarget) {
		if (predictedAimedTarget.isStandStill()){
			FindingBestFirePointResult rs = new FindingBestFirePointResult();
			PredictedFiredPoint nearestPoint = findBestPointToTargetStanding(possibleBulletHitTargetPoints, predictedAimedTarget);
			rs.setBestPoint(nearestPoint);
			rs.setImpossiblePoints(new ArrayList<PredictedFiredPoint>());
			rs.setNearestPoints(new ArrayList<PredictedFiredPoint>());
			return rs;
		}else{
			return findBestPointToTargetMoving(possibleBulletHitTargetPoints, predictedAimedTarget);
		}
    }
	
	
	private FindingBestFirePointResult findBestPointToTargetMoving(List<PredictedFiredPoint> possibleBulletHitTargetPoints, PredictStateResult predictedAimedTarget) {
		FindingBestFirePointResult nearestPointsResult = findNearestPointsToTargetMovement(possibleBulletHitTargetPoints, predictedAimedTarget);
		nearestPointsResult.setBestPoint(findBestPointToTargetFromNearestPoints(nearestPointsResult.getNearestPoints(), nearestPointsResult.getTargetCurrentMoveLine(), predictedAimedTarget));
		return nearestPointsResult;
    }
	/**
	 * If cannot found, return null
	 * @param nearestPoints
	 * @param targetCurrentMoveLine
	 * @param predictedAimedTarget
	 * @return
	 */
	private PredictedFiredPoint findBestPointToTargetFromNearestPoints(List<PredictedFiredPoint> nearestPoints, LineSegment targetCurrentMoveLine, PredictStateResult predictedAimedTarget) {
		List<PredictedFiredPoint> pointsByPower03 = new ArrayList<>();
		List<PredictedFiredPoint> pointsByPower02 = new ArrayList<>();
		List<PredictedFiredPoint> pointsByPower01 = new ArrayList<>();
	    for (PredictedFiredPoint predictedFiredPoint : nearestPoints) {
	        if (predictedFiredPoint.getFirePower() == GunHelper.BULLET_POWER_03){
	        	pointsByPower03.add(predictedFiredPoint);
	        }else if (predictedFiredPoint.getFirePower() == GunHelper.BULLET_POWER_02){
	        	pointsByPower02.add(predictedFiredPoint);
	        }else{
	        	pointsByPower01.add(predictedFiredPoint);
	        }
        }
	    if (pointsByPower03.size() > 0){
	    	shortByFireSteps(pointsByPower03);
	    	PredictedFiredPoint bestPointByPower03 = pointsByPower03.get(0);
	    	if (bestPointByPower03.getFireSteps() > 0 && bestPointByPower03.getFireSteps() <= GunHelper.FireDistance.STEPS_VERY_SHORT.getNumValue()){
	    		return bestPointByPower03;
	    	}
	    }
	    if (pointsByPower02.size() > 0){
	    	shortByFireSteps(pointsByPower02);
	    	PredictedFiredPoint bestPointByPower02 = pointsByPower02.get(0);
	    	if (bestPointByPower02.getFireSteps() > 0 && bestPointByPower02.getFireSteps() <= GunHelper.FireDistance.STEPS_SHORT.getNumValue()){
	    		return bestPointByPower02;
	    	}
	    }
	    if (pointsByPower01.size() > 0){
	    	shortByFireSteps(pointsByPower01);
	    	return pointsByPower01.get(0);
	    }else{
	    	return null;
	    }
	    
    }
	private void shortByFireSteps(List<PredictedFiredPoint> points) {
	    Comparator<PredictedFiredPoint> comparator = new Comparator<PredictedFiredPoint>(){
			@Override
            public int compare(PredictedFiredPoint pA, PredictedFiredPoint pB) {
				return pA.getFireSteps() - pB.getFireSteps();				
            }
	    };
	    Collections.sort(points, comparator);
    }

	private FindingBestFirePointResult findNearestPointsToTargetMovement(List<PredictedFiredPoint> possibleBulletHitTargetPoints, PredictStateResult predictedAimedTarget) {
		FindingBestFirePointResult result = new FindingBestFirePointResult();
		List<PredictedFiredPoint> nearestPoints = new ArrayList<>();
		List<PredictedFiredPoint> impossiblePoints = new ArrayList<>();
		List<PredictedFiredPoint> possiblePoints = new ArrayList<>();
		LineSegment targetCurrentMoveLine= new LineSegment(predictedAimedTarget.getPosition(), predictedAimedTarget.getMoveAngle(),1000);
		double targetCurrentMoveAngle = predictedAimedTarget.getMoveAngle();
		
		for (PredictedFiredPoint predictedFiredPoint : possibleBulletHitTargetPoints) {
			LineSegment targetMoveToHitFiredLine= new LineSegment(predictedAimedTarget.getPosition(), predictedFiredPoint);
			double targetMoveToHitFiredAngle = targetMoveToHitFiredLine.reckonAngle();
			if (!isInsideBattleField(predictedFiredPoint) || 
				(Math.abs(targetCurrentMoveAngle - targetMoveToHitFiredAngle) > MAX_DIFFERENT_TARGET_MOVE_ANGLE && !MathUtils.close(predictedFiredPoint, predictedAimedTarget.getPosition())) ){
				impossiblePoints.add(predictedFiredPoint);
				continue;
			}else{
				predictedFiredPoint.setDistanceToTargetMove(MathUtils.distance(targetCurrentMoveLine, predictedFiredPoint));
				if (predictedFiredPoint.getDistanceToTargetMove() > MAX_DISTANCE_TO_TARGET_MOVE){
					impossiblePoints.add(predictedFiredPoint);
					continue;
				}
			}
			possiblePoints.add(predictedFiredPoint);
	       
        }
		//Short possiblePoints by distance
		shortByDistance(possiblePoints, targetCurrentMoveLine);
		if (possiblePoints.size() > FILTER_NEAERST_POINTS_COUNT){
			nearestPoints = possiblePoints.subList(0, FILTER_NEAERST_POINTS_COUNT);
		}
		
		result.setImpossiblePoints(impossiblePoints);
		result.setTargetCurrentMoveAngle(targetCurrentMoveAngle);
		result.setTargetCurrentMoveLine(targetCurrentMoveLine);
		result.setNearestPoints(nearestPoints);
		return result;
	}
	
	private boolean isInsideBattleField(PredictedFiredPoint predictedFiredPoint) {
		double x = predictedFiredPoint.getX();
		double y = predictedFiredPoint.getY();
	    return (x >= 0 && x <= battleField.getWidth() && y >= 0 && y <= battleField.getHeight());
    }

	private void shortByDistance(List<PredictedFiredPoint> possiblePoints, final LineSegment targetCurrentMoveLine) {
	    Comparator<PredictedFiredPoint> comparator = new Comparator<PredictedFiredPoint>(){
			@Override
            public int compare(PredictedFiredPoint pA, PredictedFiredPoint pB) {
				if (pA.getDistanceToTargetMove() == null){
					pA.setDistanceToTargetMove(MathUtils.distance(targetCurrentMoveLine, pA));
				}
				if (pB.getDistanceToTargetMove() == null){
					pB.setDistanceToTargetMove(MathUtils.distance(targetCurrentMoveLine, pB));
				}
				if (pA.getDistanceToTargetMove() > pB.getDistanceToTargetMove()){
					return 1;
				}else if (pA.getDistanceToTargetMove() < pB.getDistanceToTargetMove()){
					return -1;
				}else{
					return 0;
				}
            }
	    };
	    Collections.sort(possiblePoints, comparator);
    }

	private PredictedFiredPoint findBestPointToTargetStanding(List<PredictedFiredPoint> possibleBulletHitTargetPoints, PredictStateResult predictedAimedTarget) {
		double nearestDistance = Double.MAX_VALUE;
		PredictedFiredPoint nearestPoint = null;
		for (PredictedFiredPoint predictedFiredPoint : possibleBulletHitTargetPoints) {
	        double distance = MathUtils.distance(predictedFiredPoint, predictedAimedTarget.getPosition());
	        if (distance < nearestDistance){
	        	nearestDistance = distance;
	        	nearestPoint = predictedFiredPoint;
	        }
        }
		return nearestPoint;
	}

	private List<PredictedFiredPoint> predictPossibleBulletHitTargetPoints(PredictStateResult predictedAimedSource, PredictStateResult predictedAimedTarget) {
		List<PredictedFiredPoint> results = new ArrayList<>();
		double aimedDistance = MathUtils.distance(predictedAimedSource.getPosition(), predictedAimedTarget.getPosition());

		if (predictedAimedTarget.isStandStill()) {
			PredictedFiredPoint result = new PredictedFiredPoint();
			result.set(predictedAimedTarget.getPosition().x, predictedAimedTarget.getPosition().y);
			double fireDistance = MathUtils.distance(predictedAimedSource.getPosition(), result);
			double firePower;
			if (fireDistance < GunHelper.FireDistance.DISTANCE_VERY_SHORT.getNumValue()){
				firePower = GunHelper.BULLET_POWER_03;
			}else if (fireDistance < GunHelper.FireDistance.DISTANCE_SHORT.getNumValue()){
				firePower = GunHelper.BULLET_POWER_02;
			}else{
				firePower = GunHelper.BULLET_POWER_01;
			}
			int fireSteps = (int) Math.ceil(aimedDistance / Rules.getBulletSpeed(firePower));
			result.setFireSteps(fireSteps);
			result.setFirePower(firePower);
			results.add(result);
			return results;
		}
		for (int i = 0; i < Rules.MAX_BULLET_POWER; i++) {
			double firePower = i + 1;
			List<PredictedFiredPoint> resultsByFirePower = predictPossibleBulletHitMovingTargetPointsByPower(firePower, aimedDistance, predictedAimedSource, predictedAimedTarget);
			results.addAll(resultsByFirePower);
		}
		return results;
	}
	private PredictedFiredPoint predictPossibleFirePointsWithSmallDifferentAngle(double maxPower, int aimSteps, 
			FullRobotState targetState, PredictStateResult predictedAimedSource, PredictStateResult predictedAimedTarget) {
		
	}
	/**
	 * If target's moving direction and our robot aiming direction is very the same, use this method
	 * @param firePower
	 * @param sourceAndTargetDistance
	 * @param predictedAimedSource
	 * @param predictedAimedTarget
	 * @return 
	 */
	private PredictedFiredPoint predictPossibleFirePointsByPowerWithSmallDifferentAngle(
			double firePower, double sourceAndTargetDistance, 
			PredictStateResult predictedAimedSource, PredictStateResult predictedAimedTarget) {
		List<PredictedFiredPoint> results = new ArrayList<>();
		double targetMoveAngle = predictedAimedTarget.getMoveAngle();
		double aimAngle = MathUtils.absoluteBearing(predictedAimedSource.getX(), predictedAimedSource.getY(), predictedAimedTarget.getX(), predictedAimedTarget.getY());
		double differentAngle = Math.abs(aimAngle - targetMoveAngle) % 180;//It's not important which A - B or B - A, negative or positive is not important.
		double absCosDifferentAngle = Math.abs(Math.cos(Math.toRadians(differentAngle)));
		if (absCosDifferentAngle < MIN_DIFFERENT_MOVE_AND_AIM_COS) {
			return null;
		}
		//aim to absolutely by linear target point
		double bulletSpeed = Rules.getBulletSpeed(firePower);
		int estimateFireSteps = (int)Math.ceil(sourceAndTargetDistance / bulletSpeed);
//		PredictedFiredPoint predictedFiredTarget = PredictWrapper.predictTargetPosition(estimateFireSteps, predictedAimedTarget);
		
		PredictedFiredPoint predictedFiredPoint = new PredictedFiredPoint();
		predictedFiredPoint.set(predictedAimedTarget.getX(), predictedAimedTarget.getY());
		predictedFiredPoint.setFirePower(firePower);
		predictedFiredPoint.setFireSteps(estimateFireSteps);
		predictedFiredPoint.setDistanceToTargetMove(sourceAndTargetDistance);
		return predictedFiredPoint;
	}
	/**
	 * @param firePower
	 * @param sourceAndTargetDistance
	 * @param predictedAimedSource
	 * @param predictedAimedTarget
	 *            this target is not stand still. If it's stand still, never
	 *            call this method.
	 * @return
	 */
	private List<PredictedFiredPoint> predictPossibleBulletHitMovingTargetPointsByPower(double firePower, double sourceAndTargetDistance, PredictStateResult predictedAimedSource, PredictStateResult predictedAimedTarget) {
		List<PredictedFiredPoint> results = new ArrayList<>();
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
				PredictedFiredPoint predictedFiredPoint = new PredictedFiredPoint();
				predictedFiredPoint.set(ipossibleHitPoint.x, ipossibleHitPoint.y);
				predictedFiredPoint.setFirePower(firePower);
				predictedFiredPoint.setFireSteps(isteps);
				results.add(predictedFiredPoint);
			}
		}
		return results;
	}

	
	public Robot getRobot() {
	    return robot;
    }

	public void setRobot(Robot robot) {
	    this.robot = robot;
    }
}
