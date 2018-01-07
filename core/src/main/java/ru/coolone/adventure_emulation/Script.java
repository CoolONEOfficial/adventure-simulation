package ru.coolone.adventure_emulation;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.EnumMap;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * Facade for @{@link com.badlogic.ashley.core.Entity}
 *
 * @author coolone
 */
@RequiredArgsConstructor
public class Script implements IScript {

    /**
     * Classes of @{@link Entity} @{@link Component}'s,
     * that instances will be initialized in {@link #init(Entity)}
     */
    @NonNull
    protected ArrayList<Class> componentClassesForInit;

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
     * Script @{@link Component}'s classes
     * Assigned to @{@link ComponentId}
     *
     * @see ComponentId
     */
    private static final Class[] componentClasses = new Class[]{
            MainItemComponent.class,
            DimensionsComponent.class,
            TransformComponent.class,
            LayerMapComponent.class,
            PhysicsBodyComponent.class,
            SpriterComponent.class
    };

    /**
     * Map of entity's @{@link Component}'s
     *
     * @see ComponentId
     * @see Component
     */
    @Getter private final EnumMap<ComponentId, Component> components = new EnumMap<>(ComponentId.class);

    @Override
    @SuppressWarnings("unchecked")
    public void init(Entity entity) {
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
        for (Listener mListener : listeners) {
            mListener.onInit();
        }
    }

    @Override
    public void act(float delta) {
    }

    @Override
    public void dispose() {
    }

    public boolean getVisible() {
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

    public float getHeight() {
        return ((DimensionsComponent) getComponents().get(ComponentId.DIMEN))
                .height;
    }

    /**
     * Check point to intercept
     *
     * @param point @{@link Vector2}, that will be checked
     * @return Check result
     */
    public boolean intercepts(Vector2 point) {
        return point.x > getX() && point.y > getY() &&
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

    public void setAnimation(int index) {
        ((SpriterComponent) getComponents().get(ComponentId.SPRITER))
                .player.setAnimation(index);
    }

    public Animation getAnimation() {
        return ((SpriterComponent) getComponents().get(ComponentId.SPRITER))
                .player.getAnimation();
    }

    public Player getAnimationPlayer() {
        return ((SpriterComponent) getComponents().get(ComponentId.SPRITER))
                .player;
    }

    /**
     * Flipped flag
     */
    private boolean flipped = false;

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
     * Array of @{@link Listener}'s
     */
    public final ArrayList<Listener> listeners = new ArrayList<>();

    /**
     * Listener for @{@link Script}
     */
    public interface Listener {
        /**
         * Will be called after {@link #init(Entity)}
         */
        void onInit();
    }
}
