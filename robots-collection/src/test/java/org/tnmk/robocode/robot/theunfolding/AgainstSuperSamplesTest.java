package org.tnmk.robocode.robot.theunfolding;

import java.util.Arrays;
import java.util.List;
import org.tnmk.robocode.robot.AbstractWinRateTest;
import org.tnmk.robocode.robot.TheUnfoldingRobot;
import supersample.SuperMercutio;
import supersample.SuperRamFire;
import supersample.SuperSpinBot;
import supersample.SuperWalls;

public class AgainstSuperSamplesTest extends AbstractWinRateTest {

    @Override
    public TestConfig constructTestConfig() {
        List<String> enemyNames = Arrays.asList(
                SuperWalls.class.getCanonicalName()
                , SuperRamFire.class.getCanonicalName()
                , SuperSpinBot.class.getCanonicalName()
                , SuperMercutio.class.getCanonicalName()
        );

        /**
         * - v2.4.2: Apply uTurn: 2000 rounds:
         *
         * View more at {@link AgainstBlackPearlAndOthersTest} to see interesting resuls.
         * Moebius' gun is good, but his gun alone cannot touch TheUnfoldingRobot!
         */
        TestConfig testConfig  = new TestConfig(TheUnfoldingRobot.class,enemyNames, 0.5, 2000);
        return testConfig;
    }
}
