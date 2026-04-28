package dev.xernas.hydrogen.ecs.ui;

import dev.xernas.hydrogen.rendering.material.Material;
import dev.xernas.photon.api.model.Model;

public class Slider extends UIComponent {

    private final Bar bar;

    public Slider(String name, UITransform transform, int knobSize, Material barMaterial, Model knobModel, Material knobMaterial, Material backMaterial) {
        super(name, transform.setZIndex(0.5f), barMaterial);

        bar = new Bar();
        newModules(bar);

        final int barWidth = transform.getWidth();

        UIComponent knob = new UIComponent(
                "SliderKnob",
                new UITransform(
                        0,
                        0,
                        knobSize,
                        knobSize
                ).setZIndex(1f),
                knobModel,
                knobMaterial,
                new SliderKnob() {
                    @Override
                    public void onRelease(Bar bar) {
                        Slider.this.onRelease(bar);
                    }
                }
        );

        UIComponent backgroundBar = new UIComponent(
                "BackgroundBar",
                new UITransform(
                        transform.getRawX(),
                        transform.getRawY(),
                        () -> barWidth,
                        transform::getHeight
                ).setZIndex(0f),
                backMaterial
        );

        newChildren(knob, backgroundBar);
    }

    public Slider(UITransform transform, int knobSize, Material barMaterial, Model knobModel, Material knobMaterial, Material backMaterial) {
        this(null, transform, knobSize, barMaterial, knobModel, knobMaterial, backMaterial);
    }

    public Slider(String name, UITransform transform, int knobSize, Material barMaterial, Material knobMaterial, Material backMaterial) {
        super(name, transform.setZIndex(0.5f), barMaterial);

        bar = new Bar();
        newModules(bar);

        final int barWidth = transform.getWidth();

        UIComponent knob = new UIComponent(
                "SliderKnob",
                new UITransform(
                        0,
                        0,
                        knobSize,
                        knobSize
                ).setZIndex(1f),
                knobMaterial,
                new SliderKnob() {
                    @Override
                    public void onRelease(Bar bar) {
                        Slider.this.onRelease(bar);
                    }
                }
        );

        UIComponent backgroundBar = new UIComponent(
                "BackgroundBar",
                new UITransform(
                        transform.getRawX(),
                        transform.getRawY(),
                        () -> barWidth,
                        transform::getHeight
                ).setZIndex(0f),
                backMaterial
        );

        newChildren(knob, backgroundBar);
    }

    public Slider(UITransform transform, int knobSize, Material barMaterial, Material knobMaterial, Material backMaterial) {
        this(null, transform, knobSize, barMaterial, knobMaterial, backMaterial);
    }

    public void onRelease(Bar bar) {

    }

    public float getProgress() {
        return bar.getProgress();
    }

}
