package ru.coolone.adventure_emulation.game.button;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

import java.awt.Rectangle;

import ru.coolone.adventure_emulation.GameCore;
import ru.coolone.adventure_emulation.InputGroups;

/**
 * Created by coolone on 24.12.17.
 */

public class ButtonMultitouch extends ButtonBase implements InputProcessor {

    /**
     * Pointer of touch, pressed at button
     */
    private int touchPointer;

    public ButtonMultitouch(GameCore core, String name) {
        super(core, name);
    }

    @Override
    public void init(Entity entity) {
        super.init(entity);

        button.setTouchState(false);

        InputGroups.multiplexer.addProcessor(this);
    }

    @Override
    public void act(float delta) {

    }

    @Override
    public void dispose() {

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
     * Checks coordinates to intercept button
     *
     * @param x Check x
     * @param y REVERTED Check y
     * @return Intercept bool
     */
    private boolean intercept(float x, float y) {
        y = Gdx.graphics.getHeight() - y; // y is down!

        final Vector2 scale = new Vector2(
                Gdx.graphics.getWidth() / GameCore.WIDTH,
                Gdx.graphics.getHeight() / GameCore.HEIGHT
        );

        final Vector2 cameraCoord = new Vector2(
                core.getCamera().position.x - (GameCore.WIDTH / 2),
                core.getCamera().position.y - (GameCore.HEIGHT / 2)
        );

        final Rectangle buttonRect = new Rectangle(
                (int) ((getCoord().x * scale.x) - cameraCoord.x),
                (int) ((getCoord().y * scale.y) - cameraCoord.y),
                (int) (getBoundRect().width * scale.x),
                (int) (getBoundRect().height * scale.y)
        );

        return buttonRect.contains(x, y);
    }

    private boolean intercept(Vector2 coord) {
        return intercept(
                coord.x, coord.y
        );
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (intercept(screenX, screenY)) {
            // Save touch pointer
            touchPointer = pointer;

            // Change state
            this.button.setTouchState(true);

            // Handle
            down();
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // Check pointer
        if (touchPointer == pointer) {
            // Change state
            this.button.setTouchState(false);

            // Handle
            click();
            up();
        }
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
}
