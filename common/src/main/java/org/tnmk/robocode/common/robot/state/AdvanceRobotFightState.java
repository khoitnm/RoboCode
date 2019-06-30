package org.tnmk.robocode.common.robot.state;

public class AdvanceRobotFightState extends AdvanceRobotState {
    private boolean isHitByBullet;
    private boolean isHitByEnemy;

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
}
