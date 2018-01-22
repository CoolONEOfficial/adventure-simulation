package ru.coolone.adventure_emulation.scripts.joystick;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Circle;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;

import lombok.Getter;
import lombok.val;
import ru.coolone.adventure_emulation.Core;
import ru.coolone.adventure_emulation.input.InputGroups;
import ru.coolone.adventure_emulation.other.vectors.Vector2;
import ru.coolone.adventure_emulation.script.Script;
import ru.coolone.adventure_emulation.scripts.AbsTrigger;

/**
 * Joystick class (based on CompositeItem)
 *
 * @author coolone
 */

public class Joystick extends JoystickComposite
        implements InputProcessor {

    @SuppressWarnings("unused")
    private static final String TAG = Joystick.class.getSimpleName();

    /**
     * {@link #touchPointer} empty holder
     */
    private static final int TOUCH_POINTER_EMPTY = -1;
    /**
     * Names of @{@link JoystickTrigger}'s
     * Assigned to {@link #triggers}
     */
    public static final String[] triggerNames = new String[]{
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
     * @see JoystickStick
     */
    public final JoystickStick stick;
    /**
     * @see JoystickBackground
     */
    public final JoystickBackground bg;
    /**
     * Array of @{@link JoystickListener}'s
     */
    public final ArrayList<JoystickListener> joystickListeners = new ArrayList<>();
    /**
     * General @{@link TriggerId}'s
     */
    private final TriggerId[] generalTriggerIds = new TriggerId[]{
            TriggerId.LEFT,
            TriggerId.RIGHT,
            TriggerId.UP,
            TriggerId.DOWN
    };
    /**
     * Change map for triggers
     */
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
     * Link to @{@link Core}
     */
    private Core core;
    /**
     * Active at this moment @{@link TriggerId}
     */
    @Getter
    private TriggerId currentTriggerId;
    /**
     * Array of @{@link JoystickTrigger}'s
     */
    public final JoystickTrigger[] triggers = new JoystickTrigger[]{
            // CENTER
            new JoystickTrigger(
                    new AbsTrigger.AbsTriggerListener() {
                        @Override
                        public void onTriggerActivate() {
                            // Show generalTriggerIds
                            for (TriggerId mVisTrigger : generalTriggerIds)
                                triggers[mVisTrigger.ordinal()].setVisible(true);
                        }

                        @Override
                        public void onTriggerDeactivate() {
                            if (getCurrentTriggerId() != TriggerId.LEFT)
                                getTrigger(TriggerId.LEFT).setVisible(false);
                        }
                    }
            ),

            // LEFT
            new JoystickTrigger(
                    new AbsTrigger.AbsTriggerListener() {
                        @Override
                        public void onTriggerActivate() {
                            getTrigger(TriggerId.CENTER).setVisible(true);
                            getTrigger(TriggerId.LEFT_UP).setVisible(true);
                            getTrigger(TriggerId.LEFT_DOWN).setVisible(true);
                        }

                        @Override
                        public void onTriggerDeactivate() {
                            switch (getCurrentTriggerId()) {
                                case LEFT_DOWN:
                                case LEFT_UP:
                                    break;
                                default:
                                    getTrigger(TriggerId.LEFT_UP).setVisible(false);
                                    getTrigger(TriggerId.LEFT_DOWN).setVisible(false);
                            }
                        }
                    }
            ),

            // RIGHT
            new JoystickTrigger(
                    new AbsTrigger.AbsTriggerListener() {
                        @Override
                        public void onTriggerActivate() {
                            getTrigger(TriggerId.CENTER).setVisible(true);
                            getTrigger(TriggerId.RIGHT_DOWN).setVisible(true);
                            getTrigger(TriggerId.RIGHT_UP).setVisible(true);
                        }

                        @Override
                        public void onTriggerDeactivate() {
                            switch (getCurrentTriggerId()) {
                                case RIGHT_DOWN:
                                case RIGHT_UP:
                                    break;
                                default:
                                    getTrigger(TriggerId.RIGHT_DOWN).setVisible(false);
                                    getTrigger(TriggerId.RIGHT_UP).setVisible(false);
                            }
                        }
                    }
            ),

            // UP
            new JoystickTrigger(
                    new AbsTrigger.AbsTriggerListener() {
                        @Override
                        public void onTriggerActivate() {
                            getTrigger(TriggerId.CENTER).setVisible(true);
                            getTrigger(TriggerId.LEFT_UP).setVisible(true);
                            getTrigger(TriggerId.RIGHT_UP).setVisible(true);
                        }

                        @Override
                        public void onTriggerDeactivate() {
                            switch (getCurrentTriggerId()) {
                                case LEFT_UP:
                                case RIGHT_UP:
                                    break;
                                default:
                                    getTrigger(TriggerId.LEFT_UP).setVisible(false);
                                    getTrigger(TriggerId.RIGHT_UP).setVisible(false);
                            }
                        }
                    }
            ),

            // DOWN
            new JoystickTrigger(
                    new AbsTrigger.AbsTriggerListener() {
                        @Override
                        public void onTriggerActivate() {
                            getTrigger(TriggerId.CENTER).setVisible(true);
                            getTrigger(TriggerId.LEFT_DOWN).setVisible(true);
                            getTrigger(TriggerId.RIGHT_DOWN).setVisible(true);
                        }

                        @Override
                        public void onTriggerDeactivate() {
                            switch (getCurrentTriggerId()) {
                                case LEFT_DOWN:
                                case RIGHT_DOWN:
                                    break;
                                default:
                                    getTrigger(TriggerId.LEFT_DOWN).setVisible(false);
                                    getTrigger(TriggerId.RIGHT_DOWN).setVisible(false);
                            }
                        }
                    }
            ),

            // RIGHT_UP
            new JoystickTrigger(
                    new AbsTrigger.AbsTriggerListener() {
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
                                    getTrigger(TriggerId.RIGHT_DOWN).setVisible(false);
                                    core.getInputGroups()
                                            .groupDeactivate(
                                                    triggerInputGroups.get(
                                                            TriggerId.RIGHT
                                                    )
                                            );
                                    break;
                                case UP:
                                    getTrigger(TriggerId.LEFT_UP).setVisible(false);
                                    core.getInputGroups()
                                            .groupDeactivate(
                                                    triggerInputGroups.get(
                                                            TriggerId.UP
                                                    )
                                            );
                                    break;
                            }
                            getTrigger(TriggerId.RIGHT_UP).setVisible(false);
                            core.getInputGroups()
                                    .groupDeactivate(activeInputGroup);
                        }
                    }
            ),

            // RIGHT_DOWN
            new JoystickTrigger(
                    new AbsTrigger.AbsTriggerListener() {
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
                                    getTrigger(TriggerId.RIGHT_DOWN).setVisible(false);
                                    core.getInputGroups()
                                            .groupDeactivate(
                                                    triggerInputGroups.get(
                                                            TriggerId.RIGHT
                                                    )
                                            );
                                    break;
                                case DOWN:
                                    getTrigger(TriggerId.LEFT_DOWN).setVisible(false);
                                    core.getInputGroups()
                                            .groupDeactivate(
                                                    triggerInputGroups.get(
                                                            TriggerId.DOWN
                                                    )
                                            );
                                    break;
                            }
                            getTrigger(TriggerId.RIGHT_DOWN).setVisible(false);
                            core.getInputGroups()
                                    .groupDeactivate(activeGroup);
                        }
                    }
            ),

            // LEFT_UP
            new JoystickTrigger(
                    new AbsTrigger.AbsTriggerListener() {
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
                                    getTrigger(TriggerId.LEFT_DOWN).setVisible(false);
                                    core.getInputGroups()
                                            .groupDeactivate(
                                                    triggerInputGroups.get(
                                                            TriggerId.LEFT
                                                    )
                                            );
                                    break;
                                case UP:
                                    getTrigger(TriggerId.RIGHT_UP).setVisible(false);
                                    core.getInputGroups()
                                            .groupDeactivate(
                                                    triggerInputGroups.get(
                                                            TriggerId.UP
                                                    )
                                            );
                                    break;
                            }
                            getTrigger(TriggerId.LEFT_UP).setVisible(false);
                            core.getInputGroups()
                                    .groupDeactivate(activeGroup);
                        }
                    }
            ),

            // LEFT_DOWN
            new JoystickTrigger(
                    new AbsTrigger.AbsTriggerListener() {
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
                                    getTrigger(TriggerId.LEFT_DOWN).setVisible(false);
                                    core.getInputGroups()
                                            .groupDeactivate(
                                                    triggerInputGroups.get(
                                                            TriggerId.LEFT
                                                    )
                                            );
                                    break;
                                case DOWN:
                                    getTrigger(TriggerId.RIGHT_DOWN).setVisible(false);
                                    core.getInputGroups()
                                            .groupDeactivate(
                                                    triggerInputGroups.get(
                                                            TriggerId.DOWN
                                                    )
                                            );
                                    break;
                            }
                            getTrigger(TriggerId.LEFT_DOWN).setVisible(false);
                            core.getInputGroups()
                                    .groupDeactivate(activeGroup);
                        }
                    }
            )
    };
    /**
     * Pressed touch pointer
     */
    private int touchPointer = TOUCH_POINTER_EMPTY;

    /**
     * @param core               @see {@link #core}
     * @param name               CompositeItem name
     * @param triggerInputGroups Assigned to {@link #generalTriggerIds} @{@link InputGroups.InputGroupId}'s
     */
    public Joystick(
            Core core,
            String name,
            EnumMap<TriggerId, InputGroups.InputGroupId> triggerInputGroups
    ) {
        this.core = core;
        this.triggerInputGroups = triggerInputGroups;

        // Create / Add scripts
        val root = this.core
                .getScreenManager()
                .getRootItem();

        val composite = root.getChild(name);
        composite.addScript(this);

        stick = new JoystickStick();
        composite.getChild("stick")
                .addScript(stick);

        bg = new JoystickBackground();
        composite.getChild("bg")
                .addScript(bg);

        for (int mTriggerId = 0; mTriggerId < TriggerId.COUNT.ordinal(); mTriggerId++) {
            // Add script
            val mTriggerName = triggerNames[mTriggerId];
            val mTriggerScript = triggers[mTriggerId];

            composite.getChild(mTriggerName)
                    .addScript(mTriggerScript);
        }

        // Listen input
        core.getInputGroups()
                .getMultiplexer()
                .addProcessor(this);

        // Show all general triggers
        for (TriggerId mVisTrigger : generalTriggerIds) {
            val mTrigger = triggers[mVisTrigger.ordinal()];
            if (mTrigger.isInit())
                mTrigger.setVisible(true);
            else
                mTrigger.scriptListeners.add(
                        () -> setVisible(true)
                );
        }
    }

    /**
     * @param x      Check x
     * @param y      Check y
     * @return Check intercept result
     */
    private boolean intercepts(float x, float y) {
        Gdx.app.log(TAG, "Intercept trigger: \n"
                + '\t' + "x: " + x + " y: " + y);

        Circle circle = new Circle(
                getX() + bg.getX() + bg.getWidth() / 2f,
                getY() + bg.getY() + bg.getHeight() / 2f,
                bg.getWidth() / 2
        );

        boolean ret = circle.contains(
                x, y
        );

        Gdx.app.log(TAG, "Joystick trigger intercepts:" + ret);

        return ret;
    }

    public boolean intercepts(Vector2 coord) {
        return intercepts(coord.x, coord.y);
    }

    public JoystickTrigger getTrigger(TriggerId triggerId) {
        return (triggerId != null)
                ? triggers[triggerId.ordinal()]
                : null;
    }

    public JoystickTrigger getCurrentTrigger() {
        return getTrigger(getCurrentTriggerId());
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (intercepts(
                core.screenManager.screenToWorldCoord(
                        new Vector2(
                                screenX,
                                screenY
                        )
                )
        )) {
            touchPointer = pointer;

            // Show generalTriggerIds
            for (TriggerId mVisTrigger : generalTriggerIds)
                triggers[mVisTrigger.ordinal()].setVisible(true);

            // Emulate dragged
            touchDragged(screenX, screenY, pointer);
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (pointer == touchPointer) {
            // Move stick to center of bg
            stick.setCoord(
                    new Vector2(bg.getCoord()) {{
                        x += bg.getWidth() / 2f - stick.getWidth() / 2f;
                        y += bg.getHeight() / 2f - stick.getHeight() / 2f;
                    }}
            );

            // Deactivate activated general group
            int generalGroupIdId = Arrays.asList(generalTriggerIds).indexOf(currentTriggerId);
            if (generalGroupIdId != -1)
                core.getInputGroups()
                        .groupDeactivate(triggerInputGroups.get(generalTriggerIds[generalGroupIdId]));

            // Deactivate activated trigger
            if (currentTriggerId != null)
                getCurrentTrigger().deactivate();
            currentTriggerId = null;

            // Hide center trigger
            getTrigger(TriggerId.CENTER).setVisible(false);

            // Clear touchPointer
            touchPointer = TOUCH_POINTER_EMPTY;
        }

        return false;
    }

    private void activateTrigger(TriggerId newTriggerId) {
        // Save old triggerId
        TriggerId oldTriggerId = currentTriggerId;

        // Handle new trigger activate
        getTrigger(newTriggerId).activate();

        // Change currentTriggerId
        currentTriggerId = newTriggerId;

        // Handle old trigger deactivate
        if (oldTriggerId != null)
            getTrigger(oldTriggerId).deactivate();
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (pointer == touchPointer) {
            Vector2 newStickCoord = core.screenManager.screenToWorldCoord(
                    new Vector2(
                            screenX,
                            screenY
                    )
            );

            // Untranslate from CompositeItem
            newStickCoord.x -= getX();
            newStickCoord.y -= getY();

            if (intercepts(
                    core.screenManager.screenToWorldCoord(
                            new Vector2(
                                    screenX,
                                    screenY
                            )
                    )
            )) {
                // Move stick
                stick.setCoord(
                        new Vector2(newStickCoord) {{
                            x -= stick.getWidth() / 2f;
                            y -= stick.getHeight() / 2f;
                        }}
                );

                // Check intercepts triggers
                for (int newTriggerIdId = 0; newTriggerIdId < TriggerId.COUNT.ordinal(); newTriggerIdId++) {
                    val newTriggerId = TriggerId.values()[newTriggerIdId];
                    val newTrigger = getTrigger(newTriggerId);

                    if (newTrigger.isVisible() &&
                            !newTrigger.isActive() &&
                            newTrigger.intercepts(newStickCoord)) {
                        val oldTrigger = getCurrentTrigger();
                        val oldTriggerId = getCurrentTriggerId();

                        // Handle currentTriggerId change
                        for (JoystickListener mListener : joystickListeners)
                            mListener.onJoystickTriggerChanged(currentTriggerId, newTriggerId);

                        if (oldTrigger != null) {
                            // Deactivate InputGroup
                            int generalTriggerIdId = Arrays.asList(generalTriggerIds)
                                    .indexOf(oldTriggerId);
                            if (generalTriggerIdId != -1) {
                                if (!Arrays.asList(triggerChangeMap.get(oldTriggerId))
                                        .contains(newTriggerId))
                                    core.getInputGroups()
                                            .groupDeactivate(
                                                    triggerInputGroups.get(
                                                            generalTriggerIds[generalTriggerIdId]
                                                    )
                                            );
                            }
                        }

                        // Hide all other general triggers
                        if (Arrays.asList(generalTriggerIds).contains(newTriggerId)) {
                            // Show generalTriggerIds
                            getTrigger(newTriggerId).setVisible(true);
                        }

                        // Activate InputGroup
                        int generalTriggerIdId = Arrays.asList(generalTriggerIds).indexOf(newTriggerId);
                        if (generalTriggerIdId != -1) {
                            core.getInputGroups()
                                    .groupActivate(
                                            triggerInputGroups.get(
                                                    generalTriggerIds[generalTriggerIdId]
                                            )
                                    );
                        }

                        // Activate intercepted trigger
                        activateTrigger(newTriggerId);
                    }
                }
            }
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

    @Override
    public void dispose() {
        // Stop listen input
        core.getInputGroups().getMultiplexer().removeProcessor(this);

        super.dispose();
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
     * ScriptListener for @{@link Joystick}
     */
    public interface JoystickListener {
        /**
         * Called, after {@link #currentTriggerId} changed
         */
        void onJoystickTriggerChanged(TriggerId before, TriggerId after);
    }
}

class JoystickComposite extends Script {
    public JoystickComposite() {
        super();
        componentClassesForInit.addAll(
                new ArrayList<>(
                        Arrays.asList(
                                TransformComponent.class,
                                DimensionsComponent.class
                        )
                )
        );
    }
}

class JoystickStick extends Script {
    JoystickStick() {
        super();
        componentClassesForInit.addAll(
                new ArrayList<>(
                        Arrays.asList(
                                TransformComponent.class,
                                DimensionsComponent.class
                        )
                )
        );
    }
}

class JoystickBackground extends Script {
    JoystickBackground() {
        super();
        componentClassesForInit.addAll(
                new ArrayList<>(
                        Arrays.asList(
                                TransformComponent.class,
                                DimensionsComponent.class
                        )
                )
        );
    }
}

