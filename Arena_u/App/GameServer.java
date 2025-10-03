package App;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import Controller.ClientHandler;
import Controller.Ranking;
import Model.ModelWarrior;

public class GameServer {
    private static final int PORT = 5000;
    private static final List<ClientHandler> waitingPlayers = new ArrayList<>();
    
    // Guardamos ranking por cada par de jugadores
    private static final Map<ClientHandler, Ranking> partidas = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor iniciado en el puerto " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado");

                ClientHandler handler = new ClientHandler(socket);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Empareja jugadores
    public synchronized static void addWaitingPlayer(ClientHandler player) {
        if (waitingPlayers.isEmpty()) {
            waitingPlayers.add(player);
            player.sendMessage("Esperando a otro jugador...");
        } else {
            ClientHandler opponent = waitingPlayers.remove(0);

            player.setOpponent(opponent);
            opponent.setOpponent(player);

            player.sendMessage("¡Se encontró un oponente! Luchas contra " + opponent.getPlayer().getName());
            opponent.sendMessage("¡Se encontró un oponente! Luchas contra " + player.getPlayer().getName());

            // ✅ Aquí empezamos a contar tiempo del Ranking
            Ranking ranking = Ranking.crear(player.getPlayer(), opponent.getPlayer());
            partidas.put(player, ranking);
            partidas.put(opponent, ranking);

            player.sendMessage("comandos disponibles: | Q: ATTACK_1 | W: ATTACK_2 | E: BLOCK_ATTACK | 1: HEALING_SPELL | 2: CHARGE_SPELL | 3: DEFENSIVE_SPELL | 4: STAMINA_SPELL | T: STATUS | P: EXIT |");
            opponent.sendMessage("comandos disponibles: | Q: ATTACK_1 | W: ATTACK_2 | E: BLOCK_ATTACK | 1: HEALING_SPELL | 2: CHARGE_SPELL | 3: DEFENSIVE_SPELL | 4: STAMINA_SPELL | T: STATUS | P: EXIT |");
        }
    }

    // ✅ Método que imprime el Ranking cuando termina la partida
    public static void mostrarRanking(ClientHandler ganador, ClientHandler perdedor) {
        Ranking ranking = partidas.get(ganador);
        if (ranking != null) {
            System.out.println("===== RANKING DE LA PARTIDA =====");
            ranking.mostrarRanking(ganador.getPlayer());
            partidas.remove(ganador);
            partidas.remove(perdedor);
        }
    }
}
