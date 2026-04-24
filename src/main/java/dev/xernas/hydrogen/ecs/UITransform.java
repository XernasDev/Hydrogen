package dev.xernas.hydrogen.ecs;

import dev.xernas.hydrogen.utils.ui.PositionConverter;
import dev.xernas.photon.api.Transform;
import org.joml.Vector3f;

import java.util.function.IntSupplier;

public class UITransform extends Transform {

    private final IntSupplier x;
    private final IntSupplier y;
    private final IntSupplier width;
    private final IntSupplier height;

    public UITransform(int x, int y, int width, int height) {
        this.x = () -> x;
        this.y = () -> y;
        this.width = () -> width;
        this.height = () -> height;
    }

    public UITransform(IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    //TODO: Make ui transforms mobile

    @Override
    public Vector3f getPosition() {
        return PositionConverter.pixelScaledPosToWorldScaledPos(x.getAsInt(), y.getAsInt(), width.getAsInt(), height.getAsInt());
    }

    @Override
    public Vector3f getScale() {
        return PositionConverter.pixelScaleToWorldScale(width.getAsInt(), height.getAsInt());
    }
}
