package ru.coolone.adventure_emulation;

import com.badlogic.gdx.Screen;

/**
 * Class, unite's LibGDX screen and Overlap2d scene
 */
abstract public class SceneScreen implements Screen {

    /**
     * Link for @{@link GameCore}
     */
    protected GameCore core;
    /**
     * Scene name
     */
    protected String name;

    public SceneScreen(
            GameCore core,
            String name
    ) {
        this.core = core;
        this.name = name;
    }

    String getName() {
        return name;
    }

    @Override
    public void show() {
        // Load scene
        core.loadScene(name);
    }

    @Override
    public void render(float delta) {
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
