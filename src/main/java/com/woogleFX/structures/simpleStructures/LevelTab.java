package com.woogleFX.structures.simpleStructures;

import com.woogleFX.engine.fx.FXHierarchy;
import com.woogleFX.file.FileManager;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.structures.GameVersion;
import com.woogleFX.structures.WorldLevel;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.io.FileNotFoundException;

public class LevelTab extends Tab {

    private final WorldLevel level;
    public WorldLevel getLevel() {
        return level;
    }


    public LevelTab(String text, WorldLevel level) {
        super(text);
        this.level = level;
    }

    public static final int NO_UNSAVED_CHANGES = 0;
    public static final int UNSAVED_CHANGES = 1;
    public static final int UNSAVED_CHANGES_UNMODIFIABLE = 2;

    private static Image noChangesImageOld;
    private static Image changesImageOld;
    private static Image changesUnmodifiableImageOld;

    private static Image noChangesImageNew;
    private static Image changesImageNew;
    private static Image changesUnmodifiableImageNew;

    static {
        try {

            noChangesImageOld = FileManager.getIcon("ButtonIcons\\Level\\no_unsaved_changes_old.png");
            changesImageOld = FileManager.getIcon("ButtonIcons\\Level\\unsaved_changes_old.png");
            changesUnmodifiableImageOld = FileManager.getIcon("ButtonIcons\\Level\\unsaved_changes_unmodifiable_old.png");

            noChangesImageNew = FileManager.getIcon("ButtonIcons\\Level\\no_unsaved_changes_new.png");
            changesImageNew = FileManager.getIcon("ButtonIcons\\Level\\unsaved_changes_new.png");
            changesUnmodifiableImageNew = FileManager.getIcon("ButtonIcons\\Level\\unsaved_changes_unmodifiable_new.png");

        } catch (FileNotFoundException ignored) {

        }
    }


    public void update(int editingStatus, boolean shouldSelect) {

        Image currentStatusImage = null;

        if (editingStatus == NO_UNSAVED_CHANGES) {
            currentStatusImage = level.getVersion() == GameVersion.OLD ? noChangesImageOld : noChangesImageNew;
        } else if (editingStatus == UNSAVED_CHANGES) {
            currentStatusImage = level.getVersion() == GameVersion.OLD ? changesImageOld : changesImageNew;
        } else if (editingStatus == UNSAVED_CHANGES_UNMODIFIABLE) {
            currentStatusImage = level.getVersion() == GameVersion.OLD ? changesUnmodifiableImageOld : changesUnmodifiableImageNew;
        }

        AnchorPane pane = new AnchorPane();

        pane.getChildren().add(new ImageView(currentStatusImage));

        TreeItem<EditorObject> root = FXHierarchy.getHierarchy().getRoot();

        StackPane graphicContainer = new StackPane();
        graphicContainer.prefWidthProperty().bind(getTabPane().tabMaxWidthProperty());
        StackPane.setAlignment(pane, Pos.CENTER_LEFT);
        graphicContainer.getChildren().addAll(pane, new Label(level.getLevelName()));
        setGraphic(graphicContainer);
        if (shouldSelect) {
            getTabPane().getSelectionModel().select(this);
            FXHierarchy.getHierarchy().setRoot(root);
        }
    }

}
