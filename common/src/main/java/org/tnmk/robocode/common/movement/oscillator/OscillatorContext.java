package org.tnmk.robocode.common.movement.oscillator;

import org.tnmk.robocode.common.movement.DirectionContext;
import robocode.AdvancedRobot;

/**
 * http://old.robowiki.net/robowiki?Oscillators
 * Oscillators usually play perpendicular to enemy bot, and avoid being hit by changing direction frequently. Oscillating movement is very easy to implement, needs only a few lines of code, and can be combined with avoiding and other tricks.
 * Some disadvantages of oscillating movement are that you can get easily hit by an advanced targeting system, and that you have little control on your absolute position in the battlefield (you define the position relative to the enemy).
 */
public class OscillatorContext extends DirectionContext {
    public OscillatorContext(AdvancedRobot robot) {
        super(robot);
    }
}
