package Controller;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

import App.GameServer;
import Model.ModelWarrior;
import Model.Characters.Arquero;
import Model.Characters.Boxeador;
import Model.Characters.Espadachin;
import Model.Characters.Mago;
import Model.Characters.Paladin;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ModelWarrior player;
    private ClientHandler opponent;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public ModelWarrior getPlayer() {
        return player;
    }

    public void setOpponent(ClientHandler opponent) {
        this.opponent = opponent;
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }

    @Override
    public void run() {
        try {
            // Inicializamos variables de IO
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Pedir nombre y personaje
            out.println("Escribe tu nombre:");
            out.println("");
            out.println("--------------------------------------------------------------------");
            out.println("");
            String name = in.readLine();
            out.println("");
            out.println("--------------------------------------------------------------------");
            out.println("");

            out.println("Selecciona tu personaje: | 1. Epadachin | 2. Arquero | 3. Paladin | 4. Mago | 5. Boxeador |");
            out.println("");
            out.println("--------------------------------------------------------------------");
            out.println("");
            int choice = Integer.parseInt(in.readLine());

            switch (choice) {
                case 1: player = new Espadachin(name); break;
                case 2: player = new Arquero(name); break;
                case 3: player = new Paladin(name); break;
                case 4: player = new Mago(name); break;
                case 5: player = new Boxeador(name); break;
            }

            out.println("Has seleccionado a " + player.getName());
            out.println("");
            out.println("--------------------------------------------------------------------");
            out.println("");

            System.out.println(player.getName() + " se ha conectado.");
            
            // Meter al jugador en espera
            GameServer.addWaitingPlayer(this);

            // Esperar comandos
            String command;
            while ((command = in.readLine()) != null && player.isAlive()) {
                out.print("> ");
                handleCommand(command.trim().toUpperCase());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleCommand(String command) {
        if (opponent == null) {
            out.println("Aún no tienes oponente.");
            out.println("");
            out.println("--------------------------------------------------------------------");
            out.println("");
            return;
        }

        switch (command) {
            case "Q":
                if (opponent.getPlayer().isAlive()) {
                    player.attack_1(opponent.getPlayer());
                    out.println("Atacaste a " + opponent.getPlayer().getName());
                    checkVictory();
                }
                break;

            case "W":
                if (opponent.getPlayer().isAlive()) {
                    player.attack_2(opponent.getPlayer());
                    out.println("Atacaste a " + opponent.getPlayer().getName());
                    checkVictory();
                }
                break;

            case "E":
                player.blockAttack();
                break;
                
            case "1":
                player.healingSpell();
                break;

            case "2":
                player.chargeSpell();
                break;

            case "3":
                player.defensiveSpell();
                break;

            case "4":
                player.StaminaSpell();
                break;

            case "T":
                out.println(player.estadisticasActuales(player));
                break;

            case "EXIT":
                out.println("Te desconectaste.");
                try { 
                    player.stopRegen();
                    socket.close(); 
                } catch (IOException ignored) {}
                break;

            default:
                out.println("Comando no reconocido. Usa ATTACK, HEALING, CHARGE o EXIT.");
        }
    }

   private void checkVictory() {
    if (!opponent.getPlayer().isAlive()) {
        out.println("¡Ganaste el combate!");
        opponent.sendMessage("Has sido derrotado por " + player.getName());

        // ✅ Notificamos al servidor para mostrar ranking
        App.GameServer.mostrarRanking(this, opponent);
    }
}

}
