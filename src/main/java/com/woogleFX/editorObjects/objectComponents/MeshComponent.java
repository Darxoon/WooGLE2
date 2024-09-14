package com.woogleFX.editorObjects.objectComponents;

import com.woogleFX.editorObjects.DragSettings;
import com.woogleFX.engine.LevelManager;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Affine;

public abstract class MeshComponent extends ObjectComponent {
    
    public static record Face(double[] xPositions, double[] yPositions, int vertexCount) {}
    
    public abstract Face[] getMesh();

    public abstract Image getImage();

    public abstract double getX();

    public abstract double getY();

    public abstract double getScaleX();

    public abstract double getScaleY();

    public abstract double getDepth();

    private Face[] cachedMesh;
    
    @Override
    public void draw(GraphicsContext graphicsContext, boolean selected) {
        if (cachedMesh == null)
            cachedMesh = getMesh();
        
        Image image = getImage();

        double offsetX = LevelManager.getLevel().getOffsetX();
        double offsetY = LevelManager.getLevel().getOffsetY();
        double zoom = LevelManager.getLevel().getZoom();

        graphicsContext.save();

        Affine t = graphicsContext.getTransform();
        t.appendTranslation(offsetX, offsetY);
        t.appendScale(zoom, zoom);
        graphicsContext.setTransform(t);

        graphicsContext.setFill(new ImagePattern(image, 0, 0, image.getWidth() * getScaleX(), image.getHeight() * getScaleY(), false));

        // fill triangles
        for (int i = 0; i < cachedMesh.length; i++) {
            graphicsContext.fillPolygon(cachedMesh[i].xPositions, cachedMesh[i].yPositions, cachedMesh[i].vertexCount);
        }
        
        graphicsContext.restore();

    }

    @Override
    public DragSettings mouseIntersection(double mouseX, double mouseY) {
        return DragSettings.NULL;
    }

    @Override
    public DragSettings mouseIntersectingCorners(double mouseX, double mouseY) {
        return DragSettings.NULL;
    }

}
