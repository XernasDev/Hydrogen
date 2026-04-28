package dev.xernas.hydrogen.ecs.ui;

import dev.xernas.hydrogen.Application;
import dev.xernas.hydrogen.ecs.Actor;
import dev.xernas.hydrogen.ecs.module.Module;
import dev.xernas.hydrogen.rendering.Renderer;
import dev.xernas.photon.api.window.Window;

public class Bar extends Module {

    private UITransform transform;

    private int maxSize;

    @Override
    public final void onStart(Application app, Actor actor, Window window, Renderer renderer) {
        if (!(actor.getTransform() instanceof UITransform)) throw new IllegalStateException("Actor " + actor.getName() + " has to have a UI Transform to have a ui element attached");
        this.transform = (UITransform) actor.getTransform();
        this.maxSize = transform.getWidth();
    }

    @Override
    public final void onUpdate() {
        if (transform.getWidth() > maxSize) throw new IllegalStateException("Bar size can't exceed initial size (100%)");
    }

    public void setProgress(float percent) {
        transform.setWidth((int) (maxSize * percent));
    }

    public float getProgress() {
        return (float) transform.getWidth() / maxSize;
    }

    public UITransform getUITransform() {
        return transform;
    }

    public int getMaxSize() {
        return maxSize;
    }
}
