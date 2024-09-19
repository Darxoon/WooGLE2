package com.worldOfGoo2.items;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects._2_Positionable;
import com.woogleFX.editorObjects.attributes.AttributeAdapter;
import com.woogleFX.editorObjects.attributes.EditorAttribute;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.gameData.level.GameVersion;

public class _2_Item extends _2_Positionable {

    public _2_Item(EditorObject parent) {
        super(parent, "Item", GameVersion.VERSION_WOG2,
            "animationPlayNormalizedTimeRange", "animationLocalPosition", "animationLocalScale");
        
        addAttributeAdapter("minScale", new AttributeAdapter("minScale") {
            private EditorAttribute attribute = new EditorAttribute("minScale", InputField._2_NUMBER, _2_Item.this);
            
            @Override
            public EditorAttribute getValue() {
                EditorObject limits = getChild("limits");
                if (limits != null) {
                    attribute.setValue(getChild("limits").getAttribute("minScale").stringValue());
                } else {
                    attribute.setValue("");
                }
                return attribute;
            }

            @Override
            public void setValue(String value) {
                EditorObject limits = getChild("limits");
                if (limits != null) {
                    limits.setAttribute("minScale", value);
                }
            }
        });
        
        addAttributeAdapter("maxScale", new AttributeAdapter("maxScale") {
            private EditorAttribute attribute = new EditorAttribute("maxScale", InputField._2_NUMBER, _2_Item.this);
            
            @Override
            public EditorAttribute getValue() {
                EditorObject limits = getChild("limits");
                if (limits != null) {
                    attribute.setValue(getChild("limits").getAttribute("maxScale").stringValue());
                } else {
                    attribute.setValue("");
                }
                return attribute;
            }

            @Override
            public void setValue(String value) {
                EditorObject limits = getChild("limits");
                if (limits != null) {
                    limits.setAttribute("maxScale", value);
                }
            }
        });
    }

}
