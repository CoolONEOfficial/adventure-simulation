package ru.coolone.adventure_emulation.scripts.joystick;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import ru.coolone.adventure_emulation.Core;
import ru.coolone.adventure_emulation.input.InputGroups;
import ru.coolone.adventure_emulation.scripts.AbsTrigger;

/**
 * Joystick class (based on CompositeItem)
 *
 * @author coolone
 */

public class Joystick extends JoystickComposite
        implements InputProcessor {
    private static final String TAG = Joystick.class.getSimpleName();

    /**
     * Link to @{@link Core}
     */
    private Core core;

    /**
     * @see JoystickStick
     */
    public JoystickStick stick;

    /**
     * Move @{@link JoystickTrigger}'s
     */
    public JoystickTrigger triggerLeft;
    public JoystickTrigger triggerRight;
    public JoystickTrigger triggerLeftUp;
    public JoystickTrigger triggerLeftDown;
    public JoystickTrigger triggerRightUp;
    public JoystickTrigger triggerRightDown;
    public JoystickTrigger triggerUp;
    public JoystickTrigger triggerDown;

    /**
     * @see JoystickBackground
     */
    public JoystickBackground bg;

    public Joystick(
            Core core,
            String name
    ) {
        this.core = core;

        ItemWrapper root = this.core
                .getScreenManager()
                .getRootItem();

        ItemWrapper composite = root.getChild(name);

        // Add scripts
        composite.addScript(this);
        stick = new JoystickStick();
        composite.getChild("stick")
                .addScript(stick);
        bg = new JoystickBackground();
        composite.getChild("bg")
                .addScript(bg);
        triggerLeft = new JoystickTrigger(core);
        composite.getChild("triggerLeft")
                .addScript(triggerLeft);
        triggerRight = new JoystickTrigger(core);
        composite.getChild("triggerRight")
                .addScript(triggerRight);
        triggerUp = new JoystickTrigger(core);
        composite.getChild("triggerUp")
                .addScript(triggerUp);
        triggerDown = new JoystickTrigger(core);
        composite.getChild("triggerDown")
                .addScript(triggerDown);
        triggerRightUp = new JoystickTrigger(core);
        composite.getChild("triggerRightUp")
                .addScript(triggerRightUp);
        triggerRightDown = new JoystickTrigger(core);
        composite.getChild("triggerRightDown")
                .addScript(triggerRightDown);
        triggerLeftUp = new JoystickTrigger(core);
        composite.getChild("triggerLeftUp")
                .addScript(triggerLeftUp);
        triggerLeftDown = new JoystickTrigger(core);
        composite.getChild("triggerLeftDown")
                .addScript(triggerLeftDown);

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

    /**
     * Pressed touch pointer
     */
    private static final int TOUCH_POINTER_EMPTY = -1;
    private int touchPointer = TOUCH_POINTER_EMPTY;

    /**
     * @param point Check point
     * @param circlePoint Circle center point
     * @param radius Circle radius
     * @return Intercepts circle and @point bool
     */
    static private boolean getCircleIntercepts(
            Vector2 point,
            Vector2 circlePoint,
            float radius
    ) {
        Vector2 distVec = new Vector2(
                circlePoint.x - point.x,
                circlePoint.y - point.y
        );

        double dist = Math.sqrt(
                distVec.x * distVec.x
                        + distVec.y * distVec.y
        );

        return dist <= radius;
    }

    private boolean intercepts(float x, float y, float radius) {
        return getCircleIntercepts(
                new Vector2(
                        x,
                        y
                ),
                new Vector2(
                        transform.x + bg.transform.x + bg.dimensions.width / 2,
                        transform.y + bg.transform.y + bg.dimensions.height / 2
                ),
                radius
        );
    }

    private boolean intercepts(float x, float y) {
        return intercepts(x, y, dimensions.width / 2f);
    }

    private boolean intercepts(Vector3 coord) {
        return intercepts(coord.x, coord.y);
    }

    private boolean intercepts(Vector3 coord, float radius) {
        return intercepts(coord.x, coord.y, radius);
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

//            // Emulate dragged
//            touchDragged(screenX, screenY, pointer);
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
            touchPointer = TOUCH_POINTER_EMPTY;
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

            if (intercepts(
                    core.getScreenManager()
                            .getCamera()
                            .unproject(
                                    new Vector3(
                                            screenX,
                                            screenY,
                                            0f
                                    )
                            ),
                    dimensions.width
            )) {
                // Move stick
                stick.transform.x = newStickCoord.x - stick.dimensions.width / 2f;
                stick.transform.y = newStickCoord.y - stick.dimensions.height / 2f;

                // Update triggers active states
                triggerLeft.setActiveState(triggerLeft.intercepts(newStickCoord));
                triggerRight.setActiveState(triggerRight.intercepts(newStickCoord));
                triggerUp.setActiveState(triggerUp.intercepts(newStickCoord));
                triggerDown.setActiveState(triggerDown.intercepts(newStickCoord));
                triggerLeftUp.setActiveState(triggerLeftUp.intercepts(newStickCoord));
                triggerRightUp.setActiveState(triggerRightUp.intercepts(newStickCoord));
                triggerLeftDown.setActiveState(triggerLeftDown.intercepts(newStickCoord));
                triggerRightDown.setActiveState(triggerRightDown.intercepts(newStickCoord));
            }
        }

        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        touchDragged(screenX, screenY, 0);
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}

class JoystickComposite implements IScript {

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

class JoystickStick implements IScript {

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

class JoystickBackground implements IScript {

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

