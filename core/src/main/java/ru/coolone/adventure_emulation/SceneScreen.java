package ru.coolone.adventure_emulation;

import com.badlogic.gdx.Screen;
import com.uwsoft.editor.renderer.SceneLoader;

/**
 * Class, unite's LibGDX screen and Overlap2d scene
 */
abstract public class SceneScreen implements Screen {

    /**
     * Scene loader
     */
    SceneLoader loader;
    /**
     * Scene name
     */
    String sceneName;

    public SceneScreen(
            String sceneName,
            SceneLoader loader
    ) {
        this.sceneName = sceneName;
        this.loader = loader;
    }

    @Override
    public void show() {
        loader.loadScene(sceneName);
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
