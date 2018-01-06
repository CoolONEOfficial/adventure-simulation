package ru.coolone.adventure_emulation.scripts;

import com.badlogic.gdx.math.Vector2;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.Test;

import ru.coolone.adventure_emulation.AbsTest;

import static org.testng.Assert.assertEquals;

/**
 * @author coolone
 */
abstract public class ButtonCompositeTest extends AbsTest {

    @InjectMocks
    private ButtonComposite buttonComposite = new ButtonComposite() {
    };

}