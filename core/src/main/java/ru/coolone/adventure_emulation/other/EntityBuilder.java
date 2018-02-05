package ru.coolone.adventure_emulation.other;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.brashmonkey.spriter.Player;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.ScriptComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.data.LayerItemVO;

import lombok.SneakyThrows;
import lombok.val;
import ru.coolone.adventure_emulation.scripts.AbsTrigger;
import ru.coolone.adventure_emulation.scripts.Button;

/**
 * Factory of @{@link Entity}'s
 *
 * @author coolone
 * @since 27.01.18
 */

public class EntityBuilder {
    /**
     * Names of layer in @{@link ru.coolone.adventure_emulation.scripts.AbsTrigger}'s
     */
    public static final String[] triggerLayerNames = {
            AbsTrigger.LAYER_NAME_ACTIVE,
            AbsTrigger.LAYER_NAME_PASSIVE
    };
    /**
     * Names of layers in @{@link Button}
     */
    public static final String[] buttonLayerNames = new String[]{
            Button.LAYER_NAME_ACTIVE,
            Button.LAYER_NAME_PASSIVE
    };
    /**
     * Building @{@link Entity}
     */
    public Entity entity = new Entity();

    @SuppressWarnings("unchecked")
    private static final Class<? extends Component>[] initComponents = new Class[]{
            ScriptComponent.class,
            MainItemComponent.class,
            TransformComponent.class,
            DimensionsComponent.class
    };

    @SneakyThrows
    public EntityBuilder() {
        // Init components
        for (val mComponent : initComponents) {
            entity.add(mComponent.newInstance());
        }
    }

    /**
     * Creates or founds @{@link Component} from {@link #entity}
     *
     * @param componentClass Component @{@link Class}
     * @param <T>            Component class
     * @return Created or founded @{@link Component}
     */
    @SneakyThrows
    private <T extends Component> T getComponent(Class<T> componentClass) {
        // Get / Create node component
        T component = entity.getComponent(componentClass);
        if (component == null) {
            component = componentClass.newInstance();
            entity.add(component);
        }
        return component;
    }

    /**
     * @param childs Array of child @{@link Entity}'s, that will be added in @{@link NodeComponent}
     *               in {@link #entity}
     * @return Self
     */
    public EntityBuilder addChilds(Entity... childs) {

        val node = getComponent(NodeComponent.class);
        for (val mChild : childs) {
            node.addChild(mChild);
        }

        return this;
    }

    /**
     * @param name Name, that will be added in @{@link MainItemComponent} in {@link #entity}
     * @return Self
     */
    public EntityBuilder addName(String name) {
        val mainItem = getComponent(MainItemComponent.class);
        mainItem.itemIdentifier = name;

        return this;
    }

    /**
     * @param components Array of @{@link Class}'es of @{@link Component}'s,
     *                   that will be added in {@link #entity}
     * @return Self
     */
    @SafeVarargs
    @SneakyThrows
    public final EntityBuilder addCustom(Class<? extends Component>... components) {
        for (val mComponent : components) {
            entity.add(mComponent.newInstance());
        }

        return this;
    }

    /**
     * @param layers Array of @{@link LayerItemVO}'s,
     *               that will be find in {@link #entity}
     * @return Self
     */
    public EntityBuilder addLayers(LayerItemVO... layers) {
        val layerMap = getComponent(LayerMapComponent.class);
        for (val mLayer : layers) {
            layerMap.addLayer(mLayer);
        }

        return this;
    }

    /**
     * Calls {@link #addLayers(LayerItemVO[])} with @{@link LayerItemVO}'s with only names
     *
     * @param layerNames @{@link LayerItemVO} name's
     * @return Self
     */
    public EntityBuilder addLayerMocks(String[] layerNames) {
        LayerItemVO[] layers = new LayerItemVO[layerNames.length];

        for (int mLayerNameId = 0; mLayerNameId < layerNames.length; mLayerNameId++) {
            val mLayerName = layerNames[mLayerNameId];
            layers[mLayerNameId] = new LayerItemVO(mLayerName);
        }

        return addLayers(layers);
    }

    /**
     * @param player @{@link Player}, that will be added
     *               in @{@link com.uwsoft.editor.renderer.components.spriter.SpriterComponent#player}
     * @return Self
     */
    public EntityBuilder addSpriterPlayer(Player player) {
        val spriter = getComponent(SpriterComponent.class);
        spriter.player = player;

        return this;
    }
}
