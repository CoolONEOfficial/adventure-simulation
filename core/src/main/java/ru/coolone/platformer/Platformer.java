package ru.coolone.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.uwsoft.editor.renderer.SceneLoader;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Platformer extends ApplicationAdapter {
    private SceneLoader loader;

    @Override
    public void create() {
        Viewport viewport = new FitViewport(800, 480);
        loader = new SceneLoader();
        loader.loadScene("MainScene", viewport);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(36/225f, 20/225f, 116/225f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        loader.getEngine().update(Gdx.graphics.getDeltaTime());
    }
}