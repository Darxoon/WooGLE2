package com.worldOfGoo.ball;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.GameVersion;

public class Marker extends EditorObject {

    public Marker(EditorObject _parent, GameVersion version) {
        super(_parent, "marker", version);

        addAttribute("drag",     InputField.ANY).assertRequired();
        addAttribute("detach",   InputField.ANY).assertRequired();
        addAttribute("rotspeed", InputField.ANY).assertRequired();

    }

}
