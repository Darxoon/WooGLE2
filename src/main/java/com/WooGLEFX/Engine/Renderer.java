package com.WooGLEFX.Engine;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.WooGLEFX.EditorObjects.ObjectDetection.Draw;
import com.WooGLEFX.Engine.FX.FXCanvas;
import com.WooGLEFX.Functions.LevelManager;
import com.WooGLEFX.Structures.EditorObject;
import com.WooGLEFX.Structures.WorldLevel;
import com.WorldOfGoo.Level.BallInstance;
import com.WorldOfGoo.Level.Pipe;
import com.WorldOfGoo.Level.Strand;
import com.WorldOfGoo.Level.Vertex;
import com.WorldOfGoo.Scene.Compositegeom;
import com.WorldOfGoo.Scene.Hinge;
import com.WorldOfGoo.Scene.Linearforcefield;
import com.WorldOfGoo.Scene.Scene;
import com.WorldOfGoo.Scene.Slider;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.transform.Affine;

public class Renderer {

    public static final Paint selectionOutline = Paint.valueOf("000000FF");
    public static final Paint selectionOutline2 = Paint.valueOf("FFFFFFFF");
    public static final Paint mechanics = Paint.valueOf("FFFF00FF");
    public static final Paint PIPE = Paint.valueOf("404040FF");
    public static final Paint PIPE_BEAUTY = Paint.valueOf("FFA6B7FF");
    public static final Paint PIPE_ISH = Paint.valueOf("5FFF5FFF");
    public static final Paint noLevel = Paint.valueOf("A0A0A0FF");
    public static final Paint middleColor = Paint.valueOf("808080FF");
    public static final Paint particleLabels = Paint.valueOf("A81CFF");

    public static Affine t;

    public static final Stop[] stops = new Stop[] { new Stop(0, javafx.scene.paint.Color.valueOf("802000FF")), new Stop(1, Color.valueOf("FFC040FF")) };

    public static double angleTo(Point2D p1, Point2D p2) {
        return Math.atan2(p2.getY() - p1.getY(), p2.getX() - p1.getX());
    }

    public static void addAppropriately(EditorObject obj, ArrayList<EditorObject> list) {
        if (list.size() == 0){
            list.add(obj);
        } else {
            int where = 0;
            while (where < list.size() && list.get(where).getDouble("depth") <= obj.getDouble("depth")){
                where++;
            }
            list.add(where, obj);
        }
    }

    public static ArrayList<EditorObject> orderObjectsByDepth(WorldLevel level){
        ArrayList<EditorObject> sceneLayersByDepth = new ArrayList<>();

        ArrayList<EditorObject> lowDepth = new ArrayList<>();
        ArrayList<EditorObject> strands = new ArrayList<>();
        ArrayList<EditorObject> midLowDepth = new ArrayList<>();
        ArrayList<EditorObject> pipe = new ArrayList<>();
        ArrayList<EditorObject> midHighDepth = new ArrayList<>();
        ArrayList<EditorObject> balls = new ArrayList<>();
        ArrayList<EditorObject> highDepth = new ArrayList<>();
        ArrayList<EditorObject> compositeGeom = new ArrayList<>();
        ArrayList<EditorObject> geom = new ArrayList<>();
        ArrayList<EditorObject> geomConstraints = new ArrayList<>();
        ArrayList<EditorObject> scene = new ArrayList<>();

        for (EditorObject object : level.getScene()) {
            if (object.attributeExists("depth") && !(object instanceof Linearforcefield)) {
                double depth = object.getDouble("depth");
                if (depth < -0.001) {
                    addAppropriately(object, lowDepth);
                } else if (depth < 0) {
                    addAppropriately(object, midLowDepth);
                } else if (depth < 0.002) {
                    addAppropriately(object, midHighDepth);
                } else {
                    addAppropriately(object, highDepth);
                }
            } else if (object instanceof Scene) {
                scene.add(object);
            } else if (object instanceof Hinge || object instanceof Slider) {
                geomConstraints.add(object);
            } else if (object instanceof Compositegeom) {
                compositeGeom.add(object);
            } else {
                geom.add(object);
            }
        }

        for (EditorObject object : level.getLevel()) {
            if (object.attributeExists("depth")) {
                double depth = object.getDouble("depth");
                if (depth < -0.001) {
                    addAppropriately(object, lowDepth);
                } else if (depth < 0) {
                    addAppropriately(object, midLowDepth);
                } else if (depth < 0.002) {
                    addAppropriately(object, midHighDepth);
                } else {
                    addAppropriately(object, highDepth);
                }
            } else if (object instanceof BallInstance) {
                balls.add(object);
            } else if (object instanceof Strand) {
                strands.add(object);
            } else {
                geom.add(object);
            }
        }

        sceneLayersByDepth.addAll(lowDepth);
        sceneLayersByDepth.addAll(strands);
        sceneLayersByDepth.addAll(midLowDepth);
        sceneLayersByDepth.addAll(pipe);
        sceneLayersByDepth.addAll(midHighDepth);
        sceneLayersByDepth.addAll(balls);
        sceneLayersByDepth.addAll(highDepth);
        sceneLayersByDepth.addAll(geom);
        sceneLayersByDepth.addAll(compositeGeom);
        sceneLayersByDepth.addAll(geomConstraints);
        sceneLayersByDepth.addAll(scene);

        return sceneLayersByDepth;

    }

