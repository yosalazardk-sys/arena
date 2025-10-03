package Controller;
import java.util.Scanner;
import Model.EnemyWarrior;
import Model.ModelWarrior;
import Model.Characters.Arquero;
import Model.Characters.Boxeador;
import Model.Characters.Espadachin;
import Model.Characters.Mago;
import Model.Characters.Paladin;

public class GameController {

    private final Scanner teclado = new Scanner(System.in);
   public void startGame() {
    // Crear player y enemy warriors
    ModelWarrior player = selectCharacter();
    EnemyWarrior enemy = new EnemyWarrior("Ogro", 300, 30, 10, 0, 0, 0, 15, player);

    // 🔥 Crear ranking ANTES de empezar la pelea
    Ranking ranking = Ranking.crear(player, enemy);

    Thread enemyThread = new Thread(enemy);
    enemyThread.start();

    System.out.println("El combate ha comenzado entre " + player.getName() + " y las bestias del coliseo!");
    System.out.println("");
    System.out.println("--------------------------------------------------------------------");
    System.out.println("comandos disponibles: | Q: ATTACK_1 | W: ATTACK_2 | E: BLOCK_ATTACK | 1: HEALING_SPELL | 2: CHARGE_SPELL | 3: DEFENSIVE_SPELL | 4: STAMINA_SPELL | T: STATUS | P: EXIT |");
    System.out.println("--------------------------------------------------------------------");
    System.out.println("");

    // ✅ Solo un bucle principal
    while (player.isAlive() && enemy.isAlive()) {
        System.out.print("> ");
        String command = teclado.nextLine().trim().toUpperCase();

        switch (command) {
            case "Q": player.attack_1(enemy); break;
            case "W": player.attack_2(enemy); break;
            case "E": player.blockAttack(); break;
            case "1": player.healingSpell(); break;
            case "2": player.chargeSpell(); break;
            case "3": player.defensiveSpell(); break;
            case "4": player.StaminaSpell(); break;
            case "T": System.out.println(player.estadisticasActuales(player)); break;
            case "P":
                System.out.println("Saliendo del juego...");
                enemyThread.interrupt();
                player.stopRegen();
                return; // 🔥 salir inmediatamente
            default:
                System.out.println("Comando no reconocido. Intenta de nuevo.");
        }
    }

    // Fin de la partida
    enemyThread.interrupt();
    player.stopRegen();
    checkWinner(player);

    // 🔥 Mostrar ranking
    System.out.println("¿Quieres ver el ranking? (s/n)");
    String opcion = teclado.nextLine().trim().toLowerCase();
    if (opcion.equals("s")) {
        ranking.mostrarRanking(player.isAlive() ? player : enemy);
    } else {
        System.out.println("Partida terminada sin mostrar ranking.");
    }
}


  
    public void checkWinner(ModelWarrior player) {
        if (player.isAlive()) {
            System.out.println("¡Felicidades! Has derrotado a todas las bestias del coliseo.");
        }else {
            System.out.println("Has sido derrotado. Mejor suerte la próxima vez.");
        }
    }

    public ModelWarrior selectCharacter() {
        
        System.out.println("ingresa el nombre de tu personaje:");
        String name = teclado.nextLine();
        System.out.println("");
        System.out.println("--------------------------------------------------------------------");
        System.out.println("");

        while(true){
            System.out.println("Selecciona tu personaje:");
            System.out.println("| 1. Epadachin | 2. Arquero | 3. Paladin | 4. Mago | 5. Boxeador |");
            System.out.println("");
            System.out.println("--------------------------------------------------------------------");
            System.out.println("");
            int choice = teclado.nextInt();
            teclado.nextLine();

            switch (choice) {
            case 1: 
                if (confirmarseleccion()) {
                    return new Espadachin(name);
                } else {
                    break;
                }

            case 2: 
                if (confirmarseleccion()) {
                    return new Arquero(name);
                } else {
                    break;
                }

            case 3:
                if (confirmarseleccion()) {
                    return new Paladin(name);
                } else {
                    break;
                }

            case 4:
                if (confirmarseleccion()) {
                    return new Mago(name);
                } else {
                    break;
                }

            case 5:
                if (confirmarseleccion()) {
                    return new Boxeador(name);
                } else {
                    break;
                }

            default:
                System.out.println("Seleccion invalida. Intenta de nuevo.");
                break;
            }
        }  
    }

    private boolean confirmarseleccion() {
        System.out.println("¿Estás seguro de tu selección? (Y/N)");
        System.out.println("");
        System.out.println("--------------------------------------------------------------------");
        System.out.println("");
        String confirm = teclado.nextLine().trim().toUpperCase();
        System.out.println("");
        System.out.println("--------------------------------------------------------------------");
        System.out.println("");
        return confirm.equals("Y");
        
    }
}
