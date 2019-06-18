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
    ANTI_GRAVITY(2), OSCILLATOR(1), RANDOM(5);

    /**
     * The higher the number is, the higher priority is.
     */
    private final int priorty;

    MoveStrategy(int priorty) {
        this.priorty = priorty;
    }

    public int getPriorty() {
        return priorty;
    }
}
