package io.github.steven_liu.rage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * Two circular virtual joysticks — one per player.
 * P1 joystick on the bottom-left, P2 joystick on the bottom-right.
 * Dragging the thumb beyond the base radius maps to direction + jump.
 */
public class TouchControls {

    private final OrthographicCamera hudCam;
    private final float screenW, screenH;

    // Joystick sizing (4x the old 64px button = 256px diameter base)
    private static final float BASE_RADIUS = 128f;
    private static final float THUMB_RADIUS = 48f;
    private static final float DEAD_ZONE = 20f;   // ignore tiny movements

    // Colors
    private static final Color BASE_COLOR  = new Color(1f, 1f, 1f, 0.2f);
    private static final Color THUMB_COLOR = new Color(1f, 1f, 1f, 0.45f);

    // Fixed center positions for joystick bases (never move)
    private final Vector2 p1Center;
    private final Vector2 p2Center;

    // Current thumb positions (screen-space)
    private final Vector2 p1Thumb;
    private final Vector2 p2Thumb;

    // Normalized output direction per player (-1..1 on each axis)
    public float p1DirX, p1DirY;
    public float p2DirX, p2DirY;

    // Which pointer index is controlling each joystick (-1 = none)
    private int p1Pointer = -1;
    private int p2Pointer = -1;

    public TouchControls(float screenW, float screenH) {
        this.screenW = screenW;
        this.screenH = screenH;

        hudCam = new OrthographicCamera(screenW, screenH);
        hudCam.position.set(screenW / 2f, screenH / 2f, 0);
        hudCam.update();

        // P1: bottom-left (fixed)
        float pad = BASE_RADIUS + 30f;
        p1Center = new Vector2(pad, pad);
        p1Thumb  = new Vector2(p1Center);

        // P2: bottom-right (fixed)
        p2Center = new Vector2(screenW - pad, pad);
        p2Thumb  = new Vector2(p2Center);
    }

    /** Poll touch state and compute joystick directions. */
    public void update() {
        p1DirX = 0; p1DirY = 0;
        p2DirX = 0; p2DirY = 0;
        p1Pointer = -1;
        p2Pointer = -1;

        // Reset thumbs to center (will be overwritten if touched)
        p1Thumb.set(p1Center);
        p2Thumb.set(p2Center);

        for (int i = 0; i < 10; i++) {
            if (!Gdx.input.isTouched(i)) continue;

            float tx = Gdx.input.getX(i);
            float ty = screenH - Gdx.input.getY(i);  // flip y

            if (tx < screenW / 2f) {
                // P1 joystick
                if (p1Pointer == -1) {
                    p1Pointer = i;
                    computeJoystick(tx, ty, p1Center, p1Thumb, true);
                }
            } else {
                // P2 joystick
                if (p2Pointer == -1) {
                    p2Pointer = i;
                    computeJoystick(tx, ty, p2Center, p2Thumb, false);
                }
            }
        }
    }

    private void computeJoystick(float tx, float ty, Vector2 center, Vector2 thumb, boolean isP1) {
        float dx = tx - center.x;
        float dy = ty - center.y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist < DEAD_ZONE) return;

        // Clamp thumb to base radius
        float clampedDist = Math.min(dist, BASE_RADIUS);
        float nx = dx / dist;
        float ny = dy / dist;

        thumb.set(center.x + nx * clampedDist, center.y + ny * clampedDist);

        // Normalize direction to -1..1
        float normX = nx * (clampedDist / BASE_RADIUS);
        float normY = ny * (clampedDist / BASE_RADIUS);

        if (isP1) {
            p1DirX = normX;
            p1DirY = normY;
        } else {
            p2DirX = normX;
            p2DirY = normY;
        }
    }

    /** Draw semi-transparent joystick bases and thumbs. */
    public void draw(ShapeRenderer sr) {
        sr.setProjectionMatrix(hudCam.combined);

        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);

        sr.begin(ShapeRenderer.ShapeType.Filled);

        // P1 base + thumb
        sr.setColor(BASE_COLOR);
        sr.circle(p1Center.x, p1Center.y, BASE_RADIUS, 64);
        sr.setColor(THUMB_COLOR);
        sr.circle(p1Thumb.x, p1Thumb.y, THUMB_RADIUS, 48);

        // P2 base + thumb
        sr.setColor(BASE_COLOR);
        sr.circle(p2Center.x, p2Center.y, BASE_RADIUS, 64);
        sr.setColor(THUMB_COLOR);
        sr.circle(p2Thumb.x, p2Thumb.y, THUMB_RADIUS, 48);

        sr.end();

        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);
    }
}
