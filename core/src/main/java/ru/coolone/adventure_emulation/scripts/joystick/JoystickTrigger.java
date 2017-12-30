package ru.coolone.adventure_emulation.scripts.joystick;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import ru.coolone.adventure_emulation.Core;
import ru.coolone.adventure_emulation.scripts.AbsTrigger;

/**
 * Trigger for @{@link Joystick}
 */
public class JoystickTrigger extends AbsTrigger {

    /**
     * Components
     */
    public TransformComponent transform;
    public DimensionsComponent dimensions;

    /**
     * @param core Link to @{@link Core}
     */
    protected JoystickTrigger(
            Core core
    ) {
        super(
                core,
                "active", "passive",
                false
        );
    }

    @Override
    public void init(Entity entity) {
        super.init(entity);

        // Components
        transform = ComponentRetriever.get(entity, TransformComponent.class);
        dimensions = ComponentRetriever.get(entity, DimensionsComponent.class);
    }

    boolean intercepts(Vector2 point) {
        return point.x > transform.x && point.y > transform.y &&
                point.x < transform.x + dimensions.width &&
                point.y < transform.y + dimensions.height;
    }
}
