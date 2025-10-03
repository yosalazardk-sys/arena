package Controller;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Comparator;

import Model.ModelWarrior;

public class Ranking {

    private final Instant startTime;
    private final List<ModelWarrior> jugadores;

    public Ranking(List<ModelWarrior> jugadores) {
        this.startTime = Instant.now();
        this.jugadores = jugadores;
    }

    public void mostrarRanking(ModelWarrior ganador) {
        Instant endTime = Instant.now();
        Duration duracion = Duration.between(startTime, endTime);

        System.out.println("\n==================== RANKING FINAL ====================");
        System.out.println(" Ganador: " + (ganador != null ? ganador.getName() : "Empate"));
        System.out.printf(" Duración: %d minutos %d segundos\n\n",
                duracion.toMinutes(), duracion.toSecondsPart());

        // 🔹 Mostrar ranking ordenado por daño
        jugadores.stream()
                .sorted(Comparator.comparingInt(ModelWarrior::getDamageDone).reversed())
                .forEach(j -> {
                    System.out.println("▶ " + j.getName());
                    System.out.println("   Daño Total: " + j.getDamageDone());
                    System.out.println("   Ataques realizados: " + j.getTotalAttacks());
                    System.out.println("   Esquivas: " + j.getTotalDodges());
                    System.out.println("   Bloqueos usados: " + j.getBlocksUsed());
                    System.out.println("   Hechizos defensivos: " + j.getDefensiveSpellsUsed());
                    System.out.println("   Hechizos de stamina: " + j.getStaminaSpellsUsed());
                    System.out.println("-------------------------------------------------------");
                });

        System.out.println("=======================================================\n");
    }

    public static Ranking crear(ModelWarrior... jugadores) {
        return new Ranking(List.of(jugadores));
    }
}
