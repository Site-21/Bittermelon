package com.site21.bittermelon.client.event;

public class HealthStages {

    public enum Level {
        NONE,
        HURT,
        DAMAGED,
        CRITICAL
    }

    public record HealthStage(int hurtAt, int damagedAt, int criticalAt) {
        public HealthStages.Level byHealth(int health) {
            if (health < criticalAt) {
                return HealthStages.Level.CRITICAL;
            }
            if (health < damagedAt) {
                return HealthStages.Level.DAMAGED;
            }
            if (health < hurtAt) {
                return HealthStages.Level.HURT;
            }
            return HealthStages.Level.NONE;

        }
    }

    public static final HealthStage SCP1507 = new HealthStage(5, 3, 0);
}