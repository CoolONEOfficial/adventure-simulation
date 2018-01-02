package ru.coolone.adventure_emulation.scripts.joystick;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;

import ru.coolone.adventure_emulation.Core;
import ru.coolone.adventure_emulation.input.InputGroups;
import ru.coolone.adventure_emulation.scripts.AbsTrigger;
import ru.coolone.adventure_emulation.scripts.joystick.trigger.JoystickTrigger;

/**
 * Joystick class (based on CompositeItem)
 *
 * @author coolone
 */

public class Joystick extends JoystickComposite
        implements InputProcessor {
    private static final String TAG = Joystick.class.getSimpleName();
    /**
     * Pressed touch pointer
     */
    private static final int TOUCH_POINTER_EMPTY = -1;
    /**
     * Names of @{@link JoystickTrigger}'s
     * Assigned to @{@link TriggerId}'s
     */
    public final String[] triggerNames = new String[]{
            "triggerCenter",    // CENTER
            "triggerLeft",      // LEFT
            "triggerRight",     // RIGHT
            "triggerUp",        // UP
            "triggerDown",      // DOWN
            "triggerRightUp",   // RIGHT_UP
            "triggerRightDown", // RIGHT_DOWN
            "triggerLeftUp",    // LEFT_UP
            "triggerLeftDown",  // LEFT_DOWN
    };
    /**
     * General @{@link TriggerId}'s
     */
    private final TriggerId[] generalTriggerIds = new TriggerId[]{
            TriggerId.LEFT,
            TriggerId.RIGHT,
            TriggerId.UP,
            TriggerId.DOWN
    };
    private final EnumMap<TriggerId, TriggerId[]> triggerChangeMap = new EnumMap<TriggerId, TriggerId[]>(TriggerId.class) {{
        put(
                TriggerId.LEFT,
                new TriggerId[]{
                        TriggerId.LEFT_UP,
                        TriggerId.LEFT_DOWN
                }
        );
        put(
                TriggerId.RIGHT,
                new TriggerId[]{
                        TriggerId.RIGHT_UP,
                        TriggerId.RIGHT_DOWN
                }
        );
        put(
                TriggerId.UP,
                new TriggerId[]{
                        TriggerId.LEFT_UP,
                        TriggerId.RIGHT_UP
                }
        );
        put(
                TriggerId.DOWN,
                new TriggerId[]{
                        TriggerId.LEFT_DOWN,
                        TriggerId.RIGHT_DOWN
                }
        );
    }};
    private final EnumMap<TriggerId, InputGroups.InputGroupId> triggerInputGroups;
    /**
     * Array of @{@link Listener}'s
     */
    private final ArrayList<Listener> listeners = new ArrayList<Listener>();
    /**
     * @see JoystickStick
     */
    public JoystickStick stick;
    /**
     * @see JoystickBackground
     */
    public JoystickBackground bg;
    /**
     * Link to @{@link Core}
     */
    private Core core;
    /**
     * Active at this moment @{@link TriggerId}
     */
    private TriggerId currentTriggerId;
    /**
     * Array of @{@link JoystickTrigger}'s
     */
    private final JoystickTrigger[] triggers = new JoystickTrigger[]{
            // CENTER
            new JoystickTrigger<TriggerId>(
                    new AbsTrigger.Listener() {
                        @Override
                        public void onTriggerActivate() {
                            // Show generalTriggerIds
                            for (TriggerId mVisTrigger : generalTriggerIds)
                                triggers[mVisTrigger.ordinal()].mainItem.visible = true;
                        }

                        @Override
                        public void onTriggerDeactivate() {
                        }

                        @Override
                        public void onTriggerChanged(Enum nextId) {
                            TriggerId nextTriggerId = (TriggerId) nextId;
                            if (nextTriggerId != TriggerId.LEFT)
                                getTrigger(TriggerId.LEFT).mainItem.visible = false;
                        }
                    }
            ),

            // LEFT
            new JoystickTrigger<TriggerId>(
                    new AbsTrigger.Listener() {
                        @Override
                        public void onTriggerActivate() {
                            getTrigger(TriggerId.CENTER).mainItem.visible = true;
                            getTrigger(TriggerId.LEFT_UP).mainItem.visible = true;
                            getTrigger(TriggerId.LEFT_DOWN).mainItem.visible = true;
                        }

                        @Override
                        public void onTriggerDeactivate() {
                        }

                        @Override
                        public void onTriggerChanged(Enum nextId) {
                            switch ((TriggerId) nextId) {
                                case LEFT_DOWN:
                                case LEFT_UP:
                                    break;
                                default:
                                    getTrigger(TriggerId.LEFT_UP).mainItem.visible = false;
                                    getTrigger(TriggerId.LEFT_DOWN).mainItem.visible = false;
                            }
                        }
                    }
            ),

            // RIGHT
            new JoystickTrigger<TriggerId>(
                    new AbsTrigger.Listener() {
                        @Override
                        public void onTriggerActivate() {
                            getTrigger(TriggerId.CENTER).mainItem.visible = true;
                            getTrigger(TriggerId.RIGHT_DOWN).mainItem.visible = true;
                            getTrigger(TriggerId.RIGHT_UP).mainItem.visible = true;
                        }

                        @Override
                        public void onTriggerDeactivate() {
                        }

                        @Override
                        public void onTriggerChanged(Enum nextId) {
                            switch ((TriggerId) nextId) {
                                case RIGHT_DOWN:
                                case RIGHT_UP:
                                    break;
                                default:
                                    getTrigger(TriggerId.RIGHT_DOWN).mainItem.visible = false;
                                    getTrigger(TriggerId.RIGHT_UP).mainItem.visible = false;
                            }
                        }
                    }
            ),

            // UP
            new JoystickTrigger(
                    new AbsTrigger.Listener() {
                        @Override
                        public void onTriggerActivate() {
                            getTrigger(TriggerId.CENTER).mainItem.visible = true;
                            getTrigger(TriggerId.LEFT_UP).mainItem.visible = true;
                            getTrigger(TriggerId.RIGHT_UP).mainItem.visible = true;
                        }

                        @Override
                        public void onTriggerDeactivate() {

                        }

                        @Override
                        public void onTriggerChanged(Enum nextId) {
                            switch ((TriggerId) nextId) {
                                case LEFT_UP:
                                case RIGHT_UP:
                                    break;
                                default:
                                    getTrigger(TriggerId.LEFT_UP).mainItem.visible = false;
                                    getTrigger(TriggerId.RIGHT_UP).mainItem.visible = false;
                            }
                        }
                    }
            ),

            // DOWN
            new JoystickTrigger(
                    new AbsTrigger.Listener() {
                        @Override
                        public void onTriggerActivate() {
                            getTrigger(TriggerId.CENTER).mainItem.visible = true;
                            getTrigger(TriggerId.LEFT_DOWN).mainItem.visible = true;
                            getTrigger(TriggerId.RIGHT_DOWN).mainItem.visible = true;
                        }

                        @Override
                        public void onTriggerDeactivate() {

                        }

                        @Override
                        public void onTriggerChanged(Enum nextId) {
                            switch ((TriggerId) nextId) {
                                case LEFT_DOWN:
                                case RIGHT_DOWN:
                                    break;
                                default:
                                    getTrigger(TriggerId.LEFT_DOWN).mainItem.visible = false;
                                    getTrigger(TriggerId.RIGHT_DOWN).mainItem.visible = false;
                            }
                        }
                    }
            ),

            // RIGHT_UP
            new JoystickTrigger(
                    new AbsTrigger.Listener() {
                        TriggerId oldTrigger;
                        InputGroups.InputGroupId activeInputGroup;

                        @Override
                        public void onTriggerActivate() {
                            oldTrigger = currentTriggerId;

                            // Activate InputGroup
                            TriggerId activateGroupTrigger = null;
                            switch (oldTrigger) {
                                case UP:
                                    activateGroupTrigger = TriggerId.RIGHT;
                                    break;
                                case RIGHT:
                                    activateGroupTrigger = TriggerId.UP;
                                    break;
                            }
                            if (activateGroupTrigger != null) {
                                activeInputGroup = triggerInputGroups.get(activateGroupTrigger);
                                core.getInputGroups()
                                        .groupActivate(activeInputGroup);
                            }
                        }

                        @Override
                        public void onTriggerDeactivate() {
                            switch (oldTrigger) {
                                case RIGHT:
                                    getTrigger(TriggerId.RIGHT_DOWN).mainItem.visible = false;
                                    core.getInputGroups()
                                            .groupDeactivate(
                                                    triggerInputGroups.get(
                                                            TriggerId.RIGHT
                                                    )
                                            );
                                    break;
                                case UP:
                                    getTrigger(TriggerId.LEFT_UP).mainItem.visible = false;
                                    core.getInputGroups()
                                            .groupDeactivate(
                                                    triggerInputGroups.get(
                                                            TriggerId.UP
                                                    )
                                            );
                                    break;
                            }
                            getTrigger(TriggerId.RIGHT_UP).mainItem.visible = false;
                            core.getInputGroups()
                                    .groupDeactivate(activeInputGroup);
                        }

                        @Override
                        public void onTriggerChanged(Enum nextId) {
                        }
                    }
            ),

            // RIGHT_DOWN
            new JoystickTrigger(
                    new AbsTrigger.Listener() {
                        TriggerId oldTrigger;
                        InputGroups.InputGroupId activeGroup;

                        @Override
                        public void onTriggerActivate() {
                            oldTrigger = currentTriggerId;

                            // Activate InputGroup
                            TriggerId activateGroupTrigger = null;
                            switch (oldTrigger) {
                                case DOWN:
                                    activateGroupTrigger = TriggerId.RIGHT;
                                    break;
                                case RIGHT:
                                    activateGroupTrigger = TriggerId.DOWN;
                                    break;
                            }
                            if (activateGroupTrigger != null) {
                                activeGroup = triggerInputGroups.get(activateGroupTrigger);
                                core.getInputGroups()
                                        .groupActivate(activeGroup);
                            }
                        }

                        @Override
                        public void onTriggerDeactivate() {
                            oldTrigger = currentTriggerId;

                            switch (oldTrigger) {
                                case RIGHT:
                                    getTrigger(TriggerId.RIGHT_DOWN).mainItem.visible = false;
                                    core.getInputGroups()
                                            .groupDeactivate(
                                                    triggerInputGroups.get(
                                                            TriggerId.RIGHT
                                                    )
                                            );
                                    break;
                                case DOWN:
                                    getTrigger(TriggerId.LEFT_DOWN).mainItem.visible = false;
                                    core.getInputGroups()
                                            .groupDeactivate(
                                                    triggerInputGroups.get(
                                                            TriggerId.DOWN
                                                    )
                                            );
                                    break;
                            }
                            getTrigger(TriggerId.RIGHT_DOWN).mainItem.visible = false;
                            core.getInputGroups()
                                    .groupDeactivate(activeGroup);
                        }

                        @Override
                        public void onTriggerChanged(Enum nextId) {

                        }
                    }
            ),

            // LEFT_UP
            new JoystickTrigger(
                    new AbsTrigger.Listener() {
                        TriggerId oldTrigger;
                        InputGroups.InputGroupId activeGroup;

                        @Override
                        public void onTriggerActivate() {
                            oldTrigger = currentTriggerId;

                            // Activate InputGroup
                            TriggerId activateGroupTrigger = null;
                            switch (oldTrigger) {
                                case UP:
                                    activateGroupTrigger = TriggerId.LEFT;
                                    break;
                                case LEFT:
                                    activateGroupTrigger = TriggerId.UP;

                                    break;
                            }
                            if (activateGroupTrigger != null) {
                                activeGroup = triggerInputGroups.get(activateGroupTrigger);
                                core.getInputGroups()
                                        .groupActivate(activeGroup);
                            }
                        }

                        @Override
                        public void onTriggerDeactivate() {
                            switch (oldTrigger) {
                                case LEFT:
                                    getTrigger(TriggerId.LEFT_DOWN).mainItem.visible = false;
                                    core.getInputGroups()
                                            .groupDeactivate(
                                                    triggerInputGroups.get(
                                                            TriggerId.LEFT
                                                    )
                                            );
                                    break;
                                case UP:
                                    getTrigger(TriggerId.RIGHT_UP).mainItem.visible = false;
                                    core.getInputGroups()
                                            .groupDeactivate(
                                                    triggerInputGroups.get(
                                                            TriggerId.UP
                                                    )
                                            );
                                    break;
                            }
                            getTrigger(TriggerId.LEFT_UP).mainItem.visible = false;
                            core.getInputGroups()
                                    .groupDeactivate(activeGroup);
                        }

                        @Override
                        public void onTriggerChanged(Enum nextId) {

                        }
                    }
            ),

            // LEFT_DOWN
            new JoystickTrigger(
                    new AbsTrigger.Listener() {
                        TriggerId oldTrigger;
                        InputGroups.InputGroupId activeGroup;

                        @Override
                        public void onTriggerActivate() {
                            oldTrigger = currentTriggerId;

                            // Activate InputGroup
                            TriggerId activateGroupTrigger = null;
                            switch (oldTrigger) {
                                case DOWN:
                                    activateGroupTrigger = TriggerId.LEFT;
                                    break;
                                case LEFT:
                                    activateGroupTrigger = TriggerId.DOWN;
                                    break;
                            }
                            if (activateGroupTrigger != null) {
                                activeGroup = triggerInputGroups.get(activateGroupTrigger);
                                core.getInputGroups()
                                        .groupActivate(activeGroup);
                            }
                        }

                        @Override
                        public void onTriggerDeactivate() {
                            switch (oldTrigger) {
                                case LEFT:
                                    getTrigger(TriggerId.LEFT_DOWN).mainItem.visible = false;
                                    core.getInputGroups()
                                            .groupDeactivate(
                                                    triggerInputGroups.get(
                                                            TriggerId.LEFT
                                                    )
                                            );
                                    break;
                                case DOWN:
                                    getTrigger(TriggerId.RIGHT_DOWN).mainItem.visible = false;
                                    core.getInputGroups()
                                            .groupDeactivate(
                                                    triggerInputGroups.get(
                                                            TriggerId.DOWN
                                                    )
                                            );
                                    break;
                            }
                            getTrigger(TriggerId.LEFT_DOWN).mainItem.visible = false;
                            core.getInputGroups()
                                    .groupDeactivate(activeGroup);
                        }

                        @Override
                        public void onTriggerChanged(Enum nextId) {

                        }
                    }
            ),
    };
    private int touchPointer = TOUCH_POINTER_EMPTY;

    /**
     * @param core               @see {@link #core}
     * @param name               CompositeItem name
     * @param triggerInputGroups Assigned to {@link #generalTriggerIds} @{@link ru.coolone.adventure_emulation.input.InputGroups.InputGroupId}'s
     */
    public Joystick(
            Core core,
            String name,
            EnumMap<TriggerId, InputGroups.InputGroupId> triggerInputGroups
    ) {
        this.core = core;
        if (triggerInputGroups.size() != generalTriggerIds.length)
            throw new RuntimeException("Triggered input groups array length wrong");
        this.triggerInputGroups = triggerInputGroups;

        // Create / Add scripts
        ItemWrapper root = this.core
                .getScreenManager()
                .getRootItem();

        ItemWrapper composite = root.getChild(name);
        composite.addScript(this);

        stick = new JoystickStick();
        composite.getChild("stick")
                .addScript(stick);

        bg = new JoystickBackground();
        composite.getChild("bg")
                .addScript(bg);

        for (int mTriggerId = 0; mTriggerId < TriggerId.COUNT.ordinal(); mTriggerId++) {
            // Add script
            String mTriggerName = triggerNames[mTriggerId];
            JoystickTrigger mTriggerScript = triggers[mTriggerId];

            composite.getChild(mTriggerName)
                    .addScript(mTriggerScript);
        }

        // Listen input
        core.getInputGroups()
                .multiplexer
                .addProcessor(this);
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
     * @param x      Check x
     * @param y      Check y
     * @param radius Circle radius
     * @return Check intercept result
     */
    private boolean intercepts(float x, float y, float radius) {
        Gdx.app.log(TAG, "Intercept trigger: \n"
                + '\t' + "x: " + x + " y: " + y + '\n'
                + '\t' + "radius: " + radius);

        Circle circle = new Circle(
                transform.x + bg.transform.x + bg.dimensions.width / 2,
                transform.y + bg.transform.y + bg.dimensions.height / 2,
                radius
        );

        boolean ret = circle.contains(
                x, y
        );

        Gdx.app.log(TAG, "Joystick trigger intercepts:" + ret);

        return ret;
    }

    private boolean intercepts(float x, float y) {
        return intercepts(x, y, bg.dimensions.width / 2f);
    }

    private boolean intercepts(Vector3 coord) {
        return intercepts(coord.x, coord.y);
    }

    private boolean intercepts(Vector3 coord, float radius) {
        return intercepts(coord.x, coord.y, radius);
    }

    private boolean intercepts(Vector2 coord) {
        return intercepts(coord.x, coord.y);
    }

    private boolean intercepts(Vector2 coord, float radius) {
        return intercepts(coord.x, coord.y, radius);
    }

    public JoystickTrigger<TriggerId> getTrigger(TriggerId triggerId) {
        return triggers[triggerId.ordinal()];
    }

    public TriggerId getCurrentTriggerId() {
        return currentTriggerId;
    }

    public JoystickTrigger<TriggerId> getCurrentTrigger() {
        return (currentTriggerId != null)
                ? triggers[currentTriggerId.ordinal()]
                : null;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (intercepts(
                core.screenToWorldCoord(
                        new Vector2(
                                screenX,
                                screenY
                        )
                )
        )) {
            touchPointer = pointer;

            // Show generalTriggerIds
            for (TriggerId mVisTrigger : generalTriggerIds)
                triggers[mVisTrigger.ordinal()].mainItem.visible = true;

            // Emulate dragged
            touchDragged(screenX, screenY, pointer);
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (pointer == touchPointer) {
            // Hide add triggers
            for (JoystickTrigger mTrigger : triggers)
                mTrigger.mainItem.visible = false;

            // Move stick to center of bg
            stick.transform.x = bg.transform.x
                    + bg.dimensions.width / 2f
                    - stick.dimensions.width / 2f;
            stick.transform.y = bg.transform.y
                    + bg.dimensions.height / 2f
                    - stick.dimensions.height / 2f;

            // Deactivate activated general group
            int generalGroupIdId = Arrays.asList(generalTriggerIds).indexOf(currentTriggerId);
            if (generalGroupIdId != -1)
                core.getInputGroups()
                        .groupDeactivate(triggerInputGroups.get(generalTriggerIds[generalGroupIdId]));

            // Deactivate activated trigger
            if (currentTriggerId != null)
                getCurrentTrigger().deactivate();
            currentTriggerId = null;

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
                    core.screenToWorldCoord(
                            new Vector2(
                                    screenX,
                                    screenY
                            )
                    ),
                    bg.dimensions.width
            )) {
                // Move stick
                stick.transform.x = newStickCoord.x - stick.dimensions.width / 2f;
                stick.transform.y = newStickCoord.y - stick.dimensions.height / 2f;

                // Check intercepts triggers
                for (int newTriggerIdId = 0; newTriggerIdId < TriggerId.COUNT.ordinal(); newTriggerIdId++) {
                    TriggerId newTriggerId = TriggerId.values()[newTriggerIdId];
                    JoystickTrigger<TriggerId> newTrigger = getTrigger(newTriggerId);

                    if (newTrigger.mainItem.visible &&
                            !newTrigger.isActive() &&
                            newTrigger.intercepts(newStickCoord)) {
                        JoystickTrigger<TriggerId> oldTrigger = getCurrentTrigger();
                        TriggerId oldTriggerId = getCurrentTriggerId();

                        // Handle currentTriggerId change
                        for (Listener mListener : listeners)
                            mListener.onJoystickTriggerChanged(currentTriggerId, newTriggerId);

                        if (oldTrigger != null) {
                            // Handle change trigger
                            oldTrigger.onChanged(newTriggerId);

                            // Deactivate activated trigger
                            oldTrigger.deactivate();

                            // Deactivate InputGroup
                            int generalTriggerIdId = Arrays.asList(generalTriggerIds)
                                    .indexOf(oldTriggerId);
                            if (generalTriggerIdId != -1) {
                                if (!Arrays.asList(triggerChangeMap.get(oldTriggerId))
                                        .contains(newTriggerId))
                                    core.getInputGroups()
                                            .groupDeactivate(triggerInputGroups.get(generalTriggerIds[generalTriggerIdId]));
                            }
                        }

                        // Hide all other general triggers
                        if (Arrays.asList(generalTriggerIds).contains(newTriggerId)) {
                            // Show generalTriggerIds
                            for (TriggerId mVisTrigger : generalTriggerIds)
                                triggers[mVisTrigger.ordinal()].mainItem.visible = false;
                            getTrigger(newTriggerId).mainItem.visible = true;
                        }

                        // Activate intercepted trigger
                        newTrigger.activate();

                        // Activate InputGroup
                        int generalTriggerIdId = Arrays.asList(generalTriggerIds).indexOf(newTriggerId);
                        if (generalTriggerIdId != -1) {
                            core.getInputGroups()
                                    .groupActivate(triggerInputGroups.get(generalTriggerIds[generalTriggerIdId]));
                        }

                        // Refresh activated trigger
                        currentTriggerId = newTriggerId;
                    }
                }
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

    @Override
    public void dispose() {
        super.dispose();

        // Stop listen input
        core.getInputGroups().multiplexer.removeProcessor(this);
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public boolean removeListener(Listener listener) {
        return listeners.remove(listener);
    }

    /**
     * Id's to {@link #triggers}
     */
    public enum TriggerId {
        CENTER,
        LEFT,
        RIGHT,
        UP,
        DOWN,
        RIGHT_UP,
        RIGHT_DOWN,
        LEFT_UP,
        LEFT_DOWN,

        COUNT
    }

    /**
     * Listener for @{@link Joystick}
     */
    public interface Listener {
        /**
         * Called, after {@link #currentTriggerId} changed
         */
        void onJoystickTriggerChanged(TriggerId before, TriggerId after);
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

