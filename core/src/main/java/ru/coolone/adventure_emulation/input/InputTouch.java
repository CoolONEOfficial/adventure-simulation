package ru.coolone.adventure_emulation.input;

import com.badlogic.gdx.InputProcessor;

/**
 * Just implements keyboard events in @{@link InputProcessor}
 *
 * @author coolone
 */

abstract public class InputTouch implements InputProcessor {
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
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
