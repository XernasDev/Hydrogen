package dev.xernas.hydrogen.rendering;

import dev.xernas.hydrogen.rendering.material.Material;
import dev.xernas.photon.api.IRenderer;
import dev.xernas.photon.api.framebuffer.IFramebuffer;
import dev.xernas.photon.api.model.IMesh;
import dev.xernas.photon.api.model.Model;
import dev.xernas.photon.api.shader.IShader;
import dev.xernas.photon.api.shader.Shader;
import dev.xernas.photon.api.texture.ITexture;
import dev.xernas.photon.exceptions.PhotonException;

public class RenderingData {

    private final Shader shaderObj;
    private final Model modelObj;
    private final Material materialObj;
    private IShader shader;
    private IMesh mesh;
    private ITexture texture;
    private Material material;

    private boolean loaded = false;

    public RenderingData(Shader shader, Model model, Material material) {
        this.shaderObj = shader;
        this.modelObj = model;
        this.materialObj = material;
    }

    public void load(IRenderer<IFramebuffer, IShader, IMesh, ITexture> renderer) throws PhotonException {
        this.shader = renderer.loadShader(shaderObj);
        this.mesh = renderer.loadMesh(modelObj);
        this.material = materialObj;
        if (material.getTexture() == null) this.texture = null;
        else this.texture = renderer.loadTexture(materialObj.getTexture());
        this.loaded = true;
    }

    public void unload(IRenderer<IFramebuffer, IShader, IMesh, ITexture> renderer) throws PhotonException {
        renderer.unloadShader(shader);
        renderer.unloadMesh(mesh);
        if (texture != null) renderer.unloadTexture(texture);
        this.shader = null;
        this.mesh = null;
        this.texture = null;
        this.material = null;
        loaded = false;
    }

    public IShader getShader() {
        return shader;
    }

    public IMesh getMesh() {
        return mesh;
    }

    public ITexture getTexture() {
        return texture;
    }

    public Material getMaterial() {
        return material;
    }

    public boolean isLoaded() {
        return loaded;
    }
}
