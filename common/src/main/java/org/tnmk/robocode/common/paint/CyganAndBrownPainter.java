package org.tnmk.robocode.common.paint;

import robocode.Robot;

import java.awt.*;

public class CyganAndBrownPainter {
    private static final Color ROBOT_BORDY_COLOR = new Color(170, 92, 21);// new Color(51, 153, 153);
    private static final Color ROBOT_RADAR_COLOR = new Color(211, 163, 126);// Color(117, 209, 209);
    private static final Color ROBOT_GUN_COLOR = new Color(0, 128, 128);

    public static void paint(Robot robot){
        robot.setBodyColor(ROBOT_BORDY_COLOR);
        robot.setRadarColor(ROBOT_RADAR_COLOR);
        robot.setGunColor(ROBOT_GUN_COLOR);
    }
}
