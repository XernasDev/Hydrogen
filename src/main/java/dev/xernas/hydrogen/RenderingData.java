package dev.xernas.hydrogen;

import dev.xernas.photon.api.IRenderer;
import dev.xernas.photon.api.framebuffer.IFramebuffer;
import dev.xernas.photon.api.model.IMesh;
import dev.xernas.photon.api.model.Model;
import dev.xernas.photon.api.shader.IShader;
import dev.xernas.photon.api.shader.Shader;
import dev.xernas.photon.api.texture.ITexture;
import dev.xernas.photon.api.texture.Texture;
import dev.xernas.photon.exceptions.PhotonException;

public class RenderingData {

    private final IShader shader;
    private final IMesh mesh;
    private final ITexture texture;
    private final Material material;

    public RenderingData(Shader shader, Model model, Material material, IRenderer<IFramebuffer, IShader, IMesh, ITexture> renderer) throws PhotonException {
        this.shader = renderer.loadShader(shader);
        this.mesh = renderer.loadMesh(model);
        if (material.getTexture() == null) this.texture = null;
        else this.texture = renderer.loadTexture(material.getTexture());
        this.material = material;
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
}
