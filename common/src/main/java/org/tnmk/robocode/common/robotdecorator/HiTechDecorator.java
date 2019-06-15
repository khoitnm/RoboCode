package org.tnmk.robocode.common.robotdecorator;

import robocode.Robot;

import java.awt.*;

public class HiTechDecorator {
    public static final Color AHEAD_DIRECTION_COLOR = new Color(193, 174, 147);
    public static final Color ACTUAL_MOVE_DIRECTION_COLOR = new Color(21, 202, 202);
    public static final Color FINAL_DESTINATION_COLOR = new Color(21, 255, 255);
    public static final Color ROBOT_BORDY_COLOR = new Color(25, 20, 17);// new Color(51, 153, 153);
    public static final Color ROBOT_RADAR_COLOR = new Color(0, 166, 168);// Color(117, 209, 209);
    public static final Color ROBOT_GUN_COLOR = new Color(21, 202, 202);
    public static final Color SCAN_COLOR = new Color(18, 163, 163);
    public static final Color BULLET_COLOR = new Color(21, 255, 255);
    public static final Color BULLET_GFT_COLOR = new Color(255, 0, 245);



    public static void decorate(Robot robot){
        robot.setBodyColor(ROBOT_BORDY_COLOR);
        robot.setRadarColor(ROBOT_RADAR_COLOR);
        robot.setGunColor(ROBOT_GUN_COLOR);
        robot.setScanColor(SCAN_COLOR);
        robot.setBulletColor(BULLET_COLOR);
    }
}
