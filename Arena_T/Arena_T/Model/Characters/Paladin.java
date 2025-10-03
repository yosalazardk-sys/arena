package Model.Characters;

import Model.ModelWarrior;

public class Paladin extends ModelWarrior{

    public Paladin(String name) {
        super(name + " (Paladin) ", 250, 17, 15, 75, 100, 5);
        startManaRegen(2, 15);
        startStaminaRegen(5, 20);
    }

    @Override
    public void attack_1(ModelWarrior target) {
         enviarAtaque(0,10, "tajo mata dragones",getDamage(), target, 1, true, (t) -> {});

    }

    @Override
    public void attack_2(ModelWarrior target) {
         enviarAtaque(20,10, "corte divino",getDamage()*2, target, 1, true, (t) -> t.setBlocked(false));
    }

}
