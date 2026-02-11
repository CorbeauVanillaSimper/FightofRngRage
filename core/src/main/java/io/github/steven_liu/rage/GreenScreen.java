package io.github.steven_liu.rage;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.utils.ScreenUtils;

/** A plain green screen. */
public class GreenScreen extends ScreenAdapter {

    private final FightofRngRage game;

    public GreenScreen(FightofRngRage game) {
        this.game = game;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0f, 1f, 0f, 1f);
    }
}
