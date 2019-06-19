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
         * win percentage: 31.5%
         */
        TestConfig testConfig = new TestConfig(TheUnfoldingRobot.class, enemyNames, 0.8, 50);
        return testConfig;
    }
}
