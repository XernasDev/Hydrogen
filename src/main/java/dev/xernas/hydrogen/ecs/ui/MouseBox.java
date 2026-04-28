package dev.xernas.hydrogen.ecs.ui;

import dev.xernas.hydrogen.Application;
import dev.xernas.hydrogen.ecs.Actor;
import dev.xernas.hydrogen.ecs.module.Module;
import dev.xernas.hydrogen.rendering.Renderer;
import dev.xernas.photon.api.window.Window;
import dev.xernas.photon.api.window.input.Input;
import dev.xernas.photon.api.window.input.Key;
import dev.xernas.photon.api.window.input.Mouse;

public abstract class MouseBox extends Module {

    private UITransform transform;

    private boolean isInside;

    @Override
    public void onStart(Application app, Actor actor, Window window, Renderer renderer) {
        if (!(actor.getTransform() instanceof UITransform)) throw new IllegalStateException("Actor " + actor.getName() + " has to have a UI Transform to have a ui element attached");
        this.transform = (UITransform) actor.getTransform();
    }

    @Override
    public void onInput(Input input) {
        Mouse mouse = input.getMouse();
        // This condition is fulfilled if the mouse is inside the confines of the UI Transform
        if (
                (mouse.getX() > transform.getX() && mouse.getY() > transform.getY())
             && (mouse.getX() < transform.getX() + transform.getWidth() && mouse.getY() < transform.getY() + transform.getHeight())
        ) {
            // This condition is fulfilled if the mouse is entering for the first time
            if (!isInside) {
                isInside = true;
                onMouseEnter();
            }

            whileInside(input);
        }
        else { // So if the mouse is not inside
            if (isInside) { // But it was inside the previous "onInput"
                isInside = false;
                onMouseLeave(); // We detect that it left
            }
        }
    }

    public abstract void onMouseEnter();
    public abstract void onMouseLeave();
    public abstract void whileInside(Input input);

    public UITransform getUITransform() {
        return transform;
    }
}
