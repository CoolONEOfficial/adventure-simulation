package ru.coolone.adventure_emulation.input;

import com.badlogic.gdx.InputProcessor;

/**
 * Just implements touch events in @{@link InputProcessor}
 *
 * @author coolone
 */

public abstract class InputKeyboard implements InputProcessor {
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
}
