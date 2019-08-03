package ru.itpark.gameslauncher.domain.game;

public enum GameGenre {
    ACTION(0),
    SIMULATOR(1),
    STRATEGY(2),
    ADVENTURE(3),
    RPG(4),
    PUZZLE(5),
    INTERACTIVE_FICTION(6);

    private int index;

    GameGenre(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static GameGenre getGenreByIndex(int index) {
        switch (index) {
            case 0:
                return GameGenre.ACTION;
            case 1:
                return GameGenre.SIMULATOR;
            case 2:
                return GameGenre.STRATEGY;
            case 3:
                return GameGenre.ADVENTURE;
            case 4:
                return GameGenre.RPG;
            case 5:
                return GameGenre.PUZZLE;
            case 6:
                return GameGenre.INTERACTIVE_FICTION;
            default:
                throw new RuntimeException("Unknown index:" + index);
        }
    }
}
