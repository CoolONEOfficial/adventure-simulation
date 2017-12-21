package ru.coolone.adventure_emulation;

import com.badlogic.ashley.core.Entity;
import com.uwsoft.editor.renderer.components.NinePatchComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

/**
 * Created by coolone on 20.12.17.
 */

public class Button {

    NinePatchComponent outer;

    /**
     * Outer button image script
     */
    public class OuterImage
            implements IScript {

        @Override
        public void init(Entity entity) {
            outer = ComponentRetriever.get(entity, NinePatchComponent.class);
        }

        @Override
        public void act(float delta) {
        }

        @Override
        public void dispose() {
        }
    }
}
