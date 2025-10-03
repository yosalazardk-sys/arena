package Model;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

import interfaces.StatEffect;

public abstract class ModelWarrior {
    // Estadísticas base
    private final String name;
    private final int maxHp;
    private int hp;
    private final int baseDamage;
    private int damage;
    private final int baseDefense;
    private int defense;
    private final int maxMana;
    private int mana;
    private final int maxStamina;
    private int stamina;
    private int dodgeChance;
    private boolean blocked = false;

    private ScheduledExecutorService staminaSchedul;
    private ScheduledExecutorService manaSchedul;

    // 🔹 Contadores de estadísticas adicionales
    private int damageDone = 0;
    private int totalAttacks = 0;
    private int totalDodges = 0;
    private int defensiveSpellsUsed = 0;
    private int staminaSpellsUsed = 0;
    private int blocksUsed = 0;

    // Constructor
    public ModelWarrior(String name, int maxHp, int baseDamage, int baseDefense, int maxMana, int maxStamina, int dodgeChance) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.baseDamage = baseDamage;
        this.damage = baseDamage;
        this.baseDefense = baseDefense;
        this.defense = baseDefense;
        this.maxMana = maxMana;
        this.mana = maxMana;
        this.maxStamina = maxStamina;
        this.stamina = maxStamina;
        this.dodgeChance = dodgeChance;
    }

    // Getters y Setters
    public String getName() { return name; }
    public int getMaxHp() { return maxHp; }
    public int getHp() { return hp; }
    public int getBaseDamage() { return baseDamage; }
    public int getDamage() { return damage; }
    public int getBaseDefense() { return baseDefense; }
    public int getDefense() { return defense; }
    public int getMaxMana() { return maxMana; }
    public int getMana() { return mana; }
    public int getMaxStamina() { return maxStamina; }
    public int getStamina() { return stamina; }
    public int getDodgeChance() { return dodgeChance; }
    public boolean isBlocked() { return blocked; }

    public void setHp(int hp) { this.hp = hp; }
    public void setDamage(int damage) { this.damage = damage; }
    public void setDefense(int defense) { this.defense = defense; }
    public void setMana(int mana) { this.mana = mana; }
    public void setStamina(int stamina) { this.stamina = stamina; }
    public void setDodgeChance(int dodgeChance) { this.dodgeChance = dodgeChance; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }

    // 🔹 Getters de estadísticas extras
    public int getDamageDone() { return damageDone; }
    public int getTotalAttacks() { return totalAttacks; }
    public int getTotalDodges() { return totalDodges; }
    public int getDefensiveSpellsUsed() { return defensiveSpellsUsed; }
    public int getStaminaSpellsUsed() { return staminaSpellsUsed; }
    public int getBlocksUsed() { return blocksUsed; }

    // 🔹 Incrementadores
    public void addDamageDone(int dmg) { this.damageDone += dmg; }
    public void addAttack() { totalAttacks++; }
    public void addDodge() { totalDodges++; }
    public void addDefensiveSpell() { defensiveSpellsUsed++; }
    public void addStaminaSpell() { staminaSpellsUsed++; }
    public void addBlockUsed() { blocksUsed++; }

    // Métodos de combate
    public boolean isAlive(){ return hp > 0; }

    public void blockAttack(){
        if(stamina >= 20 && !blocked){
            blocked = true;
            stamina -= 20;
            addBlockUsed(); // 👈 contamos bloqueos
            System.out.println(name + " bloqueará el siguiente ataque recibido.");
            consecuencia(true, 20, "stamina");
            escibirLineaSeparadora();
        }else{
            if(!blocked){
                mensajeInsuficiecia("Stamina", "bloquear");
            }else{
                System.out.println(name + " ya tiene un bloqueo preparado.");
            }
            escibirLineaSeparadora();
        }
    }

    protected boolean dodgeAttack(int dodgeChance){
        return ((int)(Math.random() * 100)) < dodgeChance;
    }

    public synchronized void takeDamage(int amoDam, boolean canDodge){
        if(isAlive()){
            if (canDodge && dodgeAttack(dodgeChance)) {
                addDodge(); // 👈 contamos esquiva
                System.out.println(name + " ha esquivado el ataque.");
                escibirLineaSeparadora();
                return;      
            }else if(blocked){
                amoDam= Math.max(0, (amoDam - defense));
                hp -= amoDam;
                blocked = false;
                defense = baseDefense;
                System.out.println(name + " bloqueó el ataque, recibiendo " +  amoDam + " de daño");
                escibirLineaSeparadora();
            }else{
                hp -= amoDam;
                System.out.println(name + " recibió " + amoDam + " de daño.");
                escibirLineaSeparadora();
            }       
        }
    }
    // 🔹 Dentro de ModelWarrior

public void startManaRegen(int amount, int intervalSeconds) {
    if (manaSchedul != null && !manaSchedul.isShutdown()) {
        manaSchedul.shutdownNow();
    }
    manaSchedul = Executors.newSingleThreadScheduledExecutor();
    manaSchedul.scheduleAtFixedRate(() -> {
        if (mana < maxMana) {
            mana = Math.min(maxMana, mana + amount);
            System.out.println(name + " regeneró " + amount + " de mana (" + mana + "/" + maxMana + ")");
        }
    }, intervalSeconds, intervalSeconds, TimeUnit.SECONDS);
}

