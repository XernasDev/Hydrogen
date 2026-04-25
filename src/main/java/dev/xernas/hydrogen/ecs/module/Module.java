package dev.xernas.hydrogen.ecs.module;

import dev.xernas.hydrogen.Application;
import dev.xernas.hydrogen.ecs.Actor;
import dev.xernas.hydrogen.rendering.Renderer;
import dev.xernas.hydrogen.rendering.RenderingData;
import dev.xernas.photon.api.IRenderer;
import dev.xernas.photon.api.framebuffer.IFramebuffer;
import dev.xernas.photon.api.model.IMesh;
import dev.xernas.photon.api.shader.IShader;
import dev.xernas.photon.api.texture.ITexture;
import dev.xernas.photon.api.window.Window;
import dev.xernas.photon.api.window.input.Input;

public abstract class Module {

    private boolean active = false;

    public abstract void onStart(Application app, Actor actor, Window window, Renderer renderer);

    public void onUpdate() {

    }

    public void onInput(Input input) {

    }

    public void onRender(RenderingData data, IRenderer<IFramebuffer, IShader, IMesh, ITexture> renderer) {

    }

    public void onStop() {

    }

    public final void enable(Application app, Actor actor, Window window, Renderer renderer) {
        onStart(app, actor, window, renderer);
        active = true;
    }

    public final void disable() {
        onStop();
        active = false;
    }

    public boolean isActive() {
        return active;
    }

}
