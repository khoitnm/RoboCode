package org.tnmk.robocode.common.predictor.self;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.tnmk.robocode.common.helper.BattleField;
import org.tnmk.robocode.common.helper.GunHelper;
import org.tnmk.robocode.common.helper.GunHelper.AimAndFireResult;
import org.tnmk.robocode.common.helper.MoveHelper;
import org.tnmk.robocode.common.math.Circle;
import org.tnmk.robocode.common.math.LineSegment;
import org.tnmk.robocode.common.math.MathUtils;
import org.tnmk.robocode.common.math.Point;

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

	public PredictedAimAndFireSuccess predictBestStepsToAimAndFire(double gunCoolRate, double sourceGunHeat, double maxPower, double sourceGunHeading, RobotState sourceState, RobotState targetState) {
		// Robot will aim while waiting for the gun cool down. So the steps to
		// aim is equals to the steps to cool gun down.
		int gunCoolTime = (int) Math.ceil(sourceGunHeat / gunCoolRate) + 1;
		double estimatedGunTurnRightAngle = MathUtils.calculateTurnRightAngleToTarget(sourceGunHeading, sourceState.getX(), sourceState.getY(), targetState.getX(), targetState.getY());
		int estimatedAimSteps = (int)Math.ceil(Math.abs(estimatedGunTurnRightAngle)/Rules.GUN_TURN_RATE)+2;
		// if the steps to cool gun down is too short, robot won't have enough steps to aim, so we must calculate aimSteps base on the estimated gunTurnAngle.
		int aimSteps = Math.max(gunCoolTime, estimatedAimSteps);

		PredictStateResult predictedAimedSource = PredictWrapper.predict(aimSteps, sourceState, battleField);
		PredictStateResult predictedAimedTarget = PredictWrapper.predictTargetPosition(aimSteps, targetState);
		//PredictWrapper.predict(aimSteps, targetState, battleField);
		
		PredictedAimAndFireSuccess result = new PredictedAimAndFireSuccess();
		PredictedFiredPoint bestPoint = predictPossibleFirePointsWithSmallDifferentAngle(maxPower, aimSteps, targetState, predictedAimedSource, predictedAimedTarget);
		if (bestPoint != null){
			FindNearestPointToTargetMovementResult findResult = new FindNearestPointToTargetMovementResult();
			result.findNearestPointToTargetMovementResult = findResult;
		}else{
			List<PredictedFiredPoint> possibleBulletHitTargetPoints = predictPossibleBulletHitTargetPoints(predictedAimedSource, predictedAimedTarget);// This
			FindNearestPointToTargetMovementResult findResult = findBestPointToTarget(possibleBulletHitTargetPoints, predictedAimedTarget);// Note:
			result.possibleBulletHitTargetPoints = possibleBulletHitTargetPoints;
			result.findNearestPointToTargetMovementResult = findResult;
			bestPoint = findResult.getBestPoint();
		}
		
		result.aimAndFire = new AimAndFireResult();
		result.aimAndFire.setAimSteps(aimSteps);
		
		result.predictedAimedSource = predictedAimedSource;
		result.predictedAimedTarget = predictedAimedTarget;
		result.predictedFiredTarget = PredictWrapper.predict(result.aimAndFire.getTotalSteps(), targetState, battleField);
		
		if (bestPoint != null){
			result.aimAndFire.setFireSteps(bestPoint.fireSteps);
			double predictedDistance = MathUtils.distance(predictedAimedSource.getPoint(), bestPoint);
			result.aimAndFire.setDistance(predictedDistance);
			double turnRightAngle = MathUtils.calculateTurnRightAngleToTarget(sourceGunHeading, predictedAimedSource.getX(), predictedAimedSource.getY(), bestPoint.x, bestPoint.y);
			result.aimAndFire.setTurnRightAngle(turnRightAngle);
			result.bestPredictPoint = bestPoint;
			result.waitForBetterAim = false;
		}else{
			result.waitForBetterAim = true;
		}
		return result;
	}


	private FindNearestPointToTargetMovementResult findBestPointToTarget(List<PredictedFiredPoint> possibleBulletHitTargetPoints, PredictStateResult predictedAimedTarget) {
		if (predictedAimedTarget.isStandStill()){
			FindNearestPointToTargetMovementResult rs = new FindNearestPointToTargetMovementResult();
			PredictedFiredPoint nearestPoint = findBestPointToTargetStanding(possibleBulletHitTargetPoints, predictedAimedTarget);
			rs.bestPoint = nearestPoint;
			rs.impossiblePoints = Collections.emptyList();
			rs.nearestPoints = Collections.emptyList();
			return rs;
		}else{
			return findBestPointToTargetMoving(possibleBulletHitTargetPoints, predictedAimedTarget);
		}
    }
	
	
	private FindNearestPointToTargetMovementResult findBestPointToTargetMoving(List<PredictedFiredPoint> possibleBulletHitTargetPoints, PredictStateResult predictedAimedTarget) {
		FindNearestPointToTargetMovementResult nearestPointsResult = findNearestPointsToTargetMovement(possibleBulletHitTargetPoints, predictedAimedTarget);
		nearestPointsResult.bestPoint = findBestPointToTargetFromNearestPoints(nearestPointsResult.nearestPoints, nearestPointsResult.targetCurrentMoveLine, predictedAimedTarget);
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
	        if (predictedFiredPoint.firePower == GunHelper.BULLET_POWER_03){
	        	pointsByPower03.add(predictedFiredPoint);
	        }else if (predictedFiredPoint.firePower == GunHelper.BULLET_POWER_02){
	        	pointsByPower02.add(predictedFiredPoint);
	        }else{
	        	pointsByPower01.add(predictedFiredPoint);
	        }
        }
	    if (pointsByPower03.size() > 0){
	    	shortByFireSteps(pointsByPower03);
	    	PredictedFiredPoint bestPointByPower03 = pointsByPower03.get(0);
	    	if (bestPointByPower03.fireSteps > 0 && bestPointByPower03.fireSteps <= GunHelper.FireDistance.STEPS_VERY_SHORT.getNumValue()){
	    		return bestPointByPower03;
	    	}
	    }
	    if (pointsByPower02.size() > 0){
	    	shortByFireSteps(pointsByPower02);
	    	PredictedFiredPoint bestPointByPower02 = pointsByPower02.get(0);
	    	if (bestPointByPower02.fireSteps > 0 && bestPointByPower02.fireSteps <= GunHelper.FireDistance.STEPS_SHORT.getNumValue()){
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

	private FindNearestPointToTargetMovementResult findNearestPointsToTargetMovement(List<PredictedFiredPoint> possibleBulletHitTargetPoints, PredictStateResult predictedAimedTarget) {
		FindNearestPointToTargetMovementResult result = new FindNearestPointToTargetMovementResult();
		List<PredictedFiredPoint> nearestPoints = new ArrayList<>();
		List<PredictedFiredPoint> impossiblePoints = new ArrayList<>();
		List<PredictedFiredPoint> possiblePoints = new ArrayList<>();
		LineSegment targetCurrentMoveLine= new LineSegment(predictedAimedTarget.getPoint(), predictedAimedTarget.getMoveAngle(),1000);
		double targetCurrentMoveAngle = predictedAimedTarget.getMoveAngle();
		
		for (PredictedFiredPoint predictedFiredPoint : possibleBulletHitTargetPoints) {
			LineSegment targetMoveToHitFiredLine= new LineSegment(predictedAimedTarget.getPoint(), predictedFiredPoint);
			double targetMoveToHitFiredAngle = targetMoveToHitFiredLine.reckonAngle();
			if (!isInsideBattleField(predictedFiredPoint) || 
				(Math.abs(targetCurrentMoveAngle - targetMoveToHitFiredAngle) > MAX_DIFFERENT_TARGET_MOVE_ANGLE && !MathUtils.close(predictedFiredPoint, predictedAimedTarget.getPoint())) ){
				impossiblePoints.add(predictedFiredPoint);
				continue;
			}else{
				predictedFiredPoint.distanceToTargetMove = MathUtils.distance(targetCurrentMoveLine, predictedFiredPoint);
				if (predictedFiredPoint.distanceToTargetMove > MAX_DISTANCE_TO_TARGET_MOVE){
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
		
		result.impossiblePoints = impossiblePoints;
		result.targetCurrentMoveAngle = targetCurrentMoveAngle;
		result.targetCurrentMoveLine = targetCurrentMoveLine;
		result.nearestPoints = nearestPoints;
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
	        double distance = MathUtils.distance(predictedFiredPoint, predictedAimedTarget.getPoint());
	        if (distance < nearestDistance){
	        	nearestDistance = distance;
	        	nearestPoint = predictedFiredPoint;
	        }
        }
		return nearestPoint;
	}

	private List<PredictedFiredPoint> predictPossibleBulletHitTargetPoints(PredictStateResult predictedAimedSource, PredictStateResult predictedAimedTarget) {
		List<PredictedFiredPoint> results = new ArrayList<>();
		double aimedDistance = MathUtils.distance(predictedAimedSource.getPoint(), predictedAimedTarget.getPoint());

		if (predictedAimedTarget.isStandStill()) {
			PredictedFiredPoint result = new PredictedFiredPoint();
			result.set(predictedAimedTarget.getPoint().x, predictedAimedTarget.getPoint().y);
			double fireDistance = MathUtils.distance(predictedAimedSource.getPoint(), result);
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
			RobotState targetState, PredictStateResult predictedAimedSource, PredictStateResult predictedAimedTarget) {
		
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
			Circle bulletMoveCircle = new Circle(predictedAimedSource.getPoint(), bulletMoveDistance);
			Circle targetMoveCircle = new Circle(predictedAimedTarget.getPoint(), targetMoveDistance);
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

	/**
	 * @author Khoi The point where bullet hit target
	 */
	public class PredictedFiredPoint extends Point {
		private Double distanceToTargetMove = null;
		/**
		 * This is only the bullet fly steps, not aim & fire (bullet fly) steps
		 */
		private int fireSteps;
		private int firePower;

		public int getFireSteps() {
			return fireSteps;
		}

		public void setFireSteps(int fireSteps) {
			this.fireSteps = fireSteps;
		}

		public int getFirePower() {
			return firePower;
		}

		public void setFirePower(double firePower) {
			this.firePower = (int)Math.round(firePower);
		}

		protected Double getDistanceToTargetMove() {
	        return distanceToTargetMove;
        }

		protected void setDistanceToTargetMove(Double distanceToTargetMove) {
	        this.distanceToTargetMove = distanceToTargetMove;
        }

	}

	public static class PredictedAimAndFireSuccess {
		private FindNearestPointToTargetMovementResult findNearestPointToTargetMovementResult;

		/**
		 * This prediction was cancel, we will predict again and fire base on the new prediction.
		 * The reason maybe it's too far away from target.
		 */
		private boolean waitForBetterAim = false;
		/**
		 * The time when prediction begin. 
		 */
		private long time;
		
		private RobotState currentSource;
		private RobotState currentTarget;
		private List<PredictedFiredPoint> possibleBulletHitTargetPoints;
		private PredictedFiredPoint bestPredictPoint;
		/**
		 * Status of source after aimed, not when bullet hit target
		 */
		private PredictStateResult predictedAimedSource;
		private PredictStateResult predictedAimedTarget;
		/**
		 * Status of target when it's hit by bullet.
		 */
		private PredictStateResult predictedFiredTarget;

		private AimAndFireResult aimAndFire;

		private boolean success;

		public long getFinishAimTime(){
			return time + aimAndFire.getAimSteps();
		}
		public long getFinishAllTime() {
			return time + aimAndFire.getTotalSteps();
        }
		public AimAndFireResult getAimAndFire() {
			return aimAndFire;
		}

		public void setAimAndFire(AimAndFireResult aimAndFire) {
			this.aimAndFire = aimAndFire;
		}

		public boolean isSuccess() {
			return success;
		}

		public void setSuccess(boolean success) {
			this.success = success;
		}

		public PredictStateResult getPredictedAimedSource() {
			return predictedAimedSource;
		}

		public void setPredictedAimedSource(PredictStateResult predictedAimedSource) {
			this.predictedAimedSource = predictedAimedSource;
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

		public RobotState getCurrentTarget() {
	        return currentTarget;
        }

		public void setCurrentTarget(RobotState currentTarget) {
	        this.currentTarget = currentTarget;
        }

		public RobotState getCurrentSource() {
	        return currentSource;
        }

		public void setCurrentSource(RobotState currentSource) {
	        this.currentSource = currentSource;
        }
		public PredictStateResult getPredictedAimedTarget() {
	        return predictedAimedTarget;
        }
		public void setPredictedAimedTarget(PredictStateResult predictedAimedTarget) {
	        this.predictedAimedTarget = predictedAimedTarget;
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
		public FindNearestPointToTargetMovementResult getFindNearestPointToTargetMovementResult() {
	        return findNearestPointToTargetMovementResult;
        }
		public void setFindNearestPointToTargetMovementResult(FindNearestPointToTargetMovementResult findNearestPointToTargetMovementResult) {
	        this.findNearestPointToTargetMovementResult = findNearestPointToTargetMovementResult;
        }
	}
	public static class FindNearestPointToTargetMovementResult{
		private List<PredictedFiredPoint> nearestPoints;
		public List<PredictedFiredPoint> getNearestPoints() {
			return nearestPoints;
		}
		public void setNearestPoints(List<PredictedFiredPoint> nearestPoints) {
			this.nearestPoints = nearestPoints;
		}
		public PredictedFiredPoint getBestPoint() {
			return bestPoint;
		}
		public void setBestPoint(PredictedFiredPoint bestPoint) {
			this.bestPoint = bestPoint;
		}
		private List<PredictedFiredPoint> impossiblePoints = new ArrayList<>();
		private PredictedFiredPoint bestPoint;
		private LineSegment targetCurrentMoveLine;
		private double targetCurrentMoveAngle;
		
		public double getTargetCurrentMoveAngle() {
	        return targetCurrentMoveAngle;
        }
		public void setTargetCurrentMoveAngle(double targetCurrentMoveAngle) {
	        this.targetCurrentMoveAngle = targetCurrentMoveAngle;
        }
		public LineSegment getTargetCurrentMoveLine() {
	        return targetCurrentMoveLine;
        }
		public void setTargetCurrentMoveLine(LineSegment targetCurrentMoveLine) {
	        this.targetCurrentMoveLine = targetCurrentMoveLine;
        }

		public List<PredictedFiredPoint> getImpossiblePoints() {
	        return impossiblePoints;
        }
		public void setImpossiblePoints(List<PredictedFiredPoint> impossiblePoints) {
	        this.impossiblePoints = impossiblePoints;
        }
	}
	public Robot getRobot() {
	    return robot;
    }

	public void setRobot(Robot robot) {
	    this.robot = robot;
    }
}
