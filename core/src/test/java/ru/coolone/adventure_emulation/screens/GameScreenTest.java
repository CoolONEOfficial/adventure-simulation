package ru.coolone.adventure_emulation.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.mock.input.MockInput;
import com.brashmonkey.spriter.Player;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.data.LayerItemVO;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;

import lombok.val;
import ru.coolone.adventure_emulation.AbsTest;
import ru.coolone.adventure_emulation.Core;
import ru.coolone.adventure_emulation.EntityFactory;
import ru.coolone.adventure_emulation.input.InputGroups;
import ru.coolone.adventure_emulation.screen.ScreenManager;
import ru.coolone.adventure_emulation.screen.ScreenScene;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static ru.coolone.adventure_emulation.screens.GameScreenEntityFactory.createEntity;

/**
 * @author coolone
 * @since 23.01.18
 */
public class GameScreenTest extends AbsTest {

    private GameScreen gameScreen;

    @BeforeClass
    @Override
    protected void setUpClass() throws Exception {
        super.setUpClass();

        Gdx.input = new MockInput();
        val core = spy(new Core());
        val inputGroups = new InputGroups();
        when(core.getInputGroups()).thenReturn(inputGroups);

        gameScreen = new GameScreen(core);
    }

    private static final Entity playerSpriterEntity = createEntity(
            "spriter",
            SpriterComponent.class
    );

    private static final Entity playerEntity = createEntity(
            "player",
            new Entity[]{
                    playerSpriterEntity
            }
    );

    private static final String[] triggerLayerNames = {
            "active",
            "passive"
    };

    private static final Entity joystickTriggerCenterEntity = createEntity(
            "triggerCenter",
            triggerLayerNames,
            LayerMapComponent.class
    );

    private static final Entity joystickTriggerLeftEntity = createEntity(
            "triggerLeft",
            triggerLayerNames,
            LayerMapComponent.class
    );

    private static final Entity joystickTriggerRightEntity = createEntity(
            "triggerRight",
            triggerLayerNames,
            LayerMapComponent.class
    );

    private static final Entity joystickTriggerUpEntity = createEntity(
            "triggerUp",
            triggerLayerNames,
            LayerMapComponent.class
    );

    private static final Entity joystickTriggerDownEntity = createEntity(
            "triggerDown",
            triggerLayerNames,
            LayerMapComponent.class
    );

    private static final Entity joystickTriggerRightUpEntity = createEntity(
            "triggerRightUp",
            triggerLayerNames,
            LayerMapComponent.class
    );

    private static final Entity joystickTriggerRightDownEntity = createEntity(
            "triggerRightDown",
            triggerLayerNames,
            LayerMapComponent.class
    );

    private static final Entity joystickTriggerLeftUpEntity = createEntity(
            "triggerLeftUp",
            triggerLayerNames,
            LayerMapComponent.class
    );

    private static final Entity joystickTriggerLeftDownEntity = createEntity(
            "triggerLeftDown",
            triggerLayerNames,
            LayerMapComponent.class
    );

    private static final Entity joystickBgEntity = createEntity(
            "bg"
    );

    private static final Entity joystickStickEntity = createEntity(
            "stick"
    );

    private static final Entity joystickEntity = createEntity(
            "joystick",
            new Entity[]{
                    joystickStickEntity,
                    joystickBgEntity,
                    joystickTriggerCenterEntity,
                    joystickTriggerLeftEntity,
                    joystickTriggerRightEntity,
                    joystickTriggerUpEntity,
                    joystickTriggerDownEntity,
                    joystickTriggerRightUpEntity,
                    joystickTriggerRightDownEntity,
                    joystickTriggerLeftUpEntity,
                    joystickTriggerLeftDownEntity,
            }
    );

    private static final Entity rootEntity = spy(createEntity(
            "root",
            new Entity[]{
                    playerEntity,
                    joystickEntity
            }
    ));

    static {
        playerSpriterEntity.getComponent(SpriterComponent.class)
                .player = mock(Player.class);

        val joystickTriggerDownLayers = joystickTriggerDownEntity.getComponent(LayerMapComponent.class);
        joystickTriggerDownLayers.addLayer(
                new LayerItemVO("active")
        );
        joystickTriggerDownLayers.addLayer(
                new LayerItemVO("passive")
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testShow() throws Exception {
        val loader = mock(SceneLoader.class);
        loader.rootEntity = rootEntity;
        when(loader.getRoot()).thenReturn(rootEntity);
        val map = new HashMap<Class<? extends ScreenScene>, ScreenScene>();
        val core = spy(new Core());
        val inputGroups = new InputGroups();
        when(core.getInputGroups()).thenReturn(inputGroups);
        val screenManager = new ScreenManager(loader, map, core);
        core.screenManager = screenManager;
        screenManager.openScreen(GameScreen.class);
        gameScreen.show();

        assertNotNull(gameScreen.player);
        assertNotNull(gameScreen.joystick);
        assertNotNull(gameScreen.downButton);
        assertNotNull(gameScreen.upButton);
        assertNotNull(gameScreen.leftButton);
        assertNotNull(gameScreen.rightButton);
        assertNotNull(gameScreen.font);
        assertNotNull(gameScreen.debugRenderer);

        assertTrue(gameScreen.core.getInputGroups().getListeners().contains(gameScreen));
    }

    @Test
    public void testRender() throws Exception {
    }

    @Test
    public void testHide() throws Exception {
    }

    @Test
    public void testDispose() throws Exception {
    }

    @Test
    public void testOnInputGroupActivate() throws Exception {
    }

    @Test
    public void testOnInputGroupDeactivate() throws Exception {
    }

}

class GameScreenEntityFactory extends EntityFactory {
    public static Entity createEntity(String name, Entity[] childs, String[] layerNames, Class... components) {
        val entity = createEntity(name, childs, components);

        // Add layers
        val layers = new LayerMapComponent();
        for (val mLayerName : layerNames) {
            layers.addLayer(new LayerItemVO(mLayerName));
        }
        entity.add(layers);

        return entity;
    }

    public static Entity createEntity(String name, String[] layerNames, Class... components) {
        return createEntity(name, new Entity[]{}, layerNames, components);
    }
}