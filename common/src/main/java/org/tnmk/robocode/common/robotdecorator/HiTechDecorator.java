package org.tnmk.robocode.common.robotdecorator;

import robocode.Robot;

import java.awt.*;

public class HiTechDecorator {
    private static final Color ROBOT_BORDY_COLOR = new Color(39, 33, 28);// new Color(51, 153, 153);
    private static final Color ROBOT_RADAR_COLOR = new Color(0, 166, 168);// Color(117, 209, 209);
    private static final Color ROBOT_GUN_COLOR = new Color(21, 202, 202);
    private static final Color SCAN_COLOR = new Color(18, 163, 163);
    private static final Color BULLET_COLOR = new Color(21, 202, 202);



    public static void decorate(Robot robot){
        robot.setBodyColor(ROBOT_BORDY_COLOR);
        robot.setRadarColor(ROBOT_RADAR_COLOR);
        robot.setGunColor(ROBOT_GUN_COLOR);
        robot.setScanColor(SCAN_COLOR);
        robot.setBulletColor(BULLET_COLOR);
    }
}
