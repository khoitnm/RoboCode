package org.tnmk.robocode.common.robotdecorator;

import robocode.Robot;

import java.awt.*;

public class HiTechDecorator {
    private static final Color ROBOT_BORDY_COLOR = new Color(75, 67, 57);// new Color(51, 153, 153);
    private static final Color ROBOT_RADAR_COLOR = new Color(193, 174, 147);// Color(117, 209, 209);
    private static final Color ROBOT_GUN_COLOR = new Color(21, 202, 202);

    public static void decorate(Robot robot){
        robot.setBodyColor(ROBOT_BORDY_COLOR);
        robot.setRadarColor(ROBOT_RADAR_COLOR);
        robot.setGunColor(ROBOT_GUN_COLOR);
    }
}