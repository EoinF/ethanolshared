package com.github.eoinf.ethanolshared;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class GenerateAssets {

    public static void main(String[] args) {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        TexturePacker.process(settings, "images", "assets", "game");

        File[] textureFiles = new File("assets/textures").listFiles();

        String textureNames = Arrays.stream(textureFiles)
                .map(File::getName)
                // Remove the .png extension
                .map((nameWithExtension -> nameWithExtension.subSequence(0, nameWithExtension.length() - 4)))
                .collect(Collectors.joining(","));

        try (FileOutputStream out = new FileOutputStream("assets/textureNames.txt")) {
            out.write(textureNames.getBytes());
        } catch (IOException ex) {
        }

    }
}