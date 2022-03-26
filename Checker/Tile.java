package Checker;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile extends Rectangle {
    TileType type;
    Piece piece;
    int coo_x;
    int coo_y;

    public void changeType(TileType type) {
        this.type = type;
        switch (type) {
            case LIGHT:
                setFill(Color.valueOf("#f0d9b5"));
                break;
            case DARK:
                setFill(Color.valueOf("#b58863"));
                break;
            case MOVEABLE:
                setFill(Color.valueOf("#993333"));
                break;
        }
    }

    public Tile(TileType type, int x, int y) {
        setWidth(Main.TILE_SIZE);
        setHeight(Main.TILE_SIZE);
        this.type = type;
        this.coo_x = x;
        this.coo_y = y;
        this.piece = null;
        this.relocate(x * Main.TILE_SIZE, y * Main.TILE_SIZE);
        switch (type) {
            case LIGHT:
                this.setFill(Color.valueOf("#f0d9b5"));
                break;
            case DARK:
                this.setFill(Color.valueOf("#b58863"));
                break;
            case MOVEABLE:
                this.setFill(Color.valueOf("#993333"));
                break;
        }
    }
}
