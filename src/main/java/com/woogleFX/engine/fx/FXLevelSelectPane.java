package com.woogleFX.engine.fx;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.engine.fx.hierarchy.FXHierarchy;
import com.woogleFX.engine.gui.alarms.CloseTabAlarm;
import com.woogleFX.gameData.level.LevelTab;
import com.woogleFX.gameData.level.WOG1Level;
import com.woogleFX.gameData.level.WOG2Level;
import com.woogleFX.gameData.level._Level;
import javafx.scene.control.TabPane;

public class FXLevelSelectPane {

    private static final TabPane levelSelectPane = new TabPane();
    public static TabPane getLevelSelectPane() {
        return levelSelectPane;
    }


    public static LevelTab levelSelectButton(_Level level) {

        // Instantiate the tab.
        LevelTab tab = new LevelTab(level.getLevelName(), level);

        // Override the default close operation of the tab.
        tab.setOnCloseRequest(event -> {
            event.consume();
            // If the level has unsaved changes:
            if (level.getEditingStatus() == LevelTab.UNSAVED_CHANGES) {
                // Show a dialogue asking the user if they want to close the level without
                // saving changes first.
                CloseTabAlarm.show(tab, level);
            } else {
                // Close the tab.
                if (tab.getTabPane().getTabs().size() == 1) {
                    FXLevelSelectPane.getLevelSelectPane().setMinHeight(0);
                    FXLevelSelectPane.getLevelSelectPane().setMaxHeight(0);
                    // If all tabs are closed, clear the side pane
                    FXHierarchy.getHierarchy().setRoot(null);
                    // Clear the properties pane too
                    FXPropertiesView.changeTableView(new EditorObject[]{});
                }
                tab.getTabPane().getTabs().remove(tab);
            }
        });

        tab.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            // If the user has just selected this tab:
            if (t1) {

                // Set this tab's level to be the currently selected level.
                LevelManager.setLevel(level);

                if (level instanceof WOG1Level _level) {

                    EditorObject rootObject = switch (_level.getCurrentlySelectedSection()) {
                        case "Scene" -> _level.getSceneObject();
                        case "Level" -> _level.getLevelObject();
                        case "Resrc" -> _level.getResrcObject();
                        case "Text" -> _level.getTextObject();
                        case "Addin" -> _level.getAddinObject();
                        default -> null;
                    };
                    if (rootObject == null) return;
                    FXHierarchy.getHierarchy().setRoot(rootObject.getTreeItem());

                    int i = switch (_level.getCurrentlySelectedSection()) {
                        case "Scene" -> 0;
                        case "Level" -> 1;
                        case "Resrc" -> 2;
                        case "Text" -> 3;
                        case "Addin" -> 4;
                        default -> -1;
                    };
                    FXHierarchy.getHierarchySwitcherButtons().getSelectionModel().select(i);

                } else if (level instanceof WOG2Level) {

                    int i = switch (level.getCurrentlySelectedSection()) {
                        case "Terrain" -> 0;
                        case "Terrain Groups" -> 1;
                        case "Balls" -> 2;
                        case "Items" -> 3;
                        case "Pins" -> 4;
                        case "Camera" -> 5;
                        case "Addin" -> 6;
                        default -> -1;
                    };
                    FXHierarchy.getNewHierarchySwitcherButtons().getSelectionModel().select((i + 1) % 7);
                    FXHierarchy.getNewHierarchySwitcherButtons().getSelectionModel().select(i);

                }

            } else {
                // Destroy and replace the level tab to prevent an unknown freezing issue.
                if (level.getLevelTab() != null && level.getLevelTab().getTabPane() != null
                        && level.getLevelTab().getTabPane().getTabs().contains(level.getLevelTab())
                        && !level.getLevelTab().getTabPane().getTabs().isEmpty()) {
                    level.setEditingStatus(level.getEditingStatus(), false);
                }

            }
        });

        return tab;
    }


    public static void init() {

        levelSelectPane.getSelectionModel().selectedItemProperty().addListener((observableValue, tab, t1) -> {
            if (t1 == null) {
                LevelManager.setLevel(null);
                LevelManager.onSetLevel(null);
                FXEditorButtons.updateAllButtons();
                FXMenu.updateAllButtons();
            }
        });

        levelSelectPane.widthProperty().addListener((observableValue, number, t1) -> {
            int numTabs = levelSelectPane.getTabs().size();
            double tabSize = 1 / (numTabs + 1.0);
            levelSelectPane.setTabMaxWidth(tabSize * (levelSelectPane.getWidth() - 15) - 15);
            levelSelectPane.setTabMinWidth(tabSize * (levelSelectPane.getWidth() - 15) - 15);
        });

        levelSelectPane.setMinHeight(0);
        levelSelectPane.setMaxHeight(0);

        levelSelectPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);

        FXPropertiesView.getPropertiesView().prefHeightProperty()
                .bind(FXContainers.getViewPane().heightProperty().subtract(FXPropertiesView.getPropertiesView().layoutYProperty()));

        levelSelectPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
        levelSelectPane.setStyle("-fx-open-tab-animation: NONE");

    }



}
