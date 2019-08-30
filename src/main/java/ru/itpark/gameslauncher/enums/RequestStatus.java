package ru.itpark.gameslauncher.enums;

public enum RequestStatus {
    PENDING(0),
    APPROVED(1),
    DECLINED(2);

    private int index;

    RequestStatus(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static RequestStatus getStatusByIndex(int index) {
        switch (index) {
            case 0:
                return RequestStatus.PENDING;
            case 1:
                return RequestStatus.APPROVED;
            case 2:
                return RequestStatus.DECLINED;
            default:
                throw new RuntimeException("Unknown index:" + index);
        }
    }
}
