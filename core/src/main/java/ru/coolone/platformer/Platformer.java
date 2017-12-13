package ru.coolone.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Platformer extends ApplicationAdapter {
    private SceneLoader loader;

    @Override
    public void create() {
        Viewport viewport = new FitViewport(800, 480);
        loader = new SceneLoader();
        loader.loadScene("MainScene", viewport);

        // Create scripts
        Player player = new Player(loader.world);

        InputMultiplexer inputMultiplexer = new InputMultiplexer(
                player
        );
        Gdx.input.setInputProcessor(inputMultiplexer);

        // Add scripts
        ItemWrapper root = new ItemWrapper(loader.getRoot());
        root.getChild("playerComposite")
                .addScript(player.new CompositeScript());
        root.getChild("playerComposite")
                .getChild("playerSpriter")
                .addScript(player.new SpriterScript());
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(36 / 225f, 20 / 225f, 116 / 225f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        loader.getEngine().update(Gdx.graphics.getDeltaTime());
    }
}