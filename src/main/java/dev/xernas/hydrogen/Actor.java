package dev.xernas.hydrogen;

import dev.xernas.photon.api.Transform;

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
