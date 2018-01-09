package ru.coolone.adventure_emulation;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Player;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.physics.PhysicsBodyComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.data.LayerItemVO;
import com.uwsoft.editor.renderer.scripts.IScript;

import java.util.ArrayList;
import java.util.EnumMap;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;

/**
 * Facade for @{@link com.badlogic.ashley.core.Entity}
 *
 * @author coolone
 */
@NoArgsConstructor
public class Script implements IScript {
    /**
     * Script @{@link Component}'s classes
     * Assigned to @{@link ComponentId}
     *
     * @see ComponentId
     */
    public static final Class[] componentClasses = new Class[]{
            MainItemComponent.class,
            DimensionsComponent.class,
            TransformComponent.class,
            LayerMapComponent.class,
            PhysicsBodyComponent.class,
            SpriterComponent.class
    };
    /**
     * Array of @{@link ScriptListener}'s
     */
    public final ArrayList<ScriptListener> scriptListeners = new ArrayList<>();
    /**
     * Classes of @{@link Entity} @{@link Component}'s,
     * that instances will be initialized in {@link #init(Entity)}
     */
    protected final ArrayList<Class> componentClassesForInit = new ArrayList<>();
    /**
     * Map of entity's @{@link Component}'s
     *
     * @see ComponentId
     * @see Component
     */
    @Getter
    private final EnumMap<ComponentId, Component> components = new EnumMap<>(ComponentId.class);

    /**
     * Init flag
     * Will be activated after {@link #init(Entity)}
     */
    @Getter
    private boolean init = false;
    /**
     * Instance of @{@link Entity}
     */
    @Getter
    private Entity entity;
    /**
     * Flipped flag
     */
    @Getter
    private boolean flipped = false;

    @Override
    @SuppressWarnings("unchecked")
    public void init(Entity entity) {
        this.entity = entity;

        // Get entity components
        for (val mEntityComponentClass : componentClassesForInit) {
            for (int mComponentClassId = 0; mComponentClassId < Script.componentClasses.length; mComponentClassId++) {
                val mComponentClass = Script.componentClasses[mComponentClassId];
                if (mEntityComponentClass.equals(mComponentClass))
                    components.put(
                            ComponentId.values()[mComponentClassId],
                            entity.getComponent(mEntityComponentClass)
                    );
            }
        }

        // Handle init
        for (ScriptListener mListener : scriptListeners) {
            mListener.onInit();
        }

        // Activate init flag
        init = true;
    }

    @Override
    public void act(float delta) {
    }

    @Override
    public void dispose() {
    }

    public boolean isVisible() {
        return ((MainItemComponent) getComponents().get(ComponentId.MAIN_ITEM))
                .visible;
    }

    public void setVisible(boolean visible) {
        ((MainItemComponent) getComponents().get(ComponentId.MAIN_ITEM))
                .visible = visible;
    }

    public float getX() {
        return ((TransformComponent) getComponents().get(ComponentId.TRANSFORM))
                .x;
    }

    public void setX(float x) {
        ((TransformComponent) getComponents().get(ComponentId.TRANSFORM))
                .x = x;
    }

    public float getY() {
        return ((TransformComponent) getComponents().get(ComponentId.TRANSFORM))
                .y;
    }

    public void setY(float y) {
        ((TransformComponent) getComponents().get(ComponentId.TRANSFORM))
                .y = y;
    }

    public Vector2 getCoord() {
        return new Vector2(getX(), getY());
    }

    public void setCoord(Vector2 coord) {
        setX(coord.x);
        setY(coord.y);
    }

    public float getWidth() {
        return ((DimensionsComponent) getComponents().get(ComponentId.DIMEN))
                .width;
    }

    public void setWidth(float width) {
        ((DimensionsComponent) getComponents().get(ComponentId.DIMEN))
                .width = width;
    }

    public float getHeight() {
        return ((DimensionsComponent) getComponents().get(ComponentId.DIMEN))
                .height;
    }

    public void setHeight(float height) {
        ((DimensionsComponent) getComponents().get(ComponentId.DIMEN))
                .height = height;
    }

    public Rectangle getRect() {
        return new Rectangle(
                getX(),
                getY(),
                getWidth(),
                getHeight()
        );
    }

    public void setRect(Rectangle rect) {
        setX(rect.x);
        setY(rect.y);
        setWidth(rect.width);
        setHeight(rect.height);
    }

    /**
     * Check point to intercept
     *
     * @param point @{@link Vector2}, that will be checked
     * @return Check result
     */
    public boolean intercepts(Vector2 point) {
        return point.x > getX() &&
                point.y > getY() &&
                point.x < getX() + getWidth() &&
                point.y < getY() + getHeight();
    }

    public Body getBody() {
        return ((PhysicsBodyComponent) getComponents().get(ComponentId.PHYSIC))
                .body;
    }

    /**
     * @return Physic body spawned bool
     */
    public boolean isSpawned() {
        return getBody() != null;
    }

    public LayerItemVO getLayer(String name) {
        return ((LayerMapComponent) getComponents().get(ComponentId.LAYER_MAP))
                .getLayer(name);
    }

    public Animation getAnimation() {
        return getAnimationPlayer().getAnimation();
    }

    public void setAnimation(int index) {
        getAnimationPlayer().setAnimation(index);
    }

    public Player getAnimationPlayer() {
        return ((SpriterComponent) getComponents().get(ComponentId.SPRITER))
                .player;
    }

    /**
     * Flip's spriter animation at X
     *
     * @param flipped New flipped boolean
     */
    public void setFlipped(boolean flipped) {
        if (this.flipped != flipped) {
            // Flip
            ((SpriterComponent) getComponents().get(ComponentId.SPRITER))
                    .player.flipX();

            // Update flag
            this.flipped = flipped;
        }
    }

    /**
     * Script @{@link Component}'s ids
     */
    enum ComponentId {
        /**
         * Visibility and id
         */
        MAIN_ITEM,
        /**
         * Sizes
         */
        DIMEN,
        /**
         * Coords
         */
        TRANSFORM,
        /**
         * Layers
         */
        LAYER_MAP,
        /**
         * Physic @{@link Body}
         */
        PHYSIC,
        /**
         * Spriter animations
         */
        SPRITER
    }

    /**
     * ScriptListener for @{@link Script}
     */
    public interface ScriptListener {
        /**
         * Will be called after {@link #init(Entity)}
         */
        void onInit();
    }
}
