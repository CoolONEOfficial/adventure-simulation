package ru.coolone.adventure_emulation.scripts;

import com.badlogic.ashley.core.Entity;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.data.LayerItemVO;

import java.util.ArrayList;
import java.util.Collections;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.coolone.adventure_emulation.script.Script;

/**
 * CompositeItem trigger with layers with custom names
 */

@RequiredArgsConstructor
abstract public class AbsTrigger extends AbsTriggerComposite {
    /**
     * Names of layers in @{@link com.uwsoft.editor.renderer.components.LayerMapComponent}
     */
    public static final String LAYER_NAME_ACTIVE = "active";
    public static final String LAYER_NAME_PASSIVE = "passive";
    /**
     * Layers names
     */
    private final String activeName;
    private final String passiveName;
    /**
     * Listeners array
     */
    public final ArrayList<AbsTriggerListener> absTriggerListeners = new ArrayList<>();
    /**
     * Layers
     */
    @Getter
    private LayerItemVO activeLayer;
    @Getter
    private LayerItemVO passiveLayer;
    /**
     * Active flag
     */
    @Getter
    @NonNull
    private boolean active;

    protected AbsTrigger(
            String activeLayerName,
            String passiveLayerName,
            boolean active,
            ArrayList<Class> customComponents
    ) {
        this(activeLayerName, passiveLayerName,
                active);
        componentClassesForInit.addAll(customComponents);
    }

    @Override
    public void init(Entity entity) {
        super.init(entity);

        // Get layers
        activeLayer = getLayer(activeName);
        passiveLayer = getLayer(passiveName);

        // Sync state and flag
        setActive(active);
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
                for (AbsTriggerListener mListener : absTriggerListeners)
                    mListener.onTriggerActivate();
            else
                // Deactivate
                for (AbsTriggerListener mListener : absTriggerListeners)
                    mListener.onTriggerDeactivate();
        }
    }

    /**
     * ScriptListener interface
     */
    public interface AbsTriggerListener {
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
    AbsTriggerComposite() {
        super();
        componentClassesForInit.addAll(new ArrayList<>(
                Collections.singletonList(
                        LayerMapComponent.class
                )
        ));
    }
}
