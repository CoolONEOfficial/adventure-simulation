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

    @Mock
    private DimensionsComponent dimensions;

    @Mock
    private TransformComponent transform;

    @Test
    public void testGetBoundRect() throws Exception {
        assertEquals(buttonComposite.getBoundRect(), dimensions.boundBox);
    }

    @Test
    public void testGetCoord() throws Exception {
        assertEquals(buttonComposite.getCoord().x, transform.x);
        assertEquals(buttonComposite.getCoord().y, transform.y);
    }

    @Test
    public void testSetCoord() throws Exception {
        buttonComposite.setCoord(100, 200);
        assertEquals(buttonComposite.getCoord(),
                new Vector2(
                        100,
                        200
                ));
    }

    @Test
    public void testSetX() throws Exception {
        buttonComposite.setX(123);
        assertEquals(buttonComposite.getCoord().x, 123);
    }

    @Test
    public void testSetY() throws Exception {
        buttonComposite.setY(321);
        assertEquals(buttonComposite.getCoord().y, 321);
    }

}