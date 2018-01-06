package ru.coolone.adventure_emulation;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.components.physics.PhysicsBodyComponent;
import com.uwsoft.editor.renderer.data.LayerItemVO;
import com.uwsoft.editor.renderer.scripts.IScript;

import java.lang.reflect.Array;
import java.util.EnumMap;

import lombok.NonNull;
import lombok.val;

/**
 * Facade for @{@link com.badlogic.ashley.core.Entity}
 *
 * @author coolone
 */
public class Script implements IScript {
    /**
     * Instance of @{@link com.badlogic.ashley.core.Entity}
     */
    @NonNull public com.badlogic.ashley.core.Entity entity;

    /**
     * Classes of @{@link Entity} @{@link Component}'s
     */
    @NonNull public Class<? extends Component>[] entityComponentClasses;

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
        PHYSIC
    }

    /**
     * Script @{@link Component}'s classes
     * Assigned to @{@link ComponentId}
     *
     * @see ComponentId
     */
    static final Class[] componentClasses = new Class[]{
            MainItemComponent.class,
            DimensionsComponent.class,
            TransformComponent.class,
            LayerMapComponent.class,
            PhysicsBodyComponent.class
    };

    /**
     * Map of entity's @{@link Component}'s
     *
     * @see ComponentId
     * @see Component
     */
    public final EnumMap<ComponentId, Component> components = new EnumMap<>(ComponentId.class);

    /**
     * @param entityComponentClasses @{@link Component}'s classes
     */
    @SuppressWarnings("unchecked")
    public Script(
            Class[] entityComponentClasses
    ) {
        this.entityComponentClasses = entityComponentClasses;
    }

    @SuppressWarnings("unchecked")
    protected void addComponents(Class[] entityComponentClasses) {
        this.entityComponentClasses = concatArr(this.entityComponentClasses, entityComponentClasses);
    }


    /**
     * Function, that concatenate two arrays
     * From stackoverflow.com :J
     *
     * @param a First array
     * @param b Second array
     * @param <T> Type of arrays
     * @return Concatenated array
     */
    static private <T> T[] concatArr(T[] a, T[] b) {
        int aLen = a.length;
        int bLen = b.length;

        @SuppressWarnings("unchecked")
        T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    @Override
    public void init(Entity entity) {
        this.entity = entity;

        // Get entity components
        for (val mEntityComponentClass : entityComponentClasses) {
            for(int mComponentClassId = 0; mComponentClassId < Script.componentClasses.length; mComponentClassId++) {
                val mComponentClass = Script.componentClasses[mComponentClassId];
                if(mEntityComponentClass.equals(mComponentClass))
                    components.put(
                            ComponentId.values()[mComponentClassId],
                            entity.getComponent(mEntityComponentClass)
                    );
            }
        }
    }

    @Override
    public void act(float delta) {
    }

    @Override
    public void dispose() {
    }

    public boolean getVisible() {
        return ((MainItemComponent) components.get(ComponentId.MAIN_ITEM)).visible;
    }

    public void setVisible(boolean visible) {
        ((MainItemComponent) components.get(ComponentId.MAIN_ITEM)).visible = visible;
    }

    public float getX() {
        return ((TransformComponent) components.get(ComponentId.TRANSFORM)).x;
    }

    public void setX(float x) {
        ((TransformComponent) components.get(ComponentId.TRANSFORM)).x = x;
    }

    public float getY() {
        return ((TransformComponent) components.get(ComponentId.TRANSFORM)).y;
    }

    public void setY(float y) {
        ((TransformComponent) components.get(ComponentId.TRANSFORM)).y = y;
    }

    public Vector2 getCoord() {
        return new Vector2(getX(), getY());
    }

    public void setCoord(Vector2 coord) {
        setX(coord.x);
        setY(coord.y);
    }

    public float getWidth() {
        return ((DimensionsComponent) components.get(ComponentId.DIMEN)).width;
    }

    public float getHeight() {
        return ((DimensionsComponent) components.get(ComponentId.DIMEN)).height;
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
        return ((PhysicsBodyComponent) components.get(ComponentId.PHYSIC)).body;
    }

    /**
     * @return Physic body spawned bool
     */
    public boolean isSpawned() {
        return getBody() != null;
    }

    public LayerItemVO getLayer(String name) {
        return ((LayerMapComponent)components.get(ComponentId.LAYER_MAP)).getLayer(name);
    }
}
