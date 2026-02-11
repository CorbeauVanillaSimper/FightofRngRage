package io.github.steven_liu.rage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Heads-Up Display rendered in screen-space.
 * Shows health bars and player labels (no timer).
 * All sizes are 3x the original.
 */
public class Hud {

    private final OrthographicCamera cam;
    private final float screenW, screenH;

    // Health bar sizing — 3x original (260→780, 24→72, offsets scaled too)
    private static final float BAR_WIDTH  = 780f;
    private static final float BAR_HEIGHT = 72f;
    private static final float BAR_Y_OFFSET = 48f;  // from top
    private static final float BAR_X_PAD = 60f;

    // Fonts
    private BitmapFont labelFont;
    private SpriteBatch batch;
    private FreeTypeFontGenerator generator;
    private GlyphLayout glyphLayout;

    public Hud(float screenW, float screenH) {
        this.screenW = screenW;
        this.screenH = screenH;

        cam = new OrthographicCamera(screenW, screenH);
        cam.position.set(screenW / 2f, screenH / 2f, 0);
        cam.update();

        batch = new SpriteBatch();

        generator = new FreeTypeFontGenerator(Gdx.files.internal("DMSerifDisplay-Regular.ttf"));

        // Label font — 3x original (18→54)
        FreeTypeFontParameter labelParam = new FreeTypeFontParameter();
        labelParam.size = 54;
        labelParam.color = Color.WHITE;
        labelFont = generator.generateFont(labelParam);

        glyphLayout = new GlyphLayout();
    }

    /** Draw health bar shapes. */
    public void drawBars(ShapeRenderer sr, Player p1, Player p2) {
        sr.setProjectionMatrix(cam.combined);

        Gdx.gl.glEnable(Gdx.gl.GL_BLEND);
        Gdx.gl.glBlendFunc(Gdx.gl.GL_SRC_ALPHA, Gdx.gl.GL_ONE_MINUS_SRC_ALPHA);

        sr.begin(ShapeRenderer.ShapeType.Filled);

        float barTop = screenH - BAR_Y_OFFSET;
        float barBottom = barTop - BAR_HEIGHT;

        // Limit bar width so the two bars don't overlap — leave a gap in the center
        float maxBarW = (screenW / 2f) - BAR_X_PAD - 20f;
        float barW = Math.min(BAR_WIDTH, maxBarW);

        // ── P1 health bar (left side, fills left-to-right) ──
        float p1BarX = BAR_X_PAD;
        sr.setColor(0.2f, 0.2f, 0.2f, 0.8f);
        sr.rect(p1BarX, barBottom, barW, BAR_HEIGHT);
        sr.setColor(0.9f, 0.15f, 0.15f, 1f);
        sr.rect(p1BarX, barBottom, barW * p1.healthPercent(), BAR_HEIGHT);

        // ── P2 health bar (right side, fills right-to-left) ──
        float p2BarX = screenW - BAR_X_PAD - barW;
        sr.setColor(0.2f, 0.2f, 0.2f, 0.8f);
        sr.rect(p2BarX, barBottom, barW, BAR_HEIGHT);
        float p2Fill = barW * p2.healthPercent();
        sr.setColor(0.15f, 0.3f, 0.9f, 1f);
        sr.rect(p2BarX + barW - p2Fill, barBottom, p2Fill, BAR_HEIGHT);

        sr.end();

        Gdx.gl.glDisable(Gdx.gl.GL_BLEND);

        // ── Border outlines ──
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.WHITE);
        sr.rect(p1BarX, barBottom, barW, BAR_HEIGHT);
        sr.rect(p2BarX, barBottom, barW, BAR_HEIGHT);
        sr.end();
    }

    /** Draw player labels. Call after drawBars. */
    public void drawText(Player p1, Player p2) {
        batch.setProjectionMatrix(cam.combined);
        batch.begin();

        float barTop = screenH - BAR_Y_OFFSET;

        // P1 label
        labelFont.draw(batch, "P1", BAR_X_PAD, barTop + 54f);

        // P2 label — right-align
        glyphLayout.setText(labelFont, "P2");
        labelFont.draw(batch, "P2", screenW - BAR_X_PAD - glyphLayout.width, barTop + 54f);

        batch.end();
    }

    public void dispose() {
        batch.dispose();
        labelFont.dispose();
        generator.dispose();
    }
}
