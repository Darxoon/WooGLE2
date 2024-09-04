package com.woogleFX.gameData.ball;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.gameData.level.GameVersion;
import com.worldOfGoo.resrc.Font;
import com.worldOfGoo.resrc.ResrcImage;
import com.worldOfGoo.resrc.SetDefaults;
import com.worldOfGoo.resrc.Sound;

import java.util.ArrayList;

public class _2Ball {

    private GameVersion version;
    public GameVersion getVersion() {
        return version;
    }
    public void setVersion(GameVersion version) {
        this.version = version;
    }


    public final ArrayList<EditorObject> objects;
    public ArrayList<EditorObject> getObjects() {
        return objects;
    }


    public final ArrayList<EditorObject> resources;
    public ArrayList<EditorObject> getResources() {
        return resources;
    }


    private String shapeType;
    public String getShapeType() {
        return shapeType;
    }
    public void setShapeType(String shapeType) {
        this.shapeType = shapeType;
    }


    private double width;
    public double getWidth() {
        return width;
    }
    public void setWidth(double width) {
        this.width = width;
    }

    private double height;
    public double getHeight() {
        return height;
    }
    public void setHeight(double height) {
        this.height = height;
    }

    private double sizeVariance;
    public double getSizeVariance() {
        return sizeVariance;
    }
    public void setSizeVariance(double sizeVariance) {
        this.sizeVariance = sizeVariance;
    }


    public _2Ball(ArrayList<EditorObject> _objects, ArrayList<EditorObject> resources) {
        this.objects = _objects;
        this.resources = resources;

        shapeType = "circle";

        width = _objects.get(0).getAttribute("width").doubleValue();
        height = _objects.get(0).getAttribute("height").doubleValue();
        sizeVariance = _objects.get(0).getAttribute("sizeVariance").doubleValue();

        /*

        String input2 = objects.get(0).getAttribute("shape").stringValue();

        setShapeType(input2.substring(0, input2.indexOf(",")));

        String input3 = input2.substring(input2.indexOf(",") + 1);

        double size1 = input3.contains(",") ? Double.parseDouble(input3.substring(0, input3.indexOf(","))) : Double.parseDouble(input3);

        if (getShapeType().equals("circle")) {
            if (input3.contains(",")) {
                setShapeSize(size1);
                setShapeVariance(Double.parseDouble(input3.substring(input3.indexOf(",") + 1)));
            } else {
                setShapeSize(Double.parseDouble(input3));
                setShapeVariance(0);
            }
        } else {
            setShapeSize2(size1);
            String input4 = input3.substring(input3.indexOf(",") + 1);
            if (input4.contains(",")) {
                setShapeSize(Double.parseDouble(input4.substring(0, input4.indexOf(","))));
                setShapeVariance(Double.parseDouble(input4.substring(input4.indexOf(",") + 1)));
            } else {
                setShapeSize(Double.parseDouble(input4));
                setShapeVariance(0);
            }
        }

        SetDefaults currentSetDefaults = null;

        for (EditorObject EditorObject : resources) {

            if (EditorObject instanceof SetDefaults setDefaults) {
                currentSetDefaults = setDefaults;
            }

            else if (EditorObject instanceof ResrcImage resrcImage) {
                resrcImage.setSetDefaults(currentSetDefaults);
            } else if (EditorObject instanceof Sound sound) {
                sound.setSetDefaults(currentSetDefaults);
            } else if (EditorObject instanceof Font font) {
                font.setSetDefaults(currentSetDefaults);
            }

        }


         */

    }

}
