package com.woogleFX.gameData.ball;
import java.util.ArrayList;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.gameData.level.GameVersion;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class DefaultXmlOpener extends DefaultHandler {
    public static enum Mode {
        OBJECT,
        RESOURCE,
    }
    
    public static EditorObject parent = null;
    
    private Mode mode = Mode.OBJECT;
    private final ArrayList<EditorObject> objects;
    private final ArrayList<EditorObject> resources;
    private final GameVersion version;
    public DefaultXmlOpener(ArrayList<EditorObject> objects,
                          ArrayList<EditorObject> resources,
                          GameVersion  version) {
        this.objects = objects;
        this.resources = resources;
        this.version = version;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {

        if (qName.equals("Atlas")) return;

        if (qName.equals("particles") || qName.equals("sound")) qName = "ball_" + qName;

        EditorObject obj = ObjectCreator.create(qName, parent, version);

        for (int i = 0; i < attributes.getLength(); i++) {
            try {
                if (obj.attributeExists(attributes.getQName(i)))
                    obj.setAttribute(attributes.getQName(i), attributes.getValue(i));
            } catch (Exception ignored) {

            }
        }

        if (mode == Mode.OBJECT) objects.add(obj);
        else if (mode == Mode.RESOURCE) resources.add(obj);

        parent = obj;

    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        //logger.debug("Ended: " + qName);
        if (parent.getParent() == null) System.out.println(qName);
        else parent = parent.getParent();
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        //logger.debug(Arrays.toString(ch) + start + length);
    }
}