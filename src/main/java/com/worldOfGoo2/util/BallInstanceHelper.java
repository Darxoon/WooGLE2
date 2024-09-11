package com.worldOfGoo2.util;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.AttributeAdapter;
import com.woogleFX.editorObjects.attributes.EditorAttribute;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.editorObjects.objectComponents.CircleComponent;
import com.woogleFX.editorObjects.objectComponents.ImageComponent;
import com.woogleFX.editorObjects.objectComponents.ObjectComponent;
import com.woogleFX.editorObjects.objectComponents.RectangleComponent;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.engine.fx.FXEditorButtons;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.woogleFX.gameData.ball.AtlasManager;
import com.woogleFX.gameData.ball._2Ball;
import com.woogleFX.gameData.level.GameVersion;
import com.woogleFX.gameData.level.WOG2Level;
import com.worldOfGoo2.ball._2_Ball_Image;
import com.worldOfGoo2.ball._2_Ball_Part;
import com.worldOfGoo2.level._2_Level_BallInstance;
import com.worldOfGoo2.level._2_Level_Strand;
import com.worldOfGoo2.misc._2_ImageID;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BallInstanceHelper {

    public static final Map<Integer, String> vanillaTypeEnumToTypeMap = new HashMap<>();
    
    private static Map<Integer, String> typeEnumToTypeMap;
    private static Map<String, Integer> typeToTypeEnumMap;

    static {

        vanillaTypeEnumToTypeMap.put(1, "Common");
        vanillaTypeEnumToTypeMap.put(2, "CommonAlbino");
        vanillaTypeEnumToTypeMap.put(3, "Ivy");
        vanillaTypeEnumToTypeMap.put(4, "Balloon");
        vanillaTypeEnumToTypeMap.put(5, "GoolfSingle");
        vanillaTypeEnumToTypeMap.put(6, "Anchor");
        vanillaTypeEnumToTypeMap.put(7, "LauncherL2B");
        vanillaTypeEnumToTypeMap.put(8, "GooProduct");
        vanillaTypeEnumToTypeMap.put(9, "Thruster");
        vanillaTypeEnumToTypeMap.put(10, "Terrain");
        vanillaTypeEnumToTypeMap.put(11, "BalloonEye");
        vanillaTypeEnumToTypeMap.put(12, "Conduit");
        vanillaTypeEnumToTypeMap.put(13, "LauncherL2L");
        vanillaTypeEnumToTypeMap.put(14, "GooProductWhite");
        vanillaTypeEnumToTypeMap.put(15, "Grow");
        vanillaTypeEnumToTypeMap.put(16, "BombSticky");
        vanillaTypeEnumToTypeMap.put(17, "Rope");
        vanillaTypeEnumToTypeMap.put(18, "Bouncy");
        vanillaTypeEnumToTypeMap.put(19, "Fish");
        vanillaTypeEnumToTypeMap.put(20, "TimeBug");
        vanillaTypeEnumToTypeMap.put(23, "MatchStick");
        vanillaTypeEnumToTypeMap.put(25, "Fireworks");
        vanillaTypeEnumToTypeMap.put(26, "Lightball");
        vanillaTypeEnumToTypeMap.put(27, "TwuBit");
        vanillaTypeEnumToTypeMap.put(28, "TwuBitBit");
        vanillaTypeEnumToTypeMap.put(29, "Adapter");
        vanillaTypeEnumToTypeMap.put(30, "Winch");
        vanillaTypeEnumToTypeMap.put(32, "Shrink");
        vanillaTypeEnumToTypeMap.put(33, "Jelly");
        vanillaTypeEnumToTypeMap.put(34, "Goolf");
        vanillaTypeEnumToTypeMap.put(35, "ThisWayUp");
        vanillaTypeEnumToTypeMap.put(36, "LiquidLevelExit");
        vanillaTypeEnumToTypeMap.put(37, "Eye");
        vanillaTypeEnumToTypeMap.put(38, "UtilAttachWalkable");

        // CommonBlack LightBall CommonWorld

        // by default uses vanilla ball table
        // if GlobalResourceManager finds the FistyLoader
        // ballTable.ini, it will change it from there
        setTypeEnumMaps(vanillaTypeEnumToTypeMap);
    }

    
    public static AttributeAdapter ballTypeAttributeAdapter(EditorObject object, String displayName, String realName) {
        return new AttributeAdapter(displayName) {
            private final EditorAttribute typeAttribute = new EditorAttribute(displayName,
                    InputField._2_BALL_TYPE, object);

            @Override
            public EditorAttribute getValue() {
                if (object.getAttribute2(realName).stringValue().isEmpty()) return typeAttribute;
                
                typeAttribute.setValue(BallInstanceHelper.typeEnumToTypeMap.getOrDefault(
                        object.getAttribute2(realName).intValue(), ""));
                
                return typeAttribute;
            }

            @Override
            public void setValue(String value) {
                Integer typeEnum = BallInstanceHelper.typeToTypeEnumMap.get(value);
                
                if (typeEnum != null) {
                    typeAttribute.setValue(value);
                } else {
                    try {
                        typeEnum = Integer.parseInt(value);
                        typeAttribute.setValue(value);
                    } catch (NumberFormatException ignored) {
                        typeEnum = 0;
                    }
                };
                
                object.setAttribute2(realName, typeEnum);
            }

        };
    }
    

    private static boolean part2CanBeUsed(_2_Level_BallInstance ballInstance, _2_Ball_Part part) {

        String state = "0";

        if (!ballInstance.getAttribute("discovered").booleanValue()) {
            if (!part.getAttribute("isActiveWhenUndiscovered").booleanValue()) return false;
        } else {
            for (EditorObject obj : ((WOG2Level) LevelManager.getLevel()).getObjects()) {
                if (obj instanceof _2_Level_Strand strand) {

                    String id = ballInstance.getAttribute("uid").stringValue();
                    String gb1 = strand.getAttribute("ball1UID").stringValue();
                    String gb2 = strand.getAttribute("ball2UID").stringValue();

                    if (id.equals(gb1) || id.equals(gb2)) {
                        state = "4";
                        break;
                    }

                }
            }
        }

        ArrayList<EditorObject> states = part.getChildren("states");
        if (states.isEmpty()) return true;
        else for (EditorObject stateObject : states) {
            if (stateObject.getAttribute("ballState").stringValue().equals(state)) return true;
        }
        return false;

    }


    private static BufferedImage getPartImageWoG2(_2Ball ball, _2_Ball_Part part, Random machine) {

        ArrayList<_2_Ball_Image> images = new ArrayList<>();
        for (EditorObject editorObject : part.getChildren()) if (editorObject instanceof _2_Ball_Image ball_image) images.add(ball_image);
        if (images.size() == 0) return null;

        String imageString = images.get((int)(images.size() * machine.nextDouble())).getChildren().get(0).getAttribute("imageId").stringValue();

        BufferedImage image = AtlasManager.atlas.get(imageString);
        if (image == null) {
            try {
                image = SwingFXUtils.fromFXImage(ResourceManager.getImage(ball.getResources(), imageString, GameVersion.VERSION_WOG2), null);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return image;

    }


    private static BufferedImage getPartPupilImageWoG2(_2_Ball_Part part, Random machine) {

        ArrayList<_2_ImageID> pupilImages = new ArrayList<>();
        for (EditorObject editorObject : part.getChildren()) if (editorObject instanceof _2_ImageID ball_image) pupilImages.add(ball_image);
        if (pupilImages.size() == 0) return null;

        String pupilImageString = pupilImages.get((int)(pupilImages.size() * machine.nextDouble())).getAttribute("imageId").stringValue();

        return AtlasManager.atlas.get(pupilImageString);

    }


    public static Image createBallImageWoG2(_2_Level_BallInstance ballInstance, _2Ball ball, double _scaleX, double _scaleY, Random machine) {

        ArrayList<_2_Ball_Part> parts = new ArrayList<>();
        for (EditorObject child : ball.getObjects()) if (child instanceof _2_Ball_Part part) parts.add(part);
        parts.sort((o1, o2) -> (int)Math.signum(
                o1.getAttribute("layer").doubleValue() - o2.getAttribute("layer").doubleValue()));
        if (parts.isEmpty()) return null;

        // Create image bounds
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;

        double bodyMinX = Double.POSITIVE_INFINITY;
        double bodyMinY = Double.POSITIVE_INFINITY;
        double bodyMaxX = Double.NEGATIVE_INFINITY;
        double bodyMaxY = Double.NEGATIVE_INFINITY;

        String bodyPartID = ball.getObjects().get(0).getChildren("bodyPart")
                .get(0).getAttribute("partName").stringValue();

        record PartPosition(double x, double y, double scaleX, double scaleY, BufferedImage image) {

        }

        ArrayList<PartPosition> partPositions = new ArrayList<>();

        boolean thereWasABody = false;
        for (_2_Ball_Part part : parts) {

            double sizeVariance = ball.getObjects().get(0).getAttribute("sizeVariance").doubleValue();

            Random machine2 = new Random(1);
            machine2.nextDouble();
            double random = machine2.nextDouble();

            boolean relative = part.getAttribute("scaleIsRelative").booleanValue();

            double scaleX = part.getAttribute("scale").doubleValue() * (relative ? 1 : 1 / _scaleX) * (1 + (random * 2 - 1) * sizeVariance);
            double scaleY = part.getAttribute("scale").doubleValue() * (relative ? 1 : 1 / _scaleY) * (1 + (random * 2 - 1) * sizeVariance);

            double partMinX = part.getAttribute("minX").doubleValue() / _scaleX;
            double partMinY = -part.getAttribute("minY").doubleValue() / _scaleY;
            double partMaxX = part.getAttribute("maxX").doubleValue() / _scaleX;
            double partMaxY = -part.getAttribute("maxY").doubleValue() / _scaleY;

            double partX = partMinX + (partMaxX - partMinX) * machine.nextDouble();
            double partY = partMinY + (partMaxY - partMinY) * machine.nextDouble();

            BufferedImage partImage = getPartImageWoG2(ball, part, machine);
            if (partImage == null) continue;
            if ((ballInstance == null || part2CanBeUsed(ballInstance, part)))
                partPositions.add(new PartPosition(partX, partY, scaleX, scaleY, partImage));

            BufferedImage pupilImage = getPartPupilImageWoG2(part, machine);
            if (pupilImage != null && (ballInstance == null || part2CanBeUsed(ballInstance, part)))
                partPositions.add(new PartPosition(partX, partY, scaleX, scaleY, pupilImage));

            double partImageMinX = partX - partImage.getWidth() * scaleX / 2.0;
            double partImageMinY = partY - partImage.getHeight() * scaleY / 2.0;
            double partImageMaxX = partX + partImage.getWidth() * scaleX / 2.0;
            double partImageMaxY = partY + partImage.getHeight() * scaleY / 2.0;

            if (partImageMinX < minX) minX = partImageMinX;
            if (partImageMinY < minY) minY = partImageMinY;
            if (partImageMaxX > maxX) maxX = partImageMaxX;
            if (partImageMaxY > maxY) maxY = partImageMaxY;

            if (part.getAttribute("name").stringValue().equals(bodyPartID)) {
                thereWasABody = true;
                bodyMinX = partImageMinX;
                bodyMinY = partImageMinY;
                bodyMaxX = partImageMaxX;
                bodyMaxY = partImageMaxY;
            }

        }

        if (!thereWasABody) {
            bodyMinX = 0;
            bodyMinY = 0;
            bodyMaxX = 1;
            bodyMaxY = 1;
        }

        double paddingLeft = bodyMinX - minX;
        double paddingRight = maxX - bodyMaxX;
        paddingLeft = Math.max(paddingLeft, paddingRight);
        paddingRight = paddingLeft;
        minX = bodyMinX - paddingLeft;
        maxX = bodyMaxX + paddingRight;

        double paddingTop = bodyMinY - minY;
        double paddingBottom = maxY - bodyMaxY;
        paddingTop = Math.max(paddingTop, paddingBottom);
        paddingBottom = paddingTop;
        minY = bodyMinY - paddingTop;
        maxY = bodyMaxY + paddingBottom;

        if (maxX - minX <= -100000000) return null;

        BufferedImage image = new BufferedImage((int)(maxX - minX), (int)(maxY - minY), BufferedImage.TYPE_INT_ARGB);

        Graphics drawGraphics = image.createGraphics();

        for (PartPosition partPosition : partPositions) {

            BufferedImage partImage = partPosition.image;

            int imageX = (int)(partPosition.x - partImage.getWidth() * partPosition.scaleX / 2 - minX);
            int imageY = (int)(partPosition.y - partImage.getHeight() * partPosition.scaleY / 2 - minY);
            int imageWidth = (int)(partImage.getWidth() * partPosition.scaleX);
            int imageHeight = (int)(partImage.getHeight() * partPosition.scaleY);
            drawGraphics.drawImage(partImage, imageX, imageY, imageWidth, imageHeight, null);

        }

        drawGraphics.dispose();

        return SwingFXUtils.toFXImage(image, null);

    }


    public static ArrayList<ObjectComponent> generateBallObjectComponents(_2_Level_BallInstance ballInstance) {

        _2Ball ball = ballInstance.getBall();

        ArrayList<ObjectComponent> objectComponents = new ArrayList<>();

        if (ball != null) {

            ArrayList<_2_Ball_Image> images = new ArrayList<>();
            for (EditorObject editorObject : ball.getObjects()) if (editorObject instanceof _2_Ball_Part && editorObject.getAttribute("name").stringValue().equals(ball.getObjects().get(0).getChildren("bodyPart").get(0).getAttribute("partName").stringValue())) for (EditorObject child : editorObject.getChildren())
                if (child instanceof _2_Ball_Image ball_image) images.add(ball_image);

            double _scaleX = 1;
            double _scaleY = 1;
            if (!images.isEmpty()) {

                String imageString = images.get(0).getChildren().get(0).getAttribute("imageId").stringValue();

                BufferedImage image = AtlasManager.atlas.get(imageString);
                if (image == null) {
                    try {
                        image = SwingFXUtils.fromFXImage(ResourceManager.getImage(ball.getResources(), imageString, GameVersion.VERSION_WOG2), null);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (image == null) return objectComponents;

                int _width = image.getWidth();
                int _height = image.getHeight();

                double width = ball.getObjects().get(0).getAttribute("width").doubleValue();
                double height = ball.getObjects().get(0).getAttribute("height").doubleValue();

                _scaleX = width / _width;
                _scaleY = height / _height;

            }

            Image image = createBallImageWoG2(ballInstance, ball, _scaleX, _scaleY, new Random(ballInstance.getRandomSeed()));

            EditorObject pos = ballInstance.getChildren("pos").get(0);

            double final_scaleX = _scaleX;
            double final_scaleY = _scaleY;
            objectComponents.add(new ImageComponent() {
                public double getX() {
                    return pos.getAttribute("x").doubleValue();
                }
                public void setX(double x) {
                    pos.setAttribute("x", x);
                }
                public double getY() {
                    return -pos.getAttribute("y").doubleValue();
                }
                public void setY(double y) {
                    pos.setAttribute("y", -y);
                }
                public double getRotation() {
                    return -ballInstance.getAttribute("angle").doubleValue();
                }
                public void setRotation(double rotation) {
                    ballInstance.setAttribute("angle", -rotation);
                }
                public double getScaleX() {
                    return final_scaleX;
                }
                public double getScaleY() {
                    return final_scaleY;
                }
                public double getDepth() {
                    return 0.000001;
                }
                public Image getImage() {
                    return image;
                }
                public boolean isVisible() {
                    return !ballInstance.getAttribute("type").stringValue().equals("Terrain") && LevelManager.getLevel().getVisibilitySettings().getShowGoos() == 2;
                }
                public boolean isResizable() {
                    return false;
                }
            });

        }
        boolean isCircle = ball == null || !ball.getObjects().get(0).getChildren("shape").get(0).getAttribute("ballShape").stringValue().equals("1");

        EditorObject pos = ballInstance.getChildren("pos").get(0);

        if (isCircle) {

            if (ballInstance.getAttribute("type").stringValue().equals("Terrain")) {

                objectComponents.add(new CircleComponent() {
                    public double getX() {
                        return pos.getAttribute("x").doubleValue();
                    }
                    public void setX(double x) {
                        pos.setAttribute("x", x);
                    }
                    public double getY() {
                        return -pos.getAttribute("y").doubleValue();
                    }
                    public double getRotation() {
                        return -ballInstance.getAttribute("angle").doubleValue();
                    }
                    public void setY(double y) {
                        pos.setAttribute("y", -y);
                    }
                    public double getRadius() {
                        return 0.1;
                    }
                    public double getEdgeSize() {
                        return 100;
                    }
                    public boolean isEdgeOnly() {
                        return true;
                    }
                    public javafx.scene.paint.Paint getBorderColor() {
                        return new javafx.scene.paint.Color(0.0, 0.0, 0.0, 1.0);
                    }
                    public javafx.scene.paint.Paint getColor() {
                        return new javafx.scene.paint.Color(0.0, 0.0, 0.0, 0.0);
                    }
                    public double getDepth() {
                        return 0.000001;
                    }
                    public boolean isVisible() {
                        if (LevelManager.getLevel().getVisibilitySettings().getShowGoos() == 0) return false;
                        return (ball == null || ballInstance.getAttribute("type").stringValue().equals("Terrain") && ballInstance.visibilityFunction()) || LevelManager.getLevel().getVisibilitySettings().getShowGoos() == 1 || (ballInstance.getAttribute("type").stringValue().equals("Terrain") && FXEditorButtons.comboBoxSelected == ballInstance.getAttribute("terrainGroup").intValue());
                    }
                    public boolean isRotatable() {
                        return false;
                    }
                    public boolean isResizable() {
                        return false;
                    }
                });

            }
            objectComponents.add(new CircleComponent() {
                public double getX() {
                    return pos.getAttribute("x").doubleValue();
                }
                public void setX(double x) {
                    pos.setAttribute("x", x);
                }
                public double getY() {
                    return -pos.getAttribute("y").doubleValue();
                }
                public void setY(double y) {
                    pos.setAttribute("y", -y);
                }
                public double getRotation() {
                    return -ballInstance.getAttribute("angle").doubleValue();
                }
                public void setRotation(double rotation) {
                    ballInstance.setAttribute("angle", -rotation);
                }
                public double getRadius() {
                    if (ball == null) return 0.2;
                    else if (ballInstance.getAttribute("type").stringValue().equals("Terrain")) return 0.1;
                    return ball.getWidth() / 2;
                }
                public double getEdgeSize() {
                    if (ballInstance.getAttribute("type").stringValue().equals("Terrain")) return 0.025;
                    else return 0.05;
                }
                public boolean isEdgeOnly() {
                    return false;
                }
                public javafx.scene.paint.Paint getBorderColor() {
                    if (ball == null) {
                        return new javafx.scene.paint.Color(0.5, 0.25, 0.25, 1.0);
                    } else {
                        if (ballInstance.getAttribute("type").stringValue().equals("Terrain") && FXEditorButtons.comboBoxSelected == ballInstance.getAttribute("terrainGroup").intValue() && FXEditorButtons.comboBoxSelected != -1) {
                            if (FXEditorButtons.comboBoxList.get(FXEditorButtons.comboBoxSelected)) {
                                return new javafx.scene.paint.Color(1.0 ,0.0, 1.0, 1);
                            } else {
                                return new javafx.scene.paint.Color(0.0 ,1.0, 1.0, 1);
                            }
                        } else {
                            if (ballInstance.getAttribute("type").stringValue().equals("Terrain")) {
                                return new javafx.scene.paint.Color(1.0, 1.0, 1.0, 1);
                            } else {
                                return new javafx.scene.paint.Color(0.5, 0.5, 0.5, 1);
                            }
                        }
                    }
                }
                @Override
                public javafx.scene.paint.Paint getColor() {
                    if (ballInstance.getAttribute("type").stringValue().equals("Terrain")) {
                        return new javafx.scene.paint.Color(1.0, 1.0, 1.0, 1);
                    } else {
                        return new javafx.scene.paint.Color(0, 0, 0, 0);
                    }
                }
                public double getDepth() {
                    return 0.000001;
                }
                public boolean isVisible() {
                    if (LevelManager.getLevel().getVisibilitySettings().getShowGoos() == 0) return false;
                    return (ball == null || ballInstance.getAttribute("type").stringValue().equals("Terrain") && ballInstance.visibilityFunction()) || LevelManager.getLevel().getVisibilitySettings().getShowGoos() == 1 || (ballInstance.getAttribute("type").stringValue().equals("Terrain") && FXEditorButtons.comboBoxSelected == ballInstance.getAttribute("terrainGroup").intValue());
                }
                public boolean isResizable() {
                    return false;
                }
            });

        }

        else objectComponents.add(new RectangleComponent() {
            public double getX() {
                return pos.getAttribute("x").doubleValue();
            }

            public void setX(double x) {
                double y = pos.getAttribute("y").doubleValue();
                pos.setAttribute("x", x + "," + y);
            }

            public double getY() {
                return -pos.getAttribute("y").doubleValue();
            }

            public void setY(double y) {
                pos.setAttribute("y", -y);
            }

            public double getRotation() {
                return -ballInstance.getAttribute("angle").doubleValue();
            }

            public void setRotation(double rotation) {
                ballInstance.setAttribute("angle", -rotation);
            }

            public double getWidth() {
                return ball.getWidth();
            }

            public double getHeight() {
                return ball.getHeight();
            }

            public double getEdgeSize() {
                return 0.05;
            }

            public boolean isEdgeOnly() {
                return !ballInstance.getAttribute("type").stringValue().equals("Terrain");
            }

            public javafx.scene.paint.Paint getBorderColor() {
                if (ball == null) {
                    return new javafx.scene.paint.Color(0.5, 0.25, 0.25, 1.0);
                } else {
                    return new javafx.scene.paint.Color(0.5, 0.5, 0.5, 1);
                }
            }

            public javafx.scene.paint.Paint getColor() {
                if (ballInstance.getAttribute("type").stringValue().equals("Terrain")) {
                    return new javafx.scene.paint.Color(0.5, 0.5, 0.5, 1.0);
                } else {
                    return new javafx.scene.paint.Color(0, 0, 0, 0);
                }
            }

            public double getDepth() {
                return 0.000001;
            }

            public boolean isVisible() {
                return (ball == null || ballInstance.getAttribute("type").stringValue().equals("Terrain") && ballInstance.visibilityFunction()) || LevelManager.getLevel().getVisibilitySettings().getShowGoos() == 1;
            }

            public boolean isResizable() {
                return false;
            }
        });

        return objectComponents;

    }

    public static void setTypeEnumMaps(Map<Integer, String> typeEnumToTypeMap) {
        BallInstanceHelper.typeEnumToTypeMap = typeEnumToTypeMap;
        
        HashMap<String, Integer> typeToTypeEnumMap = new HashMap<>();
        for (int key : typeEnumToTypeMap.keySet()) {
            typeToTypeEnumMap.put(typeEnumToTypeMap.get(key), key);
        }
        
        BallInstanceHelper.typeToTypeEnumMap = typeToTypeEnumMap;
    }
    
    public static Map<Integer, String> getTypeEnumToTypeMap() {
        return typeEnumToTypeMap;
    }
    
    public static Map<String, Integer> getTypeToTypeEnumMap() {
        return typeToTypeEnumMap;
    }
}
