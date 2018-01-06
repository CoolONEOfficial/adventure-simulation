package ru.coolone.adventure_emulation.scripts;

import com.badlogic.ashley.core.Entity;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.data.LayerItemVO;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.coolone.adventure_emulation.Script;

import java.util.ArrayList;

/**
 * CompositeItem trigger with layers with custom names
 */

@RequiredArgsConstructor
abstract public class AbsTrigger extends AbsTriggerComposite {

    /**
     * Listeners array
     */
    public final ArrayList<Listener> listeners = new ArrayList<>();
    /**
     * Layers names
     */
    private final String activeName;
    private final String passiveName;
    /**
     * Layers
     */
    private LayerItemVO activeLayer;
    private LayerItemVO passiveLayer;
    /**
     * Active flag
     */
    @Getter @NonNull private boolean active;

    protected AbsTrigger(
            String activeLayerName,
            String passiveLayerName,
            boolean active,
            Class[] customComponents
    ) {
        this(activeLayerName, passiveLayerName,
                active);
        addComponents(customComponents);
    }

    @Override
    public void init(Entity entity) {
        super.init(entity);

        // Get layers
        activeLayer = getLayer(activeName);
        passiveLayer = getLayer(passiveName);

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
        setActive(true);
    }

    /**
     * Show passive layer and hide active
     */
    public void deactivate() {
        setActive(false);
    }

    /**
     * Changes active flag and show/hide layers
     *
     * @param active New active flag state
     */
    public void setActive(boolean active) {
        getActiveLayer().isVisible = active;
        getPassiveLayer().isVisible = !active;
        if (this.active != active) {

            this.active = active;

            // Handle

            if (active)
                // Activate
                for (Listener mListener : listeners)
                    mListener.onTriggerActivate();
            else
                // Deactivate
                for (Listener mListener : listeners)
                    mListener.onTriggerDeactivate();
        }
    }

    public LayerItemVO getActiveLayer() {
        return activeLayer;
    }

    public LayerItemVO getPassiveLayer() {
        return passiveLayer;
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
