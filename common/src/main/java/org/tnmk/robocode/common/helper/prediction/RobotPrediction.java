package org.tnmk.robocode.common.helper.prediction;

import java.awt.geom.Point2D;

public class RobotPrediction {

        private long timePeriod;
        private Point2D position;
        private double velocity;
        private double headingRadian;
        private double distanceRemaining;
        private double turnRemainingRadian;
        private double normAcceleration;

        public long getTimePeriod() {
            return timePeriod;
        }

        public void setTimePeriod(long timePeriod) {
            this.timePeriod = timePeriod;
        }

        public Point2D getPosition() {
            return position;
        }

        public void setPosition(Point2D position) {
            this.position = position;
        }

        public double getVelocity() {
            return velocity;
        }

        public void setVelocity(double velocity) {
            this.velocity = velocity;
        }

        public double getHeadingRadian() {
            return headingRadian;
        }

        public void setHeadingRadian(double headingRadian) {
            this.headingRadian = headingRadian;
        }

        public double getDistanceRemaining() {
            return distanceRemaining;
        }

        public void setDistanceRemaining(double distanceRemaining) {
            this.distanceRemaining = distanceRemaining;
        }

        public double getTurnRemainingRadian() {
            return turnRemainingRadian;
        }

        public void setTurnRemainingRadian(double turnRemainingRadian) {
            this.turnRemainingRadian = turnRemainingRadian;
        }

        public double getNormAcceleration() {
            return normAcceleration;
        }

        public void setNormAcceleration(double normAcceleration) {
            this.normAcceleration = normAcceleration;
        }
    }