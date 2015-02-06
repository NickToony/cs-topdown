package com.nick.ant.towerdefense.components;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nick on 21/09/2014.
 */
public class CharacterManager {
    private static final String DEBUG = "CharacterManager: ";
    private static CharacterManager staticInstance;

    List<CharacterCategory> characterCategories = new ArrayList<CharacterCategory>();

    public static CharacterManager getInstance()    {
        if (staticInstance == null)   {
            staticInstance = new CharacterManager();
        }
        return staticInstance;
    }

    private CharacterManager()   {
        setupCharacterList();
    }

    private void setupCharacterList() {
        File file = new File("characters");
        if (!file.exists() || !file.isDirectory())  {
            System.out.println(DEBUG + "Invalid assets structure for character skins.");
            return;
        }

        for (File innerFile : file.listFiles())  {
            if (innerFile.exists() && innerFile.isDirectory())  {
                File charactersFile = new File(innerFile.getPath() + "/characters.xml");
                if (charactersFile.exists())    {
                    addCharacterCategory(charactersFile, innerFile);
                }
            }
        }
    }

    private void addCharacterCategory(File charactersFile, File packFile) {
        XmlReader reader = new XmlReader();
        XmlReader.Element root = null;
        try {
            root = reader.parse(new FileHandle(charactersFile));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(DEBUG + "Error reading XML: " + charactersFile.getPath());
            return;
        }

        Array<XmlReader.Element> categories = root.getChildrenByName("category");
        for (XmlReader.Element category : categories) {
            String name = category.getAttribute("name");
            File folder = packFile;

            if (folder.exists())    {
                System.out.println(DEBUG + "Added character category: " + name);

                CharacterCategory characterCategory = new CharacterCategory(name, folder);
                characterCategories.add(characterCategory);
                addCharacterSkins(characterCategory, category);
            }   else    {
                System.out.println(DEBUG + "Missing character skin directory " + folder.getPath());
            }
        }
    }

    private void addCharacterSkins(CharacterCategory category, XmlReader.Element categoryXML)   {
        Array<XmlReader.Element> skins = categoryXML.getChildrenByName("skin");
        for (XmlReader.Element skin : skins)    {
            String name = skin.getText();
            File file = new File(category.getFolder().getPath() + "/" + skin.getText());

            if (file.exists() && file.isDirectory())   {
                category.getSkins().add(new CharacterSkin(file.getPath(), category.getFolder().getPath(), name));
                System.out.println(DEBUG + "Added skin: " + file.getPath());
            }   else    {
                System.out.println(DEBUG + "Missing character skin file " + file.getPath());
            }
        }
    }

    public void dispose() {
        characterCategories.clear();
        staticInstance = null;
    }

    public class CharacterCategory  {
        private String name;
        private File folder;
        private List<CharacterSkin> skins = new ArrayList<CharacterSkin>();

        public CharacterCategory(String name, File folder)   {
            this.name = name;
            this.folder = folder;
        }

        public String getName() {
            return name;
        }

        public File getFolder() {
            return folder;
        }

        public List<CharacterSkin> getSkins()   {
            return skins;
        }
    }

    public CharacterCategory getCharacterCategories(int i)   {
        return characterCategories.get(i);
    }
}
