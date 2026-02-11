package io.github.steven_liu.rage;

import com.badlogic.gdx.graphics.Color;

/** A player fighter — holds position, physics, and health state. */
public class Player {

    // Position & size
    public float x;
    public float y;
    public float radius;
    public Color color;

    // Physics
    public float vx;       // horizontal velocity
    public float vy;       // vertical velocity
    public boolean grounded;

    // Health
    public float maxHealth = 100f;
    public float health    = 100f;

    // Movement tuning
    public static final float MOVE_SPEED = 250f;
    public static final float JUMP_VELOCITY = 400f;
    public static final float GRAVITY = -900f;

    // Facing direction: +1 = right, -1 = left
    public int facing = 1;

    public Player(float x, float y, float radius, Color color) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
    }

    /** Apply gravity and velocity, clamp to ground and screen edges. */
    public void update(float delta, float groundY, float minX, float maxX) {
        // Gravity
        if (!grounded) {
            vy += GRAVITY * delta;
        }

        // Apply velocity
        x += vx * delta;
        y += vy * delta;

        // Ground collision
        if (y - radius <= groundY) {
            y = groundY + radius;
            vy = 0;
            grounded = true;
        } else {
            grounded = false;
        }

        // Screen boundary clamping
        if (x - radius < minX) x = minX + radius;
        if (x + radius > maxX)  x = maxX - radius;
    }

    /** Make this player jump if on the ground. */
    public void jump() {
        if (grounded) {
            vy = JUMP_VELOCITY;
            grounded = false;
        }
    }

    /** Update facing so this player always faces the opponent. */
    public void faceOpponent(Player other) {
        facing = (other.x > x) ? 1 : -1;
    }

    public float healthPercent() {
        return Math.max(0, health / maxHealth);
    }
}
