package com.woogleFX.gameData.level.levelOpening;

import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectCreators.ObjectCreator;
import com.woogleFX.editorObjects.ObjectUtil;
import com.woogleFX.engine.fx.*;
import com.woogleFX.engine.fx.hierarchy.FXHierarchy;
import com.woogleFX.engine.gui.LoadingScreen;
import com.woogleFX.engine.gui.alarms.AskForLevelNameAlarm;
import com.woogleFX.engine.gui.alarms.ErrorAlarm;
import com.woogleFX.engine.gui.alarms.LoadingResourcesAlarm;
import com.woogleFX.file.FileManager;
import com.woogleFX.editorObjects.objectCreators.BlankObjectGenerator;
import com.woogleFX.engine.gui.LevelSelector;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.file.fileExport.GOOWriter;
import com.woogleFX.file.fileImport.ObjectGOOParser;
import com.woogleFX.gameData.level.*;
import com.woogleFX.gameData.level.levelSaving.LevelUpdater;
import com.worldOfGoo.level.Signpost;
import com.worldOfGoo.resrc.Resources;
import com.worldOfGoo.resrc.ResrcImage;
import com.worldOfGoo.resrc.Sound;
import com.worldOfGoo.scene.SceneLayer;
import com.worldOfGoo2.level._2_Level;
import com.worldOfGoo2.misc._2_Point;
import javafx.concurrent.Task;
import javafx.scene.control.Tab;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class LevelLoader {

    private static final Logger logger = LoggerFactory.getLogger(LevelLoader.class);


    public static void newLevel(GameVersion version) {
        AskForLevelNameAlarm.show("new", version);
    }


    public static void openLevel(GameVersion version) {
        new LevelSelector(version).start(new Stage());
    }


    /** Creates a new, default level. */
    public static void newLevel(String name, GameVersion version) {
        logger.debug("New level");

        FXAssetSelectPane.getAssetSelectPane().setMinHeight(30);
        FXAssetSelectPane.getAssetSelectPane().setMaxHeight(30);

        if (version == GameVersion.VERSION_WOG1_OLD || version == GameVersion.VERSION_WOG1_NEW) {

            ArrayList<EditorObject> sceneList = new ArrayList<>();
            sceneList.add(ObjectCreator.create("scene", null, version));
            ArrayList<EditorObject> levelList = new ArrayList<>();
            levelList.add(ObjectCreator.create("level", null, version));
            ArrayList<EditorObject> resourcesList = new ArrayList<>();
            resourcesList.add(ObjectCreator.create("ResourceManifest", null, version));
            ArrayList<EditorObject> addinList = new ArrayList<>();
            addinList.add(BlankObjectGenerator.generateBlankAddinObject(name, version));
            ArrayList<EditorObject> textList = new ArrayList<>();
            textList.add(ObjectCreator.create("strings", null, version));

            WOG1Level level = new WOG1Level(sceneList, levelList, resourcesList, addinList, textList, version);
            level.setLevelName(name);
            FXEditorButtons.updateAllButtons();
            FXMenu.updateAllButtons();

            level.getSceneObject().setAttribute("backgroundcolor", "255,255,255");
            level.getSceneObject().setAttribute("minx", "-500");
            level.getSceneObject().setAttribute("miny", "0");
            level.getSceneObject().setAttribute("maxx", "500");
            level.getSceneObject().setAttribute("maxy", "1000");

            level.getLevelObject().setAttribute("ballsrequired", "1");
            level.getLevelObject().setAttribute("letterboxed", "false");
            level.getLevelObject().setAttribute("visualdebug", "false");
            level.getLevelObject().setAttribute("autobounds", "false");
            level.getLevelObject().setAttribute("textcolor", "255,255,255");
            level.getLevelObject().setAttribute("timebugprobability", "0");
            level.getLevelObject().setAttribute("strandgeom", "false");
            level.getLevelObject().setAttribute("allowskip", "true");

            EditorObject resourcesThing = ObjectCreator.create("Resources", level.getResrcObject(), version);
            assert resourcesThing != null;
            resourcesThing.setAttribute("id", "scene_" + level.getLevelName());
            resourcesThing.getTreeItem().setExpanded(true);
            level.getResrc().add(resourcesThing);

            EditorObject linearForceField = ObjectCreator.create("linearforcefield", level.getSceneObject(), version);
            assert linearForceField != null;
            linearForceField.setAttribute("type", "gravity");
            linearForceField.setAttribute("force", "0,-10");
            linearForceField.setAttribute("dampeningfactor", "0");
            linearForceField.setAttribute("antigrav", "true");
            level.getScene().add(linearForceField);

            EditorObject lineRight = ObjectCreator.create("line", level.getSceneObject(), version);
            assert lineRight != null;
            lineRight.setAttribute("id", "right");
            lineRight.setAttribute("anchor", "500,300");
            lineRight.setAttribute("normal", "-1,0");
            lineRight.setAttribute("tag", "detaching");
            level.getScene().add(lineRight);

            EditorObject lineLeft = ObjectCreator.create("line", level.getSceneObject(), version);
            assert lineLeft != null;
            lineLeft.setAttribute("id", "left");
            lineLeft.setAttribute("anchor", "-500,300");
            lineLeft.setAttribute("normal", "1,0");
            lineLeft.setAttribute("tag", "detaching");
            level.getScene().add(lineLeft);

            EditorObject lineGround = ObjectCreator.create("line", level.getSceneObject(), version);
            assert lineGround != null;
            lineGround.setAttribute("id", "ground");
            lineGround.setAttribute("anchor", "0,20");
            lineGround.setAttribute("normal", "0,1");
            level.getScene().add(lineGround);

            EditorObject cameraNormal = ObjectCreator.create("camera", level.getLevelObject(), version);
            assert cameraNormal != null;
            cameraNormal.setAttribute("aspect", "normal");
            cameraNormal.setAttribute("endpos", "0,0");
            cameraNormal.setAttribute("endzoom", "1");
            level.getLevel().add(cameraNormal);

            EditorObject poiNormal = ObjectCreator.create("poi", cameraNormal, version);
            assert poiNormal != null;
            poiNormal.setAttribute("pos", "0,0");
            poiNormal.setAttribute("zoom", "1");
            poiNormal.setAttribute("pause", "0");
            poiNormal.setAttribute("traveltime", "0");
            level.getLevel().add(poiNormal);

            EditorObject cameraWidescreen = ObjectCreator.create("camera", level.getLevelObject(), version);
            assert cameraWidescreen != null;
            cameraWidescreen.setAttribute("aspect", "widescreen");
            cameraWidescreen.setAttribute("endpos", "0,0");
            cameraWidescreen.setAttribute("endzoom", "1");
            level.getLevel().add(cameraWidescreen);

            EditorObject poiWidescreen = ObjectCreator.create("poi", cameraWidescreen, version);
            assert poiWidescreen != null;
            poiWidescreen.setAttribute("pos", "0,0");
            poiWidescreen.setAttribute("zoom", "1");
            poiWidescreen.setAttribute("pause", "0");
            poiWidescreen.setAttribute("traveltime", "0");
            level.getLevel().add(poiWidescreen);

            EditorObject levelExit = ObjectCreator.create("levelexit", level.getLevelObject(), version);
            assert levelExit != null;
            levelExit.setAttribute("id", "theExit");
            levelExit.setAttribute("pos", "0,0");
            levelExit.setAttribute("radius", "75");
            level.getLevel().add(levelExit);


            for (EditorObject object : level.getScene()) {
                object.update();
                object.onLoaded();
            }

            for (EditorObject object : level.getLevel()) {
                object.update();
                object.onLoaded();
            }

            for (EditorObject object : level.getResrc()) {
                object.update();
                object.onLoaded();
            }

            // Put everything in the hierarchy
            level.getSceneObject().getTreeItem().setExpanded(true);
            FXHierarchy.getHierarchy().setRoot(level.getSceneObject().getTreeItem());

            // Add items from the Scene to it
            FXPropertiesView.getPropertiesView().setRoot(FXPropertiesView.makePropertiesViewTreeItem(new EditorObject[]{level.getSceneObject()}));

            LevelUpdater.saveLevel(level);

            finishOpeningLevel(level);

        } else if (version == GameVersion.VERSION_WOG2) {

            ArrayList<EditorObject> objects = new ArrayList<>();
            EditorObject levelObject = ObjectCreator.create2(_2_Level.class, null, version);
            EditorObject topRight = ObjectCreator.create2(_2_Point.class, levelObject, version);
            topRight.setAttribute("x", 5);
            topRight.setAttribute("y", 5);
            topRight.setTypeID("boundsTopRight");
            EditorObject bottomLeft = ObjectCreator.create2(_2_Point.class, levelObject, version);
            bottomLeft.setAttribute("x", -5);
            bottomLeft.setAttribute("y", -5);
            bottomLeft.setTypeID("boundsBottomLeft");
            objects.add(levelObject);
            objects.add(topRight);
            objects.add(bottomLeft);
            levelObject.setAttribute("boundsBottomLeft", "-5,-5");
            levelObject.setAttribute("boundsTopRight", "5,5");
            levelObject.setAttribute("version", 2);
            levelObject.setAttribute("type", 0);
            levelObject.setAttribute("uuid", "12345");
            levelObject.setAttribute("title", "My Test Level");
            levelObject.setAttribute("environmentId", 0);
            levelObject.setAttribute("backgroundId", "");

            EditorObject gravity = ObjectCreator.create2(_2_Point.class, levelObject, version);
            gravity.setAttribute("x", 0);
            gravity.setAttribute("y", -10);
            gravity.setTypeID("gravity");
            levelObject.setAttribute("gravity", "0,-10");

            EditorObject initialCameraPos = ObjectCreator.create2(_2_Point.class, levelObject, version);
            initialCameraPos.setAttribute("x", 0);
            initialCameraPos.setAttribute("y", 0);
            initialCameraPos.setTypeID("initialCameraPos");
            levelObject.setAttribute("initialCameraPos", "0,0");

            levelObject.setAttribute("initialCameraZoom", 1);
            levelObject.setAttribute("cameraAutoBounds", "false");
            levelObject.setAttribute("ballsRateRequired", 50);
            levelObject.setAttribute("musicId", "");
            levelObject.setAttribute("ambienceId", "");
            levelObject.setAttribute("musicOffset", 0);
            levelObject.setAttribute("ambienceOffset", 0);
            levelObject.setAttribute("pretickSeconds", 0);
            levelObject.setAttribute("enableTimeBugs", false);

            ArrayList<EditorObject> addinList = new ArrayList<>();
            addinList.add(BlankObjectGenerator.generateBlankAddinObject(name, version));

            WOG2Level level = new WOG2Level(objects, addinList);
            level.setLevelName(name);
            FXEditorButtons.updateAllButtons();
            FXMenu.updateAllButtons();

            for (EditorObject object : level.getObjects()) {
                object.update();
                object.onLoaded();
            }

            // Put everything in the hierarchy
            level.getLevel().getTreeItem().setExpanded(true);
            FXHierarchy.getHierarchy().setRoot(level.getLevel().getTreeItem());

            // Add items from the Scene to it
            FXPropertiesView.getPropertiesView().setRoot(FXPropertiesView.makePropertiesViewTreeItem(new EditorObject[]{level.getLevel()}));

            LevelUpdater.saveLevel(level);

            finishOpeningLevel(level);

        }

    }


    public static final ArrayList<String> failedResources = new ArrayList<>();


    public static void openLevel(String levelName, GameVersion version) {

        // Don't open a level if none selected
        if (levelName == null || levelName.isEmpty()) return;

        // Don't open a level if it's already open
        for (Tab tab : FXAssetSelectPane.getAssetSelectPane().getTabs()) {
            if (tab.getText().equals(levelName) && ((AssetTab)tab).getAsset().getVersion() == version) {
                FXAssetSelectPane.getAssetSelectPane().getSelectionModel().select(tab);
                return;
            }
        }

        failedResources.clear();

        _Level level;

        try {
            level = FileManager.openLevel(levelName, version);
            if (level == null) return;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            ErrorAlarm.show(e);
            return;
        }

        level.setLevelName(levelName);
        FXEditorButtons.updateAllButtons();
        FXMenu.updateAllButtons();

        FXAssetSelectPane.getAssetSelectPane().setMinHeight(30);
        FXAssetSelectPane.getAssetSelectPane().setMaxHeight(30);

        if (level instanceof WOG1Level wog1Level) {

            for (EditorObject object : wog1Level.getScene()) {
                object.update();
                object.onLoaded();
            }

            for (EditorObject object : wog1Level.getLevel()) {
                object.update();
                object.onLoaded();
            }

            for (EditorObject object : wog1Level.getResrc()) {
                object.update();
                object.onLoaded();
            }

            // Put everything in the hierarchy
            wog1Level.getSceneObject().getTreeItem().setExpanded(true);
            FXHierarchy.getHierarchy().setRoot(wog1Level.getSceneObject().getTreeItem());

            // Add items from the Scene to it
            FXPropertiesView.getPropertiesView().setRoot(FXPropertiesView.makePropertiesViewTreeItem(new EditorObject[]{wog1Level.getSceneObject()}));

        } else if (level instanceof WOG2Level wog2Level) {

            LoadingScreen loadingScreen = new LoadingScreen();

            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {

                    long count = wog2Level.getObjects().size();

                    long i = 0;
                    try {
                        for (EditorObject object : wog2Level.getObjects().toArray(new EditorObject[0])) {
                            object.onLoaded();
                            object.update();
                            i++;
                            updateProgress(i, count);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                    for (EditorObject object : wog2Level.getObjects()) {
                        object.postInit();
                    }

                    return null;
                }
            };

            loadingScreen.setTask(task);
            Stage stage = new Stage();
            loadingScreen.start(stage);
            task.setOnSucceeded(event -> stage.close());
            task.setOnCancelled(event -> stage.close());
            task.setOnFailed(event -> stage.close());
            new Thread(task).start();

            stage.setOnCloseRequest(event -> {
                task.cancel();
                FXAssetSelectPane.getAssetSelectPane().getTabs().remove(level.getAssetTab());
            });

            wog2Level.getLevel().getTreeItem().setExpanded(true);
            FXHierarchy.getHierarchy().setRoot(wog2Level.getLevel().getTreeItem());

            FXPropertiesView.getPropertiesView().setRoot(FXPropertiesView.makePropertiesViewTreeItem(new EditorObject[]{wog2Level.getLevel()}));

            FXHierarchy.getNewHierarchySwitcherButtons().getSelectionModel().select(0);

        }

        if (!failedResources.isEmpty()) {
            StringBuilder fullError = new StringBuilder();
            for (String resource : failedResources) {
                fullError.append("\n").append(resource);
            }
            LoadingResourcesAlarm.show(fullError.substring(1));
        }

        finishOpeningLevel(level);

    }


    public static void cloneLevel(String name, GameVersion version) {
        FXAssetSelectPane.getAssetSelectPane().setMinHeight(30);
        FXAssetSelectPane.getAssetSelectPane().setMaxHeight(30);

        if (LevelManager.getLevel() instanceof WOG1Level before) {

            ArrayList<EditorObject> sceneList = new ArrayList<>();
            ArrayList<EditorObject> levelList = new ArrayList<>();
            ArrayList<EditorObject> resourcesList = new ArrayList<>();
            ArrayList<EditorObject> addinList = new ArrayList<>();
            ArrayList<EditorObject> textList = new ArrayList<>();

            FileManager.supremeAddToList(resourcesList, ObjectUtil.deepClone(before.getResrcObject(), null));
            FileManager.supremeAddToList(sceneList, ObjectUtil.deepClone(before.getSceneObject(), null));
            FileManager.supremeAddToList(levelList, ObjectUtil.deepClone(before.getLevelObject(), null));
            // Generate new addin object. idk why cloning it doesn't work, but this is arguably better anyway
            FileManager.supremeAddToList(addinList, BlankObjectGenerator.generateBlankAddinObject(name, version));
            FileManager.supremeAddToList(textList, ObjectUtil.deepClone(before.getTextObject(), null));

            String oldLevelName = LevelManager.getLevel().getLevelName();
            WOG1Level level = new WOG1Level(sceneList, levelList, resourcesList, addinList, textList, version);

            level.setLevelName(name);
            FXEditorButtons.updateAllButtons();
            FXMenu.updateAllButtons();

            for (EditorObject object : level.getResrc()) {
                if (object instanceof Resources) {
                    object.setAttribute("id", "scene_" + name);
                } else if (object instanceof ResrcImage || object instanceof Sound) {
                    object.setAttribute("id", object.getAttribute("id").stringValue().replaceAll(oldLevelName.toUpperCase(), name.toUpperCase()));
                }
                object.update();
            }

            for (EditorObject object : level.getScene()) {
                if (object instanceof SceneLayer) {
                    object.setAttribute("image", object.getAttribute("image").stringValue().replaceAll(oldLevelName, name));
                }
                object.update();
            }

            for (EditorObject object : level.getLevel()) {
                if (object instanceof Signpost) {
                    object.setAttribute("image", object.getAttribute("image").stringValue().replaceAll(oldLevelName, name));
                }
                object.update();
            }

            // Put everything in the hierarchy
            level.getSceneObject().getTreeItem().setExpanded(true);
            FXHierarchy.getHierarchy().setRoot(level.getSceneObject().getTreeItem());

            // Add items from the Scene to it
            FXPropertiesView.getPropertiesView().setRoot(FXPropertiesView.makePropertiesViewTreeItem(new EditorObject[]{level.getSceneObject()}));


            LevelUpdater.saveLevel(level);

            finishOpeningLevel(level);

        } else if (LevelManager.getLevel() instanceof WOG2Level before) {

            //ArrayList<EditorObject> objectsList = new ArrayList<>();

            StringBuilder levelExport = new StringBuilder();
            GOOWriter.recursiveGOOExport(levelExport, before.getLevel(), 0);
            EditorObject levelObject = ObjectGOOParser.read(_2_Level.class, levelExport.toString());
            ArrayList<EditorObject> objects = new ArrayList<>();
            Stack<EditorObject> toAdd = new Stack<>();
            toAdd.push(levelObject);
            while (!toAdd.isEmpty()) {
                EditorObject thisObject = toAdd.pop();
                objects.add(thisObject);
                for (EditorObject child : thisObject.getChildren()) {
                    toAdd.push(child);
                }

            }

            ArrayList<EditorObject> addinList = new ArrayList<>();
            // Generate new addin object. IDK why cloning it doesn't work, but this is arguably better anyway
            FileManager.supremeAddToList(addinList, BlankObjectGenerator.generateBlankAddinObject(name, version));

            WOG2Level level = new WOG2Level(objects, addinList);

            for (EditorObject object : level.getObjects()) {
                object.update();
                object.onLoaded();
            }

            //FileManager.supremeAddToList(objectsList, ObjectUtil.deepClone(before.getLevel(), null));


            level.setLevelName(name);
            FXEditorButtons.updateAllButtons();
            FXMenu.updateAllButtons();

            // Put everything in the hierarchy
            level.getLevel().getTreeItem().setExpanded(true);
            FXHierarchy.getHierarchy().setRoot(level.getLevel().getTreeItem());

            // Add items from the Scene to it
            FXPropertiesView.getPropertiesView().setRoot(FXPropertiesView.makePropertiesViewTreeItem(new EditorObject[]{level.getLevel()}));

            LevelUpdater.saveLevel(level);

            finishOpeningLevel(level);

        }

    }


    public static void cloneLevel() {
        GameVersion version = LevelManager.getLevel().getVersion();
        AskForLevelNameAlarm.show("clone", version);
    }


    private static void finishOpeningLevel(_Level level) {

        AssetTab levelSelectButton = FXAssetSelectPane.levelSelectButton(level);
        FXAssetSelectPane.getAssetSelectPane().getTabs().add(levelSelectButton);

        int numTabs = FXAssetSelectPane.getAssetSelectPane().getTabs().size();
        double tabSize = 1 / (numTabs + 1.0);
        FXAssetSelectPane.getAssetSelectPane().setTabMaxWidth(tabSize * (FXAssetSelectPane.getAssetSelectPane().getWidth() - 15) - 15);
        FXAssetSelectPane.getAssetSelectPane().setTabMinWidth(tabSize * (FXAssetSelectPane.getAssetSelectPane().getWidth() - 15) - 15);

        level.setAssetTab(levelSelectButton);
        level.setEditingStatus(AssetTab.NO_UNSAVED_CHANGES, true);
        FXAssetSelectPane.getAssetSelectPane().getSelectionModel().select(levelSelectButton);
        LevelManager.onSetLevel(level);

    }


}
