package io.github.steven_liu.rage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

/**
 * Draws one of three randomly chosen backgrounds for the fight arena.
 * 0 = Green hills, 1 = Desert flatland, 2 = Valley dip
 */
public class Background {

    public enum Type { HILLS, DESERT, VALLEY }

    private final Type type;
    private final float worldWidth;
    private final float worldHeight;

    public Background(float worldWidth, float worldHeight) {
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;

        // Randomly pick one of the three backgrounds
        int roll = MathUtils.random(2);
        switch (roll) {
            case 0:  type = Type.HILLS;  break;
            case 1:  type = Type.DESERT; break;
            default: type = Type.VALLEY; break;
        }
    }

    /** Draw the background. Call inside shapeRenderer.begin(Filled) ... end(). */
    public void draw(ShapeRenderer sr) {
        switch (type) {
            case HILLS:  drawHills(sr);  break;
            case DESERT: drawDesert(sr); break;
            case VALLEY: drawValley(sr); break;
        }
    }

    // ── Green Hills ──────────────────────────────────────────────────────

    private void drawHills(ShapeRenderer sr) {
        // Sky — light blue
        sr.setColor(0.53f, 0.81f, 0.98f, 1f);
        sr.rect(0, 0, worldWidth, worldHeight);

        // Distant hills — darker green, smaller bumps in the back
        Color farHill = new Color(0.2f, 0.55f, 0.2f, 1f);
        sr.setColor(farHill);
        drawHillRow(sr, worldHeight * 0.45f, 60f, 5);

        // Near hills — bright green, larger bumps in front
        Color nearHill = new Color(0.3f, 0.75f, 0.3f, 1f);
        sr.setColor(nearHill);
        drawHillRow(sr, worldHeight * 0.30f, 90f, 4);

        // Ground — flat green grass
        sr.setColor(0.35f, 0.8f, 0.35f, 1f);
        sr.rect(0, 0, worldWidth, worldHeight * 0.25f);
    }

    /** Draws a row of half-circle hills across the screen. */
    private void drawHillRow(ShapeRenderer sr, float baseY, float hillRadius, int count) {
        float spacing = worldWidth / count;
        for (int i = 0; i <= count; i++) {
            float cx = i * spacing;
            // Draw a half-circle by using a full circle placed at baseY
            sr.circle(cx, baseY, hillRadius, 48);
        }
        // Fill the gap below the hills to the bottom
        sr.rect(0, 0, worldWidth, baseY);
    }

    // ── Desert Flatland ──────────────────────────────────────────────────

    private void drawDesert(ShapeRenderer sr) {
        // Sky — warm gradient from pale orange-yellow at horizon to light blue at top
        sr.setColor(0.55f, 0.78f, 0.93f, 1f);
        sr.rect(0, worldHeight * 0.5f, worldWidth, worldHeight * 0.5f);

        // Horizon haze — warm orange tint
        sr.setColor(0.95f, 0.85f, 0.65f, 1f);
        sr.rect(0, worldHeight * 0.30f, worldWidth, worldHeight * 0.25f);

        // Sand ground — flat tan
        sr.setColor(0.87f, 0.76f, 0.54f, 1f);
        sr.rect(0, 0, worldWidth, worldHeight * 0.30f);

        // Distant sand dunes — slightly darker tan bumps
        sr.setColor(0.80f, 0.68f, 0.45f, 1f);
        float duneBase = worldHeight * 0.28f;
        float duneRadius = 50f;
        for (int i = 0; i < 6; i++) {
            float cx = i * (worldWidth / 5f) + 40f;
            sr.circle(cx, duneBase, duneRadius, 48);
        }
        // Fill below dunes
        sr.setColor(0.87f, 0.76f, 0.54f, 1f);
        sr.rect(0, 0, worldWidth, duneBase);

        // Sun — bright yellow circle in the sky
        sr.setColor(1f, 0.95f, 0.4f, 1f);
        sr.circle(worldWidth * 0.75f, worldHeight * 0.80f, 35f, 48);

        // Cactus silhouettes — simple dark green rectangles
        sr.setColor(0.2f, 0.45f, 0.15f, 1f);
        drawCactus(sr, worldWidth * 0.15f, worldHeight * 0.30f, 8f, 50f);
        drawCactus(sr, worldWidth * 0.65f, worldHeight * 0.30f, 6f, 40f);
        drawCactus(sr, worldWidth * 0.85f, worldHeight * 0.30f, 7f, 45f);
    }