public void startStaminaRegen(int amount, int intervalSeconds) {
    if (staminaSchedul != null && !staminaSchedul.isShutdown()) {
        staminaSchedul.shutdownNow();
    }
    staminaSchedul = Executors.newSingleThreadScheduledExecutor();
    staminaSchedul.scheduleAtFixedRate(() -> {
        if (stamina < maxStamina) {
            stamina = Math.min(maxStamina, stamina + amount);
            System.out.println(name + " regeneró " + amount + " de stamina (" + stamina + "/" + maxStamina + ")");
        }
    }, intervalSeconds, intervalSeconds, TimeUnit.SECONDS);
}


    public abstract void attack_1(ModelWarrior target);
    public abstract void attack_2(ModelWarrior target);

    // Hechizos
    public synchronized void healingSpell(){
        usarHechizo("hechizo de curación", 30, 0, () -> (Math.min(maxHp, (hp + 30)) - hp), "vida", this::getHp, this::setHp);
    }

    public synchronized void chargeSpell(){
        usarHechizo("hechizo de carga", 40, 40, () -> (damage/2), "daño", this::getDamage, this::setDamage);
    }

    public synchronized void defensiveSpell(){
        addDefensiveSpell(); // 👈 contamos hechizo defensivo
        usarHechizo("hechizo defensivo", 20, 0, () -> (defense/4), "defensa", this::getDefense, this::setDefense);
    }

    public synchronized void StaminaSpell(){
        addStaminaSpell(); // 👈 contamos hechizo de stamina
        usarHechizo("hechizo de stamina", 20, 0, () -> (Math.min(maxStamina, (stamina + 20)) - stamina), "stamina", this::getStamina, this::setStamina);
    }

    // 🔹 Ataques
    protected synchronized void enviarAtaque(int costMana, int costStamina, String ataque, int daño, ModelWarrior target, int multiAttack, boolean canDodge, StatEffect extraEffect){
        if(getMana() >= costMana && getStamina() >= costStamina){
            setMana(getMana() - costMana);
            setStamina(getStamina() - costStamina);

            realizarAccion(ataque, " contra " + target.getName());

            if(costMana > 0)consecuencia(true, costMana, "mana");
            if(costStamina > 0)consecuencia(true, costStamina, "stamina");
            escibirLineaSeparadora();

            if(extraEffect != null) extraEffect.apply(target);

            atacar(target, daño, multiAttack, canDodge);
            setDamage(getBaseDamage());

            addAttack(); // 👈 contamos ataque
            this.addDamageDone(daño * multiAttack);
        }else {
            if(getMana() < costMana) mensajeInsuficiecia("mana", ataque);
            if(getStamina() < costStamina) mensajeInsuficiecia("stamina", ataque);
            escibirLineaSeparadora();
        }
    }

    protected void atacar(ModelWarrior target, int daño, int multiAttack, boolean canDodge){
        if(multiAttack > 0){
            target.takeDamage(daño, canDodge);
            atacar(target, daño, (multiAttack - 1), canDodge);
        }
    }

    // Utilidades
    protected synchronized void usarHechizo(String nombre, int costMana, int costStamina, IntSupplier calcularIncremento, String atributoPrincipal, IntSupplier atributo, IntConsumer guardaratributo){
        int incremento = calcularIncremento.getAsInt();
        if(incremento > 0 && getMana() >= costMana && getStamina() >= costStamina){
            setMana(getMana() - costMana);
            setStamina(getStamina() - costStamina);

            realizarAccion(nombre, "");
            if(costMana > 0)consecuencia(true, costMana, "mana");
            if(costStamina > 0)consecuencia(true, costStamina, "stamina");

            int nuevoValor = atributo.getAsInt() + incremento;
            guardaratributo.accept(nuevoValor);

            System.out.println(name + " ahora tiene " + nuevoValor + " puntos de " + atributoPrincipal);
            escibirLineaSeparadora();
        }else{
            System.out.println(name + " no puede usar " + nombre + ", recursos insuficientes o ya al máximo.");
        }
    }

    protected void mensajeInsuficiecia(String msj_1, String msj_2){
        System.out.println(name + " no tiene suficiente " + msj_1 +" para usar " + msj_2);
    }

    protected void realizarAccion(String accion, String msjx){
        System.out.println(name + " usó " + accion + msjx);
    }

    protected void consecuencia(boolean gasto, int cantidad, String estadistica){
        String msj  = gasto ? " gastó " : " obtuvo ";
        System.out.println(msj + cantidad + " puntos de " + estadistica);
    }

    public String estadisticasActuales(ModelWarrior warrior){
        return "=== Estado de " + warrior.getName() + " ===\n" +
           "HP: " + warrior.getHp() + "/" + warrior.getMaxHp() + "\n" +
           "Mana: " + warrior.getMana() + "/" + warrior.getMaxMana() + "\n" +
           "Stamina: " + warrior.getStamina() + "/" + warrior.getMaxStamina() + "\n" +
           "Daño: " + warrior.getDamage() + "\n" +
           "Defensa: " + warrior.getDefense() + "\n" +
           "Bloqueando: " + (warrior.isBlocked() ? "Sí" : "No") + "\n";
    }

    public void stopRegen(){
        if(staminaSchedul != null) staminaSchedul.shutdownNow();
        if(manaSchedul != null) manaSchedul.shutdownNow();
    }

    protected void escibirLineaSeparadora() {
        System.out.println("\n--------------------------------------------------------------------\n");
    }
}
