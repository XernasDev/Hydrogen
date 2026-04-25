package dev.xernas.hydrogen.ecs.ui;

import dev.xernas.hydrogen.HydrogenException;
import dev.xernas.hydrogen.utils.ui.PositionConverter;
import dev.xernas.hydrogen.utils.ui.UnitHelper;
import dev.xernas.photon.api.Transform;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.function.IntSupplier;

public class UITransform extends Transform {

    private IntSupplier x;
    private IntSupplier y;
    private IntSupplier width;
    private IntSupplier height;

    private final Vector2f scale = new Vector2f(1, 1);

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

    public void move(int x, int y) {
        move(() -> x, () -> y);
    }

    public void move(IntSupplier x, IntSupplier y) {
        this.x = UnitHelper.add(this.x, x);
        this.y = UnitHelper.add(this.y, y);
    }

    public void scale(float scaleX, float scaleY) {
        this.scale.x = scaleX;
        this.scale.y = scaleY;
        this.width = UnitHelper.scale(this.width, scaleX);
        this.height = UnitHelper.scale(this.height, scaleY);
    }

    @Override
    public Transform scale(float scale) {
        scale(scale, scale);
        return this;
    }

    @Override
    public Transform scale(float x, float y, float z) {
        scale(x, y);
        return this;
    }

    public void scaleX(float scale) {
        scale(scale, this.scale.y);
    }

    public void scaleY(float scale) {
        scale(this.scale.x, scale);
    }

    public void setPosition(int x, int y) {
        this.x = () -> x;
        this.y = () -> y;
    }

    public void setPosition(IntSupplier x, IntSupplier y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Vector3f getPosition() {
        return PositionConverter.pixelScaledPosToWorldScaledPos(x.getAsInt(), y.getAsInt(), width.getAsInt(), height.getAsInt());
    }

    @Override
    public Vector3f getScale() {
        return PositionConverter.pixelScaleToWorldScale(width.getAsInt(), height.getAsInt());
    }

    public int getX() {
        return x.getAsInt();
    }

    public int getY() {
        return y.getAsInt();
    }

    public int getWidth() {
        return width.getAsInt();
    }

    public int getHeight() {
        return height.getAsInt();
    }
}
