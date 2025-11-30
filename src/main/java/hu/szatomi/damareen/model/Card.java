package hu.szatomi.damareen.model;

public class Card {

    protected String name;
    protected int damage;
    protected int health;
    protected CardType type;

    protected int baseDamage;
    protected int baseHealth;

    public Card(String name, int damage, int health, CardType type) {
        this.name = name;
        this.damage = damage;
        this.health = health;
        this.type = type;

        this.baseDamage = damage;
        this.baseHealth = health;
    }

    public Card() {}

    public String getName() { return name; }
    public int getDamage() { return damage; }
    public int getHealth() { return health; }
    public CardType getType() { return type; }
    public int getBaseDamage() { return baseDamage; }
    public int getBaseHealth() { return baseHealth; }

    public void reset() {
        this.damage = baseDamage;
        this.health = baseHealth;
    }

    public void attack(int damage) { health -= damage; }

    public void increaseBaseHealth(int amount) {
        this.baseHealth += amount;
    }

    public void increaseBaseDamage(int amount) {
        this.baseDamage += amount;
    }
}
