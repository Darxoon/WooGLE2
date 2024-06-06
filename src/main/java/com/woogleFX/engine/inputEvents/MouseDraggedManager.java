package com.woogleFX.engine.inputEvents;

import com.woogleFX.editorObjects.objectFunctions.ObjectDrag;
import com.woogleFX.editorObjects.objectFunctions.ObjectResize;
import com.woogleFX.editorObjects.objectFunctions.ObjectRotate;
import com.woogleFX.editorObjects.objectFunctions.ObjectSetAnchor;
import com.woogleFX.editorObjects.splineGeom.SplineManager;
import com.woogleFX.engine.fx.FXCanvas;
import com.woogleFX.engine.fx.FXPropertiesView;
import com.woogleFX.engine.renderer.Renderer;
import com.woogleFX.engine.SelectionManager;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.editorObjects.DragSettings;
import com.woogleFX.gameData.level._Level;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Affine;

import java.awt.geom.QuadCurve2D;

public class MouseDraggedManager {

    /** Called whenever the mouse is dragged.*/
    public static void eventMouseDragged(MouseEvent event) {

        _Level level = LevelManager.getLevel();
        if (level == null) return;

        SelectionManager.setMouseX(event.getX());
        SelectionManager.setMouseY(event.getY() - FXCanvas.getMouseYOffset());

        // Calculate game-relative mouse coordinates.
        double gameRelativeMouseX = (SelectionManager.getMouseX() - level.getOffsetX()) / level.getZoom();
        double gameRelativeMouseY = (SelectionManager.getMouseY() - level.getOffsetY()) / level.getZoom();

        if (SplineManager.getSelectedPoint() != -1) {
            QuadCurve2D selectedCurve = SplineManager.getSelectedCurve();
            int selectedPoint = SplineManager.getSelectedPoint();
            if (selectedCurve != null) {
                switch (selectedPoint) {
                    case 0 -> selectedCurve.setCurve(gameRelativeMouseX + SplineManager.getOffsetX(), gameRelativeMouseY + SplineManager.getOffsetY(), selectedCurve.getCtrlX(), selectedCurve.getCtrlY(), selectedCurve.getX2(), selectedCurve.getY2());
                    case 1 -> selectedCurve.setCurve(selectedCurve.getX1(), selectedCurve.getY1(), gameRelativeMouseX + SplineManager.getOffsetX(), gameRelativeMouseY + SplineManager.getOffsetY(), selectedCurve.getX2(), selectedCurve.getY2());
                    case 2 -> selectedCurve.setCurve(selectedCurve.getX1(), selectedCurve.getY1(), selectedCurve.getCtrlX(), selectedCurve.getCtrlY(), gameRelativeMouseX + SplineManager.getOffsetX(), gameRelativeMouseY + SplineManager.getOffsetY());
                };
            } else {
                if (selectedPoint == 0) SplineManager.setSplineInitialPoint(new Point2D(gameRelativeMouseX + SplineManager.getOffsetX(), gameRelativeMouseY + SplineManager.getOffsetY()));
                else if (selectedPoint == 1) SplineManager.setSplineControlPoint(new Point2D(gameRelativeMouseX + SplineManager.getOffsetX(), gameRelativeMouseY + SplineManager.getOffsetY()));
            }

        }

        if (level.getSelected().length != 0 && SelectionManager.getDragSettings() != null) {

            // Update the selected object according to what kind of operation is being
            // performed.
            switch (SelectionManager.getDragSettings().getType()) {
                case DragSettings.MOVE -> ObjectDrag.dragFromMouse(gameRelativeMouseX, gameRelativeMouseY, SelectionManager.getDragSettings().getInitialSourceX(), SelectionManager.getDragSettings().getInitialSourceY());
                case DragSettings.RESIZE -> ObjectResize.resizeFromMouse(gameRelativeMouseX, gameRelativeMouseY, SelectionManager.getDragSettings().getInitialSourceX(), SelectionManager.getDragSettings().getInitialSourceY(), SelectionManager.getDragSettings().getAnchorX(), SelectionManager.getDragSettings().getAnchorY(), SelectionManager.getDragSettings().getInitialScaleX(), SelectionManager.getDragSettings().getInitialScaleY());
                case DragSettings.ROTATE -> ObjectRotate.rotateFromMouse(gameRelativeMouseX, gameRelativeMouseY, SelectionManager.getDragSettings().getInitialSourceX(), SelectionManager.getDragSettings().getInitialSourceY(), SelectionManager.getDragSettings().getRotateAngleOffset());
                case DragSettings.SETANCHOR -> ObjectSetAnchor.setAnchor(gameRelativeMouseX, gameRelativeMouseY, SelectionManager.getDragSettings().getInitialSourceX(), SelectionManager.getDragSettings().getInitialSourceY());
            }

            FXPropertiesView.getPropertiesView().refresh();
        }

        if (event.getButton() == MouseButton.SECONDARY) {
            // Pan the canvas according to the mouse's movement.
            level.setOffsetX(level.getOffsetX() + event.getScreenX() - SelectionManager.getMouseStartX());
            level.setOffsetY(level.getOffsetY() + event.getScreenY() - SelectionManager.getMouseStartY());
            SelectionManager.setMouseStartX(event.getScreenX());
            SelectionManager.setMouseStartY(event.getScreenY());

            // Apply the transformation to the canvas.
            Renderer.t = new Affine();
            Renderer.t.appendTranslation(level.getOffsetX(), level.getOffsetY());
            Renderer.t.appendScale(level.getZoom(), level.getZoom());

            // Redraw the canvas.
            Renderer.drawLevelToCanvas(level, FXCanvas.getCanvas());
        }

    }

}
