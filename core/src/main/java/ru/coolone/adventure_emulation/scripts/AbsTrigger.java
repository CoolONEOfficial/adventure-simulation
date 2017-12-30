package ru.coolone.adventure_emulation.scripts;

import com.badlogic.ashley.core.Entity;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.data.LayerItemVO;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import java.util.ArrayList;

import ru.coolone.adventure_emulation.Core;

/**
 * CompositeItem trigger with layers with custom names
 */

abstract public class AbsTrigger extends AbsTriggerComposite {
    /**
     * Link to @{@link Core}
     */
    protected Core core;

    /**
     * Layers names
     */
    private String activeName;
    private String passiveName;

    /**
     * Layers
     */
    private LayerItemVO activeLayer;
    private LayerItemVO passiveLayer;

    /**
     * Active flag
     */
    private boolean active;

    /**
     * @param core             Link to @{@link Core}
     * @param activeLayerName  Active layer name in CompositeItem
     * @param passiveLayerName Passive layer name in CompositeItem
     * @param active           Active or passive on init
     */
    protected AbsTrigger(
            Core core,
            String activeLayerName,
            String passiveLayerName,
            boolean active
    ) {
        this.core = core;
        this.activeName = activeLayerName;
        this.passiveName = passiveLayerName;
        this.active = active;
    }

    @Override
    public void init(Entity entity) {
        super.init(entity);

        // Get layers
        activeLayer = layers.getLayer(activeName);
        passiveLayer = layers.getLayer(passiveName);

        // Sync state and flag
        if (active)
            activate();
        else
            deactivate();
    }

    /**
     * Show active layer and hide passive
     */
    public void activate() {
        setActiveState(true);
    }

    /**
     * Show passive layer and hide active
     */
    public void deactivate() {
        setActiveState(false);
    }

    /**
     * Changes active flag and show/hide layers
     *
     * @param active New active flag state
     */
    public void setActiveState(boolean active) {
        activeLayer.isVisible = active;
        passiveLayer.isVisible = !active;
        if(this.active != active) {
            this.active = active;

            // Handle
            if(active)
                // Activate
                for(Listener mListener: listeners)
                    mListener.onTriggerActivate();
            else
                // Deactivate
                for(Listener mListener: listeners)
                    mListener.onTriggerDeactivate();
        }
    }

    public boolean isActive() {
        return active;
    }

    /**
     * Listeners array
     */
    private ArrayList<Listener> listeners = new ArrayList<Listener>();

    /**
     * @param listener @{@link Listener}, that will be added
     */
    public void addListener(Listener listener) {
        // Add listener
        listeners.add(listener);
    }

    /**
     * @param listener @{@link Listener}, that will be removed
     * @return Remove result
     */
    public boolean removeListener(Listener listener) {
        // Find listener
        int index = listeners.indexOf(listener);
        if (index != -1) {
            // Remove listener
            listeners.remove(listener);

            return true;
        }
        return false;
    }

    /**
     * Listener interface
     */
    public interface Listener {
        /**
         * Called, on trigger has been activated
         */
        void onTriggerActivate();
        /**
         * Called, on trigger has been deactivated
         */
        void onTriggerDeactivate();
    }
}

abstract class AbsTriggerComposite implements IScript {

    /**
     * Components
     */
    protected LayerMapComponent layers;

    @Override
    public void init(Entity entity) {
        // Component
        layers = ComponentRetriever.get(entity, LayerMapComponent.class);
    }

    @Override
    public void act(float delta) {

    }

    @Override
    public void dispose() {

    }
}
