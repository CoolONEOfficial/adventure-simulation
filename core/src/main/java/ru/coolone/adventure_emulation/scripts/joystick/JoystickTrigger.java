package ru.coolone.adventure_emulation.scripts.joystick;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import ru.coolone.adventure_emulation.scripts.AbsTrigger;

/**
 * Trigger for @{@link Joystick}
 */
public class JoystickTrigger extends AbsTrigger {

    /**
     * @param listener @{@link ru.coolone.adventure_emulation.scripts.AbsTrigger.Listener}, that will be added
     */
    public JoystickTrigger(Listener listener) {
        super(
                "active", "passive",
                false,
                new Class[]{
                        MainItemComponent.class,
                        DimensionsComponent.class,
                        TransformComponent.class
                }
        );
        listeners.add(listener);
    }

    @Override
    public void init(Entity entity) {
        super.init(entity);

        // Hide
        setVisible(false);
    }
}
