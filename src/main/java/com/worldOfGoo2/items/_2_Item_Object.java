package com.worldOfGoo2.items;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects._2_Positionable;
import com.woogleFX.editorObjects.attributes.AttributeAdapter;
import com.woogleFX.editorObjects.attributes.EditorAttribute;
import com.woogleFX.editorObjects.attributes.InputField;
import com.woogleFX.editorObjects.attributes.MetaEditorAttribute;
import com.woogleFX.file.resourceManagers.ResourceManager;
import com.woogleFX.gameData.level.GameVersion;
import com.worldOfGoo2.misc._2_Point;
import javafx.scene.image.Image;

import java.io.FileNotFoundException;
import java.io.IOException;

public class _2_Item_Object extends _2_Positionable {

    private Image image;

    public Image getImage() {
        if (image == null) updateImage();
        return image;
    }

    public _2_Item_Object(EditorObject parent) {
        super(parent, "Object", GameVersion.VERSION_WOG2, "position", "scale", "pivot", "windFactor");

        addAttribute("name", InputField._2_STRING).assertRequired();
        addAttribute("randomizationGroup", InputField._2_NUMBER).assertRequired();
        addAttribute("position", InputField._2_CHILD_HIDDEN).setChildAlias(_2_Point.class);
        addAttribute("rotation", InputField._2_NUMBER).assertRequired();
        addAttribute("scale", InputField._2_CHILD_HIDDEN).setChildAlias(_2_Point.class);
        addAttribute("color", InputField._2_NUMBER).setDefaultValue("4294967295");
        addAttribute("depthOffset", InputField._2_NUMBER).assertRequired();
        addAttribute("sortOffset", InputField._2_NUMBER);
        addAttribute("imageBlendingType", InputField._2_NUMBER).assertRequired();
        addAttribute("pivot", InputField._2_CHILD_HIDDEN).setChildAlias(_2_Point.class);
        addAttribute("rotationSpeed", InputField._2_NUMBER).assertRequired();
        addAttribute("invisible", InputField._2_BOOLEAN).assertRequired();
        addAttribute("clickable", InputField._2_BOOLEAN);
        addAttribute("stableFluidsDensityFactor", InputField._2_NUMBER).assertRequired();
        addAttribute("dynamicLightingFactor", InputField._2_NUMBER).assertRequired();
        addAttribute("flipHorizontal", InputField._2_BOOLEAN).assertRequired();
        addAttribute("flipVertical", InputField._2_BOOLEAN).assertRequired();
        addAttribute("ignoreScale", InputField._2_BOOLEAN);
        addAttribute("shaderFactor", InputField._2_NUMBER).assertRequired();
        addAttribute("enableWind", InputField._2_BOOLEAN);
        addAttribute("windFactor", InputField._2_CHILD_HIDDEN).setChildAlias(_2_Point.class);
        addAttribute("stencilMode", InputField._2_NUMBER);
        addAttribute("stencilMask", InputField._2_NUMBER);
        addAttribute("alphaTestValue", InputField._2_NUMBER);
        addAttribute("body", InputField._2_CHILD).setChildAlias(_2_Item_Body.class);
        addAttribute("colorFx", InputField._2_CHILD).setChildAlias(_2_Item_ColorFX.class);
        addAttribute("particleEffects", InputField._2_LIST_CHILD).setChildAlias(_2_Item_ParticleEffect.class).assertRequired();
        addAttribute("points", InputField._2_LIST_CHILD).setChildAlias(_2_Item_Point.class).assertRequired();
        addAttribute("imageAlpha", InputField._2_NUMBER).setDefaultValue("1");
        addAttribute("animationObjectName", InputField._2_STRING);

        setMetaAttributes(MetaEditorAttribute.parse("name,randomizationGroup,position,rotation,scale,color,depthOffset,"
            + "sortOffset,imageBlendingType,pivot,rotationSpeed,invisible,clickable,stableFluidsDensityFactor,dynamicLightingFactor,"
            + "flipHorizontal,flipVertical,ignoreScale,shaderFactor,enableWind,windFactor,stencilMode,stencilMask,alphaTestValue,"
            + "points,imageAlpha,animationObjectName,"));
    }


    private void updateImage() {

        try {
            if (!getAttribute("name").stringValue().isEmpty()) {
                image = ResourceManager.getImage(null, getAttribute("name").stringValue(), GameVersion.VERSION_WOG2);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

}
