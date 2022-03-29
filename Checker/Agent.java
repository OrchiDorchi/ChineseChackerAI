package Checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class Agent {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public void makeMove() {
        // for (int i = 0; i < 8; i++) {
        // for (int j = 0; j < 8; j++) {
        // System.out.print(Main.board[j][i].piece + " ");
        // }
        // System.out.println();
        // }
        HashMap<Piece, Coordinate> initialWhites = new HashMap<>();
        HashMap<Piece, Coordinate> initialBlacks = new HashMap<>();
        for (Piece piece : allPieces(Main.board, PieceType.DARK)) {
            initialBlacks.put(piece, new Coordinate(piece.x, piece.y));
        }
        for (Piece piece : allPieces(Main.board, PieceType.LIGHT)) {
            initialWhites.put(piece, new Coordinate(piece.x, piece.y));
        }
        HashMap<String, Object> map = minimax(Main.board, 2, true, null, initialWhites, initialBlacks);
        try {
            Thread.sleep(100);
        } catch (Exception e) {
            // TODO: handle exception
        }
        // for (Piece piece : allPieces(Main.board, PieceType.DARK)) {
        // Coordinate c = initialBlacks.get(piece);
        // piece.move(c.x, c.y);
        // }
        // for (Piece piece : allPieces(Main.board, PieceType.LIGHT)) {
        // Coordinate c = initialWhites.get(piece);
        // System.out.println("x: " + c.x + "y: " + c.y);
        // piece.move(c.x, c.y);
        // }
        Piece piece = (Piece) map.get("piece");
        Coordinate c = (Coordinate) map.get("moveCoordinate");
        System.out.println(piece);
        System.out.println(c);
        Main.board[piece.x][piece.y].piece = null;
        piece.move(c.x, c.y);
        Main.board[c.x][c.y].piece = piece;
        // Main.text.setText("zomzom");
        // makeRandomMove();
    }

    private int evaluateBoard(Tile[][] board) {
        int score = 0;
        if (Main.checkGameWinner(board) == 1) {
            score -= 5000;
        } else if (Main.checkGameWinner(board) == 0) {
            score += 5000;
        }
        for (Piece white : allPieces(board, PieceType.LIGHT)) {
            score -= 8 - white.x;
            score -= 8 - white.y;
            if (white.x < 3 && white.y < 3) {
                score -= 20;
            }
        }
        for (Piece black : allPieces(board, PieceType.DARK)) {
            score += black.x;
            score += black.y;
            if (black.x > 4 && black.y > 4) {
                score += 20;
            }
        }

        int blackCountInRow = 0;
        int blackCountInColumn = 0;
        for (int j = 0; j < 8; j++) {
            if (board[j][7].piece != null && board[j][7].piece.type == PieceType.DARK) {
                blackCountInRow++;
            }
            if (board[7][j].piece != null && board[7][j].piece.type == PieceType.DARK) {
                blackCountInColumn++;
            }
        }
        if (blackCountInRow > 3) {
            score -= 50000;
        }
        if (blackCountInColumn > 3) {
            score -= 50000;
        }

        int whiteCountInRow = 0;
        int whiteCountInColumn = 0;

        for (int j = 0; j < 8; j++) {
            if (board[j][7].piece != null && board[j][7].piece.type == PieceType.LIGHT) {
                whiteCountInRow++;
            }
            if (board[7][j].piece != null && board[7][j].piece.type == PieceType.LIGHT) {
                whiteCountInColumn++;
            }
        }
        if (whiteCountInRow > 3) {
            score += 50000;
        }
        if (whiteCountInColumn > 3) {
            score += 50000;
        }

        return score;
    }

    private HashMap<String, Object> minimax(Tile[][] board, int depth, boolean isMax, Piece lastPiece,
            HashMap<Piece, Coordinate> initialWhites, HashMap<Piece, Coordinate> initialBlacks) {
        HashMap<String, Object> map = new HashMap<>();
        if (depth == 0 || Main.checkGameWinner(board) != -1) {
            // System.out.println("+============O DEPTH");
            // try {
            // Thread.sleep(100);
            // } catch (InterruptedException e) {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }

            // System.out.println(ANSI_PURPLE + "PIECE: " + lastPiece + ANSI_RESET);
            int score = evaluateBoard(board);
            System.out.println(ANSI_RED + "Score : " + score + ANSI_RESET);
            map.put("score", score);
            map.put("piece", lastPiece);
            // for (Piece piece : allPieces(Main.board, PieceType.DARK)) {
            // Coordinate c = initialBlacks.get(piece);
            // piece.move(c.x, c.y);
            // }
            for (Piece piece : allPieces(Main.board, PieceType.LIGHT)) {
                Coordinate c = initialWhites.get(piece);
                Main.board[piece.x][piece.y].piece = null;
                piece.shadowMove(c.x, c.y);
                Main.board[c.x][c.y].piece = piece;
            }
            return map;
        }

        if (isMax) {
            int maxScore = Integer.MIN_VALUE;
            Piece bestPiece = null;
            Coordinate bestMove = null;
            for (Piece black : allPieces(board, PieceType.DARK)) {
                for (Piece piece : allPieces(Main.board, PieceType.DARK)) {
                    initialBlacks.put(piece, new Coordinate(piece.x, piece.y));
                }
                for (Coordinate move : Main.allPossibleMoves(board, black, false, false, false, black.x, black.y)) {
                    // System.out.println(ANSI_BLUE + "Moving: " + black + " " + move.x + " " +
                    // move.y + ANSI_RESET);
                    Main.board[black.x][black.y].piece = null;
                    black.shadowMove(move.x, move.y);
                    Main.board[move.x][move.y].piece = black;

                    int score = (int) minimax(board, depth - 1, false, black, initialWhites, initialBlacks)
                            .get("score");
                    maxScore = Math.max(maxScore, score);
                    if (maxScore == score) {
                        bestMove = move;
                        bestPiece = black;
                    }

                }
                for (Piece piece : allPieces(Main.board, PieceType.DARK)) {
                    Coordinate c = initialBlacks.get(piece);
                    Main.board[piece.x][piece.y].piece = null;
                    piece.shadowMove(c.x, c.y);
                    Main.board[c.x][c.y].piece = piece;
                }
            }
            map.put("piece", bestPiece);
            map.put("moveCoordinate", bestMove);
            map.put("score", maxScore);
            return map;
        } else {
            int minScore = Integer.MAX_VALUE;
            Piece bestPiece = null;
            Coordinate bestMove = null;
            for (Piece white : allPieces(board, PieceType.LIGHT)) {
                // for (Piece piece : allPieces(Main.board, PieceType.DARK)) {
                // initialBlacks.put(piece, new Coordinate(piece.x, piece.y));
                // }
                for (Piece piece : allPieces(Main.board, PieceType.LIGHT)) {
                    initialWhites.put(piece, new Coordinate(piece.x, piece.y));
                }
                for (Coordinate move : Main.allPossibleMoves(board, white, false, false, false, white.x, white.y)) {
                    // System.out.println(ANSI_RED + "Moving: " + white + " " + move.x + " " +
                    // move.y + ANSI_RESET);
                    Main.board[white.x][white.y].piece = null;
                    white.shadowMove(move.x, move.y);
                    Main.board[move.x][move.y].piece = white;
                    int score = (int) minimax(board, depth - 1, true, white, initialWhites, initialBlacks).get("score");
                    minScore = Math.min(minScore, score);
                    if (minScore == score) {
                        bestMove = move;
                        bestPiece = white;
                    }
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            map.put("piece", bestPiece);
            map.put("moveCoordinate", bestMove);
            map.put("score", minScore);
            return map;
        }
    }

    private void makeRandomMove() {
        try {
            Thread.sleep(100);
        } catch (Exception e) {
            // TODO: handle exception
        }
        Main.resetTiles();
        Tile[][] board = Main.board;
        ArrayList<Piece> pieces = allPieces(board, PieceType.DARK);
        Random r = new Random();
        Piece randomPiece = null;
        System.out.println(evaluateBoard(board));
        while (randomPiece == null
                || Main.allPossibleMoves(board, randomPiece, false, false, false,
                        randomPiece.x,
                        randomPiece.y).isEmpty()) {
            randomPiece = pieces.get(r.nextInt(pieces.size()));
        }
        List<Coordinate> allMoves = Main.allPossibleMoves(board, randomPiece, false,
                false, false,
                randomPiece.x, randomPiece.y);
        for (Coordinate c : allMoves) {
            // System.out.println(c);
            board[c.x][c.y].changeType(TileType.MOVEABLE);
        }
        Coordinate randomMove = allMoves.get(r.nextInt(allMoves.size()));
        board[randomPiece.x][randomPiece.y].piece = null;
        randomPiece.move(randomMove.x, randomMove.y);
        board[randomMove.x][randomMove.y].piece = randomPiece;

    }

    ArrayList<Piece> allPieces(Tile[][] board, PieceType type) {
        ArrayList<Piece> pieces = new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j].piece != null && board[i][j].piece.type == type) {
                    pieces.add(board[i][j].piece);
                }
            }
        }
        return pieces;
    }
}
