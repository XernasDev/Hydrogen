package dev.xernas.hydrogen.rendering.material;

import dev.xernas.hydrogen.asset.Asset;
import dev.xernas.photon.api.texture.Texture;

import java.awt.*;

public class TextureMaterial implements Material {

    private Asset.TextureAsset asset;
    private Texture texture;

    public TextureMaterial(Asset.TextureAsset asset) {
        this.asset = asset;
    }

    public TextureMaterial(Texture texture) {
        this.texture = texture;
    }

    @Override
    public Color getBaseColor() {
        return null;
    }

    @Override
    public Texture getTexture() {
        if (asset != null) texture = asset.getTexture();
        return texture;
    }

    public Asset.TextureAsset getTextureAsset() {
        return asset;
    }
}
