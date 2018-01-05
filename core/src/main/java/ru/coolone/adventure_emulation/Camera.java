package ru.coolone.adventure_emulation;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

import org.pushingpixels.trident.Timeline;

/**
 * Game @{@link com.badlogic.gdx.graphics.OrthographicCamera}
 */

public class Camera extends OrthographicCamera {
    private static final String TAG = Camera.class.getSimpleName();

    Timeline timeline = new Timeline();
    Vector2 indentStart;
    Vector2 indentEnd;
    Vector2 indent = new Vector2();

    public Camera() {
        super();
    }

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
        this.indentStart = indent;
        this.indentEnd = indentEnd;
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
        indent.x = indentStart.x + ((indentEnd.y - indent.y) * scaleX);
    }

    public void setIndentY(float scaleY) {
        indent.y = indentStart.y + ((indentEnd.y - indentStart.y) * scaleY);
    }
}

