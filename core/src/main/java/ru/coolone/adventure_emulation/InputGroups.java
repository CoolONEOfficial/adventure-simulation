package ru.coolone.adventure_emulation;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * Class, handle's group of keycode's to {@link InputGroupId}
 */
public class InputGroups
        implements InputProcessor {

    /**
     * Input groups
     */
    public static final int[][] inputGroups = new int[][]{
            { // UP
                    Keys.UP,
                    Keys.W,
                    Keys.SPACE
            },
            { // LEFT
                    Keys.LEFT,
                    Keys.A
            },
            { // DOWN
                    Keys.DOWN,
                    Keys.S
            },
            { // RIGHT
                    Keys.RIGHT,
                    Keys.D
            }
    };
    /**
     * Array of active @{@link InputGroupId}
     */
    static private ArrayList<InputGroupId> activeGroups = new ArrayList<InputGroupId>();
    /**
     * Array of listeners
     */
    private static ArrayList<InputGroupsListener> listeners = new ArrayList<InputGroupsListener>();

    static {
        Gdx.input.setInputProcessor(getInstance());
    }

    public static InputGroups getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    public static ArrayList<InputGroupId> getActiveGroups() {
        return activeGroups;
    }

    /**
     * Convert keyboard keycode to @{@link InputGroupId}
     *
     * @param keycode Keycode, that has been pressed
     * @return Converted @{@link InputGroupId}
     */
    public static InputGroupId keyToInputGroup(int keycode) {
        InputGroupId groupId = null;

        // Find keycode in input groups
        for (int mGroupId = 0; mGroupId < inputGroups.length; mGroupId++) {
            for (int mKeycode : inputGroups[mGroupId]) {
                if (keycode == mKeycode)
                    groupId = InputGroupId.values()[mGroupId];
            }
        }

        return groupId;
    }

    /**
     * Binding touch coords to keyboard key
     *
     * @param touchCoord Touched coord
     * @return Converted @{@link InputGroupId}
     */
    public static InputGroupId touchToInputGroup(Vector2 touchCoord) {
        InputGroupId groupId = null;

        // Bind touch to key
        if (touchCoord.x < Gdx.graphics.getWidth() / 3)
            groupId = InputGroupId.LEFT;
        else if (touchCoord.x > Gdx.graphics.getWidth() / 3 * 2)
            groupId = InputGroupId.RIGHT;
        else if (touchCoord.y < Gdx.graphics.getHeight() / 2)
            groupId = InputGroupId.UP;
        else
            groupId = InputGroupId.DOWN;

        return groupId;
    }

    public static InputGroupId touchToInputGroup(int touchX, int touchY) {
        return touchToInputGroup(new Vector2(touchX, touchY));
    }

    public static void addListener(InputGroupsListener listener) {
        // Add listener
        listeners.add(listener);
    }

    public static boolean removeListener(InputGroupsListener listener) {
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
    public static void groupActivate(InputGroupId groupId) {
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
    public static boolean groupDeactivate(InputGroupId groupId) {
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
        // Find input group by keycode
        InputGroupId groupId = touchToInputGroup(screenX, screenY);
        if (groupId != null)
            // Activate input group
            groupActivate(groupId);

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // Find input group by touch coords
        InputGroupId groupId = touchToInputGroup(screenX, screenY);
        if (groupId != null)
            // Deactivate input group
            groupDeactivate(groupId);

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

    /**
     * Input groups ids
     */
    public enum InputGroupId {
        UP,
        LEFT,
        DOWN,
        RIGHT,
        STEALTH
    }

    public interface InputGroupsListener {
        /**
         * Calls, after activate group
         *
         * @param groupId Id of activated group
         */
        void onInputGroupActivate(InputGroupId groupId);

        /**
         * Calls, after deactivate group
         *
         * @param groupId Id of deactivated group
         */
        void onInputGroupDeactivate(InputGroupId groupId);
    }

    private static class SingletonHolder {
        private static final InputGroups HOLDER_INSTANCE = new InputGroups();
    }
}
