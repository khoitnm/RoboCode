package org.tnmk.robocode.robot;

import java.util.Arrays;
import java.util.List;
import mld.Moebius;
import pez.micro.BlackWidow;
import wiki.mako.MakoHT;

public class TheUnfoldingRobotVsBestSingleEnemiesTest extends AbstractWinRateTest {

    @Override
    public TestConfig constructTestConfig() {
        List<String> enemyNames = Arrays.asList(
                Moebius.class.getCanonicalName()
                ,BlackPearl.class.getCanonicalName()
                ,BlackWidow.class.getCanonicalName()
                ,MakoHT.class.getCanonicalName()
        );

        //At 2019-06-16, The win rate is 0.4 (toughest opponents are BlackPearl and Moebius)
        TestConfig testConfig  = new TestConfig(TheUnfoldingRobot.class,enemyNames, 0.5, 20);
        return testConfig;
    }
}
