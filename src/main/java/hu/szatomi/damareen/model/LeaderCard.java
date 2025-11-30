package hu.szatomi.damareen.model;

public class LeaderCard extends Card {

    private Card baseCard;
    private LeaderType leaderType;

    public LeaderCard(String leaderName, Card baseCard, LeaderType leaderType) {
        super(
                leaderName,
                leaderType == LeaderType.SEBZES ? baseCard.getDamage() * 2 : baseCard.getDamage(),
                leaderType == LeaderType.ELETERO ? baseCard.getHealth() * 2 : baseCard.getHealth(),
                baseCard.getType()
        );

        this.baseCard = baseCard;
        this.leaderType = leaderType;
    }

    public LeaderCard() {}

    public LeaderType getLeaderType() {
        return leaderType;
    }

    public void setLeaderType(LeaderType leaderType) {
        this.leaderType = leaderType;
    }

    public Card getBaseCard() { return baseCard; }

    public void setBaseCard(Card baseCard) { this.baseCard = baseCard; }
}
