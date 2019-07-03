package org.tnmk.robocode.robot.theunfolding.oneonone.againstsupersample;

import java.util.Arrays;
import java.util.List;
import org.tnmk.robocode.robot.AbstractWinRateTest;
import org.tnmk.robocode.robot.TestRounds;
import org.tnmk.robocode.robot.TheUnfoldingRobot;
import supersample.SuperMercutio;

public class Vs_SuperMecuritoBotTest extends AbstractWinRateTest {

    @Override
    public TestConfig constructTestConfig() {
        List<String> enemyNames = Arrays.asList(
                SuperMercutio.class.getCanonicalName()
        );

        TestConfig testConfig = new TestConfig(TheUnfoldingRobot.class, enemyNames, 0.85, TestRounds.NUM_ROUNDS_QUICK);
        return testConfig;
    }
}
