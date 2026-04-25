package dev.xernas.hydrogen.ecs.module;

import dev.xernas.hydrogen.AppConstants;
import dev.xernas.hydrogen.Application;
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

    private static int lightCounter = 0;
    private static int numberOfLights = 0;

    private static float ambiantLight = 0.15f;

    private Color color;
    private float intensity;

    private Transform transform;

    public LightSource() {
        this(1f);
    }

    public LightSource(float intensity) {
        this(Color.WHITE, intensity);
    }

    public LightSource(Color color, float intensity) {
        this.color = color;
        this.intensity = intensity;
    }

    @Override
    public void onStart(Application app, Actor actor, Window window, Renderer renderer) {
        this.transform = actor.getTransform();
        numberOfLights++;
    }

    @Override
    public void onGlobalRender(IShader shader, Actor actor, IRenderer<IFramebuffer, IShader, IMesh, ITexture> renderer) {
        if (lightCounter >= AppConstants.MAX_LIGHTS) return;

        if (lightCounter == 0) {
            shader.setUniform("u_ambiantLight", ambiantLight);
            shader.setUniform("u_lightCount", numberOfLights);
        }
        shader.setUniform("u_lightPos[" + lightCounter + "]", transform.getPosition());
        shader.setUniform("u_lightIntensity[" + lightCounter + "]", intensity);
        shader.setUniform("u_lightColor[" + lightCounter + "]", color);
        lightCounter++;
        if (lightCounter == numberOfLights) lightCounter = 0;
    }

    @Override
    public void onStop() {
        numberOfLights--;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public static void setAmbiantLight(float ambiantLight) {
        LightSource.ambiantLight = ambiantLight;
    }
}