    /** Simple cactus: a vertical trunk with two small arms. */
    private void drawCactus(ShapeRenderer sr, float x, float groundY, float w, float h) {
        // Trunk
        sr.rect(x - w / 2, groundY, w, h);
        // Left arm
        sr.rect(x - w / 2 - w, groundY + h * 0.5f, w, w);
        sr.rect(x - w / 2 - w, groundY + h * 0.5f, w, h * 0.25f);
        // Right arm
        sr.rect(x + w / 2, groundY + h * 0.35f, w, w);
        sr.rect(x + w / 2, groundY + h * 0.35f, w, h * 0.3f);
    }

    // ── Valley Dip ───────────────────────────────────────────────────────

    private void drawValley(ShapeRenderer sr) {
        // Sky — soft blue
        sr.setColor(0.6f, 0.78f, 0.95f, 1f);
        sr.rect(0, 0, worldWidth, worldHeight);

        // Distant mountains — grayish purple
        sr.setColor(0.55f, 0.5f, 0.65f, 1f);
        drawMountain(sr, worldWidth * 0.1f, worldHeight * 0.55f, 200f, 180f);
        drawMountain(sr, worldWidth * 0.5f, worldHeight * 0.55f, 250f, 200f);
        drawMountain(sr, worldWidth * 0.85f, worldHeight * 0.55f, 180f, 160f);

        // Fill below mountain base — must reach up to the triangle base line
        sr.setColor(0.55f, 0.5f, 0.65f, 1f);
        sr.rect(0, 0, worldWidth, worldHeight * 0.55f);

        // Valley walls — green slopes on both sides, dipping in the middle
        sr.setColor(0.35f, 0.65f, 0.3f, 1f);

        // Left slope: rises from center-left to the left edge
        drawSlope(sr, 0, worldHeight * 0.50f, worldWidth * 0.3f, worldHeight * 0.20f);

        // Right slope: rises from center-right to the right edge
        drawSlope(sr, worldWidth * 0.7f, worldHeight * 0.20f, worldWidth, worldHeight * 0.50f);

        // Valley floor — darker green flat area in the middle
        sr.setColor(0.3f, 0.6f, 0.25f, 1f);
        sr.rect(0, 0, worldWidth, worldHeight * 0.20f);

        // Grass tufts — small green circles along the valley floor
        sr.setColor(0.25f, 0.55f, 0.2f, 1f);
        for (int i = 0; i < 12; i++) {
            float gx = worldWidth * 0.05f + i * (worldWidth * 0.9f / 11f);
            sr.circle(gx, worldHeight * 0.20f, 8f, 24);
        }
    }

    /** Simple triangle mountain. */
    private void drawMountain(ShapeRenderer sr, float cx, float baseY, float halfWidth, float height) {
        sr.triangle(
            cx - halfWidth, baseY,
            cx + halfWidth, baseY,
            cx, baseY + height
        );
    }

    /** A sloped trapezoid from (x1, y1) to (x2, y2) filled down to y=0. */
    private void drawSlope(ShapeRenderer sr, float x1, float y1, float x2, float y2) {
        // Two triangles forming a trapezoid: (x1,0) -> (x1,y1) -> (x2,y2) -> (x2,0)
        sr.triangle(x1, 0, x1, y1, x2, y2);
        sr.triangle(x1, 0, x2, y2, x2, 0);
    }

    public Type getType() {
        return type;
    }
}
