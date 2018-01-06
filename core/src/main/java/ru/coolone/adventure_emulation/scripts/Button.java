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
     * Button listeners array
     *
     * @see ButtonListener
     */
    public ArrayList<ButtonListener> listeners = new ArrayList<>();
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

    /**
     * Button click event
     */
    protected void click() {
        for (ButtonListener mListener : listeners) {
            mListener.onButtonClick();
        }
    }

    /**
     * Button press down event
     */
    protected void down() {
        for (ButtonListener mListener : listeners) {
            mListener.onButtonDown();
        }
    }

    /**
     * Button press up event
     */
    protected void up() {
        for (ButtonListener mListener : listeners) {
            mListener.onButtonUp();
        }
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
     * @param x Check x
     * @param y REVERTED Check y
     * @return Intercept bool
     */
    private boolean intercept(float x, float y) {
        y = Gdx.graphics.getHeight() - y; // y is down!

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
                getBoundRect().width,
                getBoundRect().height
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

        boolean ret = buttonRect.contains(x, y);

        Gdx.app.log(TAG, "Intercept ret: " + ret);

        return ret;
    }

    private boolean intercept(Vector2 coord) {
        return intercept(coord.x, coord.y);
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

    /**
     * Button listener
     */
    public interface ButtonListener {
        void onButtonClick();

        void onButtonDown();

        void onButtonUp();
    }
}

abstract class ButtonComposite extends AbsTrigger {

    /**
     * Transform component
     */
    private TransformComponent transform;
    /**
     * Dimensions component
     */
    private DimensionsComponent dimension;

    public ButtonComposite() {
        super(
                "pressed", "normal",
                false
        );
    }

    @Override
    public void init(Entity entity) {
        super.init(entity);

        // Transform component
        transform = ComponentRetriever.get(entity, TransformComponent.class);

        // Dimension component
        dimension = ComponentRetriever.get(entity, DimensionsComponent.class);
    }

    @Override
    public void act(float delta) {

    }

    @Override
    public void dispose() {

    }

    public Rectangle getBoundRect() {
        return dimension.boundBox;
    }

    public Vector2 getCoord() {
        return new Vector2(
                transform.x,
                transform.y
        );
    }

    public void setCoord(Vector2 coord) {
        setCoord(
                coord.x,
                coord.y
        );
    }

    public void setCoord(
            float x,
            float y
    ) {
        setX(x);
        setY(y);
    }

    public void setX(float x) {
        transform.x = x;
    }

    public void setY(float y) {
        transform.y = y;
    }

}
