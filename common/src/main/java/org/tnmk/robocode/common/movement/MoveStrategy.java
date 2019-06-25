package org.tnmk.robocode.common.movement;

public enum MoveStrategy {
    /**
     * Not specific strategy.
     */
    NONE(Integer.MIN_VALUE),
    /**
     * This is different from {@link #RUN_AWAY_FROM_WALL}.<br/>
     * This strategy is avoiding hitting wall. While {@link #RUN_AWAY_FROM_WALL} is already hit wall, but it try to run away from that wall.
     * <p/>
     * With {@link #WALL_SMOOTH}, most of the time we won't hit wall anymore.<br/>
     * The only time we can hit a wall is executing {@link MoveStrategy#RUN_AWAY_FROM_ENEMIES}.<br/>
     * Anyway, we still need both {@link MoveStrategy#RUN_AWAY_FROM_ENEMIES} and {@link MoveStrategy#RUN_AWAY_FROM_WALL}<br/>
     */
    WALL_SMOOTH(1000),

    /**
     * @see #WALL_SMOOTH
     */
    RUN_AWAY_FROM_WALL(1001),
    RUN_AWAY_FROM_ENEMIES(1001),
    ANTI_GRAVITY(2), OSCILLATOR(1), RANDOM(5),
    /**
     * We only use this to avoid robot stay still at the same place for a long time.
     * One of the reason is our robot cannot see any enemy in its radar (because enemies are out of the radar range).
     * In that case, our robot can stay still. To avoid that, we just randomly move it, and use this strategy.
     */
    WANDERING(0);

    /**
     * The higher the number is, the higher priority is.
     */
    private final int priority;

    MoveStrategy(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
