package org.tnmk.robocode.jimkirk;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import org.tnmk.robocode.common.helper.BattleField;
import org.tnmk.robocode.common.helper.FireByDistance;
import org.tnmk.robocode.common.helper.MoveHelper;
import org.tnmk.robocode.common.helper.RobotStateConverter;
import org.tnmk.robocode.common.helper.WallSmoothHelper;
import org.tnmk.robocode.common.math.LineSegment;
import org.tnmk.robocode.common.math.MathUtils;
import org.tnmk.robocode.common.math.Point;
import org.tnmk.robocode.common.model.FullRobotState;
import org.tnmk.robocode.common.predictor.self.PredictHelper;
import org.tnmk.robocode.common.predictor.self.model.FindingBestFirePointResult;
import org.tnmk.robocode.common.predictor.self.model.PredictedAimAndFireResult;
import org.tnmk.robocode.common.predictor.self.model.PredictedFirePoint;

import robocode.AdvancedRobot;
import robocode.Event;
import robocode.HitByBulletEvent;
import robocode.StatusEvent;

/**
 * @author Khoi With AdvancedRobot, the shooting time is different from basic Robot class.
 * 
 *         Term: + Bearing: the angle (degree) from pointA to pointB (or vectorA to vectorB). It can be an absolute bearing (compare to North axis) or relative bearing (compare to vectorA)
 */
public abstract class JimKirkBase extends AdvancedRobot {
	private static final Color COLOR_PREDICTED_TARGET_AIMED = Color.MAGENTA;
	private static final Color COLOR_PREDICTED_TARGET_HIT = Color.PINK;
	private static final Color COLOR_CURRENT_TARGET = Color.WHITE;
	private static final Color COLOR_CURRENT_SOURCE = Color.WHITE;

	// private static final int POINT_SIZE = 5;
	private static final Color COLOR_AVAILABLE_TARGET_POINT = Color.WHITE;
	private static final Color COLOR_AVAILABLE_TEST_TARGET_POINT = Color.YELLOW;
	private static final Color COLOR_POSSIBLE_TARGET_POINT = new Color(0, 255, 255);
	private static final Color COLOR_NEAREST_TARGET_POINT = Color.ORANGE;

	private static final Color COLOR_IMPOSSIBLE_TARGET_POINT = Color.GRAY;
	private static final Color COLOR_TOOFAR_TARGET_POINT = Color.GREEN;
	private static final Color COLOR_IMPOSSIBLE_ANGLE_TARGET_POINT = Color.RED;
	private static final Color COLOR_OUTSIDEBATTLE_TARGET_POINT = Color.BLACK;
	private static final Color COLOR_TARGET_MOVE_LINE = Color.DARK_GRAY;

	public static final Color ROBOT_BORDY_COLOR = new Color(174, 118, 77);// new Color(51, 153, 153);
	public static final Color ROBOT_RADAR_COLOR = new Color(211, 163, 126);// Color(117, 209, 209);
	public static final Color ROBOT_GUN_COLOR = new Color(51, 102, 102);
	public static final Color ROBOT_BULLET03_COLOR = new Color(250, 245, 90);
	public static final Color ROBOT_BULLET02_COLOR = new Color(255, 211, 50);
	public static final Color ROBOT_BULLET01_COLOR = new Color(255, 255, 210);

	protected BattleField battleField;
	protected FireByDistance fireByDistance;
	protected MoveHelper moveHelper;
	protected PredictHelper predictHelper;
	protected PredictedAimAndFireResult predicted;
	protected long painted = -1;

