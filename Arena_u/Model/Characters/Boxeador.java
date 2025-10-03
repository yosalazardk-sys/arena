package Model.Characters;

import Model.ModelWarrior;

public class Boxeador extends ModelWarrior {

    public Boxeador(String name) {
        super(name + " (Boxeador)", 200, 13, 16, 100, 100, 25);
        startManaRegen(2, 20);
        startStaminaRegen(10, 15);
    }

    @Override
    public void attack_1(ModelWarrior target) {
        enviarAtaque(0, 10, "jab cross", getDamage(), target, 2, true, (t)->{});
    }

    @Override
    public void attack_2(ModelWarrior target) {
        enviarAtaque(70, 70, "uppercut", getDamage(), target, 1, false, (t) -> {
            t.setStamina(0);
        });
    }

}
