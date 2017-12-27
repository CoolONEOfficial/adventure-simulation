package ru.coolone.adventure_emulation.scripts;

import com.badlogic.ashley.core.Entity;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.data.LayerItemVO;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import ru.coolone.adventure_emulation.GameCore;

/**
 * CompositeItem trigger with layers with custom names
 */

abstract public class CompositeTrigger extends Composite {
    /**
     * Link to @{@link GameCore}
     */
    protected GameCore core;

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
     * @param core             Link to @{@link GameCore}
     * @param name             Name of CompositeItem on @{@link GameCore:getRootItem}
     * @param activeLayerName  Active layer name in CompositeItem
     * @param passiveLayerName Passive layer name in CompositeItem
     * @param active           Active or passive on init
     */
    protected CompositeTrigger(
            GameCore core,
            String name,
            String activeLayerName,
            String passiveLayerName,
            boolean active
    ) {
        this.core = core;
        this.activeName = activeLayerName;
        this.passiveName = passiveLayerName;
        this.active = active;
        core.getScreenManager().getRootItem()
                .getChild(name)
                .addScript(this);
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
        activeLayer.isVisible = true;
        passiveLayer.isVisible = false;
        active = true;
    }

    /**
     * Show passive layer and hide active
     */
    public void deactivate() {
        activeLayer.isVisible = false;
        passiveLayer.isVisible = true;
        active = false;
    }

    public boolean isActive() {
        return active;
    }
}

abstract class Composite implements IScript {

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
