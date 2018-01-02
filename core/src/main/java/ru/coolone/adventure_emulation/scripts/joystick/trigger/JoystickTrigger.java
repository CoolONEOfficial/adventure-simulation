package ru.coolone.adventure_emulation.scripts.joystick.trigger;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import ru.coolone.adventure_emulation.scripts.AbsTrigger;
import ru.coolone.adventure_emulation.scripts.joystick.Joystick;

/**
 * Trigger for @{@link Joystick}
 */
public class JoystickTrigger<TriggerId extends Enum> extends AbsTrigger<TriggerId> {

    /**
     * Components
     */
    public TransformComponent transform;
    public DimensionsComponent dimensions;
    public MainItemComponent mainItem;

    /**
     * @param listener @{@link ru.coolone.adventure_emulation.scripts.AbsTrigger.Listener}, that will be added
     */
    public JoystickTrigger(Listener listener) {
        super(
                "active", "passive",
                false
        );
        addListener(listener);
    }

    @Override
    public void activate() {
        super.activate();
    }

    @Override
    public void init(Entity entity) {
        super.init(entity);

        // Components
        transform = ComponentRetriever.get(entity, TransformComponent.class);
        dimensions = ComponentRetriever.get(entity, DimensionsComponent.class);
        mainItem = ComponentRetriever.get(entity, MainItemComponent.class);

        // Hide
        mainItem.visible = false;
    }

    public boolean intercepts(Vector2 point) {
        return point.x > transform.x && point.y > transform.y &&
                point.x < transform.x + dimensions.width &&
                point.y < transform.y + dimensions.height;
    }

    public void onChanged(TriggerId triggerId) {
        for(Listener<TriggerId> mListener: listeners) {
            mListener.onTriggerChanged(triggerId);
        }
    }
}
