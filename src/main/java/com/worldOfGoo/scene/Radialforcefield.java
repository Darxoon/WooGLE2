package com.worldOfGoo.scene;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectComponents.AnchorComponent;
import com.woogleFX.editorObjects.objectComponents.CircleComponent;
import com.woogleFX.engine.renderer.Depth;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.gameData.level.GameVersion;
import com.woogleFX.editorObjects.attributes.MetaEditorAttribute;
import javafx.scene.paint.*;

public class Radialforcefield extends EditorObject {

    public Radialforcefield(EditorObject _parent, GameVersion version) {
        super(_parent, "radialforcefield", version);

        addAttribute("id",               InputField._1_STRING)                                    .assertRequired();
        addAttribute("type",             InputField._1_STRING)       .setDefaultValue("gravity")  .assertRequired();
        addAttribute("center",           InputField._1_POSITION)  .setDefaultValue("0,0")      .assertRequired();
        addAttribute("radius",           InputField._1_NUMBER)    .setDefaultValue("100")      .assertRequired();
        addAttribute("forceatcenter",    InputField._1_NUMBER)    .setDefaultValue("0")        .assertRequired();
        addAttribute("forceatedge",      InputField._1_NUMBER)    .setDefaultValue("0")        .assertRequired();
        addAttribute("dampeningfactor",  InputField._1_NUMBER)    .setDefaultValue("0")        .assertRequired();
        addAttribute(
            "rotationaldampeningfactor", InputField._1_NUMBER);
        addAttribute("antigrav",         InputField._1_FLAG)      .setDefaultValue("false")    .assertRequired();
        addAttribute("geomonly",         InputField._1_FLAG)                                   .assertRequired();
        addAttribute("enabled",          InputField._1_FLAG)      .setDefaultValue("true")     .assertRequired();

        addObjectComponent(new CircleComponent() {
            public double getX() {
                return getAttribute("center").positionValue().getX();
            }
            public void setX(double x) {
                setAttribute("center", x + "," + getY());
            }
            public double getY() {
                return -getAttribute("center").positionValue().getY();
            }
            public void setY(double y) {
                setAttribute("center", getX() + "," + -y);
            }
            public double getRadius() {
                return getAttribute("radius").doubleValue();
            }
            public void setRadius(double radius) {
                setAttribute("radius", radius);
            }
            public double getEdgeSize() {
                return 4.5;
            }
            public boolean isEdgeOnly() {
                return true;
            }
            public double getDepth() {
                return Depth.FORCEFIELDS;
            }
            public Paint getBorderColor() {
                return new Color(1.0, 1.0, 0, 1.0);
            }
            public Paint getColor() {
                return new Color(1.0, 1.0, 0, 0.05);
            }
            public boolean isVisible() {
                return LevelManager.getLevel().getVisibilitySettings().isShowForcefields();
            }
        });

        addObjectComponent(new AnchorComponent() {
            public double getX() {
                return getAttribute("center").positionValue().getX();
            }
            public double getY() {
                return -getAttribute("center").positionValue().getY();
            }
            public double getAnchorX() {
                return 0;
            }
            public double getAnchorY() {
                return -getAttribute("forceatcenter").doubleValue() * 20;
            }
            public void setAnchor(double anchorX, double anchorY) {
                setAttribute("forceatcenter", -anchorY / 20);
            }
            public double getLineWidth() {
                return 3;
            }
            public double getDepth() {
                return Depth.FORCEFIELDS;
            }
            public Paint getColor() {
                return new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.valueOf("802000FF")), new Stop(1, Color.valueOf("FFC040FF")));
            }
            public boolean isVisible() {
                return LevelManager.getLevel().getVisibilitySettings().isShowForcefields();
            }
        });

        addObjectComponent(new AnchorComponent() {
            public double getX() {
                return getAttribute("center").positionValue().getX() + getAttribute("radius").doubleValue();
            }
            public double getY() {
                return -getAttribute("center").positionValue().getY();
            }
            public double getAnchorX() {
                return 0;
            }
            public double getAnchorY() {
                return -getAttribute("forceatedge").doubleValue() * 20;
            }
            public void setAnchor(double anchorX, double anchorY) {
                setAttribute("forceatedge", -anchorY / 20);
            }
            public double getLineWidth() {
                return 3;
            }
            public double getDepth() {
                return Depth.FORCEFIELDS;
            }
            public Paint getColor() {
                return new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, Color.valueOf("802000FF")), new Stop(1, Color.valueOf("FFC040FF")));
            }
            public boolean isVisible() {
                return LevelManager.getLevel().getVisibilitySettings().isShowForcefields();
            }
        });

        setMetaAttributes(MetaEditorAttribute.parse("id,center,radius,forceatcenter,forceatedge,dampeningfactor,rotationaldampeningfactor,antigrav,"));

    }


    @Override
    public String getName() {
        return getAttribute("id").stringValue();
    }

}
