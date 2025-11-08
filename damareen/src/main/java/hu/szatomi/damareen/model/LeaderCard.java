package hu.szatomi.damareen.model;

public class LeaderCard extends Card {

    private final Card baseCard;
    private final LeaderType leaderType;

    public LeaderCard(String leaderName, Card baseCard, LeaderType leaderType) {
        super(
                leaderName,
                leaderType == LeaderType.SEBZES ? baseCard.getBaseDamage() * 2 : baseCard.getDamage(),
                leaderType == LeaderType.ELETERO ? baseCard.getBaseHealth() * 2 : baseCard.getHealth(),
                baseCard.getType()
        );

        this.baseCard = baseCard;
        this.leaderType = leaderType;
    }
}
