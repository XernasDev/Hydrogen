package dev.xernas.hydrogen;

import dev.xernas.photon.api.model.Model;

public record RenderingModule(String shader, Model model, Material material) implements Module {

    @Override
    public void onStart() {

    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onStop() {

    }
}
