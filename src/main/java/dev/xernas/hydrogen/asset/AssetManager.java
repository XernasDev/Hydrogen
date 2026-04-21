package dev.xernas.hydrogen.asset;

import dev.xernas.atom.file.FileUtils;
import dev.xernas.hydrogen.Application;
import dev.xernas.hydrogen.HydrogenException;
import dev.xernas.microscope.helper.PathHelper;
import dev.xernas.photon.api.shader.Shader;
import dev.xernas.photon.utils.ShaderResource;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AssetManager {

    private static final Map<String, Asset> loadedAssets = new HashMap<>();

    private final ClassLoader classLoader;
    private final String shadersDirectory;

    public AssetManager(ClassLoader classLoader, String shadersDirectory) {
        this.classLoader = classLoader;
        this.shadersDirectory = shadersDirectory;
    }

    public static <T extends Asset> T getAssetByName(String name) throws HydrogenException {
        Asset asset = loadedAssets.get(name);
        if (asset == null) throw new HydrogenException("No asset found with name: " + name);
        try {
            return (T) asset;
        } catch (ClassCastException e) {
            throw new HydrogenException("Asset with name " + name + " is not of the expected type.");
        }
    }

    private Path getFilePath(String resourcePath) throws HydrogenException {
        try {
            return PathHelper.getResourcePath(classLoader, resourcePath);
        } catch (FileNotFoundException fe) {
            throw new HydrogenException("File not found: " + resourcePath);
        } catch (URISyntaxException ue) {
            throw new HydrogenException("Invalid file path: " + resourcePath);
        }
    }

    private String readPathString(Path path) throws HydrogenException {
        try {
            return PathHelper.getStringOf(path);
        } catch (IOException ie) {
            throw new HydrogenException("Failed to read file: " + path);
        }
    }

    private ShaderResource loadShaderResource(String path) throws HydrogenException {
        Path resourcePath = getFilePath(shadersDirectory + "/" + path);
        String shaderCode = readPathString(resourcePath);

        return new ShaderResource(resourcePath.getFileName().toString(), shaderCode);
    }

    private void loadShader(Path path) throws HydrogenException {
        String shaderJson = readPathString(path);
        JSONTokener tokener = new JSONTokener(shaderJson);
        JSONObject root = new JSONObject(tokener);
        String shaderName = root.getString("name");
        if (isAssetLoaded(shaderName)) throw new HydrogenException("Shader with name " + shaderName + " already loaded.");
        JSONObject vertex = root.getJSONObject("vertex");
        JSONObject fragment = root.getJSONObject("fragment");
        boolean vertexFromHydro = vertex.getBoolean("fromHydrogen");
        boolean fragmentFromHydro = fragment.getBoolean("fromHydrogen");
        ShaderResource vertexResource = vertexFromHydro ? Application.getHydrogenAssetManager().loadShaderResource(vertex.getString("path")) : loadShaderResource(vertex.getString("path"));
        ShaderResource fragmentResource = fragmentFromHydro ? Application.getHydrogenAssetManager().loadShaderResource(fragment.getString("path")) : loadShaderResource(fragment.getString("path"));
        Shader shader = new Shader(vertexResource, fragmentResource);
        Asset.ShaderAsset shaderAsset = new Asset.ShaderAsset(path.toString(), shaderName, this, shader);
        loadedAssets.put(shaderName, shaderAsset);
    }

    public void loadShaders() throws HydrogenException {
        Path shadersDirPath = getFilePath(shadersDirectory);
        try {
            Set<Path> shaderFiles = PathHelper.list(shadersDirPath);
            for (Path shaderFile : shaderFiles) {
                if (!FileUtils.getFileExtension(shaderFile.getFileName().toString()).equalsIgnoreCase("json")) continue;
                loadShader(shaderFile);
            }
        } catch (IOException e) {
            throw new HydrogenException("Failed to list shader files in directory: " + shadersDirectory, e);
        }
    }

    public boolean isAssetLoaded(String name) throws HydrogenException {
        Asset asset = loadedAssets.get(name);
        return asset != null && asset.getOwner() == this;
    }

}
