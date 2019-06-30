package org.tnmk.robocode.common.robot.state;

import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import org.tnmk.robocode.common.constant.RobotPhysics;
import robocode.AdvancedRobot;
import robocode.Rules;

public class AdvanceRobotStateMapperTest {
    private static final double DOUBLE_COMPARE_PRECISION = 0.0001;

    @Test
    public void testStateMapper() {
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

        AdvanceRobotState state = AdvanceRobotStateMapper.toState(robot);

        Assert.assertEquals(robot.getName(), state.getName());
        Assert.assertEquals(robot.getTime(), state.getTime(), DOUBLE_COMPARE_PRECISION);
        Assert.assertEquals(robot.getEnergy(), state.getEnergy(), DOUBLE_COMPARE_PRECISION);
        Assert.assertEquals(robot.getX(), state.getPosition().getX(), DOUBLE_COMPARE_PRECISION);
        Assert.assertEquals(robot.getY(), state.getPosition().getY(), DOUBLE_COMPARE_PRECISION);

        Assert.assertEquals(robot.getVelocity(), state.getVelocity(), DOUBLE_COMPARE_PRECISION);
        Assert.assertEquals(robot.getHeading(), state.getHeading(), DOUBLE_COMPARE_PRECISION);
        Assert.assertEquals(robot.getGunHeading(), state.getGunHeading(), DOUBLE_COMPARE_PRECISION);
        Assert.assertEquals(robot.getRadarHeading(), state.getRadarHeading(), DOUBLE_COMPARE_PRECISION);

        Assert.assertEquals(robot.getDistanceRemaining(), state.getDistanceRemaining(), DOUBLE_COMPARE_PRECISION);
        Assert.assertEquals(robot.getTurnRemaining(), state.getTurnRemaining(), DOUBLE_COMPARE_PRECISION);
        Assert.assertEquals(robot.getGunTurnRemaining(), state.getGunTurnRemaining(), DOUBLE_COMPARE_PRECISION);
        Assert.assertEquals(robot.getRadarTurnRemaining(), state.getRadarTurnRemaining(), DOUBLE_COMPARE_PRECISION);

        Assert.assertEquals(robot.getGunHeat(), state.getGunHeat(), DOUBLE_COMPARE_PRECISION);
        Assert.assertEquals(robot.getOthers(), state.getOthers());
        Assert.assertEquals(robot.getRoundNum(), state.getRoundNum());
        Assert.assertEquals(robot.getNumSentries(), state.getNumSentries());

    }
}
