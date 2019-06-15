package org.tnmk.robocode.common.gun.briareos;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import org.tnmk.common.math.AngleUtils;
import org.tnmk.common.math.LineSegment;
import org.tnmk.common.math.Point;
import org.tnmk.robocode.common.constant.FireStatus;
import org.tnmk.robocode.common.gun.predictor.self.LinearPredictStrategy;
import org.tnmk.robocode.common.gun.predictor.self.PredictManager;
import org.tnmk.robocode.common.gun.predictor.self.model.FindingBestFirePointResult;
import org.tnmk.robocode.common.gun.predictor.self.model.PredictedAimAndFireResult;
import org.tnmk.robocode.common.gun.predictor.self.model.PredictedFirePoint;
import org.tnmk.robocode.common.helper.FireByDistance;
import org.tnmk.robocode.common.helper.GunHelper;
import org.tnmk.robocode.common.helper.RobotStateConverter;
import org.tnmk.robocode.common.model.BaseRobotState;
import org.tnmk.robocode.common.model.FullRobotState;
import org.tnmk.robocode.common.model.PredictedTarget;
import org.tnmk.robocode.common.model.TargetSet;
import org.tnmk.robocode.common.paint.PaintHelper;
import org.tnmk.robocode.common.robot.InitiableRun;
import org.tnmk.robocode.common.robot.LoopableRun;
import org.tnmk.robocode.common.robot.OnScannedRobotControl;
import org.tnmk.robocode.common.robot.tnmkmodernrobot.Config;
import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;

/**
 * The code is blindly copied from {@link Briareos}, so it will be a messed, but it works!
 */
public class BriareosGun implements InitiableRun, LoopableRun, OnScannedRobotControl {
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
    private static final int LINE_SIZE = 20;


    private final AdvancedRobot robot;
//    private final MovementContext movementContext;


    private boolean finishPrepared = false;
    private FireStatus currentFireStatus = FireStatus.STAND_STILL;
    private TargetSet<PredictedTarget> aliveTargets = new TargetSet<>();
    /**
     * Note: this target may have bestFirePoint or not, but we always can aim to it.
     */
    private PredictedTarget aimingTarget = null;
    protected PredictManager predictHelper;
    protected FireByDistance fireByDistance;
    protected long paintedTime = -1;
    private Config config = new Config();


    public BriareosGun(AdvancedRobot robot) {
        this.robot = robot;
//        this.movementContext = movementContext;
    }

    @Override
    public void runInit() {
        fireByDistance = new FireByDistance(robot);
        predictHelper = new PredictManager(robot);
        finishPrepared = true;
    }
    @Override
    public void runLoop() {
        if (isFinishAim()) {
            if (canFire()) {
                fireToAimingTargetAtRightTime();// Don't need to see target at this moment, just fire at predicted position.
            }
        }
    }
    @Override
    public void onScannedRobot(ScannedRobotEvent scannedRobotEvent) {
        long begin = System.currentTimeMillis();
        // System.out.println("OnScannedRobot Begin: "+scannedRobotEvent.getName()+" - "+getTime());
        if (!finishPrepared) {
            return;
        }

        BaseRobotState targetStatus = RobotStateConverter.toTargetState(robot, scannedRobotEvent);
        PredictedTarget target = this.updateStateToCorrespondingTarget(targetStatus);
        PredictedAimAndFireResult newPredicted = predictTarget(scannedRobotEvent);
        // begin = printRunTime("\t Predicted Target", begin);
        target.setPredicted(newPredicted);

        if (isBetterPredictThanAimingTarget(target)) {
            // Should move to target?
            // If move, we have to predict aiming again.
            updateAimingTarget(target);
            setAimTo(target);
            // begin = printRunTime("\t Stared Aiming Target", begin);
        }// else, let it continue aiming to current target.

        printNewPredict(newPredicted);
        // begin = printRunTime("OnScannedRobot End: "+scannedRobotEvent.getName(), begin);
    }

