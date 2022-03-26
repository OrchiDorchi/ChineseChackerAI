package Checker;

public enum PieceType {
    LIGHT(-1), DARK(1);

    final int moveDir;

    PieceType(int moveDir) {
        this.moveDir = moveDir;
    }
}
