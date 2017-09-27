package com.github.eoinf.ethanolshared;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.github.eoinf.ethanolshared.GameObjects.GameObject;
import com.github.eoinf.ethanolshared.GameObjects.Prop;
import com.github.eoinf.ethanolshared.GameObjects.TerrainShape;
import com.github.eoinf.ethanolshared.GameObjects.Entity;
import com.github.eoinf.ethanolshared.GameObjects.Item;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

public class Config {

    public String[] textureNames;
    public Texture[] textureValues;
    private Dictionary<String, Texture> textureMap;

    private String[] spriteNames;
    public String[] baseEntityNames;
    public String[] baseItemNames;

    public TextureAtlas atlas;

    public String atlasFileName;
    public String texturesFilePath;

    //Default Constructor for json deserialization
    public Config() {}

    public static Config loadConfig() {
        FileHandle file = Gdx.files.internal("ethanol.config");
        Json json = new Json();
        Config config = json.fromJson(Config.class, file);

        //
        //Load the texture atlas for each sprite
        //
        config.atlas = new TextureAtlas(Gdx.files.internal(config.atlasFileName));
        TextureAtlas.AtlasRegion[] atlasRegionList = config.atlas.getRegions().toArray(TextureAtlas.AtlasRegion.class);
        config.spriteNames = new String[atlasRegionList.length];

        ArrayList<String> idleSpriteNamesList = new ArrayList<>();
        ArrayList<String> activeSpriteNamesList = new ArrayList<>();
        for (int i = 0; i < atlasRegionList.length; i++) {
            String regionName = atlasRegionList[i].name;
            config.spriteNames[i] = regionName;
            if (regionName.endsWith("/Idle")) {
                //Record this sprite, but remove the "/Idle" suffix and the "Entity/" prefix
                idleSpriteNamesList.add(regionName.substring(7, regionName.length() - 5));
            }
            if (regionName.endsWith("/Active")) {
                //Record this sprite, but remove the "/Active" suffix and the "Item/" prefix
                activeSpriteNamesList.add(regionName.substring(5, regionName.length() - 7));
            }
        }
        config.baseEntityNames = idleSpriteNamesList.toArray(new String[idleSpriteNamesList.size()]);
        config.baseItemNames = activeSpriteNamesList.toArray(new String[activeSpriteNamesList.size()]);

        //
        //Get the list of texture files and their names
        //
        FileHandle[] textureFiles = Gdx.files.internal(config.texturesFilePath).list();
        config.textureNames = new String[textureFiles.length];
        config.textureValues = new Texture[textureFiles.length];
        for (int i = 0; i < textureFiles.length; i++) {
            config.textureNames[i] = textureFiles[i].nameWithoutExtension();
            config.textureValues[i] = new Texture(textureFiles[i]);
        }

        config.textureMap = new Hashtable<>();
        //
        //Create a dictionary of string to texture mappings
        //
        for (int i = 0; i < config.textureNames.length; i++) {
            config.textureMap.put(config.textureNames[i], config.textureValues[i]);
        }

        return config;
    }

    public void dispose() {
        atlas.dispose();
    }

    public TextureRegion getTexture(String textureName, Class<?> type) {
        if (!GameObject.class.isAssignableFrom(type)) {
            throw new RuntimeException("Tried to fetch texture for non game object class: " + type);
        }
        TextureRegion r;

        String fullName;

        if (type == Entity.class) {
            if (textureName == null) {
                textureName = baseEntityNames[0];
            }
            fullName = "Entity/" + textureName + "/Idle";
            r = atlas.findRegion(fullName);
        }
        else if (type == Item.class) {
            if (textureName == null) {
                textureName = baseItemNames[0];
            }
            fullName = "Item/" + textureName + "/Active";
            r = atlas.findRegion(fullName);
        }
        else if (type == Prop.class) {
            fullName = "Prop/" + textureName + "/Active";
            r = atlas.findRegion(fullName);
        }
        else if (type == TerrainShape.class) {
            fullName = textureName;
            Texture t = fullName == null ?
                    textureMap.elements().nextElement() : // Grab the first texture in the list
                    textureMap.get(fullName); // Grab the given texture

            if (t != null) {
                t.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
                r = new TextureRegion(t);
            }
            else {
                r = null;
            }
        }
        else {
            fullName = textureName;
            r = atlas.findRegion(textureName);
        }

        //If no texture was found, make a debug texture to prevent the application from crashing
        if (r == null) {
            Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pix.setColor(0xDD0000FF);
            pix.fill();
            Gdx.app.error("Resources", "Texture " + fullName + " not found!");
            r = new TextureRegion(new Texture(pix));
        }
        return r;
    }

    public String[] getTextureNames(Class<?> type) {
        if (type == Entity.class) {
            return baseEntityNames;
        } else if (type == Item.class) {
            return baseItemNames;
        } else if (type == TerrainShape.class) {
            return textureNames;
        } else {
            return textureNames;
        }
    }

    public String getDefaultTerrainTexture() {
        return textureNames[0];
    }
}
