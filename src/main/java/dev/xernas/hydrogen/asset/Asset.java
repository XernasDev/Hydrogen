package dev.xernas.hydrogen.asset;

import dev.xernas.photon.api.shader.Shader;

public class Asset {

    private final String path;
    private final String name;

    public Asset(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public static class ShaderAsset extends Asset {

        private final Shader shader;

        public ShaderAsset(String path, String name, Shader shader) {
            super(path, name);
            this.shader = shader;
        }

        public Shader getShader() {
            return shader;
        }
    }

}
