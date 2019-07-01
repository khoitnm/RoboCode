package org.tnmk.robocode.common.robot.state;

import java.util.Random;
import org.tnmk.robocode.common.constant.RobotPhysics;
import robocode.AdvancedRobot;
import robocode.Rules;

public class AdvanceRobotFactory {
    public static AdvancedRobot constructAdvanceRobot(){
        AdvancedRobot robot = new AdvancedRobot();
        MockAdvanceRobotPeer peer = new MockAdvanceRobotPeer();
        robot.setPeer(peer);

        peer.setName("Robot_" + System.nanoTime());
        peer.setTime(new Random().nextLong());
        peer.setEnergy(Math.random() * RobotPhysics.ROBOT_INITIATE_ENERGY);
        peer.setX(Math.random() * 1200);
        peer.setY(Math.random() * 1200);

        peer.setVelocity(Math.random() * Rules.MAX_VELOCITY);
        peer.setBodyHeading(Math.random() * 360);
        peer.setGunHeading(Math.random() * 360);
        peer.setRadarHeading(Math.random() * 360);

        peer.setDistanceRemaining(Math.random() * 360);
        peer.setBodyTurnRemaining(Math.random() * 360);
        peer.setGunTurnRemaining(Math.random() * 360);
        peer.setRadarTurnRemaining(Math.random() * 360);

        peer.setGunHeat(Math.random() * 10);
        peer.setOthers(new Random().nextInt(10));
        peer.setRoundNum(new Random().nextInt(100));
        peer.setNumSentries(new Random().nextInt(10));
        return robot;
    }
}
