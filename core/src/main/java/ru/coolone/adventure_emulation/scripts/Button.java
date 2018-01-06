package ru.coolone.adventure_emulation.scripts;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import java.util.ArrayList;

import ru.coolone.adventure_emulation.Core;

/**
 * My button implementation, copy of @{@link com.uwsoft.editor.renderer.components.additional.ButtonComponent.ButtonListener}
 * Supported multitouch
 *
 * @author coolone
 */

public class Button extends ButtonComposite
        implements InputProcessor {

    private static final String TAG = Button.class.getSimpleName();
    /**
     * Pointer of touch, pressed at button
     */
    private static final int TOUCH_POINTER_EMPTY = -1;
    /**
     * Link to @{@link Core}
     */
    private final Core core;
    /**
     * Touch id
     */
    private int touchPointer = TOUCH_POINTER_EMPTY;

    public Button(
            Core core,
            String name
    ) {
        super();

        this.core = core;

        // Button script
        this.core
                .getScreenManager()
                .getRootItem()
                .getChild(name)
                .addScript(this);

        // Listen input
        this.core.getInputGroups()
                .getMultiplexer()
                .addProcessor(this);
    }

    @Override
    public void act(float delta) {
    }

    @Override
    public void dispose() {
        // Stop listen input
        core.getInputGroups()
                .getMultiplexer()
                .removeProcessor(this);
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
     * @param coord Coord with REVERTED y axis, that will be checked
     * @return Intercept bool
     */
    private boolean intercept(Vector2 coord) {
        coord.y = Gdx.graphics.getHeight() - coord.y; // y is down!

        // Get camera center coord
        Vector2 cameraCenterCoord = new Vector2(
                core.getScreenManager().camera.position.x,
                core.getScreenManager().camera.position.y
        );

        // Get camera coord
        final Vector2 cameraCoord = new Vector2(
                cameraCenterCoord.x - (Core.WIDTH / 2),
                cameraCenterCoord.y - (Core.HEIGHT / 2)
        );

        // Get button collision rect
        final Rectangle buttonRect = new Rectangle(
                getCoord().x - cameraCoord.x,
                getCoord().y - cameraCoord.y,
                getWidth(),
                getHeight()
        );

        // Scale
        final Vector2 scale = new Vector2(
                (float) Gdx.graphics.getWidth() / Core.WIDTH,
                (float) Gdx.graphics.getHeight() / Core.HEIGHT
        );
        buttonRect.x *= scale.x;
        buttonRect.y *= scale.y;
        buttonRect.width *= scale.x;
        buttonRect.height *= scale.y;

        boolean ret = buttonRect.contains(coord);

        Gdx.app.log(TAG, "Intercept ret: " + ret);

        return ret;
    }

    private boolean intercept(float x, float y) {
        return intercept(new Vector2(x, y));
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (intercept(screenX, screenY)) {
            // Save touch pointer
            touchPointer = pointer;

            // Change state
            activate();

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
            deactivate();

            // Handle
            click();
            up();

            // Clear touch pointer
            touchPointer = TOUCH_POINTER_EMPTY;
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

    public void click() {
        for (ButtonListener mListener : listeners) {
            mListener.onButtonClick();
        }
    }

    public void down() {
        for (ButtonListener mListener : listeners) {
            mListener.onButtonDown();
        }
    }

    public void up() {
        for (ButtonListener mListener : listeners) {
            mListener.onButtonUp();
        }
    }

    /**
     * Button listeners array
     *
     * @see ButtonListener
     */
    public ArrayList<ButtonListener> listeners = new ArrayList<>();

    /**
     * Button listener
     */
    public interface ButtonListener {
        /**
         * Will be called after button click
         */
        void onButtonClick();
        /**
         * Will be called after button press down
         */
        void onButtonDown();
        /**
         * Will be called after button press up
         */
        void onButtonUp();
    }
}

abstract class ButtonComposite extends AbsTrigger {

    public ButtonComposite() {
        super(
                "pressed", "normal",
                false
        );
        addComponents(
                new Class[]{
                        TransformComponent.class,
                        DimensionsComponent.class
                }
        );
    }
}
