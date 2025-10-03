package Model;

public class EnemyWarrior extends ModelWarrior implements Runnable {
    public int coldown;
    public ModelWarrior target;

    public EnemyWarrior(String name, int maxHp, int baseDamage, int dodgeChance, int maxMana, int baseDefense, int maxStamina, int coldown, ModelWarrior target) {
        super(name, maxHp, baseDamage, dodgeChance, maxMana, baseDefense, maxStamina);
        this.coldown = coldown;
        this.target = target;
    }
     
    @Override
    public void run() {
        try {
            while (isAlive()) {

                Thread.sleep((coldown * 1000)); // Espera entre ataques
                if(target.isAlive()){
                    attack_1(target);
                }

            }
        } catch (InterruptedException e) {
            System.out.println(getName() + " ha sido interrumpido.");
        }    
    }
    @Override
    public synchronized void attack_1(ModelWarrior target) {
        enviarAtaque(0, 0, "maza", getDamage(), target, 1, true, (t) -> {});
    }
    @Override
    public synchronized void attack_2(ModelWarrior target) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'attack_2'");
    }

}
