package ru.coolone.adventure_emulation;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.uwsoft.editor.renderer.physics.PhysicsBodyLoader;

import lombok.Getter;
import ru.coolone.adventure_emulation.input.InputGroups;
import ru.coolone.adventure_emulation.screen.ScreenManager;
import ru.coolone.adventure_emulation.screen.ScreenScene;
import ru.coolone.adventure_emulation.screens.MenuScreen;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Core extends Game {

    @SuppressWarnings("unused")
    private static final String TAG = Core.class.getSimpleName();

    /**
     * Debug output flag
     */
    static public boolean DEBUG = true;
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
     * @see ScreenManager
     */
    @Getter
    private ScreenManager screenManager;
    /**
     * Font for debug text
     */
    private BitmapFont font;
    /**
     * Butch for drawing ui
     */
    @Getter
    private Batch uiBatch;
    /**
     * @see InputGroups
     */
    @Getter
    private InputGroups inputGroups;

    @Override
    public void create() {
        create(
                new InputGroups(),
                new ScreenManager(this),
                START_SCREEN
        );
    }

    public void create(
            InputGroups inputGroups,
            ScreenManager screenManager,
            Class<? extends ScreenScene> startScreen
    ) {
        // Input groups
        this.inputGroups = inputGroups;

        // Debug
        uiBatch = new SpriteBatch();
        font = new BitmapFont();

        // Screen manager
        this.screenManager = screenManager;
        screenManager.openScreen(startScreen);
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
            getUiBatch().begin();

            // Current scene and screen
            font.draw(getUiBatch(),
                    "Screen: " + getScreen().getClass().getSimpleName() + '\n'
                            + "Scene: " + screenManager.getCurrentScreen().getName() + '\n'
                            + "Camera indent: " + screenManager.getCamera().indent + '\n'
                            + "World scale: " + PhysicsBodyLoader.getScale() + '\n'
                            + "Active input groups: " + getInputGroups().getActiveGroups(),
                    Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() - 10
            );

            getUiBatch().end();
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