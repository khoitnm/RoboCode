package org.tnmk.robocode.robot.theunfolding.melee;

import java.util.Arrays;
import java.util.List;
import jekl.mini.BlackPearl;
import org.tnmk.robocode.robot.AbstractWinRateTest;
import org.tnmk.robocode.robot.TestRounds;
import org.tnmk.robocode.robot.TheUnfoldingRobot;
import rz.HawkOnFireOS;
import supersample.SuperCrazy;
import supersample.SuperSpinBot;

public class Vs_HawkOnFire_BlackPearl_OthersTest extends AbstractWinRateTest {

    @Override
    public TestConfig constructTestConfig() {
        List<String> enemyNames = Arrays.asList(
                HawkOnFireOS.class.getCanonicalName()
                , BlackPearl.class.getCanonicalName()
                , SuperCrazy.class.getCanonicalName()
                , SuperSpinBot.class.getCanonicalName()
        );


        TestConfig testConfig = new TestConfig(TheUnfoldingRobot.class, enemyNames, 0.5, TestRounds.NUM_ROUNDS_HIGH_CERTAINTY);
        return testConfig;
    }
}
