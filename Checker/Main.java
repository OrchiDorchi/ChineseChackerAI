package Checker;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {
    public static final int TILE_SIZE = 100;
    public static final int WIDTH = 8;
    public static final int HEIGHT = 8;
    public static int turn = 1;
    public static Tile[][] board = new Tile[WIDTH][HEIGHT];
    public static Text text;
    private Piece clickedPiece = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Group root = new Group();
        Scene scene = new Scene(root, Color.LIGHTBLUE);
        Group tiles = new Group();
        Group pieces = new Group();
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                Tile tile = new Tile((x + y) % 2 == 0 ? TileType.LIGHT : TileType.DARK, x, y);
                board[x][y] = tile;
                tile.setOnMousePressed(e -> {
                    if (tile.type == TileType.MOVEABLE) {
                        // System.out.println("oldX: " + toBoard(clickedPiece.getOldX()));
                        // System.out.println("oldY: " + toBoard(clickedPiece.getOldY()));
                        board[clickedPiece.x][clickedPiece.y].piece = null;
                        clickedPiece.move(tile.coo_x, tile.coo_y);
                        resetTiles();
                        board[tile.coo_x][tile.coo_y].piece = clickedPiece;
                        turn = 0;
                    }
                });
                tiles.getChildren().add(tile);
                Piece piece = null;
                if (y < 3 && x < 3) {
                    piece = makePiece(PieceType.DARK, x, y);
                }
                if (y > 4 && x > 4) {
                    piece = makePiece(PieceType.LIGHT, x, y);
                }
                if (piece != null) {
                    tile.piece = piece;
                    pieces.getChildren().add(piece);
                }
            }
        }

        text = new Text();
        text.setText("YOUR TURN");
        text.setFont(Font.font("Verdana", 30));
        text.setX(50);
        text.setY((double) HEIGHT * TILE_SIZE + 40);
        root.getChildren().addAll(tiles, pieces, text);

        primaryStage.setWidth(WIDTH * TILE_SIZE);
        primaryStage.setHeight(HEIGHT * TILE_SIZE + 200);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Checker");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.show();
        // Stage stage = new Stage();
        // stage.show();
    }

    public static void resetTiles() {
        for (int coo_y = 0; coo_y < HEIGHT; coo_y++) {
            for (int coo_x = 0; coo_x < WIDTH; coo_x++) {
                board[coo_x][coo_y].changeType((coo_x + coo_y) % 2 == 0 ? TileType.LIGHT : TileType.DARK);
            }
        }
    }

    private Piece makePiece(PieceType type, int x, int y) {
        Piece piece = new Piece(type, x, y);
        if (type == PieceType.LIGHT) {
            piece.setOnMousePressed(e -> {
                if (turn == 1) {
                    resetTiles();
                    clickedPiece = piece;
                    for (Coordinate c : allPossibleMoves(board, piece, false, false, false, piece.x,
                            piece.y)) {
                        board[c.x][c.y].changeType(TileType.MOVEABLE);
                    }
                }
            });
        }
        return piece;
    }

    static void agentTurn() {
        if (checkGameWinner(board) != -1) {
            return;
        }
        new Agent().makeMove();
        text.setText("YOUR TURN");
        turn = 1;
    }

    public static ArrayList<Coordinate> allPossibleMoves(Tile[][] board, Piece piece, boolean jumpedBefore,
            boolean jumpedX,
            boolean jumpedY,
            int x, int y) {
        ArrayList<Coordinate> moves = new ArrayList<Coordinate>();
        try {
            if (!jumpedBefore) {

                if (x + piece.type.moveDir >= 0 && x + piece.type.moveDir <= 7
                        && board[x + piece.type.moveDir][y].piece == null) {
                    moves.add(new Coordinate(x + piece.type.moveDir, y));
                } else {
                    moves.addAll(allPossibleMoves(board, piece, true, true, false, x + 2 * piece.type.moveDir, y));
                }
                if (y + piece.type.moveDir >= 0 && y + piece.type.moveDir <= 7
                        && board[x][y + piece.type.moveDir].piece == null) {
                    moves.add(new Coordinate(x, y + piece.type.moveDir));
                } else {
                    moves.addAll(allPossibleMoves(board, piece, true, false, true, x, y + 2 * piece.type.moveDir));
                }
            } else {
                if (x >= 0 && x <= board.length && y >= 0 && y <= board[0].length) {
                    if (((jumpedX && board[x - piece.type.moveDir][y].piece != null)
                            || (jumpedY && board[x][y - piece.type.moveDir].piece != null))
                            && board[x][y].piece == null) {
                        moves.add(new Coordinate(x, y));
                        moves.addAll(
                                allPossibleMoves(board, piece, true, false, true, x, y + 2 * piece.type.moveDir));
                        moves.addAll(
                                allPossibleMoves(board, piece, true, true, false, x + 2 * piece.type.moveDir, y));
                    }
                }

            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("allpossiblemoves: " + e);
        }

        return moves;
    }

    public static int checkGameWinner(Tile[][] board) {
        for (int i = 0; i < 3; i++) {
            try {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j].piece.type != PieceType.LIGHT) {
                        throw new Exception("White didn't win");
                    }
                    if (i == 2 && j == 2) {
                        System.out.println("White wins!!!!!!");
                        return 1;
                    }
                }
            } catch (Exception e) {
                break;
            }
        }
        for (int i = 5; i < 8; i++) {
            try {
                for (int j = 5; j < 8; j++) {
                    if (board[i][j].piece.type != PieceType.DARK) {
                        throw new Exception("Black didn't win");
                    }
                    if (i == 2 && j == 2) {
                        System.out.println("Agent wins!!!!!!");
                        return 0;
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                if (turn == 0) {
                    text.setText("AI THINKING");
                    agentTurn();
                }
            }
        }, 0, 500);
        launch(args);
    }

}