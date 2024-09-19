package com.worldOfGoo2.items;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects._2_Positionable;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.editorObjects.attributes.MetaEditorAttribute;
import com.woogleFX.gameData.level.GameVersion;
import com.worldOfGoo2.misc._2_Point;

public class _2_Item_Body extends _2_Positionable {

    public _2_Item_Body(EditorObject parent) {
        super(parent, "Body", GameVersion.VERSION_WOG2);

        addAttribute("id", InputField._2_STRING);
        addAttribute("type", InputField._2_NUMBER).assertRequired();
        addAttribute("pos", InputField._2_CHILD_HIDDEN).setChildAlias(_2_Point.class);
        addAttribute("rotation", InputField._2_NUMBER).assertRequired();
        addAttribute("collisionEnabled", InputField._2_BOOLEAN).assertRequired();
        addAttribute("materialName", InputField._2_STRING).assertRequired();
        addAttribute("tags", InputField._2_STRING);
        addAttribute("geoms", InputField._2_LIST_CHILD).setChildAlias(_2_Item_Geometry.class).assertRequired();
        addAttribute("categoryBits", InputField._2_STRING).assertRequired();

        setMetaAttributes(MetaEditorAttribute.parse("id,type,pos,rotation,collisionEnabled,materialName,tags,geoms,categoryBits,"));
    }

}
