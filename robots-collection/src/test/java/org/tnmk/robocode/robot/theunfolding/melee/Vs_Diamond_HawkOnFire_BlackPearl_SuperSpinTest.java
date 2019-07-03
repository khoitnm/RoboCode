package org.tnmk.robocode.robot.theunfolding.melee;

import java.util.Arrays;
import java.util.List;
import jekl.mini.BlackPearl;
import org.tnmk.robocode.robot.AbstractWinRateTest;
import org.tnmk.robocode.robot.TestRounds;
import org.tnmk.robocode.robot.TheUnfoldingRobot;
import rz.HawkOnFireOS;
import supersample.SuperSpinBot;
import voidiousdiamond.voidious.Diamond;

public class Vs_Diamond_HawkOnFire_BlackPearl_SuperSpinTest extends AbstractWinRateTest {

    @Override
    public TestConfig constructTestConfig() {
        List<String> enemyNames = Arrays.asList(
                HawkOnFireOS.class.getCanonicalName()
                , Diamond.class.getCanonicalName()
                , BlackPearl.class.getCanonicalName()
                , SuperSpinBot.class.getCanonicalName()
        );


        TestConfig testConfig = new TestConfig(TheUnfoldingRobot.class, enemyNames, 0.1, TestRounds.NUM_ROUNDS_QUICK);
        return testConfig;
    }
}
