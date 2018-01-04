package ru.coolone.adventure_emulation.scripts;

import com.uwsoft.editor.renderer.data.LayerItemVO;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.testng.annotations.Test;

import java.util.ArrayList;

import ru.coolone.adventure_emulation.AbsTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Created by coolone on 04.01.18.
 */

public class AbsTriggerTest extends AbsTest {

    @InjectMocks
    private AbsTrigger absTrigger = new AbsTrigger(
            "active",
            "passive",
            false
    ) {
    };

    @Mock
    private LayerItemVO activeLayer;

    @Mock
    private LayerItemVO passiveLayer;

    @Mock
    private ArrayList<AbsTrigger.Listener> listeners;

    @Test
    public void testSetActiveState() throws Exception {
        absTrigger.setActiveState(true);
        assertTrue(absTrigger.isActive());
        assertTrue(activeLayer.isVisible);
        assertFalse(passiveLayer.isVisible);

        absTrigger.setActiveState(false);
        assertFalse(absTrigger.isActive());
        assertFalse(activeLayer.isVisible);
        assertTrue(passiveLayer.isVisible);
    }

    @Test
    public void testActivate() throws Exception {
        absTrigger.activate();
        assertTrue(absTrigger.isActive());
        assertTrue(activeLayer.isVisible);
        assertFalse(passiveLayer.isVisible);
    }

    @Test
    public void testDeactivate() throws Exception {
        absTrigger.deactivate();
        assertFalse(absTrigger.isActive());
        assertFalse(activeLayer.isVisible);
        assertTrue(passiveLayer.isVisible);
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
        assertEquals(absTrigger.getActiveLayer(), activeLayer);
    }

    @Test
    public void testGetPassiveLayer() throws Exception {
        assertEquals(absTrigger.getPassiveLayer(), passiveLayer);
    }
}