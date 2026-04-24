package dev.xernas.hydrogen.ecs.module;

import dev.xernas.hydrogen.ecs.Actor;
import dev.xernas.hydrogen.rendering.Renderer;
import dev.xernas.hydrogen.rendering.RenderingData;
import dev.xernas.hydrogen.rendering.material.Material;
import dev.xernas.photon.api.IRenderer;
import dev.xernas.photon.api.framebuffer.IFramebuffer;
import dev.xernas.photon.api.model.IMesh;
import dev.xernas.photon.api.model.Model;
import dev.xernas.photon.api.shader.IShader;
import dev.xernas.photon.api.texture.ITexture;
import dev.xernas.photon.api.window.Window;
import dev.xernas.photon.exceptions.PhotonException;

public class RenderingModule extends Module {

    private Actor actor;
    private Renderer renderer;

    private final String shader;
    private Model model;
    private Material material;

    public RenderingModule(String shader, Model model, Material material) {
        this.shader = shader;
        this.model = model;
        this.material = material;
    }

    @Override
    public void onStart(Actor actor, Window window, Renderer renderer) {
        this.actor = actor;
        this.renderer = renderer;
    }

    @Override
    public void onRender(RenderingData data, IRenderer<IFramebuffer, IShader, IMesh, ITexture> renderer) {
        IShader shader = data.getShader();
        if (data.getMaterial().getBaseColor() != null) shader.setUniform("u_color", data.getMaterial().getBaseColor());
        if (data.getTexture() != null) {
            shader.setUniform("u_useTexture", true);
            renderer.useTexture("u_sampler", data.getTexture(), 0, shader);
        }
        else shader.setUniform("u_useTexture", false);
    }

    public void setModel(Model model) {
        this.model = model;
        try {
            renderer.reloadActor(actor);
        } catch (PhotonException e) {
            e.printStackTrace(); // Pour l'instant mais on va handle ça proprement (il faut un nouveau systeme d'erreurs pour les boucles principales)
        }
    }

    public void setMaterial(Material material) {
        this.material = material;
        try {
            renderer.reloadActor(actor);
        } catch (PhotonException e) {
            e.printStackTrace(); // Pour l'instant mais on va handle ça proprement (il faut un nouveau systeme d'erreurs pour les boucles principales)
        }
    }

    public String getShader() {
        return shader;
    }

    public Model getModel() {
        return model;
    }

    public Material getMaterial() {
        return material;
    }
}
