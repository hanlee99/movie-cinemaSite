package com.example.demo.entity;

public enum WatchStatus {
    WATCHED("시청 완료"),
    WISH_TO_WATCH("보고 싶음");

    private final String displayName;

    WatchStatus(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }
}
