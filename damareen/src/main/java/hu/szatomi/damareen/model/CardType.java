package hu.szatomi.damareen.model;

public enum CardType {
    FOLD,
    VIZ,
    TUZ,
    LEVEGO;

    public boolean isStrongAgainst(CardType other) {
        return (this == LEVEGO && (other == FOLD   || other == VIZ))  ||
                (this == VIZ   && (other == LEVEGO || other == TUZ))  ||
                (this == TUZ   && (other == VIZ    || other == FOLD)) ||
                (this == FOLD  && (other == LEVEGO || other == TUZ));
    }

    public boolean isWeakAgainst(CardType other) {
        return (this == LEVEGO && other == TUZ)    ||
                (this == VIZ   && other == FOLD)   ||
                (this == TUZ   && other == LEVEGO) ||
                (this == FOLD  && other == VIZ);
    }

}
