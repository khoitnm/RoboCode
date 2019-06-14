package org.tnmk.robocode.common.gun.pattern;

public enum EnemyMovePattern {
    CIRCULAR,
    LINEAR,
    STAY_STILL,
    /**
     * This could be we have never identify the pattern for this enemy.<br/>
     * Or it also could be we cannot identify any specific pattern for this enemy. The most advanced robot will go into this category.
     * <br/>
     */
    UNIDENTIFIED;
}
