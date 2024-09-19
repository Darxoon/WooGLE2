package com.woogleFX.editorObjects;

import java.util.Arrays;

import com.woogleFX.editorObjects.attributes.AttributeAdapter;
import com.woogleFX.editorObjects.attributes.dataTypes.Position;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.gameData.level.GameVersion;
import com.worldOfGoo2.misc._2_Point;

public abstract class _2_Positionable extends EditorObject {

    private String[] pointAttributes;
    
    public _2_Positionable(EditorObject parent, String type, GameVersion version, String... pointAttributes) {
        super(parent, type, version);
        
        if (pointAttributes.length == 0) pointAttributes = new String[] { "pos" };
        this.pointAttributes = pointAttributes;
        
        for (String pointName : pointAttributes) {
            addAttributeAdapter(pointName, AttributeAdapter.pointAttributeAdapter(this, pointName, pointName));
        }
    }
    
    @Override
    public void onLoaded() {
        super.onLoaded();
        
        for (String pointName : pointAttributes) {
            EditorObject point = getChild(pointName);
            
            if (point != null) {
                setAttribute2(pointName, point.getAttribute("x").stringValue() +
                        "," + point.getAttribute("y").stringValue());
                point.getAttribute("x").addChangeListener((observable, oldValue, newValue) ->
                        setAttribute2(pointName, newValue + "," + getAttribute2(pointName).positionValue().getY()));
                point.getAttribute("y").addChangeListener((observable, oldValue, newValue) ->
                        setAttribute2(pointName, getAttribute2(pointName).positionValue().getX() + "," + newValue));
            }
        }
    }
    
    public void createPosition() {
        for (String pointName : pointAttributes) {
            EditorObject pos = ObjectCreator.create2(_2_Point.class, this, GameVersion.VERSION_WOG2);
            pos.setAttribute("x", 0);
            pos.setAttribute("y", 0);
            pos.setTypeID(pointName);
        }
    }

    public Position getPosition() {
        EditorObject posObject = getChild("pos");
        return new Position(
            posObject.getAttribute("x").doubleValue(),
            posObject.getAttribute("y").doubleValue()
        );
    }
    
    public void setPosition(double x, double y) {
        setAttribute("pos", x + "," + y);
    }
    
}
