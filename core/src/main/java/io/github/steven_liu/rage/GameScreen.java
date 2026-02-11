package io.github.steven_liu.rage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Main game screen — 2D fighting arena with physics, controls, collision, and HUD.
 */
public class GameScreen extends ScreenAdapter {

    // World dimensions in virtual units
    private static final float WORLD_WIDTH  = 800;
    private static final float WORLD_HEIGHT = 480;
    private static final float PLAYER_RADIUS = 30f;
    private static final float GROUND_Y = 60f;  // ground level in world units

    private final FightofRngRage game;

    private OrthographicCamera camera;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;

    private Player player1;
    private Player player2;
    private Background background;

    private Hud hud;
    private TouchControls touchControls;

    public GameScreen(FightofRngRage game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        camera.update();

        shapeRenderer = new ShapeRenderer();

        // Randomly pick one of three backgrounds
        background = new Background(WORLD_WIDTH, WORLD_HEIGHT);

        // Player 1 (red) on the left, standing on the ground
        player1 = new Player(
            WORLD_WIDTH * 0.2f,
            GROUND_Y + PLAYER_RADIUS,
            PLAYER_RADIUS,
            Color.RED
        );

        // Player 2 (blue) on the right, standing on the ground
        player2 = new Player(
            WORLD_WIDTH * 0.8f,
            GROUND_Y + PLAYER_RADIUS,
            PLAYER_RADIUS,
            Color.BLUE
        );

        // HUD & touch controls use actual screen pixels
        float sw = Gdx.graphics.getWidth();
        float sh = Gdx.graphics.getHeight();
        hud = new Hud(sw, sh);
        touchControls = new TouchControls(sw, sh);
    }

    @Override
    public void render(float delta) {
        // Cap delta to avoid physics explosions on lag spikes
        delta = Math.min(delta, 1 / 30f);

        // ── Input ────────────────────────────────────────────────
        handleInput(delta);

        // ── Physics update ───────────────────────────────────────
        player1.update(delta, GROUND_Y, 0, WORLD_WIDTH);
        player2.update(delta, GROUND_Y, 0, WORLD_WIDTH);

        // ── Collision ────────────────────────────────────────────
        resolvePlayerCollision(player1, player2);

        // ── Facing ───────────────────────────────────────────────
        player1.faceOpponent(player2);
        player2.faceOpponent(player1);

        // (no timer to update)

        // ── Render ───────────────────────────────────────────────
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        background.draw(shapeRenderer);
        shapeRenderer.end();

        // Players (drawn on top of background)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(player1.color);
        shapeRenderer.circle(player1.x, player1.y, player1.radius, 64);
        shapeRenderer.setColor(player2.color);
        shapeRenderer.circle(player2.x, player2.y, player2.radius, 64);
        shapeRenderer.end();

        // Reset GL viewport to full screen for HUD & touch overlays
        // (FitViewport letterboxes, which would squash screen-space draws)
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // HUD (screen-space)
        hud.drawBars(shapeRenderer, player1, player2);
        hud.drawText(player1, player2);

        // Touch controls overlay (screen-space)
        touchControls.draw(shapeRenderer);
    }

    // ── Input handling ───────────────────────────────────────────────────

    private void handleInput(float delta) {
        // Reset horizontal velocity each frame (stop when no key held)
        player1.vx = 0;
        player2.vx = 0;

        // ── Keyboard (desktop) ──

        // Player 1: A/D move, W jump
        if (Gdx.input.isKeyPressed(Input.Keys.A)) player1.vx = -Player.MOVE_SPEED;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) player1.vx =  Player.MOVE_SPEED;
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) player1.jump();

        // Player 2: Arrow keys
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  player2.vx = -Player.MOVE_SPEED;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) player2.vx =  Player.MOVE_SPEED;
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) player2.jump();

        // ── Touch joystick (android / touch) ──
        touchControls.update();

        // P1 joystick — proportional horizontal, push up to jump
        float p1x = touchControls.p1DirX;
        if (Math.abs(p1x) > 0.15f) player1.vx = p1x * Player.MOVE_SPEED;
        if (touchControls.p1DirY > 0.5f) player1.jump();

        // P2 joystick
        float p2x = touchControls.p2DirX;
        if (Math.abs(p2x) > 0.15f) player2.vx = p2x * Player.MOVE_SPEED;
        if (touchControls.p2DirY > 0.5f) player2.jump();
    }

    // ── Collision ────────────────────────────────────────────────────────

    /** Push two circle-players apart if they overlap. */
    private void resolvePlayerCollision(Player a, Player b) {
        float dx = b.x - a.x;
        float dy = b.y - a.y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        float minDist = a.radius + b.radius;

        if (dist < minDist && dist > 0) {
            float overlap = (minDist - dist) / 2f;
            float nx = dx / dist;  // collision normal
            float ny = dy / dist;

            a.x -= nx * overlap;
            a.y -= ny * overlap;
            b.x += nx * overlap;
            b.y += ny * overlap;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        hud.dispose();
    }
}