    public static ArrayList<EditorObject> orderObjectsBySelectionDepth(WorldLevel level){
        ArrayList<EditorObject> sceneLayersByDepth = new ArrayList<>();

        ArrayList<EditorObject> lowDepth = new ArrayList<>();
        ArrayList<EditorObject> midLowDepth = new ArrayList<>();
        ArrayList<EditorObject> pipe = new ArrayList<>();
        ArrayList<EditorObject> midHighDepth = new ArrayList<>();
        ArrayList<EditorObject> highDepth = new ArrayList<>();
        ArrayList<EditorObject> geomConstraints = new ArrayList<>();
        ArrayList<EditorObject> geom = new ArrayList<>();
        ArrayList<EditorObject> compositeGeom = new ArrayList<>();
        ArrayList<EditorObject> scene = new ArrayList<>();
        ArrayList<EditorObject> strands = new ArrayList<>();
        ArrayList<EditorObject> balls = new ArrayList<>();

        for (EditorObject object : level.getScene()) {
            if (object.attributeExists("depth") && !(object instanceof Linearforcefield)) {
                double depth = object.getDouble("depth");
                if (depth < -0.001) {
                    addAppropriately(object, lowDepth);
                } else if (depth < 0) {
                    addAppropriately(object, midLowDepth);
                } else if (depth < 0.002) {
                    addAppropriately(object, midHighDepth);
                } else {
                    addAppropriately(object, highDepth);
                }
            } else if (object instanceof Scene) {
                scene.add(object);
            } else if (object instanceof Hinge || object instanceof Slider) {
                geomConstraints.add(object);
            } else if (object instanceof Compositegeom) {
                compositeGeom.add(object);
            } else {
                geom.add(object);
            }
        }

        for (EditorObject object : level.getLevel()) {
            if (object.attributeExists("depth")) {
                double depth = object.getDouble("depth");
                if (depth < -0.001) {
                    addAppropriately(object, lowDepth);
                } else if (depth < 0) {
                    addAppropriately(object, midLowDepth);
                } else if (depth < 0.002) {
                    addAppropriately(object, midHighDepth);
                } else {
                    addAppropriately(object, highDepth);
                }
            } else if (object instanceof BallInstance) {
                balls.add(object);
            } else if (object instanceof Strand) {
                strands.add(object);
            } else {
                geom.add(object);
            }
        }

        sceneLayersByDepth.addAll(lowDepth);
        sceneLayersByDepth.addAll(midLowDepth);
        sceneLayersByDepth.addAll(pipe);
        sceneLayersByDepth.addAll(midHighDepth);
        sceneLayersByDepth.addAll(highDepth);
        sceneLayersByDepth.addAll(geom);
        sceneLayersByDepth.addAll(compositeGeom);
        sceneLayersByDepth.addAll(scene);
        sceneLayersByDepth.addAll(geomConstraints);
        sceneLayersByDepth.addAll(strands);
        sceneLayersByDepth.addAll(balls);

        return sceneLayersByDepth;

    }

