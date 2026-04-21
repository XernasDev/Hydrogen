package dev.xernas.hydrogen.asset;

import dev.xernas.photon.api.shader.Shader;

public class Asset {

    private final String path;
    private final String name;
    private final AssetManager owner;

    public Asset(String path, String name, AssetManager owner) {
        this.path = path;
        this.name = name;
        this.owner = owner;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public AssetManager getOwner() {
        return owner;
    }

    public static class ShaderAsset extends Asset {

        private final Shader shader;

        public ShaderAsset(String path, String name, AssetManager owner, Shader shader) {
            super(path, name, owner);
            this.shader = shader;
        }

        public Shader getShader() {
            return shader;
        }
    }

}
