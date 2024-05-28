package com.worldOfGoo.ball;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.InputField;
import com.woogleFX.structures.GameVersion;

public class Detachstrand extends EditorObject {

    public Detachstrand(EditorObject _parent, GameVersion version) {
        super(_parent, "detachstrand", version);

        addAttribute("image",  InputField.ANY);
        addAttribute("maxlen", InputField.ANY).assertRequired();

    }

}
