package dev.xernas.hydrogen.ecs.module;

import dev.xernas.hydrogen.ecs.Actor;
import dev.xernas.photon.api.window.Window;
import dev.xernas.photon.api.window.input.Input;

public interface Module {

    default void onStart(Actor actor, Window window) {

    }

    default void onUpdate() {

    }

    default void onInput(Input input) {

    }

    default void onStop() {

    }

}
