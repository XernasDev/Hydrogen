package dev.xernas.hydrogen.ecs;

import dev.xernas.hydrogen.HydrogenException;
import dev.xernas.hydrogen.ecs.module.Module;
import dev.xernas.hydrogen.rendering.Renderer;
import dev.xernas.photon.api.Transform;
import dev.xernas.photon.api.window.Window;
import dev.xernas.photon.api.window.input.Input;
import dev.xernas.photon.exceptions.PhotonException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Scene {

    private final String name;
    private final List<Actor> actors = new ArrayList<>();

    private boolean is3D;

    public Scene(String name, Actor... actors) {
        this.name = name;
        this.actors.addAll(List.of(actors));
    }

    public void load(Window window, Renderer renderer) throws PhotonException {
        if (!hasCameraActor()) throw new HydrogenException("Scene " + name + " must have at least one camera actor to be loaded.");
        renderer.loadScene(this);
        actors.forEach(actor -> actor.start(window));
    }

    public void update() {
        actors.forEach(Actor::update);
    }

    public void input(Input input) {
        actors.forEach(actor -> actor.input(input));
    }

    public void stop() {
        actors.forEach(Actor::stop);
    }

    public Scene addActor(Actor actor) {
        actors.add(actor);
        return this;
    }

    public String getName() {
        return name;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public List<Actor> getActorsWithModule(Class<? extends Module> moduleClass) {
        return actors.stream()
                     .filter(actor -> actor.hasModule(moduleClass))
                     .toList();
    }

    public Actor getCameraActor() {
        Optional<Actor> cameraActor = actors.stream()
                .filter(actor -> actor.getTransform() instanceof Transform.CameraTransform)
                .findAny();
        return cameraActor.orElse(null);
    }

    public boolean hasCameraActor() {
        return actors.stream()
                .anyMatch(actor -> actor.getTransform() instanceof Transform.CameraTransform);
    }

    public boolean is3D() {
        return is3D;
    }

    public void set3D(boolean is3D) {
        this.is3D = is3D;
    }
}
