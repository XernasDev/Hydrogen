package dev.xernas.hydrogen.ecs.ui.modules;

import dev.xernas.hydrogen.Application;
import dev.xernas.hydrogen.HydrogenException;
import dev.xernas.hydrogen.ecs.Actor;
import dev.xernas.hydrogen.ecs.module.Module;
import dev.xernas.hydrogen.ecs.ui.UITransform;
import dev.xernas.hydrogen.rendering.Renderer;
import dev.xernas.photon.api.window.Window;
import dev.xernas.photon.api.window.cursor.CursorShape;
import dev.xernas.photon.api.window.input.Input;
import dev.xernas.photon.api.window.input.Mouse;
import dev.xernas.photon.exceptions.PhotonException;

public abstract class MouseBox extends Module {

    private Window window;
    private UITransform transform;

    private boolean isInside;
    private CursorShape originalCursorShape = CursorShape.ARROW;

    @Override
    public void onStart(Application app, Actor actor, Window window, Renderer renderer) throws HydrogenException {
        if (!(actor.getTransform() instanceof UITransform)) throw new HydrogenException("Actor " + actor.getName() + " has to have a UI Transform to have a ui element attached");
        this.transform = (UITransform) actor.getTransform();
        this.window = window;
    }

    @Override
    public void onInput() throws HydrogenException {
        Mouse mouse = Input.getMouse();
        // This condition is fulfilled if the mouse is inside the confines of the UI Transform
        if (
                (mouse.getX() > transform.getX() && mouse.getY() > transform.getY())
             && (mouse.getX() < transform.getX() + transform.getWidth() && mouse.getY() < transform.getY() + transform.getHeight())
        ) {
            // This condition is fulfilled if the mouse is entering for the first time
            if (!isInside) {
                isInside = true;
                CursorShape shape = getCursor();
                if (shape != null) {
                    originalCursorShape = window.getCursorShape();
                    try {
                        window.setCursorShape(getCursor());
                    } catch (PhotonException e) {
                        throw new HydrogenException(e);
                    }
                }
                onMouseEnter();
            }

            whileInside();
        }
        else { // So if the mouse is not inside
            if (isInside) { // But it was inside the previous "onInput"
                isInside = false;
                if (getCursor() != null) {
                    try {
                        window.setCursorShape(originalCursorShape);
                    } catch (PhotonException e) {
                        throw new HydrogenException(e);
                    }
                }
                onMouseLeave(); // We detect that it left
            }
        }
    }

    public abstract void onMouseEnter();
    public abstract void onMouseLeave();
    public abstract void whileInside();

    public abstract CursorShape getCursor();

    public UITransform getUITransform() {
        return transform;
    }
}
