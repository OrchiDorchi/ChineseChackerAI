package Checker;

import static Checker.Main.TILE_SIZE;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;

public class Piece extends StackPane {
    PieceType type;
    int x, y;

    public Piece(PieceType type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
        move(x, y);

        Ellipse ellipse = new Ellipse(TILE_SIZE * 0.3125, TILE_SIZE * 0.26);
        ellipse.setFill(type == PieceType.LIGHT
                ? Color.valueOf("#ffffff")
                : Color.valueOf("#000000"));

        ellipse.setStroke(Color.BLACK);
        ellipse.setStrokeWidth(TILE_SIZE * 0.03);

        ellipse.setTranslateX((TILE_SIZE - TILE_SIZE * 0.3125 * 2) / 2);
        ellipse.setTranslateY((TILE_SIZE - TILE_SIZE * 0.26 * 2) / 2);

        getChildren().addAll(ellipse);
    }

    public void move(int x, int y) {
        // Main.board[this.x][this.y].piece = null;
        this.x = x;
        this.y = y;
        // Main.board[this.x][this.y].piece = this;
        relocate(x * TILE_SIZE, y * TILE_SIZE);
    }

    public void shadowMove(int x, int y) {
        this.x = x;
        this.y = y;
    }

}
