package dev.xernas.hydrogen.ecs.module;

import dev.xernas.hydrogen.rendering.material.Material;
import dev.xernas.photon.api.model.Model;

public record RenderingModule(String shader, Model model, Material material) implements Module {

}
