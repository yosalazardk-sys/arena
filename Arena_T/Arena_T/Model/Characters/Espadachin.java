package Model.Characters;

import Model.ModelWarrior;

public class Espadachin extends ModelWarrior{

    public Espadachin(String name) {
        super(name + " (Espadachin) ", 200, 10, 8, 100, 100, 10);
        startManaRegen(5, 20);
        startStaminaRegen(5, 20);
    }

    @Override
    public synchronized void attack_1(ModelWarrior target) {
        enviarAtaque(0,5, "corte rapido",getDamage(), target, 1, true, (t) -> {});   
    }

    @Override
    public synchronized void attack_2(ModelWarrior target) {
        enviarAtaque(0, 10, "estocada", getDamage()*2, target, 1, true, (t) -> {});
    }
}
