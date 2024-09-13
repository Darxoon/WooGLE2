package com.worldOfGoo2.level;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.attributes.AttributeAdapter;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.editorObjects.attributes.MetaEditorAttribute;
import com.woogleFX.editorObjects.objectComponents.ImageComponent;
import com.woogleFX.editorObjects.objectComponents.RectangleComponent;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.engine.fx.FXCanvas;
import com.woogleFX.engine.renderer.Depth;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.woogleFX.gameData.environments.EnvironmentManager;
import com.woogleFX.gameData.level.GameVersion;
import com.worldOfGoo2.environments._2_Environment;
import com.worldOfGoo2.environments._2_Environment_Layer;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.FileNotFoundException;

public class _2_Level extends EditorObject {

    public _2_Level(EditorObject parent) {
        super(parent, "Level", GameVersion.VERSION_WOG2);

        addAttributeAdapter("gravity", AttributeAdapter.pointAttributeAdapter(this, "gravity", "gravity"));
        addAttributeAdapter("boundsTopRight", AttributeAdapter.pointAttributeAdapter(this, "boundsTopRight", "boundsTopRight"));
        addAttributeAdapter("boundsBottomLeft", AttributeAdapter.pointAttributeAdapter(this, "boundsBottomLeft", "boundsBottomLeft"));
        addAttributeAdapter("initialCameraPos", AttributeAdapter.pointAttributeAdapter(this, "initialCameraPos", "initialCameraPos"));

        setTypeID("_2_Level");

    }


    @Override
    public Class<? extends EditorObject>[] getPossibleChildren() {
        return new Class[] {
                _2_Level_BallInstance.class,
                _2_Level_Strand.class,
                _2_Level_TerrainGroup.class,
                _2_Level_Item.class,
                _2_Level_Pin.class,
                _2_Level_CameraKeyFrame.class,
                _2_Level_TerrainBall.class
        };
    }


    @Override
    public String[] getPossibleChildrenTypeIDs() {
        return new String[] { "balls", "strands", "terrainGroups", "items", "pins", "initialCameraKeyframes", "terrainBalls" };
    }


    @Override
    public void onLoaded() {
        super.onLoaded();

        EditorObject gravity = getChildren("gravity").get(0);
        setAttribute2("gravity", gravity.getAttribute("x").stringValue() + "," + gravity.getAttribute("y").stringValue());
        gravity.getAttribute("x").addChangeListener((observable, oldValue, newValue) ->
                setAttribute2("gravity", newValue + "," + getAttribute2("gravity").positionValue().getY()));
        gravity.getAttribute("y").addChangeListener((observable, oldValue, newValue) ->
                setAttribute2("gravity", getAttribute2("gravity").positionValue().getX() + "," + newValue));

        EditorObject boundsTopRight = getChildren("boundsTopRight").get(0);
        setAttribute2("boundsTopRight", boundsTopRight.getAttribute("x").stringValue() + "," + boundsTopRight.getAttribute("y").stringValue());
        boundsTopRight.getAttribute("x").addChangeListener((observable, oldValue, newValue) ->
                setAttribute2("boundsTopRight", newValue + "," + getAttribute2("boundsTopRight").positionValue().getY()));
        boundsTopRight.getAttribute("y").addChangeListener((observable, oldValue, newValue) ->
                setAttribute2("boundsTopRight", getAttribute2("boundsTopRight").positionValue().getX() + "," + newValue));

        EditorObject boundsBottomLeft = getChildren("boundsBottomLeft").get(0);
        setAttribute2("boundsBottomLeft", boundsBottomLeft.getAttribute("x").stringValue() + "," + boundsBottomLeft.getAttribute("y").stringValue());
        boundsBottomLeft.getAttribute("x").addChangeListener((observable, oldValue, newValue) ->
                setAttribute2("boundsBottomLeft", newValue + "," + getAttribute2("boundsBottomLeft").positionValue().getY()));
        boundsBottomLeft.getAttribute("y").addChangeListener((observable, oldValue, newValue) ->
                setAttribute2("boundsBottomLeft", getAttribute2("boundsBottomLeft").positionValue().getX() + "," + newValue));

        EditorObject initialCameraPos = getChildren("initialCameraPos").get(0);
        setAttribute2("initialCameraPos", initialCameraPos.getAttribute("x").stringValue() + "," + initialCameraPos.getAttribute("y").stringValue());
        initialCameraPos.getAttribute("x").addChangeListener((observable, oldValue, newValue) ->
                setAttribute2("initialCameraPos", newValue + "," + getAttribute2("initialCameraPos").positionValue().getY()));
        initialCameraPos.getAttribute("y").addChangeListener((observable, oldValue, newValue) ->
                setAttribute2("initialCameraPos", getAttribute2("initialCameraPos").positionValue().getX() + "," + newValue));

        updateObjectPositions();
        getAttribute("backgroundId").addChangeListener((observable, oldValue, newValue) -> updateObjectPositions());

    }


