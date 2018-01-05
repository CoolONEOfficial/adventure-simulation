package ru.coolone.adventure_emulation.scripts;

import com.badlogic.ashley.core.Entity;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.data.LayerItemVO;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;
import ru.coolone.adventure_emulation.Script;

import java.util.ArrayList;

/**
 * CompositeItem trigger with layers with custom names
 */

abstract public class AbsTrigger extends AbsTriggerComposite {

    /**
     * Listeners array
     */
    protected final ArrayList<Listener> listeners = new ArrayList<>();
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
    private boolean activeState;

    /**
     * @param activeLayerName  Active layer name in CompositeItem
     * @param passiveLayerName Passive layer name in CompositeItem
     * @param activeState      Active or passive on init
     */
    public AbsTrigger(
            String activeLayerName,
            String passiveLayerName,
            boolean activeState
    ) {
        this.activeName = activeLayerName;
        this.passiveName = passiveLayerName;
        this.activeState = activeState;
    }

    @Override
    public void init(Entity entity) {
        super.init(entity);

        // Get layers
        activeLayer = getLayer(activeName);
        passiveLayer = getLayer(passiveName);

        // Sync state and flag
        if (activeState)
            activate();
        else
            deactivate();
    }

    /**
     * Show activeState layer and hide passive
     */
    public void activate() {
        setActiveState(true);
    }

    /**
     * Show passive layer and hide activeState
     */
    public void deactivate() {
        setActiveState(false);
    }

    /**
     * Changes activeState flag and show/hide layers
     *
     * @param activeState New activeState flag state
     */
    public void setActiveState(boolean activeState) {
        getActiveLayer().isVisible = activeState;
        getPassiveLayer().isVisible = !activeState;
        if (this.activeState != activeState) {

            this.activeState = activeState;

            // Handle

            if (activeState)
                // Activate
                for (Listener mListener : listeners)
                    mListener.onTriggerActivate();
            else
                // Deactivate
                for (Listener mListener : listeners)
                    mListener.onTriggerDeactivate();
        }
    }

    public boolean isActive() {
        return activeState;
    }

    public LayerItemVO getActiveLayer() {
        return activeLayer;
    }

    public LayerItemVO getPassiveLayer() {
        return passiveLayer;
    }

    /**
     * @param listener @{@link Listener}, that will be added
     * @return Added @{@link Listener} index
     */
    public int addListener(Listener listener) {
        // Add listener
        listeners.add(listener);

        return listeners.size() - 1;
    }

    /**
     * @param listener @{@link Listener}, that will be removed
     * @return Remove result
     */
    public boolean removeListener(Listener listener) {
        return listeners.remove(listener);
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

abstract class AbsTriggerComposite extends Script {

    public AbsTriggerComposite() {
        super(
                new Class[]{
                        LayerMapComponent.class
                }
        );
    }
}
