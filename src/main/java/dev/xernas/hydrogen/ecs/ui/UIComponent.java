package dev.xernas.hydrogen.ecs.ui;

import dev.xernas.hydrogen.ecs.Actor;
import dev.xernas.hydrogen.ecs.module.Module;
import dev.xernas.hydrogen.ecs.module.RenderingModule;
import dev.xernas.hydrogen.rendering.material.Material;
import dev.xernas.hydrogen.utils.ui.UIUtils;
import dev.xernas.photon.api.model.Model;
import dev.xernas.photon.utils.Models;

public class UIComponent extends Actor {

    public UIComponent(String name, UITransform transform, Model model, Material material, String shaderName, Module... modules) {
        super(name, transform, modules);

        RenderingModule renderingModule = new RenderingModule(shaderName, model, material);
        newModules(renderingModule);
    }

    public UIComponent(String name, UITransform transform, Model model, Material material, Module... modules) {
        this(name, transform, model, material, "ui", modules);
    }

    public UIComponent(String name, UITransform transform, Material material, int pixelRounding, String shaderName, Module... modules) {
        this(name, transform, UIUtils.createUIModel(pixelRounding, transform.getWidth(), transform.getHeight(), 16), material, shaderName, modules);
    }

    public UIComponent(String name, UITransform transform, Material material, int pixelRounding, Module... modules) {
        this(name, transform, UIUtils.createUIModel(pixelRounding, transform.getWidth(), transform.getHeight(), 16), material, modules);
    }

    public UIComponent(String name, UITransform transform, Material material, Module... modules) {
        this(name, transform, UIUtils.createUIModel(0, transform.getWidth(), transform.getHeight(), 16), material, modules);
    }

    public UIComponent(UITransform transform, Model model, Material material, Module... modules) {
        this(null, transform, model, material, modules);
    }

    public UIComponent(UITransform transform, Material material, int pixelRounding, Module... modules) {
        this(null, transform, material, pixelRounding, modules);
    }

    public UIComponent(UITransform transform, Material material, Module... modules) {
        this(null, transform, material, 0, modules);
    }

}
