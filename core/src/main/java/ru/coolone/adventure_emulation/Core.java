package ru.coolone.adventure_emulation;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.uwsoft.editor.renderer.physics.PhysicsBodyLoader;

import ru.coolone.adventure_emulation.input.InputGroups;
import ru.coolone.adventure_emulation.screen.ScreenManager;
import ru.coolone.adventure_emulation.screen.ScreenScene;
import ru.coolone.adventure_emulation.screens.MenuScreen;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Core extends Game {

    private static final String TAG = Core.class.getSimpleName();

    /**
     * Debug output flag
     */
    static public final boolean DEBUG = true;
    /**
     * Scenes and screen size
     */
    public static final int WIDTH = 800;
    public static final int HEIGHT = 480;
    /**
     * Screen class pointer, that will be opens on startup
     */
    private static final Class<? extends ScreenScene> START_SCREEN = MenuScreen.class;
    /**
     * Font for debug text
     */
    private BitmapFont font;
    /**
     * Butch for drawing ui
     */
    private Batch uiBatch;
    /**
     * @see ScreenManager
     */
    private ScreenManager screenManager;
    /**
     * @see InputGroups
     */
    private InputGroups inputGroups;

    @Override
    public void create() {
        // Screen manager
        screenManager = new ScreenManager(this);
        screenManager.openScreen(START_SCREEN);

        // Input groups
        inputGroups = new InputGroups();

        // Debug
        uiBatch = new SpriteBatch();
        font = new BitmapFont();
    }

    /**
     * @return @{@link ScreenManager}
     */
    public ScreenManager getScreenManager() {
        return screenManager;
    }

    /**
     * @return @{@link InputGroups}
     */
    public InputGroups getInputGroups() {
        return inputGroups;
    }

    /**
     * @return Ui @{@link Batch} for drawing some top of all
     */
    public Batch getUiBatch() {
        return uiBatch;
    }

    public Vector2 screenToWorldCoord(final Vector2 coord) {
        Vector3 screenCoord3d = new Vector3(coord.x, coord.y, 0f);

        Vector3 worldCoord3d = screenManager.getCamera().unproject(screenCoord3d);

        Vector2 worldCoord = new Vector2(worldCoord3d.x, worldCoord3d.y);

        return worldCoord;
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(36 / 225f, 20 / 225f, 116 / 225f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update Overlap2d scene
        screenManager.updateEngine(Gdx.graphics.getDeltaTime());

        // Render current screen
        super.render();

        // Debug
        if (DEBUG) {
            uiBatch.begin();

            // Current scene and screen
            font.draw(uiBatch,
                    "Screen: " + getScreen().getClass().getSimpleName() + '\n'
                            + "Scene: " + screenManager.getCurrentScreen().getName() + '\n'
                            + "Camera position: " + screenManager.getCamera().position + '\n'
                            + "World scale: " + PhysicsBodyLoader.getScale() + '\n'
                            + "Active input groups: " + inputGroups.getActiveGroups(),
                    Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() - 10
            );

            uiBatch.end();
        }
    }

    @Override
    public void dispose() {
    }

    @Override
    public void resize(int width, int height) {
        screenManager.openScreen(screenManager.getCurrentScreen().getClass());

        super.resize(width, height);
    }
}