package dev.xernas.hydrogen.utils.ui;

import dev.xernas.photon.api.DrawMode;
import dev.xernas.photon.api.model.Model;

public class UIUtils {

    public static Model createUIModel(int radius, int width, int height, int resolution) {
        resolution = Math.max(2, resolution);
        float rx = Math.min((float) radius / width, 0.5f);
        float ry = Math.min((float) radius / height, 0.5f);

        int perimeterCount = 4 * resolution;
        Model.Vertex[] vertices = new Model.Vertex[1 + perimeterCount];

        vertices[0] = new Model.Vertex(0f, 0f, 0f, 0.5f, 0.5f, 0f, 0f, 1f);

        float[] arcCentersX = {  0.5f - rx, -0.5f + rx, -0.5f + rx,  0.5f - rx };
        float[] arcCentersY = {  0.5f - ry,  0.5f - ry, -0.5f + ry, -0.5f + ry };
        float[] startAngles = { 0f, (float)Math.PI/2, (float)Math.PI, 3*(float)Math.PI/2 };

        int vi = 1;
        for (int corner = 0; corner < 4; corner++) {
            float cx = arcCentersX[corner];
            float cy = arcCentersY[corner];
            float startAngle = startAngles[corner];
            for (int i = 0; i < resolution; i++) {
                float angle = startAngle + ((float) i / (resolution - 1)) * (float)(Math.PI / 2);
                float x = cx + rx * (float) Math.cos(angle);
                float y = cy + ry * (float) Math.sin(angle);
                vertices[vi++] = new Model.Vertex(x, y, 0f, x + 0.5f, y + 0.5f, 0f, 0f, 1f);
            }
        }

        int[] indices = new int[perimeterCount + 2];
        indices[0] = 0;
        for (int i = 0; i < perimeterCount; i++) indices[i + 1] = i + 1;
        indices[perimeterCount + 1] = 1;

        String modelTag = "UIModel-" + radius + "-" + resolution;
        if (radius != 0) modelTag += "-" + width + "-" + height;
        return new Model(modelTag, vertices, indices, new Model.ModelSettings(DrawMode.TRIANGLE_FAN, false, true));
    }

}