    /**
     * If we didn't found any best fire point, it won't fire. If this is not the right time to fire, it won't fire at all. And it will fire at next tick.
     */
    protected void fireToAimingTargetAtRightTime() {
        if (aimingTarget == null || !aimingTarget.getPredicted().isFoundBestPoint()) {
            return;
        }
        PredictedAimAndFireResult predicted = aimingTarget.getPredicted();
        if (robot.getTime() == predicted.getAimedTime() && !predicted.isWaitForBetterAim()) {
            Color bulletColor = predicted.getPredictStrategy().getPredictBulletColor();
            // TODO set bullet color make robot slow a step (tick)???
            robot.setBulletColor(bulletColor);
            robot.setFire(predicted.getBestFirePoint().getFirePower());

            currentFireStatus = FireStatus.STARTED_FIRE;
        }
    }

    public PredictedAimAndFireResult predictTarget(ScannedRobotEvent targetEvent) {
        /**
         * The code is copied from {@link Briareos}, and it has real direction, but we don't have it here.
         * So we use fakeMoveDirection value which is not important to predict enemy
         * We actually can get that value from MovementContext.getDirection() but why on the earth we need moveContext for a Gun strategy?
         */
        int fakeMoveDirection = 1;
        FullRobotState thisState = RobotStateConverter.toRobotState(robot, fakeMoveDirection);
        BaseRobotState targetState = RobotStateConverter.toTargetState(robot, targetEvent);
        int maxPower = GunHelper.reckonMaxNecessaryPower(targetEvent.getEnergy());
        return predictHelper.predictBestStepsToAimAndFire(robot.getTime(), robot.getGunCoolingRate(), robot.getGunHeat(), maxPower, robot.getGunHeading(), thisState, targetState);
    }

    protected PredictedTarget updateStateToCorrespondingTarget(BaseRobotState targetState) {
        PredictedTarget target = new PredictedTarget(targetState);
        this.aliveTargets.set(target);
        return target;
    }

    /**
     * @param bearingFromRobotHeading this is the relative bearing from our robot's body heading to target.
     */
    protected void setTurnRadarToTarget(double bearingFromRobotHeading) {
        double turnRightDegree = AngleUtils.normalizeDegree(robot.getHeading() - robot.getRadarHeading() + bearingFromRobotHeading);
        robot.setTurnRadarRight(turnRightDegree);
    }

    /**
     * shorter aim time
     *
     * @param newTarget
     * @return
     */
    protected boolean isBetterPredictThanAimingTarget(PredictedTarget newTarget) {
        // If never aim before, or that aiming was old, use the new one.
        if (aimingTarget == null || aimingTarget.getPredicted().getAimedTime() < robot.getTime()) {
            return true;
        }
        PredictedAimAndFireResult aimingTargetPredicted = aimingTarget.getPredicted();
        PredictedAimAndFireResult newTargetPredicted = newTarget.getPredicted();

        if (!aimingTargetPredicted.isFoundBestPoint()) {
            if (newTargetPredicted.isFoundBestPoint()) {
                return true;
            } else {
                return (newTargetPredicted.getAimedTime() < aimingTargetPredicted.getAimedTime());
            }
        } else {
            if (!newTargetPredicted.isFoundBestPoint()) {
                return false;
            } else {
                return (newTargetPredicted.getAimedTime() < aimingTargetPredicted.getAimedTime());
            }
        }
    }

    protected void updateAimingTarget(PredictedTarget target) {
        this.setAimingTarget(target);
    }

    protected void setAimTo(PredictedTarget target) {
        double gunTurnRightDirection;
        if (target.getPredicted().isFoundBestPoint()) {
            gunTurnRightDirection = target.getPredicted().getAimResult().getGunTurnRightDirection();
        } else {
            gunTurnRightDirection = target.getPredicted().getFirstAimEstimation().getGunTurnRightDirection();
        }
        robot.setTurnGunRight(gunTurnRightDirection);
        currentFireStatus = FireStatus.STARTED_AIM;
    }

    /**
     * @return aiming target
     */
    protected boolean isAiming() {
        return currentFireStatus == FireStatus.STARTED_AIM;
    }

    protected boolean isFinishAim() {
        if (robot.getGunTurnRemaining() == 0) {
            currentFireStatus = FireStatus.AIMED;
        }
        return currentFireStatus.getNumValue() >= FireStatus.AIMED.getNumValue();
    }

    protected boolean isStartedFire() {
        return currentFireStatus.getNumValue() >= FireStatus.STARTED_FIRE.getNumValue();
    }


    public void setAimingTarget(PredictedTarget aimingTarget) {
        // this.aimingTarget = SerializationUtils.clone(aimingTarget);
        // TODO copy properties.
        this.aimingTarget = new PredictedTarget(aimingTarget.getState());
        this.aimingTarget.setPredicted(aimingTarget.getPredicted());
    }

