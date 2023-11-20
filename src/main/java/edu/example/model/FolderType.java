package edu.example.model;

public enum FolderType {
    TEST("Контрольная работа"),
    NOTES("Конспекты семинаров"),
    LITERATURE("Литература");

    private final String displayName;

    FolderType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
