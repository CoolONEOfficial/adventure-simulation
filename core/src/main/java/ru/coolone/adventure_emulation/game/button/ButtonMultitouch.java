package ru.coolone.adventure_emulation.game.button;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

import ru.coolone.adventure_emulation.GameCore;
import ru.coolone.adventure_emulation.InputGroups;

/**
 * Created by coolone on 24.12.17.
 */

public class ButtonMultitouch extends ButtonBase implements InputProcessor {

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
    private boolean intercept(int x, int y) {
        int touchX = x;
        int touchY = Gdx.graphics.getHeight() - y;

        float scaleX = Gdx.graphics.getWidth() / GameCore.WIDTH;
        float scaleY = Gdx.graphics.getHeight() / GameCore.HEIGHT;

        float minX = getCoord().x * scaleX;
        float minY = getCoord().y * scaleY;

        float maxX = minX + (getBoundRect().width * scaleX);
        float maxY = minY + (getBoundRect().height * scaleY);

        Gdx.app.log(TAG,
                "Touched coords: " + touchX + " and " + touchY);
        Gdx.app.log(TAG, "Rect coords: " + '\n'
                + "\tStart: " + minX + " and " + minY + '\n'
                + "\tEnd: " + maxX + " and " + maxY);

        return touchX > minX && touchX < maxX &&
                touchY > minY && touchY < maxY;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (intercept(screenX, screenY)) {
            if (InputGroups.touchCount > 1)
                this.button.setTouchState(true);
            down();
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (intercept(screenX, screenY)) {
            if (InputGroups.touchCount > 1)
                this.button.setTouchState(false);
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
