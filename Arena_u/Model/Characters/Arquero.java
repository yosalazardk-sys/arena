package Model.Characters;

import Model.ModelWarrior;

public class Arquero extends ModelWarrior{

    public Arquero(String name) {
        super(name + " (Arquero) ", 150, 15, 8, 150, 100, 25);
        startManaRegen(8, 20);
        startStaminaRegen(5, 20);
    }

    @Override
    public synchronized void attack_1(ModelWarrior target) {
        enviarAtaque(0, 10, "tiro certero", getDamage(), target, 1, false, (t) -> t.setBlocked(false));
    }

    @Override
    public void attack_2(ModelWarrior target) {
        enviarAtaque(20, 30, "triple lanzamiento", getDamage(), target, 3, true, (t)->{});
    }


}
