package com.nicktoony.engine.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

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
        FileHandle file = Gdx.files.internal("characters");
        if (!file.exists() || !file.isDirectory())  {
            System.out.println(DEBUG + "Invalid assets structure for character skins.");
            return;
        }

//        for (FileHandle innerFile : file.list())  {
//            if (innerFile.exists() && innerFile.isDirectory())  {
//                FileHandle charactersFile = Gdx.files.internal(innerFile.path() + "/characters.xml");
//                if (charactersFile.exists())    {
//                    addCharacterCategory(charactersFile, innerFile);
//                }
//            }
//        }

        // Temporary
        addCharacterCategory(Gdx.files.internal("characters/nicktoony/characters.xml"),
                Gdx.files.internal("characters/nicktoony/"));
    }

    private void addCharacterCategory(FileHandle charactersFile, FileHandle packFile) {
        XmlReader reader = new XmlReader();
        XmlReader.Element root = null;
        try {
            root = reader.parse(charactersFile);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(DEBUG + "Error reading XML: " + charactersFile.path());
            return;
        }

        Array<XmlReader.Element> categories = root.getChildrenByName("category");
        for (XmlReader.Element category : categories) {
            String name = category.getAttribute("name");

            if (packFile.exists())    {
                System.out.println(DEBUG + "Added character category: " + name);

                CharacterCategory characterCategory = new CharacterCategory(name, packFile);
                characterCategories.add(characterCategory);
                addCharacterSkins(characterCategory, category);
            }   else    {
                System.out.println(DEBUG + "Missing character skin directory " + packFile.path());
            }
        }
    }

    private void addCharacterSkins(CharacterCategory category, XmlReader.Element categoryXML)   {
        Array<XmlReader.Element> skins = categoryXML.getChildrenByName("skin");
        for (XmlReader.Element skin : skins)    {
            String name = skin.getText();
            FileHandle file = Gdx.files.internal(category.getFolder().path() + "/" + skin.getText());

            if (file.exists() && file.isDirectory())   {
                category.getSkins().add(new CharacterSkin(file.path(), category.getFolder().path(), name));
                System.out.println(DEBUG + "Added skin: " + file.path());
            }   else    {
                System.out.println(DEBUG + "Missing character skin file " + file.path());
            }
        }
    }

    public void dispose() {
        for (CharacterCategory category : characterCategories) {
            for (CharacterSkin skin : category.skins) {
                skin.dispose();
            }
        }
        characterCategories.clear();
        staticInstance = null;
    }

    public class CharacterCategory  {
        private String name;
        private FileHandle folder;
        private List<CharacterSkin> skins = new ArrayList<CharacterSkin>();

        public CharacterCategory(String name, FileHandle folder)   {
            this.name = name;
            this.folder = folder;
        }

        public String getName() {
            return name;
        }

        public FileHandle getFolder() {
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
