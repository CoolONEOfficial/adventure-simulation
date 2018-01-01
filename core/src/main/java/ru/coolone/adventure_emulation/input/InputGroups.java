package ru.coolone.adventure_emulation.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class, handle's group of keycode's to {@link InputGroupId}
 *
 * @author coolone
 */
public class InputGroups
        implements InputProcessor {
    private static final String TAG = InputGroups.class.getSimpleName();

    /**
     * Input groups ids
     */
    public enum InputGroupId {
        JUMP,
        MOVE_LEFT,
        CROUCH,
        MOVE_RIGHT
    }

    /**
     * Keycodes groups
     */
    private static final Map<InputGroupId, Integer[]> keyGroups = new HashMap<InputGroupId, Integer[]>() {{
        put(
                InputGroupId.JUMP,
                new Integer[]{
                        Keys.UP,
                        Keys.W,
                        Keys.SPACE
                }
        );
        put(
                InputGroupId.MOVE_LEFT,
                new Integer[]{
                        Keys.LEFT,
                        Keys.A
                }
        );
        put(
                InputGroupId.CROUCH,
                new Integer[]{
                        Keys.DOWN,
                        Keys.S
                }
        );
        put(
                InputGroupId.MOVE_RIGHT,
                new Integer[]{
                        Keys.RIGHT,
                        Keys.D
                }
        );
    }};

    public InputGroups() {
        // Set input multiplexer
        Gdx.input.setInputProcessor(multiplexer);

        // Listen input
        multiplexer.addProcessor(this);
    }

    /**
     * General input multiplexer
     */
    public final InputMultiplexer multiplexer = new InputMultiplexer();
    /**
     * Array of active @{@link InputGroupId}
     */
    private final ArrayList<InputGroupId> activeGroups = new ArrayList<InputGroupId>();
    /**
     * Array of listeners
     */
    private final ArrayList<InputGroupsListener> listeners = new ArrayList<InputGroupsListener>();

    /**
     * @return Active @{@link InputGroupId}'s
     */
    public ArrayList<InputGroupId> getActiveGroups() {
        return activeGroups;
    }

    /**
     * Convert keyboard keycode to @{@link InputGroupId}
     *
     * @param keycode Keycode, that has been pressed
     * @return Converted @{@link InputGroupId}
     */
    private static InputGroupId keyToInputGroup(int keycode) {
        InputGroupId groupId = null;

        // Find keycode in groups
        for (InputGroupId mGroup : keyGroups.keySet()) {
            for (Integer mKeycode : keyGroups.get(mGroup)) {
                if (keycode == mKeycode)
                    groupId = mGroup;
            }
        }

        return groupId;
    }

    public void addListener(InputGroupsListener listener) {
        // Add listener
        listeners.add(listener);
    }

    public boolean removeListener(InputGroupsListener listener) {
        // Find listener
        int removeIndex = listeners.indexOf(listener);
        if (removeIndex != -1) {
            // Remove listener
            listeners.remove(listener);

            return true;
        }
        return false;
    }

    /**
     * Calls all @{@link InputGroupsListener}'s groupActivate method
     *
     * @param groupId Id of activated group
     */
    public void groupActivate(InputGroupId groupId) {
        Gdx.app.log(TAG, "Input group " + groupId + " activating...");

        // Start group
        activeGroups.add(groupId);

        // Handle input group activate
        for (InputGroupsListener mListener : listeners) {
            mListener.onInputGroupActivate(groupId);
        }
    }

    /**
     * Calls all @{@link InputGroupsListener}'s groupDeactivate method
     *
     * @param groupId Id of deactivated group
     */
    public boolean groupDeactivate(InputGroupId groupId) {
        Gdx.app.log(TAG, "Input group " + groupId + " DEactivating...");

        // Deactivate group
        int deactivateIndex = activeGroups.indexOf(groupId);
        if (deactivateIndex != -1)
            activeGroups.remove(deactivateIndex);
        else
            return false;

        // Handle input group deactivate
        for (InputGroupsListener mListener : listeners) {
            mListener.onInputGroupDeactivate(groupId);
        }

        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        // Find input group by keycode
        InputGroupId groupId = keyToInputGroup(keycode);
        if (groupId != null)
            // Activate input group
            groupActivate(groupId);

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        // Find input group by keycode
        InputGroupId groupId = keyToInputGroup(keycode);
        if (groupId != null)
            // Deactivate input group
            groupDeactivate(groupId);

        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
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

    public interface InputGroupsListener {
        /**
         * Calls, after activate group
         *
         * @param groupId Id of activated group
         */
        boolean onInputGroupActivate(InputGroupId groupId);

        /**
         * Calls, after deactivate group
         *
         * @param groupId Id of deactivated group
         */
        boolean onInputGroupDeactivate(InputGroupId groupId);
    }
}
