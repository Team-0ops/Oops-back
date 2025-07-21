package Oops.backend.domain.post.model;

public enum Situation {
    OOPS("웁스중"),
    OVERCOMING("극복중"),
    OVERCOME("극복완료");

    private final String label;

    Situation(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}