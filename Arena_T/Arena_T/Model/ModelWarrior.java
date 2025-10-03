package Model;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

import interfaces.StatEffect;

public abstract class ModelWarrior {
//estadisticas del guerrero
    private final String name; //nombre del guerrero
    private final int maxHp; //puntos de vida maxima del guerrero
    private int hp; //puntos de vida actuales del guerrero 
    private final int baseDamage; //puntos de daño base que puede realizar el guerrero
    private int damage; //puntos de daño actuale que puede realizar el guerrero
    private final int baseDefense;//puntos de defensa maximos del guerrero
    private int defense; //puntos de defensa del guerrero para bloquear un ataque y reducir el daño recibido
    private final int maxMana; //puntos de mana maximos del guerro para realizar hechizos
    private int mana; //puntos de mana actuales del guerrero
    private final int maxStamina; //puntos de energia maxima del guerrero para realizar distintas acciones
    private int stamina; //puntos de energia actuales del guerrero
    private int dodgeChance; //probabilidad de evadir un ataque

    boolean blocked = false;
    private ScheduledExecutorService staminaSchedul;
    private ScheduledExecutorService manaSchedul;


// constructor
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

//getters and setters

    public String getName() {
        return name;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getHp() {
        return hp;
    }

    public int getBaseDamage() {
        return baseDamage;
    }

    public int getDamage() {
        return damage;
    }

    public int getBaseDefense() {
        return baseDefense;
    }

    public int getDefense() {
        return defense;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public int getMana() {
        return mana;
    }

    public int getMaxStamina() {
        return maxStamina;
    }

    public int getStamina() {
        return stamina;
    }

    public int getDodgeChance() {
        return dodgeChance;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public void setDodgeChance(int dodgeChance) {
        this.dodgeChance = dodgeChance;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

//metodos de combate

    //metodo para verificar si el guerrero esta vivo
    public boolean isAlive(){
        return hp > 0;
    }

    public void status(){
        if(hp < 0){
            hp = 0;
            System.out.println(name + " esta muerto.");
            escibirLineaSeparadora();
        }else{
            System.out.println(name + " tiene " + hp + " puntos de vida restantes.");
            escibirLineaSeparadora();
        }
        
    }

    //metodo para verificar si el guerrero es capaz de bloquear un ataque
    public void blockAttack(){
        if(stamina >= 20 && blocked == false){
            blocked = true;
            stamina -= 20;
            System.out.println(name + "bloqueara el siguiente ataque recibido.");
            consecuencia(true, 20, "stamina");
            escibirLineaSeparadora();
        }else{
            if(blocked == false){
                mensajeInsuficiecia("Stamina", "bloquear");
                blocked = false;
            }else{
                System.out.println(name + "ya preparo un bloqueo, no puede bloquear denuevo");
                escibirLineaSeparadora();
            }
        }
    }

    //metodo para calcular si el guerrero esquiva un ataque
    protected boolean dodgeAttack(int dodgeChance){
        int random = (int)(Math.random() * 100);
        return random < dodgeChance;
    }

    public synchronized void takeDamage(int amoDam, boolean canDodge){
        if(isAlive()){
            if (canDodge && dodgeAttack(dodgeChance)) {
                System.out.println(name + " ha esquivado el ataque.");
                escibirLineaSeparadora();
                return;      
            }else if(blocked){
                amoDam= Math.max(0, (amoDam - defense));
                hp -= amoDam;
                blocked = false;
                defense = baseDefense;
                System.out.println(name + " ha bloqueado el ataque, recibiendo solo " +  amoDam + " puntos de daño");
                escibirLineaSeparadora();
                status();
            }else{
                hp -= amoDam;
                System.out.println(name + " ha recibido " + amoDam + " puntos de daño.");
                escibirLineaSeparadora();
                status();
            }       
        }
    }
    
    public abstract void attack_1(ModelWarrior target);

    public abstract void attack_2(ModelWarrior target);

    //metodo para regenerar la Stamina
    public void startStaminaRegen(int amount, int speedRegen){
        staminaSchedul = startRegen(isAlive(),"stamina", maxStamina, this::getStamina, this::setStamina, amount, speedRegen);
    }

    // metodo para regenerar el mana
    public void startManaRegen(int amount, int speedRegen){
        manaSchedul = startRegen(isAlive(), "mana", maxMana, this::getMana, this::setMana, amount, speedRegen);
    }

    //metodo que realiza un hechizo que convierte mana en hp
    public synchronized void healingSpell(){
        usarHechizo("hechizo de curacion", 30, 0, () -> (Math.min(maxHp, (hp + 30)) - hp), "vida", this::getHp, this::setHp);
    }

    //metodo que realiza un hechizo que aumenta el daño realizado en el siguiente ataque
    public synchronized void chargeSpell(){
        usarHechizo("hechizo de carga", 40, 40, () -> (damage/2), "daño", this::getDamage, this::setDamage);
    }

    //metodo que realiza un hechizo que aumenta la defensa del guerrero
    public synchronized void defensiveSpell(){
        usarHechizo("hechizo defensivo", 20, 0, () -> (defense/4), "defensa", this::getDefense, this::setDefense);
    }

    //metodo que realiza un hechizo que conviere Mana en Stamina
    public synchronized void StaminaSpell(){
        usarHechizo("hechizo de stamina", 20, 0, () -> (Math.min(maxStamina, (stamina + 20)) - stamina), "stamina", this::getStamina, this::setStamina);
    }


//metodos adicionales
    public ScheduledExecutorService startRegen(boolean alive, String atributo, int max, IntSupplier getter, IntConsumer setter , int amount, int speedRegen){
        ScheduledExecutorService sched = Executors.newSingleThreadScheduledExecutor();
        sched.scheduleAtFixedRate(() ->{
            try{
                synchronized(this){
                    int actual = getter.getAsInt();
                    if(alive && actual < max){
                        actual = Math.min(max,(actual + amount));
                        setter.accept(actual);
                    }
                }
            }catch(Exception e){
                System.out.println("Error en regeneracion de " + atributo + "" + e.getMessage());
                e.printStackTrace();
            }
        }, 0, speedRegen, TimeUnit.SECONDS);
        return sched;
    }

    protected synchronized void usarHechizo(String nombre, int costMana, int costStamina, IntSupplier calcularIncremento, String atributoPrincipal, IntSupplier atributo, IntConsumer guardaratributo){
        int incremento = calcularIncremento.getAsInt();
        if(incremento > 0){
            if(getMana() >= costMana && getStamina() >= costStamina){

                setMana(getMana() - costMana);
                setStamina(getStamina() - costStamina);

                realizarAccion(nombre,"");

                if(costMana > 0)consecuencia(true, costMana, "mana");
                if(costStamina > 0)consecuencia(true, costStamina, "stamina");
                consecuencia(false, incremento, atributoPrincipal);
                escibirLineaSeparadora();

                int actual = atributo.getAsInt();
                int nuevoValor = actual + incremento;
                guardaratributo.accept(nuevoValor);

                System.out.println(name + "ahora tiene " + nuevoValor + " puntos de " + atributoPrincipal);
                escibirLineaSeparadora();

            }else{
                String insuficiente = "";

                if(getMana() < costMana) {insuficiente += "mana"; mensajeInsuficiecia(insuficiente, nombre);}
                if(getStamina() < costStamina) {insuficiente += "stamina"; mensajeInsuficiecia(insuficiente, nombre);}
                escibirLineaSeparadora();
            }
        }else{
            System.out.println("no es posible usar " + nombre + " porque " + atributoPrincipal + " ya esta al maximo");
        }
    }

    protected synchronized void enviarAtaque(int costMana, int costStamina, String ataque, int daño, ModelWarrior target, int multiAttack, boolean canDodge, StatEffect extraEffect){
        if(getMana() >= costMana && getStamina() >= costStamina){

            setMana(getMana() - costMana);
            setStamina(getStamina() - costStamina);

            realizarAccion(ataque, " para atacar a " + target.getName());

            if(costMana > 0)consecuencia(true, costMana, "mana");
            if(costStamina > 0)consecuencia(true, costStamina, "stamina");
            escibirLineaSeparadora();

            extraEffect.apply(target);
            atacar(target, daño, multiAttack, canDodge);
            setDamage(getBaseDamage());
        }else {
            String insuficiente = "";

            if(getMana() < costMana) {insuficiente += "mana"; mensajeInsuficiecia(insuficiente, ataque);}
            if(getStamina() < costStamina) {insuficiente += "stamina"; mensajeInsuficiecia(insuficiente, ataque);}
            escibirLineaSeparadora();
        }
    }

    protected void atacar(ModelWarrior target, int daño, int multiAttack, boolean canDodge){
        if(multiAttack > 0){
            target.takeDamage(daño, canDodge);
            atacar(target, daño, (multiAttack - 1), canDodge);
        }
    }


    protected void mensajeInsuficiecia(String msj_1, String msj_2){
        System.out.println(name + " no tiene suficiente " + msj_1 +" para usar " + msj_2);
    }

    protected void realizarAccion(String accion, String msjx){
        System.out.println(name + " uso " + accion + msjx);
    }

    protected void consecuencia(boolean gasto, int cantidad, String estadistica){
        String msj  = gasto ? " gasto " : " obtuvo ";
        System.out.println(msj + cantidad + " puntos de " + estadistica);
    }

    public String estadisticasActuales(ModelWarrior warrior){
        return "=== Estado de " + warrior.getName() + " ===\n" +
           "HP: " + warrior.getHp() + "/" + warrior.getMaxHp() + "\n" +
           "Mana: " + warrior.getMana() + "/" + warrior.getMana() + "\n" +
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
        System.out.println("");
        System.out.println("--------------------------------------------------------------------");
        System.out.println("");
    }
    
}