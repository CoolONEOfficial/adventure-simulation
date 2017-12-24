package ru.coolone.adventure_emulation.game.button;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.data.LayerItemVO;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import java.util.ArrayList;

import ru.coolone.adventure_emulation.GameCore;

/**
 * Created by coolone on 20.12.17.
 */

abstract public class ButtonBase
        implements IScript {
    protected static final String TAG = ButtonBase.class.getSimpleName();
    /**
     * Button component
     */
    public ButtonComponent button;
    /**
     * Composite layers component
     */
    public LayerMapComponent layers;
    /**
     * ButtonBase listeners array
     *
     * @see ButtonListener
     */
    protected ArrayList<ButtonListener> listeners = new ArrayList<ButtonListener>();
    /**
     * Link for @{@link GameCore}
     */
    private GameCore core;
    /**
     * Transform component
     */
    private TransformComponent transform;
    /**
     * Dimensions component
     */
    private DimensionsComponent dimension;
    /**
     * Layer with normal button
     */
    private LayerItemVO layerNormal;
    /**
     * Layer with clicked button
     */
    private LayerItemVO layerClicked;

    public ButtonBase(
            GameCore core,
            String name
    ) {
        this.core = core;

        // ButtonBase script
        this.core.getRootItem()
                .getChild(name)
                .addScript(this);
    }

    public LayerItemVO getLayerNormal() {
        return layerNormal;
    }

    public LayerItemVO getLayerClicked() {
        return layerClicked;
    }

    @Override
    public void init(Entity entity) {
        // Layers component
        layers = ComponentRetriever.get(entity, LayerMapComponent.class);
        layerClicked = layers.getLayer("clicked");
        layerNormal = layers.getLayer("normal");

        // Transform component
        transform = ComponentRetriever.get(entity, TransformComponent.class);

        // Dimension component
        dimension = ComponentRetriever.get(entity, DimensionsComponent.class);

        // Button touch component
        button = new ButtonComponent();
        entity.add(button);
    }

    public void addListener(ButtonListener listener) {
        // Add listener
        listeners.add(listener);
    }

    public boolean removeListener(ButtonListener listener) {
        // Find index
        int removeIndex = listeners.indexOf(listener);
        if (removeIndex != -1) {
            // Remove listener
            listeners.remove(listener);
            return true;
        }
        return false;
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
