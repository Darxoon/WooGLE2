package com.woogleFX.gameData.level;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.engine.fx.FXContainers;
import com.worldOfGoo2.level._2_Level;

import java.util.ArrayList;

/** A level from World of Goo 2.
 * What... is this? */
public class WOG2Level extends _Level {

    private final EditorObject level;
    public EditorObject getLevel() {
        return level;
    }


    private final ArrayList<EditorObject> objects;
    public ArrayList<EditorObject> getObjects() {
        return objects;
    }

    public WOG2Level(ArrayList<EditorObject> objects, ArrayList<EditorObject> addin) {
        super(GameVersion.VERSION_WOG2, addin);

        this.objects = objects;

        this.level = objects.get(0);

        // setCurrentlySelectedSection("Terrain");

        LevelManager.setLevel(this);

        // for (EditorObject ball : getLevel().getChildren("balls")) {
        //     EditorObject terrainBall = getLevel().getChildren("terrainBalls").remove(0);
        //     getObjects().remove(terrainBall);
        //     getLevel().getChildren().remove(terrainBall);
        //     ball.setAttribute("terrainGroup", terrainBall.getAttribute("group").stringValue());
        // }

        // resetCamera();

    }


    @Override
    public void resetCamera() {
        EditorObject boundsBottomLeft = level.getChildren("boundsBottomLeft").get(0);
        EditorObject boundsTopRight = level.getChildren("boundsTopRight").get(0);

        double sceneWidth = boundsTopRight.getAttribute("x").doubleValue() - boundsBottomLeft.getAttribute("x").doubleValue();
        double sceneHeight = boundsTopRight.getAttribute("y").doubleValue() - boundsBottomLeft.getAttribute("y").doubleValue();
        double middleX = -(boundsBottomLeft.getAttribute("x").doubleValue() + boundsTopRight.getAttribute("x").doubleValue()) / 2;
        double middleY = (boundsBottomLeft.getAttribute("y").doubleValue() + boundsTopRight.getAttribute("y").doubleValue()) / 2;

        double canvasWidth = FXContainers.getSplitPane().getDividers().get(0).getPosition() * FXContainers.getSplitPane().getWidth();
        double canvasHeight = FXContainers.getSplitPane().getHeight();

        setOffsetX(canvasWidth / 2 - middleX);
        setOffsetY(canvasHeight / 2 + middleY);

        double zoomX = canvasWidth / sceneWidth;
        double zoomY = canvasHeight / sceneHeight;

        setZoom(Math.min(Math.abs(zoomX), Math.abs(zoomY)));

        setOffsetX((getOffsetX() - canvasWidth / 2) * getZoom() + canvasWidth / 2);
        setOffsetY((getOffsetY() - canvasHeight / 2) * getZoom() + canvasHeight / 2);
    }

}
