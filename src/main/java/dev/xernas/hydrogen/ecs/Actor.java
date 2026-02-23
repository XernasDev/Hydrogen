package dev.xernas.hydrogen.ecs;

import dev.xernas.hydrogen.ecs.module.Module;
import dev.xernas.photon.api.Transform;
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

    public String getName() {
        return name;
    }

    public Transform getTransform() {
        return transform;
    }

    public void start(Window window) {
        modules.values().forEach(module -> module.onStart(this, window));
    }

    public void update() {
        modules.values().forEach(Module::onUpdate);
    }

    public void input(Input input) {
        modules.values().forEach(module -> module.onInput(input));
    }

    public void stop() {
        modules.values().forEach(Module::onStop);
    }

    public Collection<Module> getModules() {
        return modules.values();
    }

    public <T extends Module> T getModule(Class<T> clazz) {
        return (T) modules.get(clazz);
    }

    public boolean hasModule(Class<? extends Module> moduleClass) {
        return modules.containsKey(moduleClass);
    }
}
