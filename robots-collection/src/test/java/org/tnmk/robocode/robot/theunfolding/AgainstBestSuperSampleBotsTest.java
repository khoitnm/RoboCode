package org.tnmk.robocode.robot.theunfolding;

import java.util.Arrays;
import java.util.List;
import org.tnmk.robocode.robot.AbstractWinRateTest;
import org.tnmk.robocode.robot.TheUnfoldingRobot;
import supersample.SuperCrazy;
import supersample.SuperMercutio;
import supersample.SuperSpinBot;

public class AgainstBestSuperSampleBotsTest extends AbstractWinRateTest {

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
        TestConfig testConfig = new TestConfig(TheUnfoldingRobot.class, enemyNames, 0.63, 2000);
        return testConfig;
    }
}
