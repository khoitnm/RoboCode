package org.tnmk.robocode.robot.theunfolding.melee;

import java.util.Arrays;
import java.util.List;
import jekl.mini.BlackPearl;
import mld.Moebius;
import org.tnmk.robocode.robot.AbstractWinRateTest;
import org.tnmk.robocode.robot.TestRounds;
import org.tnmk.robocode.robot.TheUnfoldingRobot;
import pez.micro.BlackWidow;
import wiki.mako.MakoHT;

public class Vs_BlackPearl_AndMoebiusTest extends AbstractWinRateTest {

    @Override
    public TestConfig constructTestConfig() {
        List<String> enemyNames = Arrays.asList(
                Moebius.class.getCanonicalName()
                , BlackPearl.class.getCanonicalName()
                , BlackWidow.class.getCanonicalName()
                , MakoHT.class.getCanonicalName()
        );

        /**
         * At 2019-06-16, The champion percentage is 34.1% (0.34) which was tested with 1000 rounds (matches).
         * - v2.4.0: After using random movement for 1-on-1: 37.4%, 41.1%
         *      2000 rounds: win 44.3%, 42.0%
         * - v2.4.2: Apply uTurn: 2000 rounds: 41.55%
         * The toughest opponents are BlackPearl and Moebius because of their gun!!! See more at {@link Vs_Moebius_And_OthersTest
         */
        TestConfig testConfig = new TestConfig(TheUnfoldingRobot.class, enemyNames, 0.45, TestRounds.NUM_ROUNDS_ABSOLUTE_CERTAINTY);
        return testConfig;
    }
}
