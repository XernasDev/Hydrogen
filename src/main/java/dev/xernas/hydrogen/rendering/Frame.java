package dev.xernas.hydrogen.rendering;

import dev.xernas.photon.api.texture.ITexture;

public class Frame {

    private final ITexture texture;

    public Frame(ITexture texture) {
        this.texture = texture;
    }

    public ITexture getTexture() {
        return texture;
    }

}
