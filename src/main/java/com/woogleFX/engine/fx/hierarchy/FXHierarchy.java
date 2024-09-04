package com.woogleFX.engine.fx.hierarchy;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.engine.fx.FXPropertiesView;
import com.woogleFX.engine.fx.FXStage;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.gameData.level.WOG1Level;
import com.woogleFX.gameData.level.WOG2Level;
import com.worldOfGoo.addin.Addin;
import com.worldOfGoo.level.Level;
import com.worldOfGoo.resrc.ResourceManifest;
import com.worldOfGoo.scene.Scene;
import com.worldOfGoo.text.TextStrings;
import com.worldOfGoo2.level._2_Level_TerrainGroup;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;

public class FXHierarchy {

    private static final TreeTableView<EditorObject> hierarchy = new TreeTableView<>();
    public static TreeTableView<EditorObject> getHierarchy() {
        return hierarchy;
    }


    public static void init() {

        hierarchy.setPlaceholder(new Label());

        // Create the columns the hierarchy uses ("Element" and its "ID or Name")
        TreeTableColumn<EditorObject, String> hierarchyElements = new TreeTableColumn<>();
        hierarchyElements.setGraphic(new Label("Elements"));
        hierarchyElements.setCellValueFactory(new TreeItemPropertyValueFactory<>("type"));
        hierarchy.getColumns().add(hierarchyElements);
        hierarchyElements.setPrefWidth(200);
        hierarchyElements.setSortable(false);

        TreeTableColumn<EditorObject, String> hierarchyNames = new TreeTableColumn<>();
        hierarchyNames.setGraphic(new Label("ID or Name"));
        hierarchyNames.setSortable(false);

        hierarchyNames.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getValue().getName()));

        hierarchy.getColumns().add(hierarchyNames);

        hierarchyElements.setCellFactory(column -> new TreeTableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                HierarchyManager.updateElementCell(this, item, empty);
            }
        });

        hierarchyNames.setCellFactory(column -> new TreeTableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                HierarchyManager.updateNameCell(this, item, empty);
            }
        });

        // If a cell is clicked from the hierarchy, update the selected object and
        // properties view.
        hierarchy.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {

            if (newValue == null || newValue.getValue() == null || hierarchy.getSelectionModel().getSelectedItems().isEmpty() ||
                    hierarchy.getSelectionModel().getSelectedItems().get(0) == null) return;

            EditorObject absoluteParent = newValue.getValue();

            while (absoluteParent.getParent() != null) absoluteParent = absoluteParent.getParent();
            if (absoluteParent instanceof Scene) hierarchySwitcherButtons.getSelectionModel().select(0);
            else if (absoluteParent instanceof Level) hierarchySwitcherButtons.getSelectionModel().select(1);
            else if (absoluteParent instanceof ResourceManifest)
                hierarchySwitcherButtons.getSelectionModel().select(2);
            else if (absoluteParent instanceof TextStrings) hierarchySwitcherButtons.getSelectionModel().select(3);
            else if (absoluteParent instanceof Addin) hierarchySwitcherButtons.getSelectionModel().select(4);

            EditorObject[] selectedNow = new EditorObject[hierarchy.getSelectionModel().getSelectedItems().size()];
            for (int i = 0; i < selectedNow.length; i++)
                selectedNow[i] = hierarchy.getSelectionModel().getSelectedItems().get(i).getValue();
            LevelManager.getLevel().setSelected(selectedNow);

            FXPropertiesView.changeTableView(selectedNow);

        });

        // Make the rows small.
        hierarchy.setFixedCellSize(18);

        hierarchy.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        hierarchy.setRowFactory(treeTableView -> HierarchyManager.createRow(hierarchy));

        hierarchy.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

        hierarchy.setPrefHeight(FXStage.getStage().getHeight() * 0.4);

        hierarchy.setId("hierarchy");

        hierarchySwitcherButtons();
        newHierarchySwitcherButtons();

    }


    private static final TabPane hierarchySwitcherButtons = new TabPane();
    public static TabPane getHierarchySwitcherButtons() {
        return hierarchySwitcherButtons;
    }


    public static void hierarchySwitcherButtons() {

        // Create the three buttons.
        Tab scene = new Tab("Scene");
        Tab level = new Tab("Level");
        Tab resrc = new Tab("Resrc");
        Tab text = new Tab("Text");
        Tab addin = new Tab("Addin");

        hierarchySwitcherButtons.getTabs().addAll(scene, level, resrc, text, addin);
        hierarchySwitcherButtons.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        hierarchySwitcherButtons.setMinHeight(30);
        hierarchySwitcherButtons.setMaxHeight(30);
        hierarchySwitcherButtons.setPrefHeight(30);
        hierarchySwitcherButtons.setPadding(new Insets(-6, -6, -6, -6));

        hierarchySwitcherButtons.getSelectionModel().selectedItemProperty().addListener((observableValue, tab, t1) -> {

            if (LevelManager.getLevel() == null) return;

            WOG1Level _level = (WOG1Level) LevelManager.getLevel();

            EditorObject rootObject;
            if (t1 == scene) rootObject = _level.getScene().get(0);
            else if (t1 == level) rootObject = _level.getLevel().get(0);
            else if (t1 == resrc) rootObject = _level.getResrc().get(0);
            else if (t1 == text) rootObject = _level.getText().get(0);
            else if (t1 == addin) rootObject = _level.getAddin().get(0);
            else return;

            hierarchy.setRoot(rootObject.getTreeItem());
            hierarchy.refresh();
            hierarchy.getRoot().setExpanded(true);
            _level.setCurrentlySelectedSection(t1.getText());
            hierarchy.setShowRoot(true);

        });

    }


    private static final TabPane newHierarchySwitcherButtons = new TabPane();
    public static TabPane getNewHierarchySwitcherButtons() {
        return newHierarchySwitcherButtons;
    }


    public static void newHierarchySwitcherButtons() {

        // Create the three buttons.
        Tab terrain = new Tab("Terrain");
        Tab terrainGroups = new Tab("Terrain Groups");
        Tab balls = new Tab("Balls");
        Tab items = new Tab("Items");
        Tab pins = new Tab("Pins");
        Tab camera = new Tab("Camera");
        Tab addin = new Tab("Addin");

        newHierarchySwitcherButtons.getTabs().addAll(terrain, terrainGroups, balls, items, pins, camera, addin);
        newHierarchySwitcherButtons.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        newHierarchySwitcherButtons.setMinHeight(30);
        newHierarchySwitcherButtons.setMaxHeight(30);
        newHierarchySwitcherButtons.setPrefHeight(30);
        newHierarchySwitcherButtons.setPadding(new Insets(-6, -6, -6, -6));

        newHierarchySwitcherButtons.getSelectionModel().selectedItemProperty().addListener((observableValue, tab, t1) -> {

            if (LevelManager.getLevel() == null) return;

            WOG2Level level = (WOG2Level) LevelManager.getLevel();

            TreeItem<EditorObject> root = level.getLevel().getTreeItem();
            hierarchy.setRoot(root);

            root.getChildren().clear();

            for (EditorObject child : level.getLevel().getChildren()) {

                if ((child.getType().equals("BallInstance") && child.getAttribute("type").stringValue().equals("Terrain")) && t1 == terrain) root.getChildren().add(child.getTreeItem());
                else if (child instanceof _2_Level_TerrainGroup && t1 == terrainGroups) root.getChildren().add(child.getTreeItem());
                else if (((child.getType().equals("BallInstance") && !child.getAttribute("type").stringValue().equals("Terrain")) || child.getType().equals("Strand")) && t1 == balls) root.getChildren().add(child.getTreeItem());
                else if (child.getType().equals("Item") && t1 == items) root.getChildren().add(child.getTreeItem());
                else if (child.getType().equals("Pin") && t1 == pins) root.getChildren().add(child.getTreeItem());
                else if (child.getType().equals("CameraKeyFrame") && t1 == camera) root.getChildren().add(child.getTreeItem());

            }

            if (t1 == addin) root.getChildren().add(level.getAddin().get(0).getTreeItem());

            hierarchy.refresh();
            hierarchy.getRoot().setExpanded(true);
            level.setCurrentlySelectedSection(t1.getText());
            hierarchy.setShowRoot(true);

        });

    }

}
