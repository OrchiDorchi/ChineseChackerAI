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
        HashMap<String, Object> map = minimax(Main.board, 5, true, null);
        try {
            Thread.sleep(100);
        } catch (Exception e) {
            // TODO: handle exception
        }
        for (Piece piece : allPieces(Main.board, PieceType.DARK)) {
            Coordinate c = initialBlacks.get(piece);
            piece.move(c.x, c.y);
        }
        for (Piece piece : allPieces(Main.board, PieceType.LIGHT)) {
            Coordinate c = initialWhites.get(piece);
            System.out.println("x: " + c.x + "y: " + c.y);
            piece.move(c.x, c.y);
        }
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
            if (board[j][8].piece != null && board[j][8].piece.type == PieceType.DARK) {
                blackCountInRow++;
            }
            if (board[8][j].piece != null && board[8][j].piece.type == PieceType.DARK) {
                blackCountInRow++;
            }
        }
        if (blackCountInRow > 3) {
            score -= 50000;
        }

        for (int i = 0; i < 3; i++) {
            int whiteCountInRow = 0;
            for (int j = 0; j < 8; j++) {
                if (board[j][i].piece != null && board[j][i].piece.type == PieceType.LIGHT) {
                    whiteCountInRow++;
                }
            }
            if (whiteCountInRow > 3) {
                score -= 5000;
            }
        }
        return score;
    }

    private HashMap<String, Object> minimax(Tile[][] board, int depth, boolean isMax, Piece lastPiece) {
        HashMap<String, Object> map = new HashMap<>();
        if (depth == 0 || Main.checkGameWinner(board) != -1) {
            map.put("score", evaluateBoard(board));
            map.put("piece", lastPiece);
            return map;
        }
        if (isMax) {
            int maxScore = Integer.MIN_VALUE;
            Piece bestPiece = null;
            Coordinate bestMove = null;
            for (Piece black : allPieces(board, PieceType.DARK)) {
                System.out.println(ANSI_BLUE + "Checking: " + black + " " + black.x + " " + black.y + ANSI_RESET);
                try {
                    for (Coordinate move : Main.allPossibleMoves(board, black, false, false, false, black.x, black.y)) {
                        // System.out.println(ANSI_BLUE + "Moving: " + black + " " + move.x + " " +
                        // move.y + ANSI_RESET);
                        black.shadowMove(move.x, move.y);
                        int score = (int) minimax(board, depth - 1, false, black).get("score");
                        System.out.println("Score: " + score);
                        maxScore = Math.max(maxScore, score);
                        if (maxScore == score) {
                            bestMove = move;
                            bestPiece = black;
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {

                }
            }
            System.out.println(ANSI_BLUE + "BESTPIECe: " + bestPiece + ANSI_RESET);
            map.put("piece", bestPiece);
            map.put("moveCoordinate", bestMove);
            map.put("score", maxScore);
            return map;
        } else {
            int minScore = Integer.MAX_VALUE;
            Piece bestPiece = null;
            Coordinate bestMove = null;
            for (Piece white : allPieces(board, PieceType.LIGHT)) {
                System.out.println(ANSI_RED + "Checking: " + white + " " + white.x + " " + white.y + ANSI_RESET);
                for (Coordinate move : Main.allPossibleMoves(board, white, false, false, false, white.x, white.y)) {
                    // System.out.println(ANSI_RED + "Moving: " + white + " " + move.x + " " +
                    // move.y + ANSI_RESET);
                    white.shadowMove(move.x, move.y);
                    int score = (int) minimax(board, depth - 1, true, white).get("score");
                    minScore = Math.min(minScore, score);
                    if (minScore == score) {
                        bestMove = move;
                        bestPiece = white;
                    }
                }
            }
            System.out.println(ANSI_RED + "BESTPIECe: " + bestPiece + ANSI_RESET);
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
