package ru.itpark.gameslauncher.enums;

public enum GameStatus {
    TODO(0),
    IN_PROGRESS(1),
    BETA(2),
    IN_RELEASE(3);

    private int index;

    GameStatus(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static GameStatus getStatusByIndex(int index) {
        switch (index) {
            case 0:
                return GameStatus.TODO;
            case 1:
                return GameStatus.IN_PROGRESS;
            case 2:
                return GameStatus.BETA;
            case 3:
                return GameStatus.IN_RELEASE;
            default:
                throw new RuntimeException("Unknown index:" + index);
        }
    }
}
