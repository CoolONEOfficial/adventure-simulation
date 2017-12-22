package ru.coolone.adventure_emulation;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.physics.PhysicsBodyLoader;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import ru.coolone.adventure_emulation.screens.MenuScreen;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class GameCore extends Game {
    /**
     * Scenes and screen size
     */
    public static final int WIDTH = 800;
    public static final int HEIGHT = 480;
    private static final String TAG = GameCore.class.getSimpleName();
    /**
     * Overlap2d scene loader
     */
    public SceneLoader loader;
    /**
     * Camera to world
     */
    public OrthographicCamera camera;

    public static GameCore getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    @Override
    public void create() {
        // Open scene
        loader = new SceneLoader();

        // Camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false,
                WIDTH * PhysicsBodyLoader.getScale(),
                HEIGHT * PhysicsBodyLoader.getScale()
        );

        setScreen(new MenuScreen(
                        "MenuScene", loader
                )
        );
    }

    /**
     * Root item of loader
     */
//    private static ItemWrapper rootItem;
    public ItemWrapper getRootItem() {
        return new ItemWrapper(loader.getRoot());
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(36 / 225f, 20 / 225f, 116 / 225f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Overlap2d scene
        loader.getEngine().update(Gdx.graphics.getDeltaTime());

        super.render();
    }

    @Override
    public void dispose() {
    }

    /**
     * Singleton
     */
    private static class SingletonHolder {
        private static final GameCore HOLDER_INSTANCE = new GameCore();
    }
}