package ru.coolone.adventure_emulation.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import ru.coolone.adventure_emulation.Core;
import ru.coolone.adventure_emulation.camera.Camera;

/**
 * Manages @{@link ScreenScene}'s
 *
 * @author coolone
 */

public class ScreenManager {

    @SuppressWarnings("unused")
    private static final String TAG = ScreenManager.class.getSimpleName();

    /**
     * Camera, looks on {@link #viewport}
     */
    public final Camera camera = new Camera();
    /**
     * Viewport to scene
     */
    private final FitViewport viewport = new FitViewport(Core.WIDTH, Core.HEIGHT);
    /**
     * Loads {@link ru.coolone.adventure_emulation.screen.ScreenScene}'s scene
     */
    private final SceneLoader loader = new SceneLoader();
    /**
     * Map of {@link ru.coolone.adventure_emulation.screen.ScreenScene}
     */
    private final Map<Class<? extends ScreenScene>, ScreenScene> screenMap = new HashMap<>();
    /**
     * Link to @{@link Game}
     */
    private final Core core;
    /**
     * Current @{@link ScreenScene} link
     */
    @Getter
    private ScreenScene currentScreen;
    /**
     * Scene root @{@link ItemWrapper}
     */
    @Getter
    private ItemWrapper rootItem;

    public ScreenManager(Core core) {
        this.core = core;
        viewport.setCamera(
                camera
        );
    }

    /**
     * Updates current loaded @{@link ScreenScene :scene}
     *
     * @param delta Delta time in milliseconds
     */
    public void updateEngine(float delta) {
        loader.getEngine().update(delta);
    }

    /**
     * Loads screen scene and opens screen
     *
     * @param screenClass @{@link ScreenScene} class, for example @{@link ru.coolone.adventure_emulation.screens.GameScreen}
     */
    @SneakyThrows
    public void openScreen(Class<? extends ScreenScene> screenClass) {
        ScreenScene screenInstance;

        // Hide old screen
        if (currentScreen != null)
            currentScreen.hide();

        // Add screen to map if no exists
        if (!screenMap.containsKey(screenClass)) {
            // Get constructor
            val screenConstr = screenClass.getConstructor(Core.class);
            if (screenConstr != null) {
                // Create screen
                screenInstance = screenConstr.newInstance(core);

                // Add screen to map
                screenMap.put(screenClass, screenInstance);
            } else throw new RuntimeException("Screen constructor find error!");
        } else screenInstance = screenMap.get(screenClass);

        // Load scene
        loader.loadScene(screenInstance.name, viewport);

        // Refresh root item
        rootItem = new ItemWrapper(loader.getRoot());

        // Open screen
        core.setScreen(screenInstance);

        // Change currentScreen
        currentScreen = screenInstance;
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

    public Vector2 screenToWorldCoord(Vector2 coord) {
        Vector3 screenCoord3d = new Vector3(coord.x, coord.y, 0f);

        Vector3 worldCoord3d = camera.unproject(screenCoord3d);

        return new Vector2(worldCoord3d.x, worldCoord3d.y);
    }
}
