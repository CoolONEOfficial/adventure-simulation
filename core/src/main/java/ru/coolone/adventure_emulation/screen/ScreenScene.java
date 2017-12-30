package ru.coolone.adventure_emulation.screen;

import com.badlogic.gdx.Screen;

import ru.coolone.adventure_emulation.Core;

/**
 * Class, unite's LibGDX screen and @{@link com.uwsoft.editor.renderer.SceneLoader} scene
 *
 * @author coolone
 */
abstract public class ScreenScene implements Screen {

    /**
     * Link for @{@link Core}
     */
    protected Core core;
    /**
     * Scene name
     */
    protected String name;

    public ScreenScene(
            Core core,
            String name
    ) {
        this.core = core;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void show() {
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
