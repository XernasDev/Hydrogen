package dev.xernas.hydrogen.rendering;

import dev.xernas.hydrogen.*;
import dev.xernas.hydrogen.asset.Asset;
import dev.xernas.hydrogen.asset.AssetManager;
import dev.xernas.hydrogen.ecs.Actor;
import dev.xernas.hydrogen.ecs.module.GlobalModule;
import dev.xernas.hydrogen.ecs.module.RenderingModule;
import dev.xernas.hydrogen.ecs.Scene;
import dev.xernas.hydrogen.rendering.material.Material;
import dev.xernas.photon.api.IRenderer;
import dev.xernas.photon.api.Transform;
import dev.xernas.photon.api.framebuffer.IFramebuffer;
import dev.xernas.photon.api.model.IMesh;
import dev.xernas.photon.api.model.Model;
import dev.xernas.photon.api.shader.IShader;
import dev.xernas.photon.api.shader.Shader;
import dev.xernas.photon.api.texture.ITexture;
import dev.xernas.photon.api.window.Window;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.utils.MatrixUtils;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.*;

public class DefaultRenderer implements Renderer {

    private final IRenderer<IFramebuffer, IShader, IMesh, ITexture> renderer;
    private final Window window;
    private final Map<Actor, RenderingData> loadedData;

    private Scene currentScene;

    public DefaultRenderer(IRenderer<IFramebuffer, IShader, IMesh, ITexture> renderer, Window window) {
        this.renderer = renderer;
        this.window = window;
        this.loadedData = new HashMap<>();
    }

    @Override
    public void render(Scene scene) throws PhotonException {
        if (isNotSceneLoaded(scene)) throw new HydrogenException("Scene " + scene.getName() + " not loaded");
        renderer.clear(AppConstants.APP_COLOR);
        for (Map.Entry<Actor, RenderingData> entry : loadedData.entrySet()) {
            Actor actor = entry.getKey();
            RenderingData data = entry.getValue();
            if (!data.isLoaded()) continue;
            IShader shader = data.getShader();

            renderer.render(shader, data.getMesh(), () -> {
                // Vertex
                Matrix4f modelMatrix = MatrixUtils.createTransformationMatrix(actor.getTransform());
                shader.setUniform("u_modelMatrix", modelMatrix);
                if (scene.is3D()) {
                    shader.setUniform("u_viewProjectionMatrix",
                            MatrixUtils.createProjectionMatrix(window, 80, 0.1f, 1000f)
                                    .mul(MatrixUtils.createViewMatrix((Transform.CameraTransform) scene.getCameraActor().getTransform())));
                }
                else {
                    shader.setUniform("u_viewProjectionMatrix",
                            MatrixUtils.createOrthoMatrix(window)
                                    .mul(MatrixUtils.create2DViewMatrix((Transform.CameraTransform) scene.getCameraActor().getTransform())));
                }
                shader.setUniform("u_normalMatrix", new Matrix3f(modelMatrix).invert().transpose());
                shader.setUniform("u_cameraWorldPos", scene.getCameraActor().getTransform().getPosition());

                // Actor rendering
                actor.render(data, renderer);
            });

            for (Actor globalModuleActor : scene.getActorsWithModule(GlobalModule.class)) {
                GlobalModule globalModule = globalModuleActor.getModule(GlobalModule.class);
                globalModule.onGlobalRender(shader, renderer);
            }
        }
    }

    @Override
    public void loadScene(Scene scene) throws PhotonException {
        List<Actor> loadedActors = new ArrayList<>();
        for (Actor renderableActor : scene.getActorsWithModule(RenderingModule.class)) {
            if (loadedActors.contains(renderableActor)) continue;
            RenderingModule renderingModule = renderableActor.getModule(RenderingModule.class);

            Asset.ShaderAsset shaderAsset = AssetManager.getAssetByName(renderingModule.getShader());
            if (shaderAsset == null) throw new HydrogenException("Shader not found: " + renderingModule.getShader());
            Shader shader = shaderAsset.getShader();
            Model model = renderingModule.getModel();
            if (model.is3D()) scene.set3D(true);
            Material material = renderingModule.getMaterial();

            RenderingData data = new RenderingData(shader, model, material);
            data.load(renderer);
            loadedData.put(renderableActor, data);
            loadedActors.add(renderableActor);
        }
        if (isNotSceneLoaded(scene)) currentScene = scene;
    }

    @Override
    public void loadActor(Actor actor) throws PhotonException {
        if (!actor.hasModule(RenderingModule.class)) return;
        RenderingModule renderingModule = actor.getModule(RenderingModule.class);

        Asset.ShaderAsset shaderAsset = AssetManager.getAssetByName(renderingModule.getShader());
        if (shaderAsset == null) throw new HydrogenException("Shader not found: " + renderingModule.getShader());
        Shader shader = shaderAsset.getShader();
        Model model = renderingModule.getModel();
        Material material = renderingModule.getMaterial();

        RenderingData data = new RenderingData(shader, model, material);
        data.load(renderer);
        loadedData.put(actor, data);
    }

    @Override
    public void unloadScene() throws PhotonException {
        for (RenderingData value : loadedData.values()) {
            if (!value.isLoaded()) continue;
            value.unload(renderer);
        }
        loadedData.clear();
        currentScene = null;
    }

    @Override
    public void unloadActor(Actor actor) throws PhotonException {
        if (!loadedData.containsKey(actor)) return;
        RenderingData data = loadedData.get(actor);
        if (!data.isLoaded()) return;
        data.unload(renderer);
        loadedData.remove(actor);
    }

    public boolean isNotSceneLoaded(Scene scene) {
        return currentScene != scene;
    }
}
