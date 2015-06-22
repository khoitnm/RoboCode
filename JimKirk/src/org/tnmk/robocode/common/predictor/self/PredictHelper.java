package org.tnmk.robocode.common.predictor.self;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;

import org.tnmk.robocode.common.helper.BattleField;
import org.tnmk.robocode.common.helper.GunHelper;
import org.tnmk.robocode.common.helper.MoveHelper;
import org.tnmk.robocode.common.math.Circle;
import org.tnmk.robocode.common.math.LineSegment;
import org.tnmk.robocode.common.math.MathUtils;
import org.tnmk.robocode.common.math.Point;
import org.tnmk.robocode.common.model.FullRobotState;
import org.tnmk.robocode.common.predictor.self.model.FindingBestFirePointResult;
import org.tnmk.robocode.common.predictor.self.model.PredictStateResult;
import org.tnmk.robocode.common.predictor.self.model.PredictedAimAndFireResult;
import org.tnmk.robocode.common.predictor.self.model.PredictedAimResult;
import org.tnmk.robocode.common.predictor.self.model.PredictedFirePoint;
import org.tnmk.robocode.common.predictor.self.model.RawEstimateAimResult;

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

	public PredictedAimAndFireResult predictBestStepsToAimAndFire(long time, double gunCoolRate, double sourceGunHeat, int maxPower, double sourceGunHeading, FullRobotState sourceState, FullRobotState targetState) {
		// Robot will aim while waiting for the gun cool down. So the steps to
		// aim is equals to the steps to cool gun down.
		int gunCoolTime = (int) Math.ceil(sourceGunHeat / gunCoolRate) + 1;
		double estimatedGunTurnRightAngle = MathUtils.calculateTurnRightDirectionToTarget(sourceGunHeading, sourceState.getX(), sourceState.getY(), targetState.getX(), targetState.getY());
		int estimatedAimSteps = (int)Math.ceil(Math.abs(estimatedGunTurnRightAngle)/Rules.GUN_TURN_RATE)+2;
		// if the steps to cool gun down is too short, robot won't have enough steps to aim, so we must calculate aimSteps base on the estimated gunTurnAngle.
		int aimSteps = Math.max(gunCoolTime, estimatedAimSteps);

		PredictStateResult predictedAimedSource = PredictWrapper.predict(aimSteps, sourceState, battleField);
		PredictStateResult predictedAimedTarget = PredictWrapper.predictTargetPosition(aimSteps, targetState);
		
		PredictedAimAndFireResult result = new PredictedAimAndFireResult();
		result.setBeginSource(sourceState);
		result.setBeginTarget(targetState);
		result.setTime(time);
		
		RawEstimateAimResult firstAimEstimation = result.getFirstAimEstimation();
		firstAimEstimation.setGunTurnRightDirection(estimatedGunTurnRightAngle);
		firstAimEstimation.setAimSteps(aimSteps);
		firstAimEstimation.setSource(predictedAimedSource);
		firstAimEstimation.setTarget(predictedAimedTarget);
		
		PredictedFirePoint bestPoint = predictPossibleFirePointsWithSmallDifferentAngle(maxPower, targetState, predictedAimedSource, predictedAimedTarget);
		if (bestPoint != null){
			result.getFireResult().getFindingBestPointResult().setBestPoint(bestPoint);
		}else{
			List<PredictedFirePoint> availabelFireTargetPoints = predictAvailabelFireTargetPoints(predictedAimedSource, predictedAimedTarget);// This
			FindingBestFirePointResult findResult = findBestPointToTarget(availabelFireTargetPoints, predictedAimedTarget);// Note:
			result.getFireResult().setAvailableFirePoints(availabelFireTargetPoints);
			result.getFireResult().setFindingBestPointResult(findResult);
			bestPoint = findResult.getBestPoint();
		}
		
		PredictedAimResult aimResult = result.getAimResult();
		aimResult.setAimSteps(aimSteps);
		aimResult.setSource(predictedAimedSource);
		if (bestPoint != null){
			double gunTurnRightDirection = MathUtils.calculateTurnRightDirectionToTarget(sourceGunHeading, predictedAimedSource.getX(), predictedAimedSource.getY(), bestPoint.x, bestPoint.y);
			aimResult.setGunTurnRightDirection(gunTurnRightDirection);
			aimResult.setFiredTarget(bestPoint);
			
			result.setWaitForBetterAim(false);
		}else{
			result.setWaitForBetterAim(true);
		}
		return result;
	}


	private FindingBestFirePointResult findBestPointToTarget(List<PredictedFirePoint> possibleBulletHitTargetPoints, PredictStateResult predictedAimedTarget) {
		if (predictedAimedTarget.isStandStill()){
			FindingBestFirePointResult rs = new FindingBestFirePointResult();
			PredictedFirePoint nearestPoint = findBestPointToTargetStanding(possibleBulletHitTargetPoints, predictedAimedTarget);
			rs.setBestPoint(nearestPoint);
			rs.setImpossiblePoints(new ArrayList<PredictedFirePoint>());
			rs.setNearestPoints(new ArrayList<PredictedFirePoint>());
			return rs;
		}else{
			return findBestPointToTargetMoving(possibleBulletHitTargetPoints, predictedAimedTarget);
		}
    }
	
	
	private FindingBestFirePointResult findBestPointToTargetMoving(List<PredictedFirePoint> possibleBulletHitTargetPoints, PredictStateResult predictedAimedTarget) {
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
	private PredictedFirePoint findBestPointToTargetFromNearestPoints(List<PredictedFirePoint> nearestPoints, LineSegment targetCurrentMoveLine, PredictStateResult predictedAimedTarget) {
		List<PredictedFirePoint> pointsByPower03 = new ArrayList<>();
		List<PredictedFirePoint> pointsByPower02 = new ArrayList<>();
		List<PredictedFirePoint> pointsByPower01 = new ArrayList<>();
	    for (PredictedFirePoint predictedFiredPoint : nearestPoints) {
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
	    	PredictedFirePoint bestPointByPower03 = pointsByPower03.get(0);
	    	if (bestPointByPower03.getFireSteps() > 0 && GunHelper.isShouldFireBySteps(GunHelper.BULLET_POWER_03, bestPointByPower03.getFireSteps())){
	    		return bestPointByPower03;
	    	}
	    }
	    if (pointsByPower02.size() > 0){
	    	shortByFireSteps(pointsByPower02);
	    	PredictedFirePoint bestPointByPower02 = pointsByPower02.get(0);
	    	if (bestPointByPower02.getFireSteps() > 0 && GunHelper.isShouldFireBySteps(GunHelper.BULLET_POWER_02, bestPointByPower02.getFireSteps())){
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
	private void shortByFireSteps(List<PredictedFirePoint> points) {
	    Comparator<PredictedFirePoint> comparator = new Comparator<PredictedFirePoint>(){
			@Override
            public int compare(PredictedFirePoint pA, PredictedFirePoint pB) {
				return pA.getFireSteps() - pB.getFireSteps();				
            }
	    };
	    Collections.sort(points, comparator);
    }

	private FindingBestFirePointResult findNearestPointsToTargetMovement(List<PredictedFirePoint> availableFireTargetPoints, PredictStateResult predictedAimedTarget) {
		FindingBestFirePointResult result = new FindingBestFirePointResult();
		List<PredictedFirePoint> possiblePoints = new ArrayList<>();
		List<PredictedFirePoint> nearestPoints = new ArrayList<>();
		
		List<PredictedFirePoint> allImpossiblePoints = new ArrayList<>();
		List<PredictedFirePoint> outsideBattlePoints = new ArrayList<>();
		List<PredictedFirePoint> impossibleAnglePoints = new ArrayList<>();
		List<PredictedFirePoint> tooFarPoints = new ArrayList<>();
		
		LineSegment targetCurrentMoveLine= new LineSegment(predictedAimedTarget.getPosition(), predictedAimedTarget.getMoveAngle(),1000);
		double targetCurrentMoveAngle = predictedAimedTarget.getMoveAngle();
		
		for (PredictedFirePoint predictedFiredPoint : availableFireTargetPoints) {
			LineSegment targetMoveToHitFiredLine= new LineSegment(predictedAimedTarget.getPosition(), predictedFiredPoint);
			double targetMoveToHitFiredAngle = targetMoveToHitFiredLine.reckonAngle();
			if (Math.abs(targetCurrentMoveAngle - targetMoveToHitFiredAngle) > MAX_DIFFERENT_TARGET_MOVE_ANGLE && !MathUtils.close(predictedFiredPoint, predictedAimedTarget.getPosition())) {
				impossibleAnglePoints.add(predictedFiredPoint);
				allImpossiblePoints.add(predictedFiredPoint);
				continue;
			};
			if (!isInsideBattleField(predictedFiredPoint)){
				outsideBattlePoints.add(predictedFiredPoint);
				allImpossiblePoints.add(predictedFiredPoint);
				//Only for debuging
				double testDistance = MathUtils.distance(targetCurrentMoveLine, predictedFiredPoint);
				if (testDistance < 15 && isInsideBattleField(predictedFiredPoint)){
					System.out.println("Debug somethign wrong");
				}
				predictedFiredPoint.setDistanceToTargetMove(testDistance);
				
				continue;
			}else{
				predictedFiredPoint.setDistanceToTargetMove(MathUtils.distance(targetCurrentMoveLine, predictedFiredPoint));
				if (predictedFiredPoint.getDistanceToTargetMove() > MAX_DISTANCE_TO_TARGET_MOVE){
					tooFarPoints.add(predictedFiredPoint);
					allImpossiblePoints.add(predictedFiredPoint);
					continue;
				}
			}
			
			possiblePoints.add(predictedFiredPoint);
	       
        }
		//Short possiblePoints by distance
		shortByDistance(possiblePoints, targetCurrentMoveLine);
		if (possiblePoints.size() > FILTER_NEAERST_POINTS_COUNT){
			nearestPoints = possiblePoints.subList(0, FILTER_NEAERST_POINTS_COUNT);
		}else{
			nearestPoints.addAll(possiblePoints);
		}
		
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
	
	private boolean isInsideBattleField(Point point) {
		double x = point.getX();
		double y = point.getY();
	    return (x >= 0 && x <= battleField.getWidth() && y >= 0 && y <= battleField.getHeight());
    }

	private void shortByDistance(List<PredictedFirePoint> possiblePoints, final LineSegment targetCurrentMoveLine) {
	    Comparator<PredictedFirePoint> comparator = new Comparator<PredictedFirePoint>(){
			@Override
            public int compare(PredictedFirePoint pA, PredictedFirePoint pB) {
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

	private PredictedFirePoint findBestPointToTargetStanding(List<PredictedFirePoint> possibleBulletHitTargetPoints, PredictStateResult predictedAimedTarget) {
		double nearestDistance = Double.MAX_VALUE;
		PredictedFirePoint nearestPoint = null;
		for (PredictedFirePoint predictedFiredPoint : possibleBulletHitTargetPoints) {
	        double distance = MathUtils.distance(predictedFiredPoint, predictedAimedTarget.getPosition());
	        if (distance < nearestDistance){
	        	nearestDistance = distance;
	        	nearestPoint = predictedFiredPoint;
	        }
        }
		return nearestPoint;
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
	private PredictedFirePoint predictPossibleFirePointsWithSmallDifferentAngle(int maxPower, 
			FullRobotState targetState, PredictStateResult predictedAimedSource, PredictStateResult predictedAimedTarget) {
		double sourceAndTargetDistance = MathUtils.distance(predictedAimedSource.getPosition(), predictedAimedTarget.getPosition());
		double targetMoveAngle = predictedAimedTarget.getMoveAngle();
		double aimAngle = MathUtils.absoluteBearing(predictedAimedSource.getX(), predictedAimedSource.getY(), predictedAimedTarget.getX(), predictedAimedTarget.getY());
		double differentAngle = Math.abs(aimAngle - targetMoveAngle) % 180;//It's not important which A - B or B - A, negative or positive is not important.
		double absCosDifferentAngle = Math.abs(Math.cos(Math.toRadians(differentAngle)));
		if (absCosDifferentAngle < MIN_DIFFERENT_MOVE_AND_AIM_COS) {
			return null;
		}
		
		for (int firePower = maxPower; firePower >= GunHelper.BULLET_POWER_02; firePower--) {
			PredictedFirePoint predictPoint = predictPossibleFirePointsByPowerWithSmallDifferentAngle(firePower, sourceAndTargetDistance, predictedAimedSource, predictedAimedTarget);
			if (GunHelper.isShouldFireBySteps(firePower, predictPoint.getFireSteps())){
				return predictPoint;
			}
        }
		return predictPossibleFirePointsByPowerWithSmallDifferentAngle(GunHelper.BULLET_POWER_01, sourceAndTargetDistance, predictedAimedSource, predictedAimedTarget);		
	}
	/**
	 * If target's moving direction and our robot aiming direction is very the same, use this method
	 * @param firePower
	 * @param sourceAndTargetDistance raw estimate source and target distance. It's usally the distance of aimedSource and aimedTarget (target position when source is aimed, not target when it will get fired)
	 * @param predictedAimedSource
	 * @param predictedAimedTarget
	 * @return 
	 */
	private PredictedFirePoint predictPossibleFirePointsByPowerWithSmallDifferentAngle(
			double firePower, double sourceAndTargetDistance, 
			PredictStateResult predictedAimedSource, PredictStateResult predictedAimedTarget) {
		//aim to absolutely by linear target point
		double bulletSpeed = Rules.getBulletSpeed(firePower);
		int estimateFireSteps = (int)Math.ceil(sourceAndTargetDistance / bulletSpeed);
		
		PredictStateResult predictedFiredTarget = PredictWrapper.predictTargetPosition(estimateFireSteps, predictedAimedTarget);
		double correctDistance;
		int correctFireSteps;
		if (isInsideBattleField(predictedFiredTarget.getPosition())){
			correctDistance = MathUtils.distance(predictedAimedSource.getPosition(), predictedFiredTarget.getPosition());
			correctFireSteps = (int)Math.ceil(correctDistance / bulletSpeed);	 
		}else{
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
	/**
	 * @param firePower
	 * @param sourceAndTargetDistance
	 * @param predictedAimedSource
	 * @param predictedAimedTarget
	 *            this target is not stand still. If it's stand still, never
	 *            call this method.
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

	
	public Robot getRobot() {
	    return robot;
    }

	public void setRobot(Robot robot) {
	    this.robot = robot;
    }
}
