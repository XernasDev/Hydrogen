package dev.xernas.hydrogen.ecs.module;

import dev.xernas.hydrogen.ecs.Actor;
import dev.xernas.hydrogen.rendering.RenderingData;
import dev.xernas.photon.api.IRenderer;
import dev.xernas.photon.api.framebuffer.IFramebuffer;
import dev.xernas.photon.api.model.IMesh;
import dev.xernas.photon.api.shader.IShader;
import dev.xernas.photon.api.texture.ITexture;

public abstract class GlobalModule extends Module {

    public abstract void onGlobalRender(IShader shader, Actor actor, IRenderer<IFramebuffer, IShader, IMesh, ITexture> renderer);

    public final void onRender(RenderingData data, IRenderer<IFramebuffer, IShader, IMesh, ITexture> renderer) {

    }

}
