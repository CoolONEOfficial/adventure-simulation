package ru.coolone.adventure_emulation.scripts.joystick;

import com.badlogic.ashley.core.Entity;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;

import java.util.ArrayList;
import java.util.Arrays;

import ru.coolone.adventure_emulation.scripts.AbsTrigger;

/**
 * Trigger for @{@link Joystick}
 */
public class JoystickTrigger extends AbsTrigger {

    @SuppressWarnings("unused")
    private static final String TAG = JoystickTrigger.class.getSimpleName();
    /**
     * Names of layers in @{@link com.uwsoft.editor.renderer.components.LayerMapComponent}
     */
    public static final String LAYER_NAME_ACTIVE = "active";
    public static final String LAYER_NAME_PASSIVE = "passive";

    /**
     * @param listener @{@link AbsTriggerListener}, that will be added
     */
    public JoystickTrigger(AbsTriggerListener listener) {
        super(
                LAYER_NAME_ACTIVE, LAYER_NAME_PASSIVE,
                false,
                new ArrayList<>(
                        Arrays.asList(
                                MainItemComponent.class,
                                DimensionsComponent.class,
                                TransformComponent.class
                        )
                )
        );
        absTriggerListeners.add(listener);
    }

    @Override
    public void init(Entity entity) {
        super.init(entity);

        // Hide
        setVisible(false);
    }
}
