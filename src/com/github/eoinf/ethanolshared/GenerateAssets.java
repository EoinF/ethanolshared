package com.github.eoinf.ethanolshared;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class GenerateAssets {

    public static void main(String[] args) {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        TexturePacker.process(settings, "images", "assets", "game");
    }
}