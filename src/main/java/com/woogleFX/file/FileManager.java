package com.woogleFX.file;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.SupremeMain;
import com.woogleFX.editorObjects.EditorObject;
import com.woogleFX.editorObjects.objectCreators.BlankObjectGenerator;
import com.woogleFX.file.aesEncryption.AESBinFormat;
import com.woogleFX.file.fileImport.ObjectGOOParser;
import com.woogleFX.gameData.ball.DefaultXmlOpener;
import com.woogleFX.file.fileImport.LevelOpener;
import com.woogleFX.file.fileImport.PropertiesOpener;
import com.woogleFX.engine.LevelManager;
import com.woogleFX.gameData.ball.PaletteManager;
import com.woogleFX.gameData.ball._2Ball;
import com.woogleFX.gameData.level.GameVersion;
import com.woogleFX.gameData.level.WOG1Level;
import com.woogleFX.gameData.level.WOG2Level;
import com.worldOfGoo2.ball._2_Ball;
import com.worldOfGoo2.items._2_Item;
import com.worldOfGoo2.level._2_Level;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.woogleFX.gameData.ball._Ball;
import com.woogleFX.gameData.ball.DefaultXmlOpener.Mode;
import com.woogleFX.gameData.items.ItemManager;
import com.woogleFX.gameData.level._Level;

import javafx.scene.image.Image;


public class FileManager {

    private static final Logger logger = LoggerFactory.getLogger(FileManager.class);


    private static String oldWOG1dir = "";
    public static void setOldWOG1dir(String oldWOG1dir) {
        FileManager.oldWOG1dir = oldWOG1dir;
    }


    private static String newWOG1dir = "";
    public static void setNewWOG1dir(String newWOG1dir) {
        FileManager.newWOG1dir = newWOG1dir;
    }


    private static String wog2dir = "";
    public static void setWog2dir(String WOG2dir) {
        FileManager.wog2dir = WOG2dir;
    }


    public static String getGameDir(GameVersion version) {
        return switch (version) {
            case VERSION_WOG1_OLD -> oldWOG1dir;
            case VERSION_WOG1_NEW -> newWOG1dir;
            case VERSION_WOG2 -> wog2dir;
        };
    }


    private static Image failedImage;
    public static Image getFailedImage() {
        return failedImage;
    }
    public static void openFailedImage() throws IOException {
        failedImage = openImageFromFilePath(editorLocation + "ObjectIcons/failed.png");
    }


