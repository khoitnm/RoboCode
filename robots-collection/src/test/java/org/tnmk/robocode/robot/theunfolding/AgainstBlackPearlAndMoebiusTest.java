package org.tnmk.robocode.robot.theunfolding;

import java.util.Arrays;
import java.util.List;
import mld.Moebius;
import org.tnmk.robocode.robot.AbstractWinRateTest;
import jekl.mini.BlackPearl;
import org.tnmk.robocode.robot.TheUnfoldingRobot;
import pez.micro.BlackWidow;
import wiki.mako.MakoHT;

public class AgainstBlackPearlAndMoebiusTest extends AbstractWinRateTest {

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
         * The toughest opponents are BlackPearl and Moebius because of their gun!!! See more at {@link AgainstMoebiusAndOthersTest
         */
        TestConfig testConfig = new TestConfig(TheUnfoldingRobot.class, enemyNames, 0.7, 20);
        return testConfig;
    }
}
