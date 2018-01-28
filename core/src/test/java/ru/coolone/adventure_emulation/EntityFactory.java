package ru.coolone.adventure_emulation;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.ScriptComponent;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;

/**
 * Factory of @{@link Entity}'s
 *
 * @author coolone
 * @since 27.01.18
 */

public class EntityFactory {

    @SneakyThrows
    private static Entity createEntity(@NonNull Class... components) {
        val entity = new Entity();

        //noinspection unchecked
        for (val mComponent : (Class<? extends Component>[]) components) {
            entity.add(mComponent.newInstance());
        }

        return entity;
    }

    public static Entity createEntity(String name, Entity[] childs, Class... components) {
        val entity = createEntity(components);

        // Set name
        val main = new MainItemComponent();
        main.itemIdentifier = name;
        entity.add(main);

        // Add script
        entity.add(new ScriptComponent());

        // Add childs
        val node = new NodeComponent();
        for (val mChild : childs) {
            node.addChild(mChild);
        }
        entity.add(node);

        return entity;
    }

    public static Entity createEntity(String name, Class... components) {
        return createEntity(name, new Entity[]{}, components);
    }
}