    // Editor location should be the current folder
    private static final String editorLocation;
    static {
        String editorLocation1;
        try {
            editorLocation1 = new File(SupremeMain.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent() + "/";
            if (!Files.exists(Path.of(editorLocation1 + "/src"))) {
                editorLocation1 = new File(SupremeMain.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getParent() + "/";
            }
            logger.debug(editorLocation1);
        } catch (URISyntaxException ignored) {
            editorLocation1 = "";
        }
        editorLocation = editorLocation1;
    }
    public static String getEditorLocation() {
        return editorLocation;
    }


    public static void readWOGdirs() throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        PropertiesOpener defaultHandler = new PropertiesOpener();
        File properties = new File(editorLocation + "properties.xml");
        if (!properties.exists()) {
            oldWOG1dir = "";
            newWOG1dir = "";
            wog2dir = "";
            return;
        }
        saxParser.parse(properties, defaultHandler);
    }


    public static Image openImageFromFilePath(String file_path) throws IOException {
        InputStream inputStream = new FileInputStream(file_path);
        Image image = new Image(inputStream);
        inputStream.close();
        return image;
    }


    public static Image getIcon(String imagePath) {
        try {
            InputStream inputStream = new FileInputStream(editorLocation + imagePath);
            Image iconImage = new Image(inputStream);
            inputStream.close();
            return iconImage;
        } catch (IOException e) {
            logger.error("", e);
            return null;
        }
    }

    private static String bytesToString(byte[] input) {
        return new String(input, StandardCharsets.UTF_8);
    }


    public static void supremeAddToList(ArrayList<EditorObject> list, EditorObject object) {
        list.add(object);
        for (EditorObject child : object.getChildren()) {
            supremeAddToList(list, child);
        }
    }


    public static _Level openLevel(String levelName, GameVersion version) throws ParserConfigurationException, SAXException, IOException {

        LevelManager.setLevel(null);

        ArrayList<EditorObject> scene = new ArrayList<>();
        ArrayList<EditorObject> level = new ArrayList<>();
        ArrayList<EditorObject> resrc = new ArrayList<>();
        ArrayList<EditorObject> addin = new ArrayList<>();
        ArrayList<EditorObject> text = new ArrayList<>();

        SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
        LevelOpener defaultHandler = new LevelOpener(scene, level, resrc, addin, text, version);

        switch (version) {

            case VERSION_WOG1_OLD -> {

                String levelDir = oldWOG1dir + "/res/levels/" + levelName + "/" + levelName;

                File sceneF = new File(levelDir + ".scene.bin");
                saxParser.parse(new InputSource(new StringReader(bytesToString(AESBinFormat.decodeFile(sceneF)))), defaultHandler);

                File levelF = new File(levelDir + ".level.bin");
                saxParser.parse(new InputSource(new StringReader(bytesToString(AESBinFormat.decodeFile(levelF)))), defaultHandler);

                File resrcF = new File(levelDir + ".resrc.bin");
                saxParser.parse(new InputSource(new StringReader(bytesToString(AESBinFormat.decodeFile(resrcF)))), defaultHandler);

                File addinF = new File(levelDir + ".addin.xml");
                if (addinF.exists()) saxParser.parse(addinF, defaultHandler);
                else supremeAddToList(addin, BlankObjectGenerator.generateBlankAddinObject(levelName, version));

                File textF = new File(levelDir + ".text.xml");
                if (textF.exists()) saxParser.parse(textF, defaultHandler);
                else supremeAddToList(text, BlankObjectGenerator.generateBlankTextObject(version));

                return new WOG1Level(scene, level, resrc, addin, text, version);

            }

            case VERSION_WOG1_NEW -> {

                String levelDir = newWOG1dir + "/res/levels/" + levelName + "/" + levelName;

                File sceneF = new File(levelDir + ".scene");
                saxParser.parse(sceneF, defaultHandler);

                File levelF = new File(levelDir + ".level");
                saxParser.parse(levelF, defaultHandler);

                File resrcF = new File(levelDir + ".resrc");
                saxParser.parse(resrcF, defaultHandler);

                File addinF = new File(levelDir + ".addin.xml");
                if (addinF.exists()) saxParser.parse(addinF, defaultHandler);
                else supremeAddToList(addin, BlankObjectGenerator.generateBlankAddinObject(levelName, version));

                File textF = new File(levelDir + ".text.xml");
                if (textF.exists()) saxParser.parse(textF, defaultHandler);
                else supremeAddToList(text, BlankObjectGenerator.generateBlankTextObject(version));

                return new WOG1Level(scene, level, resrc, addin, text, version);

            }

            case VERSION_WOG2 -> {

                String contents = Files.readString(Path.of(wog2dir + "/res/levels/" + levelName + ".wog2"));
                EditorObject levelObject;

                try {
                    levelObject = ObjectGOOParser.read(_2_Level.class, contents);
                } catch (Exception e) {
                    throw new IOException("Failed to deserialize level " + levelName);
                }

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

                File addinF = new File(wog2dir + "/res/levels/" + levelName + ".addin.xml");
                if (addinF.exists()) saxParser.parse(addinF, defaultHandler);
                else supremeAddToList(addin, BlankObjectGenerator.generateBlankAddinObject(levelName, version));

                return new WOG2Level(objects, addin);

            }

        }

        return null;

    }


    public static WOG2Level openWog2Item(String itemId) throws ParserConfigurationException, SAXException, IOException {

        LevelManager.setLevel(null);

        ArrayList<EditorObject> addin = new ArrayList<>();
        _2_Item item = ItemManager.getItem(itemId);

        item.getParent().getChildren().remove(item);
        item.setParent(null);
        
        ArrayList<EditorObject> objects = new ArrayList<>();
        Stack<EditorObject> toAdd = new Stack<>();
        toAdd.push(item);
        while (!toAdd.isEmpty()) {
            EditorObject thisObject = toAdd.pop();
            System.out.println(thisObject);
            objects.add(thisObject);
            for (EditorObject child : thisObject.getChildren()) {
                toAdd.push(child);
            }

        }

        supremeAddToList(addin, BlankObjectGenerator.generateBlankAddinObject(itemId, GameVersion.VERSION_WOG2));

        return new WOG2Level(objects, addin);
    }


    public static _Ball openBall(String ballName, GameVersion version) throws ParserConfigurationException, SAXException, IOException {

        /* Make sure a ball from an invalid version isn't being opened (possible because of properties.xml) */
        if (version == GameVersion.VERSION_WOG1_OLD && oldWOG1dir.isEmpty() ||
            version == GameVersion.VERSION_WOG1_NEW && newWOG1dir.isEmpty()) {
            return null;
        }

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        ArrayList<EditorObject> objects = new ArrayList<>();
        ArrayList<EditorObject> resources = new ArrayList<>();

        DefaultXmlOpener defaultHandler = new DefaultXmlOpener(objects, resources, version);
        if (version == GameVersion.VERSION_WOG1_OLD) {
            File ballFile = new File(oldWOG1dir + "/res/balls/" + ballName + "/balls.xml.bin");
            File ballFileR = new File(oldWOG1dir + "/res/balls/" + ballName + "/resources.xml.bin");
            defaultHandler.setMode(Mode.OBJECT);
            saxParser.parse(new InputSource(new StringReader(bytesToString(AESBinFormat.decodeFile(ballFile)))), defaultHandler);
            defaultHandler.setMode(Mode.RESOURCE);
            saxParser.parse(new InputSource(new StringReader(bytesToString(AESBinFormat.decodeFile(ballFileR)))), defaultHandler);
            return new _Ball(objects, resources);
        } else if (version == GameVersion.VERSION_WOG1_NEW) {
            File ballFile = new File(newWOG1dir + "/res/balls/" + ballName + "/balls.xml");
            File ballFileR = new File(newWOG1dir + "/res/balls/" + ballName + "/resources.xml");
            defaultHandler.setMode(Mode.OBJECT);
            saxParser.parse(ballFile, defaultHandler);
            defaultHandler.setMode(Mode.RESOURCE);
            saxParser.parse(ballFileR, defaultHandler);
            return new _Ball(objects, resources);
        } else {
            return null;
        }
    }


    public static ArrayList<EditorObject> openResources(GameVersion version) throws ParserConfigurationException, SAXException, IOException {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        ArrayList<EditorObject> resources = new ArrayList<>();

        DefaultXmlOpener defaultHandler = new DefaultXmlOpener(null, resources, version);
        defaultHandler.setMode(Mode.RESOURCE);
        File ballFile;
        String dir;
        if (version == GameVersion.VERSION_WOG1_OLD) {
            dir = oldWOG1dir;
            ballFile = new File(dir + "/properties/resources.xml.bin");
            saxParser.parse(new InputSource(new StringReader(bytesToString(AESBinFormat.decodeFile(ballFile)))), defaultHandler);
        } else if (version == GameVersion.VERSION_WOG1_NEW) {
            dir = newWOG1dir;
            ballFile = new File(dir + "/properties/resources.xml");
            saxParser.parse(ballFile, defaultHandler);
        } else if (version == GameVersion.VERSION_WOG2) {
            dir = wog2dir;
            ballFile = new File(dir + "/res/properties/resources.xml");
            try {
                String text = Files.readString(ballFile.toPath());
                text = text.substring(22);
                text = "<?xml version = '1.0' encoding = 'UTF-8'?>" + text;
                saxParser.parse(new InputSource(new StringReader(text)), defaultHandler);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("", e);
            }
            ballFile = new File(dir + "/res/items/images/_resources.xml");
            saxParser.parse(ballFile, defaultHandler);
            ballFile = new File(dir + "/res/environments/images/_resources.xml");
            saxParser.parse(ballFile, defaultHandler);
            ballFile = new File(dir + "/res/terrain/images/_resources.xml");
            saxParser.parse(ballFile, defaultHandler);
        }
        
        return resources;
    }


    public static ArrayList<EditorObject> openWog2ResourceFile(String path) throws ParserConfigurationException, SAXException, IOException {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        ArrayList<EditorObject> resources = new ArrayList<>();

        DefaultXmlOpener defaultHandler = new DefaultXmlOpener(null, resources, GameVersion.VERSION_WOG2);
        defaultHandler.setMode(Mode.RESOURCE);
        
        File file = new File(wog2dir + path);
        saxParser.parse(file, defaultHandler);
        
        return resources;
    }


    public static ArrayList<EditorObject> openParticles(GameVersion version) throws ParserConfigurationException, SAXException, IOException {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        ArrayList<EditorObject> objects = new ArrayList<>();

        DefaultXmlOpener defaultHandler = new DefaultXmlOpener(objects, null, version);
        defaultHandler.setMode(Mode.OBJECT);

        if (version == GameVersion.VERSION_WOG1_OLD && !oldWOG1dir.isEmpty()) {
            File ballFile = new File(oldWOG1dir + "/properties/fx.xml.bin");
            saxParser.parse(new InputSource(new StringReader(bytesToString(AESBinFormat.decodeFile(ballFile)))), defaultHandler);
        } else if (version == GameVersion.VERSION_WOG1_NEW && !newWOG1dir.isEmpty()) {
            File ballFile2 = new File(newWOG1dir + "/properties/fx.xml");
            saxParser.parse(ballFile2, defaultHandler);
        }
        return objects;
    }


    public static ArrayList<EditorObject> openText(GameVersion version) throws ParserConfigurationException, SAXException, IOException {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        ArrayList<EditorObject> objects = new ArrayList<>();

        DefaultXmlOpener defaultHandler = new DefaultXmlOpener(objects, null, version);
        defaultHandler.setMode(Mode.OBJECT);
        
        if (version == GameVersion.VERSION_WOG1_OLD && !oldWOG1dir.isEmpty()) {
            File ballFile = new File(oldWOG1dir + "/properties/text.xml.bin");
            byte[] bytes = AESBinFormat.decodeFile(ballFile);
            // If the file starts with EF BB BF, strip these three bytes (not sure why it does this)
            if (bytes[0] == (byte)0xEF && bytes[1] == (byte)0xBB && bytes[2] == (byte)0xBF) {
                byte[] newBytes = new byte[bytes.length - 3];
                System.arraycopy(bytes, 3, newBytes, 0, bytes.length - 3);
                bytes = newBytes;
            }
            String stringBytes = bytesToString(bytes);
            saxParser.parse(new InputSource(new StringReader(stringBytes)), defaultHandler);
        } else if (version == GameVersion.VERSION_WOG1_NEW && !newWOG1dir.isEmpty()) {
            File ballFile2 = new File(newWOG1dir + "/properties/text.xml");
            saxParser.parse(ballFile2, defaultHandler);
        }
        return objects;
    }


    public static ArrayList<EditorObject> openMaterials(GameVersion version) throws ParserConfigurationException, SAXException, IOException {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        ArrayList<EditorObject> objects = new ArrayList<>();

        DefaultXmlOpener defaultHandler = new DefaultXmlOpener(objects, null, version);
        defaultHandler.setMode(Mode.OBJECT);
        
        if (version == GameVersion.VERSION_WOG1_OLD && !oldWOG1dir.isEmpty()) {
            File ballFile = new File(oldWOG1dir + "/properties/materials.xml.bin");
            byte[] bytes = AESBinFormat.decodeFile(ballFile);
            String stringBytes = bytesToString(bytes);
            saxParser.parse(new InputSource(new StringReader(stringBytes)), defaultHandler);
        } else if (version == GameVersion.VERSION_WOG1_NEW && !newWOG1dir.isEmpty()) {
            File ballFile2 = new File(newWOG1dir + "/properties/materials.xml");
            saxParser.parse(ballFile2, defaultHandler);
        }
        return objects;
    }


    public static void saveProperties() throws IOException {
        StringBuilder export = new StringBuilder("<properties>\n" +
                "\n<oldWOG filepath=\"" + oldWOG1dir + "\"/>" +
                "\n<newWOG filepath=\"" + oldWOG1dir + "\"/>" +
                "\n<WOG2 filepath=\"" + wog2dir + "\"/>" +
                "\n<gooBallPalette>\n");
        for (int i = 0; i < PaletteManager.getPaletteBalls().size(); i++) {
            export.append("\t<Ball ball=\"").append(PaletteManager.getPaletteBalls().get(i)).append("\" version=\"").append(PaletteManager.getPaletteVersions().get(i).toString()).append("\"/>\n");
        }
        export.append("</gooBallPalette>\n</properties>");

        Files.write(Paths.get(editorLocation + "properties.xml"), Collections.singleton(export.toString()), StandardCharsets.UTF_8);
    }


    public static _2Ball open2Ball(String ballName, GameVersion version) throws ParserConfigurationException, SAXException, IOException {

        String contents = Files.readString(Path.of(wog2dir + "/res/balls/" + ballName + "/ball.wog2"));

        EditorObject levelObject = ObjectGOOParser.read(_2_Ball.class, contents);
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

        ArrayList<EditorObject> resources = new ArrayList<>();

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();

        DefaultXmlOpener defaultHandler = new DefaultXmlOpener(objects, resources, GameVersion.VERSION_WOG2);

        File ballFileR = new File(wog2dir + "/res/balls/" + ballName + "/resources.xml");
        defaultHandler.setMode(Mode.RESOURCE);
        saxParser.parse(ballFileR, defaultHandler);

        return new _2Ball(objects, resources);

    }


    public static FileChooser.ExtensionFilter get2ExtensionFilter() {

        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            return new FileChooser.ExtensionFilter("World of Goo 2 executable", "World Of Goo 2.exe");
        } else if (os.contains("mac")) {
            return new FileChooser.ExtensionFilter("World of Goo 2 executable", "WorldOfGoo2.app");
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            return new FileChooser.ExtensionFilter("World of Goo 2 executable", "WorldOfGoo2");
        } else {
            throw new RuntimeException("Unsupported OS: " + os);
        }

    }

}