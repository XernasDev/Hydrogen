package dev.xernas.hydrogen.ecs.ui.modules;

import dev.xernas.hydrogen.Application;
import dev.xernas.hydrogen.HydrogenException;
import dev.xernas.hydrogen.ecs.Actor;
import dev.xernas.hydrogen.ecs.module.Module;
import dev.xernas.hydrogen.ecs.module.RenderingModule;
import dev.xernas.hydrogen.ecs.ui.UITransform;
import dev.xernas.hydrogen.rendering.Renderer;
import dev.xernas.hydrogen.rendering.RenderingData;
import dev.xernas.photon.api.IRenderer;
import dev.xernas.photon.api.framebuffer.IFramebuffer;
import dev.xernas.photon.api.model.IMesh;
import dev.xernas.photon.api.shader.IShader;
import dev.xernas.photon.api.texture.ITexture;
import dev.xernas.photon.api.window.Window;

public class Bar extends Module {

    private UITransform transform;

    private int maxSize;

    private float progress = 0f;

    @Override
    public final void onStart(Application app, Actor actor, Window window, Renderer renderer) throws HydrogenException {
        if (!(actor.getTransform() instanceof UITransform)) throw new HydrogenException("Actor " + actor.getName() + " has to have a UI Transform to have a ui element attached");
        if (!actor.hasModule(RenderingModule.class)) throw new HydrogenException("Actor " + actor.getName() + " has to have a rendering module to have a progress bar");
        RenderingModule rm = actor.getModule(RenderingModule.class);
        if (!rm.getShader().equalsIgnoreCase("progress")) throw new HydrogenException("Rendering module of actor " + actor.getName() + " has to use the progress shader");
        this.transform = (UITransform) actor.getTransform();
        this.maxSize = transform.getWidth();
    }

    @Override
    public final void onUpdate() throws HydrogenException {
        if (transform.getWidth() > maxSize) throw new HydrogenException("Bar size can't exceed initial size (100%)");
    }

    @Override
    public void onRender(RenderingData data, IRenderer<IFramebuffer, IShader, IMesh, ITexture> renderer) {
        data.getShader().setUniform("u_progress", progress);
    }

    public void setProgress(float percent) {
//        transform.setWidth((int) (maxSize * percent));
        this.progress = percent;
    }

    public float getProgress() {
//        return (float) transform.getWidth() / maxSize;
        return progress;
    }

    public UITransform getUITransform() {
        return transform;
    }

    public int getMaxSize() {
        return maxSize;
    }
}
