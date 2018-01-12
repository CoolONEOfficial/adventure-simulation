package ru.coolone.adventure_emulation.scripts;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.data.LayerItemVO;

import org.mockito.InjectMocks;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import lombok.NoArgsConstructor;
import ru.coolone.adventure_emulation.AbsTest;
import ru.coolone.adventure_emulation.script.Script;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author coolone
 * @since 08.01.18
 */
@NoArgsConstructor
public class AbsTriggerTest extends AbsTest {

    @InjectMocks
    private AbsTrigger absTrigger = new AbsTrigger(
            "active",
            "passive",
            false
    ) {
    };

    @BeforeClass
    @Override
    protected void setUpClass() throws Exception {
        super.setUpClass();
        absTrigger.init(
                new Entity() {{
                    //noinspection unchecked
                    for (Class<? extends Component> mComponentClass : Script.componentClasses)
                        add(mComponentClass.newInstance());

                    getComponent(LayerMapComponent.class).addLayer(
                            new LayerItemVO("active")
                    );
                    getComponent(LayerMapComponent.class).addLayer(
                            new LayerItemVO("passive")
                    );
                }}
        );
    }

    @Test
    public void testSetActiveState() throws Exception {
        absTrigger.setActive(true);
        assertTrue(absTrigger.isActive());
        assertTrue(absTrigger.getActiveLayer().isVisible);
        assertFalse(absTrigger.getPassiveLayer().isVisible);

        absTrigger.setActive(false);
        assertFalse(absTrigger.isActive());
        assertFalse(absTrigger.getActiveLayer().isVisible);
        assertTrue(absTrigger.getPassiveLayer().isVisible);
    }

    @Test
    public void testActivate() throws Exception {
        absTrigger.activate();
        assertTrue(absTrigger.isActive());
        assertTrue(absTrigger.getActiveLayer().isVisible);
        assertFalse(absTrigger.getPassiveLayer().isVisible);
    }

    @Test
    public void testDeactivate() throws Exception {
        absTrigger.deactivate();
        assertFalse(absTrigger.isActive());
        assertFalse(absTrigger.getActiveLayer().isVisible);
        assertTrue(absTrigger.getPassiveLayer().isVisible);
    }

    @Test
    public void testIsActive() throws Exception {
        absTrigger.activate();
        assertTrue(absTrigger.isActive());
        absTrigger.deactivate();
        assertFalse(absTrigger.isActive());
    }

    @Test
    public void testGetActiveLayer() throws Exception {
        assertEquals(absTrigger.getActiveLayer(), absTrigger.getActiveLayer());
    }

    @Test
    public void testGetPassiveLayer() throws Exception {
        assertEquals(absTrigger.getPassiveLayer(), absTrigger.getPassiveLayer());
    }
}