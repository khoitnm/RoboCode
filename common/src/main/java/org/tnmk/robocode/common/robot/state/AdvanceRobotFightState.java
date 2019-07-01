package org.tnmk.robocode.common.robot.state;

public class AdvanceRobotFightState extends AdvanceRobotState {
//    private final RobotStatus robotStatus;
    private boolean isHitByBullet;
    private boolean isHitByEnemy;

//    public AdvanceRobotFightState(RobotStatus robotStatus) {
//        this.robotStatus = robotStatus;
//    }

    public boolean isHitByBullet() {
        return isHitByBullet;
    }

    public void setHitByBullet(boolean hitByBullet) {
        isHitByBullet = hitByBullet;
    }

    public boolean isHitByEnemy() {
        return isHitByEnemy;
    }

    public void setHitByEnemy(boolean hitByEnemy) {
        isHitByEnemy = hitByEnemy;
    }

//    public RobotStatus getRobotStatus() {
//        return robotStatus;
//    }
}
