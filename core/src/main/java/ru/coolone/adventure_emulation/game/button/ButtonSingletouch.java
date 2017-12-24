package ru.coolone.adventure_emulation.game.button;

import com.badlogic.ashley.core.Entity;
import com.uwsoft.editor.renderer.components.additional.ButtonComponent;

import ru.coolone.adventure_emulation.GameCore;

/**
 * Created by coolone on 24.12.17.
 */

public class ButtonSingletouch extends ButtonBase
        implements ButtonComponent.ButtonListener {

    public ButtonSingletouch(GameCore core, String name) {
        super(core, name);
    }

    @Override
    public void init(Entity entity) {
        super.init(entity);
        button.addListener(this);
    }

    @Override
    public void touchUp() {
        // Handle up
        up();
    }

    @Override
    public void touchDown() {
        // Handle down
        down();
    }

    @Override
    public void clicked() {
        // Handle click
        click();
    }
}