	public void init() {
		battleField = MoveHelper.createBattleField(this);
		moveHelper = new MoveHelper(this);
		fireByDistance = new FireByDistance(this);
		predictHelper = new PredictHelper(this);

		setBodyColor(ROBOT_BORDY_COLOR);
		setRadarColor(ROBOT_RADAR_COLOR);
		setGunColor(ROBOT_GUN_COLOR);
		// divorce radar movement from gun movement
		setAdjustRadarForGunTurn(true);
		setAdjustGunForRobotTurn(true);
	}
	public void avoidWallWhenNecessary(){
		FullRobotState robotState = RobotStateConverter.toRobotState(this);
		if (robotState.getX() > 700 || robotState.getY() > 550 || robotState.getX() < 120 || robotState.getY() < 120){
			System.out.println("Debug");
		}
		Double shouldTurnRightDirection = WallSmoothHelper.shouldAvoidWall(this.battleField, robotState);
		if (shouldTurnRightDirection != null){
			this.setTurnRight(shouldTurnRightDirection);
		}
	}
	public void paintPredict() {
		FindingBestFirePointResult findingBestPointResult = predicted.getFireResult().getFindingBestPointResult();
		List<PredictedFirePoint> impossibleTargetPoints = findingBestPointResult.getImpossiblePoints();
		List<PredictedFirePoint> impossibleAngleTargetPoints = findingBestPointResult.getImpossibleAnglePoints();
		List<PredictedFirePoint> outsideBattleTargetPoints = findingBestPointResult.getOutsideBattlePoints();
		List<PredictedFirePoint> tooFarTargetPoints = findingBestPointResult.getTooFarPoints();

		List<PredictedFirePoint> availableBulletHitTargetPoints = predicted.getFireResult().getAvailableFirePoints();
		List<PredictedFirePoint> availableTestPoints = findingBestPointResult.getAvailablePoints();
		List<PredictedFirePoint> possiblePoints = findingBestPointResult.getPossiblePoints();
		List<PredictedFirePoint> nearestPoints = findingBestPointResult.getNearestPoints();

		LineSegment targetMoveLine = findingBestPointResult.getTargetCurrentMoveLine();
		paintLine(COLOR_TARGET_MOVE_LINE, targetMoveLine);

		for (PredictedFirePoint point : availableBulletHitTargetPoints) {
			paintPoint(1, COLOR_AVAILABLE_TARGET_POINT, point, null);
		}
		for (PredictedFirePoint point : availableTestPoints) {
			paintPoint(2, COLOR_AVAILABLE_TEST_TARGET_POINT, point, null);
		}
		for (PredictedFirePoint point : possiblePoints) {
			paintPoint(3, COLOR_POSSIBLE_TARGET_POINT, point, null);
		}
		for (PredictedFirePoint point : nearestPoints) {
			paintPoint(3, COLOR_NEAREST_TARGET_POINT, point, null);
		}

		for (PredictedFirePoint point : impossibleTargetPoints) {
			paintPoint(2, COLOR_IMPOSSIBLE_TARGET_POINT, point, null);
		}
		for (PredictedFirePoint point : impossibleAngleTargetPoints) {
			paintPoint(2, COLOR_IMPOSSIBLE_ANGLE_TARGET_POINT, point, null);
		}
		for (PredictedFirePoint point : outsideBattleTargetPoints) {
			paintPoint(2, COLOR_OUTSIDEBATTLE_TARGET_POINT, point, null);
		}
		for (PredictedFirePoint point : tooFarTargetPoints) {
			paintPoint(2, COLOR_TOOFAR_TARGET_POINT, point, null);
		}

		// for (PredictedFirePoint nearestPoint : findingBestPointResult.getNearestPoints()) {
		// paintPoint(3, COLOR_NEAREST_TARGET_POINT, nearestPoint, null);
		// }

		paintPoint(2, COLOR_CURRENT_SOURCE, predicted.getBeginSource().getPosition(), "");
		paintPoint(4, COLOR_CURRENT_TARGET, predicted.getBeginTarget().getPosition(), null);
		paintPoint(4, COLOR_PREDICTED_TARGET_AIMED, predicted.getFirstAimEstimation().getTarget().getPosition(), null);

		if (predicted.getFireResult().getFindingBestPointResult().getBestPoint() != null) {
			paintPoint(5, COLOR_PREDICTED_TARGET_HIT, predicted.getAimResult().getFiredTarget(), "");
			if (predicted.getBestFirePoint() != null) {
				paintPoint(5, COLOR_PREDICTED_TARGET_HIT, predicted.getBestFirePoint(), "p-" + predicted.getBestFirePoint().getFirePower() + "_");
			}
		}
		paintPredictInfo();
		painted = getTime();
	}