    private void updateObjectPositions() {

        clearObjectPositions();

        EditorObject boundsBottomLeft = getChildren("boundsBottomLeft").get(0);
        EditorObject boundsTopRight = getChildren("boundsTopRight").get(0);

        addObjectComponent(new RectangleComponent() {
            public double getX() {
                double minx = boundsBottomLeft.getAttribute("x").doubleValue();
                double maxx = boundsTopRight.getAttribute("x").doubleValue();
                return (minx + maxx) / 2;
            }

            public void setX(double x) {
                double width = getWidth();
                boundsBottomLeft.setAttribute("x", x - width / 2);
                boundsTopRight.setAttribute("x", x + width / 2);
            }

            public double getY() {
                double miny = -boundsBottomLeft.getAttribute("y").doubleValue();
                double maxy = -boundsTopRight.getAttribute("y").doubleValue();
                return (miny + maxy) / 2;
            }

            public void setY(double y) {
                double height = getHeight();
                boundsBottomLeft.setAttribute("y", -y - height / 2);
                boundsTopRight.setAttribute("y", -y + height / 2);
            }

            public double getWidth() {
                double minx = boundsBottomLeft.getAttribute("x").doubleValue();
                double maxx = boundsTopRight.getAttribute("x").doubleValue();
                return Math.abs(maxx - minx);
            }

            public void setWidth(double width) {
                double x = getX();
                boundsBottomLeft.setAttribute("x", x - width / 2);
                boundsTopRight.setAttribute("x", x + width / 2);
            }

            public double getHeight() {
                double miny = -boundsBottomLeft.getAttribute("y").doubleValue();
                double maxy = -boundsTopRight.getAttribute("y").doubleValue();
                return Math.abs(maxy - miny);
            }

            public void setHeight(double height) {
                double y = getY();
                boundsBottomLeft.setAttribute("y", -y - height / 2);
                boundsTopRight.setAttribute("y", -y + height / 2);
            }

            public double getDepth() {
                return Depth.SCENE;
            }

            public double getEdgeSize() {
                return 0.1;
            }

            public Paint getBorderColor() {
                return new Color(0.0, 0.0, 0.0, 1.0);
            }

            public boolean isEdgeOnly() {
                return true;
            }

            public Paint getColor() {
                return new Color(0.0, 0.0, 0.0, 0.0);
            }

            public boolean isRotatable() {
                return false;
            }
        });

        try {

            if (getAttribute("backgroundId").stringValue().isEmpty()) return;

            _2_Environment environment = EnvironmentManager.getEnvironment(getAttribute("backgroundId").stringValue());

            for (EditorObject part : environment.getChildren()) if (part instanceof _2_Environment_Layer) {

                Image image = part.getAttribute("imageName").imageValue(null, GameVersion.VERSION_WOG2);

                double partRotation = 0;

                addObjectComponent(new ImageComponent() {
                    public double getX() {
                        return (boundsTopRight.getAttribute("x").doubleValue() + boundsBottomLeft.getAttribute("x").doubleValue()) / 2;
                    }
                    public double getY() {
                        return (-boundsTopRight.getAttribute("y").doubleValue() - boundsBottomLeft.getAttribute("y").doubleValue()) / 2;
                    }
                    public double getRotation() {
                        return partRotation;
                    }
                    public double getScaleX() {
                        double dx = boundsTopRight.getAttribute("x").doubleValue() - boundsBottomLeft.getAttribute("x").doubleValue();
                        return dx / image.getWidth();
                    }
                    public double getScaleY() {
                        double dy = boundsTopRight.getAttribute("y").doubleValue() - boundsBottomLeft.getAttribute("y").doubleValue();
                        return dy / image.getHeight();
                    }
                    public double getDepth() {
                        //System.out.println(part.getAttribute("depth").doubleValue());
                        return part.getAttribute("depth").doubleValue() + -100000;
                        //return -100000;
                    }
                    public double getAlpha() {
                        return 1.0; // part.getAttribute("imageAlpha").doubleValue();
                    }
                    public Image getImage() {
                        return image;
                    }
                    public boolean isVisible() {
                        return LevelManager.getLevel().getVisibilitySettings().isShowSceneBGColor();
                    }
                    public boolean isResizable() {
                        return false;
                    }
                    public boolean isDraggable() {
                        return false;
                    }
                    public boolean isRotatable() {
                        return false;
                    }
                    public boolean isSelectable() {
                        return false;
                    }
                });

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

}
