package Model.Characters;

import Model.ModelWarrior;

public class Mago extends ModelWarrior{

    public Mago(String name) {
        super(name + " (Mago) ", 200, 20, 5, 150, 75, 15);
        startManaRegen(5, 10);
        startStaminaRegen(2, 20);
    }

    @Override
    public void attack_1(ModelWarrior target) {
        enviarAtaque(10, 5, "magic punch", getDamage(), target, 1, true, (t)->{});
    }

    @Override
    public void attack_2(ModelWarrior target) {
        enviarAtaque(50, 5, "toque drenador", (getDamage() - getBaseDamage()/2), target, 1, true, (t) -> {
            t.setMana(0);
            setStamina(getMaxStamina());
        });
    }

}
