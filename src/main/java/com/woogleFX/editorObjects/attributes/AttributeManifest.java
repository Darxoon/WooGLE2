package com.woogleFX.editorObjects.attributes;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.woogleFX.gameData.level.GameVersion;

public class AttributeManifest {

    @JacksonXmlProperty(isAttribute = true)
    private String localName;
    @JacksonXmlProperty
    public String getLocalName() {
        return localName;
    }
    @JacksonXmlProperty
    public void setLocalName(String localName) {
        this.localName = localName;
    }

    @JacksonXmlProperty(isAttribute = true)
    private GameVersion version;
    @JacksonXmlProperty
    public GameVersion getVersion() {
        return version;
    }
    @JacksonXmlProperty
    public void setVersion(GameVersion version) {
        this.version = version;
    }

    @JacksonXmlElementWrapper(localName = "attributes")
    @JacksonXmlProperty(localName = "EditorAttribute")
    private EditorAttribute[] attributes;
    @JacksonXmlProperty
    public EditorAttribute[] getAttributes() {
        return attributes;
    }
    @JacksonXmlProperty
    public void setAttributes(EditorAttribute[] attributes) {
        this.attributes = attributes;
    }

    @JacksonXmlElementWrapper(localName = "metaAttributes")
    @JacksonXmlProperty(localName = "MetaEditorAttribute")
    private MetaEditorAttribute[] metaAttributes;
    @JacksonXmlProperty
    public MetaEditorAttribute[] getMetaAttributes() {
        return metaAttributes;
    }
    @JacksonXmlProperty
    public void setMetaAttributes(MetaEditorAttribute[] metaAttributes) {
        this.metaAttributes = metaAttributes;
    }

}
