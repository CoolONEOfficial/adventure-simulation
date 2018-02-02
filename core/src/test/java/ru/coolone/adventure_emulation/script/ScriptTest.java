package ru.coolone.adventure_emulation.script;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Mainline;
import com.brashmonkey.spriter.Player;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.physics.PhysicsBodyComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.data.LayerItemVO;

import org.mockito.InjectMocks;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;

import lombok.NoArgsConstructor;
import lombok.val;
import ru.coolone.adventure_emulation.AbsTest;
import ru.coolone.adventure_emulation.other.EntityBuilder;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * @author coolone
 * @since 08.01.18
 */
@NoArgsConstructor
public class ScriptTest extends AbsTest {
    private SpriterComponent spriter;
    private PhysicsBodyComponent physic;
    private LayerMapComponent layers;
    private LayerItemVO layerOne = new LayerItemVO("one");
    private LayerItemVO layerTwo = new LayerItemVO("two");
    @InjectMocks
    private ru.coolone.adventure_emulation.script.Script script;
    private int spriterAnimationId;

    @BeforeMethod
    void setUpComponents() throws Exception {

        script.componentClassesForInit.addAll(
                Arrays.asList(ru.coolone.adventure_emulation.script.Script.componentClasses)
        );

        val player = mock(Player.class);
        doAnswer(
                invocation -> {
                    spriterAnimationId = invocation.getArgument(0);
                    return null;
                }
        ).when(player)
                .setAnimation(anyInt());

        // Animation class with public constructor
        class MyAnimation extends Animation {
            private MyAnimation(Mainline mainline, int id, String name, int length,
                                boolean looping, int timelines) {
                super(mainline, id, name, length, looping, timelines);
            }
        }

        when(player.getAnimation()).thenAnswer(
                invocation -> {
                    // Return current animation
                    int index = spriterAnimationId;

                    return new MyAnimation(
                            mock(Mainline.class),
                            index,
                            String.valueOf(index),
                            2,
                            false,
                            1
                    );
                }
        );

        val entity = new EntityBuilder()
                .addCustom(SpriterComponent.class)
                .addLayers(
                        layerOne,
                        layerTwo
                ).addSpriterPlayer(player)
                .addCustom(PhysicsBodyComponent.class)
                .entity;
        spriter = entity.getComponent(SpriterComponent.class);
        physic = entity.getComponent(PhysicsBodyComponent.class);
        layers = entity.getComponent(LayerMapComponent.class);

        script.init(entity);
    }

    @Test
    public void testIsVisible() throws Exception {
        assertEquals(
                script.isVisible(),
                ((MainItemComponent) script.getComponents().get(ru.coolone.adventure_emulation.script.Script.ComponentId.MAIN_ITEM))
                        .visible
        );
    }

    @Test
    public void testSetVisible() throws Exception {
        script.setVisible(true);
        assertTrue(script.isVisible());
        script.setVisible(false);
        assertFalse(script.isVisible());
    }

    @Test
    public void testGetX() throws Exception {
        assertEquals(
                script.getX(),
                ((TransformComponent) script.getComponents().get(ru.coolone.adventure_emulation.script.Script.ComponentId.TRANSFORM))
                        .x
        );
    }

    @Test
    public void testSetX() throws Exception {
        val checkX = (float) (Math.random() * 100.);
        script.setX(checkX);
        assertEquals(script.getX(), checkX);
    }

    @Test
    public void testGetY() throws Exception {
        assertEquals(
                script.getY(),
                ((TransformComponent) script.getComponents().get(ru.coolone.adventure_emulation.script.Script.ComponentId.TRANSFORM))
                        .y
        );
    }

    @Test
    public void testSetY() throws Exception {
        val checkY = (float) (Math.random() * 100.);
        script.setY(checkY);
        assertEquals(script.getY(), checkY);
    }

    @Test
    public void testGetCoord() throws Exception {
        val transform = (TransformComponent) script.getComponents().get(ru.coolone.adventure_emulation.script.Script.ComponentId.TRANSFORM);
        assertEquals(
                script.getCoord(),
                new Vector2(
                        transform.x,
                        transform.y
                )
        );
    }

