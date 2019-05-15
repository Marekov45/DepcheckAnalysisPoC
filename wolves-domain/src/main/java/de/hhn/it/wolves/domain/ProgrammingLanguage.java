package de.hhn.it.wolves.domain;

public enum ProgrammingLanguage {
    JAVA("Java"), NODE_JS("Node_JS"), UNKNOWN("Unknown");

    private final String name;

    private ProgrammingLanguage(String s) {
        name = s;
    }

    public static ProgrammingLanguage fromString(String text) {
        for (ProgrammingLanguage b : ProgrammingLanguage.values()) {
            if (b.name.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}

