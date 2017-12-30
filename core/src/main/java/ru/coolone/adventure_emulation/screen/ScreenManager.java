package ru.coolone.adventure_emulation.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import ru.coolone.adventure_emulation.Core;

/**
 * Manages @{@link ScreenScene}'s
 *
 * @author coolone
 */

public class ScreenManager {

    private static final String TAG = ScreenManager.class.getSimpleName();

    /**
     * Link to @{@link Game}
     */
    private Core core;

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
    private final Map<Class<? extends ScreenScene>, ScreenScene> screenMap = new HashMap<Class<? extends ScreenScene>, ScreenScene>();

    /**
     * Current @{@link ScreenScene} link
     */
    private ScreenScene currentScreen;

    /**
     * Scene root @{@link ItemWrapper}
     */
    private ItemWrapper rootItem;

    public ScreenManager(Core core) {
        this.core = core;
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
        ScreenScene screenInstance = null;

        // Hide old screen
        if (currentScreen != null)
            currentScreen.hide();

        // Add screen to map if no exists
        if (!screenMap.containsKey(screenClass)) {
            // Get constructor
            Constructor<? extends ScreenScene> screenConstr = null;
            try {
                screenConstr = screenClass.getConstructor(Core.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            if (screenConstr != null) {
                // Create screen
                try {
                    screenInstance = screenConstr.newInstance(core);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                if (screenInstance != null) {
                    // Add screen to map
                    screenMap.put(screenClass, screenInstance);
                } else throw new RuntimeException("Screen constructor create error!");
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
     * @return Current loaded @{@link ScreenScene} @{@link Camera}
     */
    public Camera getCamera() {
        return viewport.getCamera();
    }

    public ScreenScene getCurrentScreen() {
        return currentScreen;
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

    /**
     * @return Root world @{@link ItemWrapper}
     */
    public ItemWrapper getRootItem() {
        return rootItem;
    }
}
