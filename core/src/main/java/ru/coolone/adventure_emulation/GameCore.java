package ru.coolone.adventure_emulation;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.physics.box2d.World;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.physics.PhysicsBodyLoader;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import ru.coolone.adventure_emulation.screens.GameScreen;
import ru.coolone.adventure_emulation.screens.MenuScreen;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class GameCore extends Game {

    static public final boolean DEBUG = true;

    /**
     * Scenes and screen size
     */
    public static final int WIDTH = 800;
    public static final int HEIGHT = 480;
    private static final String TAG = GameCore.class.getSimpleName();
    public MenuScreen menuScreen;
    public GameScreen gameScreen;
    /**
     * Camera to world
     */
    public OrthographicCamera camera;
    BitmapFont font;
    /**
     * Overlap2d scene loader
     */
    private SceneLoader loader;
    /**
     * Root item of loader
     */
    private ItemWrapper rootItem;

    public ItemWrapper getRootItem() {
        return rootItem;
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

        // Debug
        font = new BitmapFont();

        // Screens
        menuScreen = new MenuScreen(this);
        gameScreen = new GameScreen(this);

        setScreen(
                menuScreen
        );
    }

    /**
     * Loads and opens scene
     *
     * @param sceneName Name of loading scene
     */
    public void loadScene(String sceneName) {
        // Refresh loader
        loader = new SceneLoader();

        // Load scene
        loader.loadScene(sceneName);

        // Refresh root item
        this.rootItem = new ItemWrapper(loader.getRoot());
    }

    /**
     * @return Current loaded scene @{@link Batch} for drawing in scene
     */
    public Batch getBatch() {
        return loader.getBatch();
    }

    /**
     * @return Current loaded scene @{@link World}
     */
    public World getWorld() {
        return loader.world;
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(36 / 225f, 20 / 225f, 116 / 225f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update Overlap2d scene
        loader.getEngine().update(Gdx.graphics.getDeltaTime());

        // Render current screen
        super.render();

        // Debug
        if (DEBUG) {
            Batch batch = loader.getBatch();
            batch.begin();

            // Current scene and screen
            font.draw(batch,
                    "Screen: " + getScreen().getClass().getSimpleName() + '\n'
                            + "Scene: " + loader.getSceneVO().sceneName,
                    WIDTH / 2, HEIGHT - 10
            );

            batch.end();
        }
    }

    @Override
    public void dispose() {
    }
}