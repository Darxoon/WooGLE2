package com.WooGLEFX.EditorObjects.ObjectDetection;

import com.WooGLEFX.EditorObjects.Components.ObjectPosition;
import com.WooGLEFX.EditorObjects.ObjectCollision.AnchorCollider;
import com.WooGLEFX.EditorObjects.ObjectCollision.CircleCollider;
import com.WooGLEFX.EditorObjects.ObjectCollision.ImageCollider;
import com.WooGLEFX.EditorObjects.ObjectCollision.RectangleCollider;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.SimpleStructures.DragSettings;
import javafx.scene.image.Image;

public class MouseIntersectingCorners {

    public static DragSettings mouseIntersectingCorners(EditorObject editorObject, double mouseX, double mouseY) {

        if (!editorObject.isVisible()) return DragSettings.NULL;

        for (ObjectPosition objectPosition : editorObject.getObjectPositions()) {

            DragSettings dragSettings = forPosition(objectPosition, mouseX, mouseY, editorObject.getImage());
            if (dragSettings != DragSettings.NULL) return dragSettings;

        }

        return DragSettings.NULL;

    }


    public static DragSettings forPosition(ObjectPosition objectPosition, double mouseX, double mouseY, Image image) {

        switch (objectPosition.getId()) {

            case ObjectPosition.POINT, ObjectPosition.LINE -> {
                return DragSettings.NULL;
            }
            case ObjectPosition.RECTANGLE, ObjectPosition.RECTANGLE_HOLLOW -> {
                boolean canRotate = (objectPosition.getId() == ObjectPosition.RECTANGLE);
                return RectangleCollider.mouseIntersectingCorners(objectPosition, mouseX, mouseY, canRotate);
            }
            case ObjectPosition.CIRCLE, ObjectPosition.CIRCLE_HOLLOW -> {
                return CircleCollider.mouseIntersectingCorners(objectPosition, mouseX, mouseY);
            }
            case ObjectPosition.ANCHOR -> {
                return AnchorCollider.mouseIntersectingCorners(objectPosition, mouseX, mouseY);
            }
            case ObjectPosition.IMAGE -> {
                if (image == null) return DragSettings.NULL;
                return ImageCollider.mouseIntersectingCorners(objectPosition, mouseX, mouseY);
            }

        }

        throw new RuntimeException("Invalid object position id: " + objectPosition.getId());

    }

}
