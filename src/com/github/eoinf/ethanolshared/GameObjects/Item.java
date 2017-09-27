package com.github.eoinf.ethanolshared.GameObjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Item extends GameObject {

    public ItemType type;

    public Item(ItemType type, String textureName, Color colour, Vector2 position) {
        super(textureName, colour, position);
        this.type = type;
    }

    public Item(ItemType type) {
        this.type = type;
    }

    // Default constructor for json deserialization
    public Item() {
        this.type = ItemType.INVALID;
    }
}
