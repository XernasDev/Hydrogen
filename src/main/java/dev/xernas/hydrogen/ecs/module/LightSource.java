package dev.xernas.hydrogen.ecs.module;

import dev.xernas.hydrogen.ecs.Actor;
import dev.xernas.hydrogen.rendering.Renderer;
import dev.xernas.hydrogen.rendering.RenderingData;
import dev.xernas.photon.api.IRenderer;
import dev.xernas.photon.api.Transform;
import dev.xernas.photon.api.framebuffer.IFramebuffer;
import dev.xernas.photon.api.model.IMesh;
import dev.xernas.photon.api.shader.IShader;
import dev.xernas.photon.api.texture.ITexture;
import dev.xernas.photon.api.window.Window;

import java.awt.*;

public class LightSource extends GlobalModule {

    private float intensity;

    private Transform transform;

    public LightSource(float intensity) {
        this.intensity = intensity;
    }

    @Override
    public void onStart(Actor actor, Window window, Renderer renderer) {
        this.transform = actor.getTransform();
    }

    @Override
    public void onGlobalRender(IShader shader, IRenderer<IFramebuffer, IShader, IMesh, ITexture> renderer) {
        shader.setUniform("lightPos", transform.getPosition());
        shader.setUniform("lightIntensity", intensity);
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}
