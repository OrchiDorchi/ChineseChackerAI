package Checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

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
        HashMap<Piece, Coordinate> initialWhites = new HashMap<>();
        HashMap<Piece, Coordinate> initialBlacks = new HashMap<>();
        for (Piece piece : allPieces(Main.board, PieceType.DARK)) {
            initialBlacks.put(piece, new Coordinate(piece.x, piece.y));
        }
        for (Piece piece : allPieces(Main.board, PieceType.LIGHT)) {
            initialWhites.put(piece, new Coordinate(piece.x, piece.y));
        }
        HashMap<String, Object> map = minimax(Main.board, 5, 5, Integer.MIN_VALUE, Integer.MAX_VALUE, true, null,
                initialWhites, initialBlacks);
        try {
            Thread.sleep(200);
        } catch (Exception e) {
            // TODO: handle exception
        }
        for (Piece piece : allPieces(Main.board, PieceType.DARK)) {
            Coordinate c = initialBlacks.get(piece);
            Main.board[piece.x][piece.y].piece = null;
            piece.move(c.x, c.y);
            Main.board[c.x][c.y].piece = piece;
        }
        for (Piece piece : allPieces(Main.board, PieceType.LIGHT)) {
            Coordinate c = initialWhites.get(piece);
            Main.board[piece.x][piece.y].piece = null;
            piece.move(c.x, c.y);
            Main.board[c.x][c.y].piece = piece;
        }
        Piece piece = (Piece) map.get("piece");
        Coordinate c = (Coordinate) map.get("moveCoordinate");
        System.out.println(piece);
        System.out.println(c);
        Main.board[piece.x][piece.y].changeType(TileType.PREV_MOVE);
        Main.board[piece.x][piece.y].piece = null;
        Main.board[c.x][c.y].piece = piece;
        piece.move(c.x, c.y);
        Main.board[c.x][c.y].changeType(TileType.PREV_MOVE);
        Main.printBoard();
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
        int blackCountInRow7 = 0;
        int blackCountInColumn7 = 0;
        int blackCountInRow6 = 0;
        int blackCountInColumn6 = 0;
        for (int j = 0; j < 8; j++) {
            if (board[j][7].piece != null && board[j][7].piece.type == PieceType.DARK) {
                blackCountInRow7++;
            }
            if (board[7][j].piece != null && board[7][j].piece.type == PieceType.DARK) {
                blackCountInColumn7++;
            }
            if (board[j][6].piece != null && board[j][6].piece.type == PieceType.DARK) {
                blackCountInRow6++;
            }
            if (board[6][j].piece != null && board[6][j].piece.type == PieceType.DARK) {
                blackCountInColumn6++;
            }
        }
        if ((blackCountInRow7 > 3) || (blackCountInColumn7 > 3) || (blackCountInRow7 == 3 && blackCountInRow6 > 3)
                || (blackCountInColumn7 == 3 && blackCountInColumn6 > 3)) {
            score -= 50000;
        }
        int whiteCountInRow7 = 0;
        int whiteCountInColumn7 = 0;
        int whiteCountInRow6 = 0;
        int whiteCountInColumn6 = 0;
        for (int j = 0; j < 8; j++) {
            if (board[j][7].piece != null && board[j][7].piece.type == PieceType.LIGHT) {
                whiteCountInRow7++;
            }
            if (board[7][j].piece != null && board[7][j].piece.type == PieceType.LIGHT) {
                whiteCountInColumn7++;
            }
            if (board[j][6].piece != null && board[j][6].piece.type == PieceType.LIGHT) {
                whiteCountInRow6++;
            }
            if (board[6][j].piece != null && board[6][j].piece.type == PieceType.LIGHT) {
                whiteCountInColumn6++;
            }
        }
        if ((whiteCountInRow7 > 3) || (whiteCountInColumn7 > 3) || (whiteCountInRow7 == 3 && whiteCountInRow6 > 3)
                || (whiteCountInColumn7 == 3 && whiteCountInColumn6 > 3)) {
            score += 50000;
        }
        return score;
    }

    private HashMap<String, Object> minimax(Tile[][] board, int max_depth, int depth, int alpha, int beta,
            boolean isMax,
            Piece lastPiece,
            HashMap<Piece, Coordinate> initialWhites, HashMap<Piece, Coordinate> initialBlacks) {
        HashMap<String, Object> map = new HashMap<>();
        if (depth == 0 || Main.checkGameWinner(board) != -1) {
            // System.out.println(ANSI_PURPLE + "PIECE: " + lastPiece + ANSI_RESET);
            int score = evaluateBoard(board);
            // System.out.println(ANSI_RED + "Score : " + score + ANSI_RESET);
            map.put("score", score);
            map.put("piece", lastPiece);
            // for (Piece piece : allPieces(Main.board, PieceType.DARK)) {
            // Coordinate c = initialBlacks.get(piece);
            // piece.move(c.x, c.y);
            // }
            if (max_depth % 2 == 0) {
                for (Piece piece : allPieces(Main.board, PieceType.LIGHT)) {
                    Coordinate c = initialWhites.get(piece);
                    Main.board[piece.x][piece.y].piece = null;
                    piece.shadowMove(c.x, c.y);
                    Main.board[c.x][c.y].piece = piece;
                }
            } else {
                for (Piece piece : allPieces(Main.board, PieceType.DARK)) {
                    Coordinate c = initialBlacks.get(piece);
                    Main.board[piece.x][piece.y].piece = null;
                    piece.shadowMove(c.x, c.y);
                    Main.board[c.x][c.y].piece = piece;
                }
            }
            return map;
        }
        if (isMax) {
            int maxScore = Integer.MIN_VALUE;
            Piece bestPiece = null;
            Coordinate bestMove = null;
            for (Piece black : allPieces(board, PieceType.DARK)) {
                HashMap<Piece, Coordinate> white_copy = copy(initialWhites);
                HashMap<Piece, Coordinate> black_copy = copy(initialBlacks);
                for (Piece piece : allPieces(Main.board, PieceType.DARK)) {
                    black_copy.put(piece, new Coordinate(piece.x, piece.y));
                }
                for (Coordinate move : Main.allPossibleMoves(board, black, false, false, false, black.x, black.y)) {
                    // System.out.println(ANSI_BLUE + "Moving: " + black + " " + move.x + " " +
                    // move.y + ANSI_RESET);
                    Main.board[black.x][black.y].piece = null;
                    black.shadowMove(move.x, move.y);
                    Main.board[move.x][move.y].piece = black;

                    int score = (int) minimax(board, max_depth, depth - 1, alpha, beta, false, black, white_copy,
                            black_copy)
                            .get("score");
                    maxScore = Math.max(maxScore, score);
                    alpha = Math.max(alpha, score);
                    if (beta <= alpha) {
                        // System.out.println("Pruned");
                        break;
                    }
                    if (maxScore == score) {
                        bestMove = move;
                        bestPiece = black;
                    }
                }
                for (Piece piece : allPieces(Main.board, PieceType.DARK)) {
                    Coordinate c = black_copy.get(piece);
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
                HashMap<Piece, Coordinate> white_copy = copy(initialWhites);
                HashMap<Piece, Coordinate> black_copy = copy(initialBlacks);
                for (Piece piece : allPieces(Main.board, PieceType.LIGHT)) {
                    white_copy.put(piece, new Coordinate(piece.x, piece.y));
                }
                for (Coordinate move : Main.allPossibleMoves(board, white, false, false, false, white.x, white.y)) {
                    // System.out.println(ANSI_RED + "Moving: " + white + " " + move.x + " " +
                    // move.y + ANSI_RESET);
                    Main.board[white.x][white.y].piece = null;
                    white.shadowMove(move.x, move.y);
                    Main.board[move.x][move.y].piece = white;
                    int score = (int) minimax(board, max_depth, depth - 1, alpha, beta, true, white, white_copy,
                            black_copy)
                            .get("score");
                    minScore = Math.min(minScore, score);
                    beta = Math.min(beta, score);
                    if (beta <= alpha) {
                        // System.out.println("Pruned");
                        break;
                    }
                    if (minScore == score) {
                        bestMove = move;
                        bestPiece = white;
                    }
                }
                for (Piece piece : allPieces(Main.board, PieceType.LIGHT)) {
                    Coordinate c = white_copy.get(piece);
                    Main.board[piece.x][piece.y].piece = null;
                    piece.shadowMove(c.x, c.y);
                    Main.board[c.x][c.y].piece = piece;
                }
            }
            map.put("piece", bestPiece);
            map.put("moveCoordinate", bestMove);
            map.put("score", minScore);
            return map;
        }
    }

    public static HashMap<Piece, Coordinate> copy(
            HashMap<Piece, Coordinate> original) {
        HashMap<Piece, Coordinate> copy = new HashMap<Piece, Coordinate>();
        for (Map.Entry<Piece, Coordinate> entry : original.entrySet()) {
            copy.put(entry.getKey(),
                    // Or whatever List implementation you'd like here.
                    new Coordinate(entry.getValue().x, entry.getValue().y));
        }
        return copy;
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