    @Test
    public void testSetCoord() throws Exception {
        val checkCoord = new Vector2(
                (float) (Math.random() * 100.),
                (float) (Math.random() * 30.)
        );
        script.setCoord(checkCoord);
        assertEquals(
                script.getCoord(),
                checkCoord
        );
    }

    @Test
    public void testGetWidth() throws Exception {
        assertEquals(
                script.getWidth(),
                ((DimensionsComponent) script.getComponents().get(ru.coolone.adventure_emulation.script.Script.ComponentId.DIMEN))
                        .width
        );
    }

    @Test
    public void testSetWidth() throws Exception {
        val checkWidth = (float) (Math.random() * 100.);
        script.setWidth(checkWidth);
        assertEquals(script.getWidth(), checkWidth);
    }

    @Test
    public void testGetHeight() throws Exception {
        assertEquals(
                script.getHeight(),
                ((DimensionsComponent) script.getComponents().get(ru.coolone.adventure_emulation.script.Script.ComponentId.DIMEN))
                        .height
        );
    }

    @Test
    public void testSetHeight() throws Exception {
        val checkHeight = (float) (Math.random() * 130.);
        script.setHeight(checkHeight);
        assertEquals(script.getHeight(), checkHeight);
    }

    @Test
    public void testIntercepts() throws Exception {
        val checkRect = new Rectangle(
                (float) (Math.random() * 100.),
                (float) (Math.random() * 50.),
                (float) (Math.random() * 50.) + 2f,
                (float) (Math.random() * 65.) + 2f
        );
        script.setRect(checkRect);
        assertFalse(script.intercepts(
                new Vector2(
                        checkRect.x - 1f,
                        checkRect.y - 1f
                )
        ));
        assertTrue(script.intercepts(
                new Vector2(
                        checkRect.x + 1f,
                        checkRect.y + 1f
                )
        ));
        assertTrue(script.intercepts(
                new Vector2(
                        checkRect.x + checkRect.width - 1f,
                        checkRect.y + checkRect.height - 1f
                )
        ));
        assertFalse(script.intercepts(
                new Vector2(
                        checkRect.x + checkRect.width + 1f,
                        checkRect.y + checkRect.height + 1f
                )
        ));
    }

    @Test
    public void testGetBody() throws Exception {
        Body body = mock(Body.class);
        physic.body = body;
        assertEquals(script.getBody(), body);
    }

    @Test
    public void testIsSpawned() throws Exception {
        physic.body = null;
        assertFalse(script.isSpawned());

        physic.body = mock(Body.class);
        assertTrue(script.isSpawned());
    }

    @Test
    public void testGetLayer() throws Exception {
        assertEquals(script.getLayer("one"), layerOne);
        assertEquals(script.getLayer("two"), layerTwo);
        assertNull(script.getLayer("some other"));
    }

    @Test
    public void testSetAnimation() throws Exception {
        script.setAnimation(1);
        assertEquals(script.getAnimation().id, 1);
    }

    @Test
    public void testGetAnimation() throws Exception {
        assertEquals(
                script.getAnimation().id,
                script.getAnimationPlayer().getAnimation().id
        );
    }

    @Test
    public void testGetAnimationPlayer() throws Exception {
        assertEquals(
                script.getAnimationPlayer(),
                ((SpriterComponent) script.getComponents().get(ru.coolone.adventure_emulation.script.Script.ComponentId.SPRITER))
                        .player
        );
    }

    @Test
    public void testSetFlipped() throws Exception {
        script.setFlipped(true);
        assertTrue(script.isFlipped());

        script.setFlipped(false);
        assertFalse(script.isFlipped());
    }

    @Test
    public void testGetRect() throws Exception {
        assertEquals(
                script.getRect().x,
                script.getX()
        );
        assertEquals(
                script.getRect().y,
                script.getY()
        );
        assertEquals(
                script.getRect().width,
                script.getWidth()
        );
        assertEquals(
                script.getRect().height,
                script.getHeight()
        );
    }

    @Test
    public void testSetRect() throws Exception {
        val checkRect = new Rectangle(
                (float) (Math.random() * 30.),
                (float) (Math.random() * 50.),
                (float) (Math.random() * 20.),
                (float) (Math.random() * 100.)
        );
        script.setRect(checkRect);
        assertEquals(script.getRect(), checkRect);
    }
}