    protected void printNewPredict(PredictedAimAndFireResult newPredicted) {
        if (!newPredicted.getPredictStrategy().getClass().equals(LinearPredictStrategy.class)) {
            String msg = "Debug predict not linear";
        }
        String currentPredictText = "";
        if (aimingTarget != null && aimingTarget.getPredicted() != null) {
            Long currentFiredTime = aimingTarget.getPredicted().getFiredTime() != null ? aimingTarget.getPredicted().getFiredTime() : 0;
            currentPredictText = String.format("Current Predict: %s\t%s. AimedTime: %s, TotalTime: %s", robot.getTime(), aimingTarget.getPredicted().getPredictStrategy().getClass().getSimpleName(), aimingTarget.getPredicted().getAimedTime(), currentFiredTime);
        }

        String newPredictText = "";
        if (newPredicted != null) {
            Long newFiredTime = newPredicted.getFiredTime() != null ? newPredicted.getFiredTime() : 0;
            newPredictText = String.format("New Predict: %s\t%s. AimedTime: %s, TotalTime: %s", robot.getTime(), newPredicted.getPredictStrategy().getClass().getSimpleName(), newPredicted.getAimedTime(), newFiredTime);
        }
        paint(currentPredictText, newPredictText);
        paintPredict(newPredicted);
    }

    public void paint(String... msgs) {
        for (int line = 0; line < msgs.length; line++) {
            PaintHelper.paintText(robot.getGraphics(), msgs[line], 20, line);
        }
        paintedTime = robot.getTime();
    }

    public void paintPredict(PredictedAimAndFireResult predicted) {
        if (!getConfig().isPaintTargetPrediction())
            return;

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
        paintPoint(4, COLOR_PREDICTED_TARGET_AIMED, predicted.getFirstAimEstimation().getAimedTarget().getPosition(), null);

        if (predicted.isFoundBestPoint()) {
            paintPoint(5, COLOR_PREDICTED_TARGET_HIT, predicted.getAimResult().getFiredTarget(), "");
            paintPoint(5, COLOR_PREDICTED_TARGET_HIT, predicted.getBestFirePoint(), "p-" + predicted.getBestFirePoint().getFirePower() + "_");
        }
        paintPredictInfo(predicted);
        paintedTime = robot.getTime();
    }

    public void paintPredictInfo(PredictedAimAndFireResult predicted) {
        Graphics graphic = robot.getGraphics();
        graphic.setColor(Color.WHITE);
        graphic.drawString("Target heading: " + predicted.getBeginTarget().getHeading(), 0, 10);
        graphic.drawString("Target velocity: " + predicted.getBeginTarget().getVelocity(), 0, 30);
    }

    public Config getConfig() {
        return config;
    }

    public void paintLine(Color color, LineSegment line) {
        if (line == null) {
            return;
        }
        Graphics graphic = robot.getGraphics();
        graphic.setColor(color);
        graphic.drawLine((int) line.getPointA().x, (int) line.getPointA().y, (int) line.getPointB().x, (int) line.getPointB().y);
    }

    /**
     * @param pointSize
     * @param color
     * @param point
     * @param printText
     */
    @Deprecated
    public void paintPoint(int pointSize, Color color, Point point, String printText) {
        Graphics graphic = robot.getGraphics();
        graphic.setColor(color);
        if (printText != null) {
            graphic.drawString(printText + point, (int) point.x + pointSize, (int) point.y);
        }
        graphic.drawLine((int) point.x - pointSize, (int) point.y, (int) point.x + pointSize, (int) point.y);
        graphic.drawLine((int) point.x, (int) point.y - pointSize, (int) point.x, (int) point.y + pointSize);
    }

    /**
     * @param string
     * @param x
     * @param line
     * @deprecated replaced by {@link org.tnmk.robocode.common.paint.PaintHelper#paintText(Graphics, String, int, int)}
     */
    @Deprecated
    public void paintText(String string, int x, int line) {
        Graphics graphic = robot.getGraphics();
        graphic.setColor(Color.WHITE);
        graphic.drawString(string, x, (line + 1) * LINE_SIZE);
    }

    protected boolean canFire() {
        return robot.getGunHeat() == 0;
    }
}
