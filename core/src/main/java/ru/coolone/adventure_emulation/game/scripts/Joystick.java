package ru.coolone.adventure_emulation.game.scripts;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import ru.coolone.adventure_emulation.GameCore;
import ru.coolone.adventure_emulation.input.InputGroups;

/**
 * Joystick class (based on CompositeItem)
 *
 * @author coolone
 */

public class Joystick extends Composite
        implements InputProcessor {
    private static final String TAG = Joystick.class.getSimpleName();

    /**
     * Link to @{@link GameCore}
     */
    private GameCore core;

    /**
     * @see Stick
     */
    public Stick stick;

    /**
     * @see Background
     */
    public Background bg;

    public Joystick(
            GameCore core,
            String name
    ) {
        this.core = core;

        ItemWrapper root = this.core
                .getScreenManager()
                .getRootItem();

        ItemWrapper composite = root.getChild(name);

        // Add scripts
        composite.addScript(this);
        stick = new Stick();
        composite.getChild("stick")
                .addScript(stick);
        bg = new Background();
        composite.getChild("bg")
                .addScript(bg);

        InputGroups.multiplexer.addProcessor(this);
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    private int touchPointer = -1;

    private boolean intercepts(float x, float y) {
        x -= transform.x + bg.transform.x + bg.dimensions.width / 2;
        y -= transform.y + bg.transform.y + bg.dimensions.height / 2;
        final float radius = dimensions.width / 2f;

        return Math.sqrt(x * x + y * y) < radius;
    }

    private boolean intercepts(Vector2 coord) {
        return intercepts(coord.x, coord.y);
    }

    private boolean intercepts(Vector3 coord) {
        return intercepts(coord.x, coord.y);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (intercepts(
                core.getScreenManager()
                        .getCamera()
                        .unproject(
                                new Vector3(
                                        screenX,
                                        screenY,
                                        0f
                                )
                        )
        )) {
            touchPointer = pointer;
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (pointer == touchPointer) {
            // Move stick to center of bg
            stick.transform.x = bg.transform.x
                    + bg.dimensions.width / 2f
                    - stick.dimensions.width / 2f;
            stick.transform.y = bg.transform.y
                    + bg.dimensions.height / 2f
                    - stick.dimensions.height / 2f;

            // Clear touchPointer
            touchPointer = -1;
        }

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (pointer == touchPointer) {
            Vector2 newStickCoord = core.screenToWorldCoord(
                    new Vector2(
                            screenX,
                            screenY
                    )
            );

            // Untranslate from CompositeItem
            newStickCoord.x -= transform.x;
            newStickCoord.y -= transform.y;

            // To center of stick
            newStickCoord.x -= stick.dimensions.width / 2f;
            newStickCoord.y -= stick.dimensions.height / 2f;

            stick.transform.x = newStickCoord.x;
            stick.transform.y = newStickCoord.y;
        }

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}

class Composite implements IScript {

    /**
     * Components
     */
    public TransformComponent transform;
    public DimensionsComponent dimensions;

    @Override
    public void init(Entity entity) {
        // Components
        transform = ComponentRetriever.get(entity, TransformComponent.class);
        dimensions = ComponentRetriever.get(entity, DimensionsComponent.class);
    }

    @Override
    public void act(float delta) {

    }

    @Override
    public void dispose() {

    }
}

class Stick implements IScript {

    /**
     * Components
     */
    public TransformComponent transform;
    public DimensionsComponent dimensions;

    @Override
    public void init(Entity entity) {
        // Components
        transform = ComponentRetriever.get(entity, TransformComponent.class);
        dimensions = ComponentRetriever.get(entity, DimensionsComponent.class);
    }

    @Override
    public void act(float delta) {

    }

    @Override
    public void dispose() {

    }
}

class Background implements IScript {

    /**
     * Components
     */
    public TransformComponent transform;
    public DimensionsComponent dimensions;

    @Override
    public void init(Entity entity) {
        // Components
        transform = ComponentRetriever.get(entity, TransformComponent.class);
        dimensions = ComponentRetriever.get(entity, DimensionsComponent.class);
    }

    @Override
    public void act(float delta) {

    }

    @Override
    public void dispose() {

    }
}