    public static void drawEverything(WorldLevel level, Canvas canvas, Canvas imageCanvas) {
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        GraphicsContext imageGraphicsContext = imageCanvas.getGraphicsContext2D();

        canvas.resize(canvas.getWidth(), canvas.getHeight());
        canvas.autosize();
        imageCanvas.resize(canvas.getWidth(), canvas.getHeight());
        imageCanvas.autosize();

        //Define bounds of where content is displayed on the canvas
        int left = 0; //(int)(-Main.offsetX / Main.zoom);
        int top = 0; //(int)(-Main.offsetY / Main.zoom);
        int right = 1920 - 520; //left + (int)((1920 - 520) / Main.zoom);
        int bottom = 1080; //top + (int)((1080) / Main.zoom);

        BufferedImage toWriteOn = new BufferedImage(right - left, bottom - top, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = toWriteOn.createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));

        //Loop through and display every object in an arbitrary order
        graphicsContext.save();
        EditorObject previousVertex = null;
        Paint previousPipeColor = null;
        for (EditorObject object : level.getLevel()) {
            if (object instanceof Pipe && previousPipeColor == null) {
                String pipeColor = object.getAttribute("type");
                previousPipeColor = switch (pipeColor) {
                    case "BEAUTY" -> PIPE_BEAUTY;
                    case "ISH" -> PIPE_ISH;
                    default -> PIPE;
                };
            }
            if (object instanceof Vertex && LevelManager.getLevel().isShowGeometry()) {
                if (previousVertex != null) {
                    ((Vertex) object).drawTo(graphicsContext, (Vertex) previousVertex, previousPipeColor);
                }
                previousVertex = object;
            }
        }
        graphicsContext.restore();

        for (EditorObject object : orderObjectsByDepth(level)) {
            graphicsContext.save();
            imageGraphicsContext.save();
            Draw.draw(graphicsContext, imageGraphicsContext, object);
            graphicsContext.restore();
            imageGraphicsContext.restore();
        }

        if (SelectionManager.getMode() == SelectionManager.STRAND) {
            graphicsContext.save();

            if (SelectionManager.getStrand1Gooball() != null) {

                double x = SelectionManager.getStrand1Gooball().getDouble("x") * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetX();
                double y = -SelectionManager.getStrand1Gooball().getDouble("y") * LevelManager.getLevel().getZoom() + LevelManager.getLevel().getOffsetY();

                graphicsContext.setLineWidth(3 * LevelManager.getLevel().getZoom());

                graphicsContext.strokeLine(x, y, SelectionManager.getMouseX(), SelectionManager.getMouseY());

            }

            graphicsContext.restore();
        }

    }

    public static void clear(WorldLevel level, Canvas canvas, Canvas imageCanvas) {
        canvas.getGraphicsContext2D().setFill(noLevel);
        imageCanvas.getGraphicsContext2D().setFill(noLevel);
        canvas.getGraphicsContext2D().fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        imageCanvas.getGraphicsContext2D().fillRect(0, 0, imageCanvas.getWidth(), imageCanvas.getHeight());
    }


    public static void draw() {

        WorldLevel level = LevelManager.getLevel();
        Canvas canvas = FXCanvas.getCanvas();
        Canvas imageCanvas = FXCanvas.getImageCanvas();

        if (level != null) {
            if (level.isShowSceneBGColor()) {
                imageCanvas.getGraphicsContext2D()
                        .setFill(Paint.valueOf(level.getSceneObject().getColor("backgroundcolor").toHexRGBA()));
                imageCanvas.getGraphicsContext2D().fillRect(-5000000, -5000000, 10000000, 10000000);
            } else {
                imageCanvas.getGraphicsContext2D().clearRect(-5000000, -5000000, 10000000, 10000000);
            }
            canvas.getGraphicsContext2D().clearRect(-5000000, -5000000, 10000000, 10000000);
            Renderer.drawEverything(level, canvas, imageCanvas);
        } else {
            Renderer.clear(null, canvas, imageCanvas);
        }
    }

}
