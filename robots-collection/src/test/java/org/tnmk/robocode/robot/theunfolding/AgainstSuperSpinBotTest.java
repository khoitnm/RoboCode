package org.tnmk.robocode.robot.theunfolding;

import java.util.Arrays;
import java.util.List;
import org.tnmk.robocode.robot.AbstractWinRateTest;
import org.tnmk.robocode.robot.TheUnfoldingRobot;
import supersample.SuperSpinBot;

public class AgainstSuperSpinBotTest extends AbstractWinRateTest {

    @Override
    public TestConfig constructTestConfig() {
        List<String> enemyNames = Arrays.asList(
                SuperSpinBot.class.getCanonicalName()
        );

        /**
         * Run 1000 rounds with win percentage:
         * - 31.5%
         * - 61.6%
         */
        TestConfig testConfig = new TestConfig(TheUnfoldingRobot.class, enemyNames, 0.58, 100);
        return testConfig;
    }
}
