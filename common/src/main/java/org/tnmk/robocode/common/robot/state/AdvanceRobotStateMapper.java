package org.tnmk.robocode.common.robot.state;

import java.awt.geom.Point2D;
import robocode.AdvancedRobot;

public class AdvanceRobotStateMapper {
    public static void copyState(AdvancedRobot robot, AdvanceRobotState state) {
        state.setName(robot.getName());
        state.setTime(robot.getTime());
        state.setPosition(new Point2D.Double(robot.getX(), robot.getY()));
        state.setEnergy(robot.getEnergy());

        state.setVelocity(robot.getVelocity());
        state.setHeading(robot.getHeading());
        state.setRadarHeading(robot.getRadarHeading());
        state.setGunHeading(robot.getGunHeading());

        state.setDistanceRemaining(robot.getDistanceRemaining());
        state.setTurnRemaining(robot.getTurnRemaining());
        state.setRadarTurnRemaining(robot.getRadarTurnRemaining());
        state.setGunTurnRemaining(robot.getGunTurnRemaining());

        state.setGunHeat(robot.getGunHeat());
        state.setOthers(robot.getOthers());
        state.setNumSentries(robot.getNumSentries());
        state.setRoundNum(robot.getRoundNum());
    }

    public static AdvanceRobotState toState(AdvancedRobot robot) {
        AdvanceRobotState state = new AdvanceRobotState();
        copyState(robot, state);
        return state;
    }
    public static AdvanceRobotFightState toFightState(AdvancedRobot robot) {
        AdvanceRobotFightState state = new AdvanceRobotFightState();
        copyState(robot, state);
        return state;
    }
}
