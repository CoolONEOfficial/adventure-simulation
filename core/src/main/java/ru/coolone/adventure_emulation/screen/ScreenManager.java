package ru.coolone.adventure_emulation.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.HashMap;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import ru.coolone.adventure_emulation.Core;
import ru.coolone.adventure_emulation.camera.Camera;
import ru.coolone.adventure_emulation.other.vectors.Vector2;
import ru.coolone.adventure_emulation.other.vectors.Vector3;

/**
 * Manages @{@link ScreenScene}'s
 *
 * @author coolone
 */
@RequiredArgsConstructor
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
    final FitViewport viewport = new FitViewport(Core.WIDTH, Core.HEIGHT);
    /**
     * Loads {@link ru.coolone.adventure_emulation.screen.ScreenScene}'s scene
     */
    final SceneLoader loader;
    /**
     * Map of {@link ru.coolone.adventure_emulation.screen.ScreenScene}
     */
    private final HashMap<Class<? extends ScreenScene>, ScreenScene> screenMap;
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
        loader = new SceneLoader();
        screenMap = new HashMap<>();
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
    public void openScreen(Class<? extends ScreenScene> screenClass) {
        // Hide old screen
        if (currentScreen != null)
            currentScreen.hide();

        val screenInstance = getScreen(screenClass);

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
     * Find in {@link #screenMap} or create @{@link ScreenScene} and return him
     *
     * @param screenClass Class of @{@link ScreenScene}
     * @return New or exists in {@link #screenMap} @{@link ScreenScene}
     */
    @SneakyThrows
    ScreenScene getScreen(Class<? extends ScreenScene> screenClass) {
        ScreenScene screen;

        if (!screenMap.containsKey(screenClass)) {
            // Add screen to map

            // Get constructor
            val screenConstr = screenClass.getConstructor(Core.class);
            if (screenConstr != null) {
                // Create screen
                screen = screenConstr.newInstance(core);

                // Add screen to map
                screenMap.put(screenClass, screen);
            } else throw new RuntimeException("Screen constructor find error!");
        } else screen = screenMap.get(screenClass);

        return screen;
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
        return new Vector2(
                camera.unproject(
                        new Vector3(coord)
                )
        );
    }
}
