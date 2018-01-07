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
     * @param listener @{@link ru.coolone.adventure_emulation.scripts.AbsTrigger.Listener}, that will be added
     */
    public JoystickTrigger(Listener listener) {
        super(
                "active", "passive",
                false,
                new ArrayList<>(
                        Arrays.asList(
                                MainItemComponent.class,
                                DimensionsComponent.class,
                                TransformComponent.class
                        )
                )
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
