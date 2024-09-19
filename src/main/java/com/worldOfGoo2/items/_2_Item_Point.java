package com.worldOfGoo2.items;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects._2_Positionable;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.editorObjects.attributes.MetaEditorAttribute;
import com.woogleFX.gameData.level.GameVersion;
import com.worldOfGoo2.misc._2_Point;

public class _2_Item_Point extends _2_Positionable {

    public _2_Item_Point(EditorObject parent) {
        super(parent, "Item_Point", GameVersion.VERSION_WOG2, "position");

        addAttribute("position", InputField._2_CHILD_HIDDEN).setChildAlias(_2_Point.class);
        addAttribute("type", InputField._2_STRING);
        addAttribute("rotation", InputField._2_STRING);
        addAttribute("radius", InputField._2_STRING);
        addAttribute("userValue", InputField._2_STRING);

        setMetaAttributes(MetaEditorAttribute.parse("position,type,rotation,radius,userValue,"));
    }

}
