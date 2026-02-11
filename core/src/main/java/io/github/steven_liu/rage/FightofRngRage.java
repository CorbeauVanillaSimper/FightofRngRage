package io.github.steven_liu.rage;

import com.badlogic.gdx.Game;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class FightofRngRage extends Game {

    @Override
    public void create() {
        setScreen(new TitleScreen(this));
    }
}
