package org.tnmk.robocode.robot.theunfolding.melee;

import java.util.Arrays;
import java.util.List;
import org.tnmk.robocode.robot.AbstractWinRateTest;
import org.tnmk.robocode.robot.TestRounds;
import org.tnmk.robocode.robot.TheUnfoldingRobot;
import supersample.SuperCrazy;
import supersample.SuperMercutio;
import supersample.SuperSpinBot;

public class AgainstSuperSampleBots01Test extends AbstractWinRateTest {

    @Override
    public TestConfig constructTestConfig() {
        List<String> enemyNames = Arrays.asList(
                SuperCrazy.class.getCanonicalName()
                , SuperSpinBot.class.getCanonicalName()
                , SuperMercutio.class.getCanonicalName()
        );

        /**
         * - v2.4.1: 67.85%
         * - v2.4.2:
         */
        TestConfig testConfig = new TestConfig(TheUnfoldingRobot.class, enemyNames, 0.63, TestRounds.NUM_ROUNDS_ABSOLUTE_CERTAINTY);
        return testConfig;
    }
}
