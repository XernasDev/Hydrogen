package dev.xernas.hydrogen.ecs;

import dev.xernas.hydrogen.ecs.module.Module;
import dev.xernas.hydrogen.rendering.Renderer;
import dev.xernas.hydrogen.rendering.RenderingData;
import dev.xernas.photon.api.IRenderer;
import dev.xernas.photon.api.Transform;
import dev.xernas.photon.api.framebuffer.IFramebuffer;
import dev.xernas.photon.api.model.IMesh;
import dev.xernas.photon.api.shader.IShader;
import dev.xernas.photon.api.texture.ITexture;
import dev.xernas.photon.api.window.Window;
import dev.xernas.photon.api.window.input.Input;

import java.util.*;

public class Actor {

    private final String name;
    private final Transform transform;
    private final Map<Class<? extends Module>, Module> modules;

    public Actor(Module... modules) {
        this(new Transform(), modules);
    }

    public Actor(Transform transform, Module... modules) {
        this(UUID.randomUUID().toString(), transform, modules);
    }

    public Actor(String name, Module... modules) {
        this(name, new Transform(), modules);
    }

    public Actor(String name, Transform transform, Module... modules) {
        this.name = name;
        this.transform = transform;
        this.modules = new HashMap<>();
        for (Module module : modules) {
            this.modules.put(module.getClass(), module);
        }
    }

    public void newModules(Module... modules) {
        for (Module module : modules) {
            this.modules.put(module.getClass(), module);
        }
    }

    public String getName() {
        return name;
    }

    public Transform getTransform() {
        return transform;
    }

    public void start(Window window, Renderer renderer) {
        modules.values().forEach(module -> module.enable(this, window, renderer));
    }

    public void update() {
        modules.values().forEach(module -> {
            if (module.isActive()) module.onUpdate();
        });
    }

    public void input(Input input) {
        modules.values().forEach(module -> {
            if (module.isActive()) module.onInput(input);
        });
    }

    public void render(RenderingData data, IRenderer<IFramebuffer, IShader, IMesh, ITexture> renderer) {
        modules.values().forEach(module -> module.onRender(data, renderer));
    }

    public void stop() {
        modules.values().forEach(Module::onStop);
        modules.clear();
    }

    public Collection<Module> getModules() {
        return modules.values();
    }

    public <T extends Module> T getModule(Class<T> clazz) {
        for (Module module : modules.values()) {
            if (clazz.isAssignableFrom(module.getClass())) {
                return clazz.cast(module);
            }
        }
        return null;
    }

    public boolean hasModule(Class<? extends Module> moduleClass) {
        for (Module module : modules.values()) {
            if (moduleClass.isAssignableFrom(module.getClass())) {
                return true;
            }
        }
        return false;
    }
}
