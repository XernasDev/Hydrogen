package dev.xernas.hydrogen.ecs.ui.modules;

import dev.xernas.photon.api.window.cursor.CursorShape;
import dev.xernas.photon.api.window.input.Input;
import dev.xernas.photon.api.window.input.Key;

public abstract class Button extends MouseBox {

    @Override
    public final void whileInside() {
        if (Input.hasPressed(Key.MOUSE_LEFT)) onMousePress(Key.MOUSE_LEFT);
        if (Input.hasPressed(Key.MOUSE_RIGHT)) onMousePress(Key.MOUSE_RIGHT);
        if (Input.hasPressed(Key.MOUSE_MIDDLE)) onMousePress(Key.MOUSE_MIDDLE);
        if (Input.hasReleased(Key.MOUSE_LEFT)) onMouseRelease(Key.MOUSE_LEFT);
        if (Input.hasReleased(Key.MOUSE_RIGHT)) onMouseRelease(Key.MOUSE_RIGHT);
        if (Input.hasReleased(Key.MOUSE_MIDDLE)) onMouseRelease(Key.MOUSE_MIDDLE);
    }

    public abstract void onMouseClick(Key mouseKey);

    public void onMousePress(Key mouseKey) {
        onMouseClick(mouseKey);
    }

    public void onMouseRelease(Key mouseKey) {

    }

    @Override
    public CursorShape getCursor() {
        return CursorShape.HAND;
    }
}
