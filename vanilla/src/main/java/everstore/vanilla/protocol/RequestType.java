package everstore.vanilla.protocol;

public enum RequestType {
    INVALID(0),
    ERROR(1),
    AUTHENTICATE(3),
    NEW_TRANSACTION(4),
    COMMIT_TRANSACTION(5),
    ROLLBACK_TRANSACTION(6),
    READ_JOURNAL(7),
    JOURNAL_EXISTS(8);

    public final int id;

    RequestType(int id) {
        this.id = id;
    }

    public static RequestType fromId(int id) {
        switch (id) {
            case 1:
                return ERROR;
            case 3:
                return AUTHENTICATE;
            case 4:
                return NEW_TRANSACTION;
            case 5:
                return COMMIT_TRANSACTION;
            case 6:
                return ROLLBACK_TRANSACTION;
            case 7:
                return READ_JOURNAL;
            case 8:
                return JOURNAL_EXISTS;
            default:
                return INVALID;
        }
    }
}
