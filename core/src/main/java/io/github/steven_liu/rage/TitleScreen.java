package io.github.steven_liu.rage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.utils.ScreenUtils;

/** Title screen — blue background with an ominous message. */
public class TitleScreen extends ScreenAdapter {

    private final FightofRngRage game;
    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;
    private BitmapFont bottomFont;
    private GlyphLayout bottomLayout;
    private FreeTypeFontGenerator generator;

    // Stored positions for hit-testing the bottom text
    private float bx, by;

    private static final String MESSAGE = "You will regret playing this.";
    private static final String BOTTOM_MESSAGE = "Click here to begin ur suffering :)";

    public TitleScreen(FightofRngRage game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        generator = new FreeTypeFontGenerator(Gdx.files.internal("DMSerifDisplay-Regular.ttf"));

        // Top font — black, ~90px (same effective size as before)
        FreeTypeFontParameter topParam = new FreeTypeFontParameter();
        topParam.size = 90;
        topParam.color = Color.BLACK;
        font = generator.generateFont(topParam);

        layout = new GlyphLayout(font, MESSAGE);

        // Bottom font — red, 50px (was 200px effective, now 4x smaller)
        FreeTypeFontParameter bottomParam = new FreeTypeFontParameter();
        bottomParam.size = 50;
        bottomParam.color = Color.RED;
        bottomFont = generator.generateFont(bottomParam);

        bottomLayout = new GlyphLayout(bottomFont, BOTTOM_MESSAGE);

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // Convert screen Y (top-down) to world Y (bottom-up)
                float worldY = Gdx.graphics.getHeight() - screenY;

                // Check if click is inside the bottom text bounding box
                float textLeft = bx;
                float textRight = bx + bottomLayout.width;
                float textBottom = by - bottomLayout.height;
                float textTop = by;

                if (screenX >= textLeft && screenX <= textRight
                    && worldY >= textBottom && worldY <= textTop) {
                    game.setScreen(new GameScreen(game));
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(100 / 255f, 100 / 255f, 200 / 255f, 1f);

        float screenWidth = Gdx.graphics.getWidth();

        // center the text horizontally, place it near the top
        float x = (screenWidth - layout.width) / 2f;
        float y = Gdx.graphics.getHeight() - 40f;

        // center the bottom text horizontally, place it near the bottom
        bx = (screenWidth - bottomLayout.width) / 2f;
        by = bottomLayout.height + 40f;

        batch.begin();
        font.draw(batch, MESSAGE, x, y);
        bottomFont.draw(batch, BOTTOM_MESSAGE, bx, by);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        layout.setText(font, MESSAGE);
        bottomLayout.setText(bottomFont, BOTTOM_MESSAGE);
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        bottomFont.dispose();
        generator.dispose();
    }
}
