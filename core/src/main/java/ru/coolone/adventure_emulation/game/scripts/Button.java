package ru.coolone.adventure_emulation.game.scripts;

import com.badlogic.ashley.core.Entity;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.NinePatchComponent;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;
import com.uwsoft.editor.renderer.data.LayerItemVO;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import java.util.ArrayList;

/**
 * Created by coolone on 20.12.17.
 */

public class Button
        implements IScript, ButtonComponent.ButtonListener {
    private static final String TAG = Button.class.getSimpleName();
    /**
     * Outer image component
     */
    public NinePatchComponent outer;
    /**
     * Inner image component
     */
    public NinePatchComponent inner;
    /**
     * Composite layers component
     */
    public LayerMapComponent layers;
    /**
     * Button composite component
     */
    public ButtonComponent button;
    /**
     * Layer with normal button
     */
    private LayerItemVO layerNormal;
    /**
     * Layer with clicked button
     */
    private LayerItemVO layerClicked;
    /**
     * Button listeners array
     *
     * @see ButtonListener
     */
    private ArrayList<ButtonListener> listeners = new ArrayList<ButtonListener>();

    public Button(
            ItemWrapper root,
            String name
    ) {
        root.getChild(name)
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

        // Button component
        button = new ButtonComponent();
        entity.add(button);
        button.addListener(this);
    }

    @Override
    public void act(float delta) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void touchUp() {
        // Handle up
        for (ButtonListener mListener : listeners)
            if (mListener != null)
                mListener.onButtonUp();
    }

    @Override
    public void touchDown() {
        // Handle down
        for (ButtonListener mListener : listeners)
            if (mListener != null)
                mListener.onButtonDown();
    }

    @Override
    public void clicked() {
        // Handle click
        for (ButtonListener mListener : listeners)
            if (mListener != null)
                mListener.onButtonClick();
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

    /**
     * Button listener
     */
    public interface ButtonListener {
        void onButtonClick();

        void onButtonDown();

        void onButtonUp();
    }

    /**
     * Outer button image script
     */
    public class OuterImageScript
            implements IScript {

        @Override
        public void init(Entity entity) {
            // Outer image component
            outer = ComponentRetriever.get(entity, NinePatchComponent.class);
        }

        @Override
        public void act(float delta) {
        }

        @Override
        public void dispose() {
        }
    }

    public class InnerImageScript
            implements IScript {

        @Override
        public void init(Entity entity) {
            // Inner image component
            inner = ComponentRetriever.get(entity, NinePatchComponent.class);
        }

        @Override
        public void act(float delta) {

        }

        @Override
        public void dispose() {

        }
    }
}
