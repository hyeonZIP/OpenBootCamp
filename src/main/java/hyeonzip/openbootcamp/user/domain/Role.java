package hyeonzip.openbootcamp.user.domain;

public enum Role {
    STUDENT, BOOTCAMP_ADMIN, ADMIN;

    private static final String PREFIX = "ROLE_";

    public String getAuthority() {
        return PREFIX + this.name();
    }
}
