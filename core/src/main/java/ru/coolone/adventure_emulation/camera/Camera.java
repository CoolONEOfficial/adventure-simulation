package ru.coolone.adventure_emulation.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

import org.pushingpixels.trident.Timeline;

import lombok.NoArgsConstructor;

/**
 * Game @{@link com.badlogic.gdx.graphics.OrthographicCamera}
 */

@NoArgsConstructor
public class Camera extends OrthographicCamera {
    private static final String TAG = Camera.class.getSimpleName();

    Timeline timeline = new Timeline();

    Vector2 indentAnimStart;
    Vector2 indentAnimEnd;

    public Vector2 indent = new Vector2();

    @Override
    public void update(boolean updateFrustum) {
        position.x += indent.x;
        position.y += indent.y;

        super.update(updateFrustum);

        position.x -= indent.x;
        position.y -= indent.y;
    }

    public void moveIndentTo(Vector2 indentEnd, long duration) {
        timeline = new Timeline(this);
        this.indentAnimStart = indent;
        this.indentAnimEnd = indentEnd;
        timeline.addPropertyToInterpolate(
                "indentX",
                0f,
                1f
        );
        timeline.addPropertyToInterpolate(
                "indentY",
                0f,
                1f
        );
        timeline.setDuration(duration);
        timeline.play();
    }

    public void setIndentX(float scaleX) {
        indent.x = indentAnimStart.x + ((indentAnimEnd.y - indent.y) * scaleX);
    }

    public void setIndentY(float scaleY) {
        indent.y = indentAnimStart.y + ((indentAnimEnd.y - indentAnimStart.y) * scaleY);
    }
}