	/**
	 * @Override
	 */
	public void setFire(double power) {
		if (power > 2) {
			setBulletColor(ROBOT_BULLET03_COLOR);
		} else if (power > 1) {
			setBulletColor(ROBOT_BULLET02_COLOR);
		} else {
			setBulletColor(ROBOT_BULLET01_COLOR);
		}
		super.setFire(power);
	}

	public void paintPredictInfo() {
		Graphics graphic = getGraphics();
		graphic.setColor(Color.WHITE);
		graphic.drawString("Target heading: " + predicted.getBeginTarget().getHeading(), 0, 10);
		graphic.drawString("Target velocity: " + predicted.getBeginTarget().getVelocity(), 0, 30);
	}

	public void paintLine(Color color, LineSegment line) {
		if (line == null) {
			return;
		}
		Graphics graphic = getGraphics();
		graphic.setColor(color);
		graphic.drawLine((int) line.getPointA().x, (int) line.getPointA().y, (int) line.getPointB().x, (int) line.getPointB().y);
	}

	public void paintPossiblePoint(int pointSize, Color color, PredictedFirePoint point, String printText) {
		Graphics graphic = getGraphics();
		graphic.setColor(color);
		String text = String.format("%d", point.getFireSteps());
		graphic.drawString(text, (int) point.x + pointSize, (int) point.y);
		graphic.drawLine((int) point.x - pointSize, (int) point.y, (int) point.x + pointSize, (int) point.y);
		graphic.drawLine((int) point.x, (int) point.y - pointSize, (int) point.x, (int) point.y + pointSize);
	}

	public void paintPoint(int pointSize, Color color, Point point, String printText) {
		Graphics graphic = getGraphics();
		graphic.setColor(color);
		if (printText != null) {
			graphic.drawString(printText + point, (int) point.x + pointSize, (int) point.y);
		}
		graphic.drawLine((int) point.x - pointSize, (int) point.y, (int) point.x + pointSize, (int) point.y);
		graphic.drawLine((int) point.x, (int) point.y - pointSize, (int) point.x, (int) point.y + pointSize);
	}

	/**
	 * @param bearingFromRobotHeading
	 *            this is the relative bearing from our robot's body heading to target.
	 */
	protected void setTurnRadarToTarget(double bearingFromRobotHeading) {
		double turnRightDegree = MathUtils.normalizeDegree(getHeading() - getRadarHeading() + bearingFromRobotHeading);
		setTurnRadarRight(turnRightDegree);
	}

	/**
	 * @param bearingFromRobotHeading
	 *            this is the relative bearing from our robot's body heading to target.
	 */
	protected void setTurnGunToTarget(double bearingFromRobotHeading) {
		double turnRightDegree = MathUtils.normalizeDegree(getHeading() - getGunHeading() + bearingFromRobotHeading);
		String msg = String.format("GUN Right(%s) - turnRemain:%s", turnRightDegree, getGunTurnRemaining());
		System.out.println(msg);
		setTurnGunRight(turnRightDegree);
	}

	protected void moveAwayFromTarget(Event e, double bearing) {
		setTurnLeft(90 - bearing);
	}

	/**
	 * We were hit! Turn perpendicular to the bullet, so our robot might avoid a future shot.
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		moveAwayFromTarget(e, e.getBearing());
	}

	protected boolean canFire() {
		return getGunHeat() == 0;
	}

}