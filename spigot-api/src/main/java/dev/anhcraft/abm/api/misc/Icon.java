package dev.anhcraft.abm.api.misc;

public enum Icon {
    MONEY_BAG("\u3400"),
    LETTER("\u3401"),
    ONLINE("\u3402"),
    USER("\u3403"),
    MEDAL("\u3404"),
    GOLD_CUP("\u3405"),
    HEADSHOT("\u3406"),
    GUN("\u3407"),
    EXIT("\u3408"),
    CHART( "\u3409"),
    ACCEPT("\u340a"),
    DENY( "\u340b"),
    WARN("\u340c"),
    INFO("\u340d");

    private String character;

    Icon(String character) {
        this.character = character;
    }

    public String getChar() {
        return this.character;
    }
}
