import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.Behaviour; 
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

import java.io.PrintStream;
import java.util.ArrayList;

public class MainAgent extends Agent {

    private GUI gui;
    private AID[] playerAgents;
    private GameParametersStruct parameters = new GameParametersStruct();
    private double sharesPrice = 1.5;
    private Behaviour gameManagerBehaviour;

    @Override
    protected void setup() {
        gui = new GUI(this);
        System.setOut(new PrintStream(gui.getLoggingOutputStream()));

        updatePlayers();
        gui.logLine("Agent " + getAID().getName() + " is ready.");
    }

    public int updatePlayers() {
        gui.logLine("Updating player list...");

        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Player");
        template.addServices(sd);

        try {
            DFAgentDescription[] result = DFService.search(this, template);

            if (result.length > 0) {
                gui.logLine("Found " + result.length + " players.");
            } else {
                gui.logLine("No players found.");
                return 0;
            }

            playerAgents = new AID[result.length];
            for (int i = 0; i < result.length; ++i) {
                playerAgents[i] = result[i].getName();
            }

            String[] playerNames = new String[playerAgents.length];
            for (int i = 0; i < playerAgents.length; i++) {
                playerNames[i] = playerAgents[i].getName();
                gui.addRow(playerAgents[i].getName(), "x", "x", "x", "x", "x");
            }

            gui.setPlayersUI(playerNames);
            return result.length;

        } catch (FIPAException fe) {
            gui.logLine("Error while searching for players: " + fe.getMessage());
            return 0;
        }
    }

    public int newGame() {
        gui.logLine("Starting a new game...");
        this.gameManagerBehaviour = new GameManager(); 
        addBehaviour(this.gameManagerBehaviour);
        return 0;
    }

    private boolean stopGame = false;
    private boolean paused = false;

    public void pauseGame() throws InterruptedException {
        paused = true;
        gui.logLine("Game is paused.");
        // Thread.sleep(1000); // This line throws InterruptedException
    }


    public void continueGame() {
        if (paused) {
            paused = false;
            gui.logLine("Game is resuming.");
            synchronized (gameManagerBehaviour) {
                gameManagerBehaviour.notifyAll();
            }
        } else {
            gui.logLine("Game is already running.");
        }
    }


    private class GameManager extends SimpleBehaviour {

        private final ArrayList<PlayerInformation> players = new ArrayList<>();

        @Override
        public void action() {
            // a lógica de 'paused' pode ser removida se você não for usar
            synchronized (this) { 
                while (paused) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            players.clear();
            int lastId = 0;
            for (AID a : playerAgents) {
                players.add(new PlayerInformation(a, lastId++));
            }

            // Envia a configuração inicial para os jogadores
            for (PlayerInformation player : players) {
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.setContent("Id#" + player.id + "#" + parameters.N + "," + parameters.R + "," + parameters.F);
                msg.addReceiver(player.aid);
                send(msg);
            }

            // --- loop principal do jogo
            for (int currentRound = 1; currentRound <= parameters.R; currentRound++) {
                gui.leftPanelRoundsLabel.setText(currentRound + "/" + parameters.R);

                for (int i = 0; i < players.size(); i++) {
                    for (int j = i + 1; j < players.size(); j++) {
                        playGame(players.get(i), players.get(j));
                    }
                }
                
                gui.logLine("--- Round " + currentRound + " Finished ---");
                gui.updateTable(players); // Atualiza a GUI com os novos scores
            }

            // Finaliza o jogo
            if (!stopGame) {
                gameOver(players);
            }
            gui.updateTable(players);
        }

        private void gameOver(ArrayList<PlayerInformation> players) {
            players.forEach(player -> {
                String msgContent = String.format("GameOver#%d#%d", player.id, player.totalPayoff);

                gui.logLine(msgContent);

                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(player.aid);
                msg.setContent(msgContent);
                send(msg);
            });
        }

        // private void processSharesOperation(ACLMessage decisionMessage, PlayerInformation player) {
        //     try {
        //         gui.logLine(String.format("Main Received decision %s from %s",
        //                 decisionMessage.getContent(),
        //                 decisionMessage.getSender().getName()));

        //         String[] decisionSplit = decisionMessage.getContent().split("#");
        //         String decision = decisionSplit[0];

        //         if (decision.equals("Action")) {
        //             String move = decisionSplit[1];
        //             gui.logLine("Player made a move: " + move);

        //             if (move.equals("C")) {
        //                 player.decisionC++;
        //             } else if (move.equals("D")) {
        //                 player.decisionD++;
        //             } else {
        //                 gui.logLine("Invalid move received: " + move);
        //             }

        //         } else if (decision.equals("Buy") || decision.equals("Sell")) {
        //             double assetsToOperate = Double.parseDouble(decisionSplit[1]);

        //             synchronized (this) {
        //                 switch (decision) {
        //                     case "Buy":
        //                         player.buyShares(assetsToOperate, sharesPrice);
        //                         break;
        //                     case "Sell":
        //                         player.sellShares(assetsToOperate, sharesPrice, parameters.F);
        //                         break;
        //                     default:
        //                         gui.logLine("Invalid decision received: " + decision);
        //                         break;
        //                 }
        //             }
        //         } else {
        //             gui.logLine("Unexpected decision type: " + decision);
        //         }

        //         sendAccounting(player);
        //         gui.updateTable(players);

        //     } catch (Exception e) {
        //         gui.logLine("Error processing shares operation: " + e.getMessage());
        //     }
        // }

        // public void sendAccounting(PlayerInformation player) {
        //     ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        //     msg.addReceiver(player.aid);
        //     msg.setContent(String.format("Accounting#%d#%.2f#%.2f", player.id, player.currentReward, player.Shares));
        //     send(msg);
        // }

        // public double getIndexValue(int round) {
        //     return 7.35;
        // }

        // public double getInflationRate(int round) {
        //     return 0.05;
        // }

        private void playGame(PlayerInformation player1, PlayerInformation player2) {
            ACLMessage newGameMsg = new ACLMessage(ACLMessage.INFORM);
            newGameMsg.addReceiver(player1.aid);
            newGameMsg.addReceiver(player2.aid);
            newGameMsg.setContent("NewGame#" + player1.id + "," + player2.id);
            send(newGameMsg);

            ACLMessage actionRequest1 = new ACLMessage(ACLMessage.REQUEST);
            actionRequest1.addReceiver(player1.aid);
            actionRequest1.setContent("Action");
            send(actionRequest1);

            ACLMessage move1 = blockingReceive(5000);
            if (move1 == null) {
                gui.logLine("Player " + player1.id + " did not respond in time.");
                return;
            }
            String pos1 = move1.getContent().split("#")[1];
            player1.incrementDecision(pos1);

            ACLMessage actionRequest2 = new ACLMessage(ACLMessage.REQUEST);
            actionRequest2.addReceiver(player2.aid);
            actionRequest2.setContent("Action");
            send(actionRequest2);

            ACLMessage move2 = blockingReceive(5000);
            if (move2 == null) {
                gui.logLine("Player " + player2.id + " did not respond in time.");
                return;
            }
            String pos2 = move2.getContent().split("#")[1];
            player2.incrementDecision(pos2);

            int[] payoff = getReward(pos1, pos2);

            ACLMessage resultsMsg = new ACLMessage(ACLMessage.INFORM);
            resultsMsg.addReceiver(player1.aid);
            resultsMsg.addReceiver(player2.aid);
            resultsMsg.setContent(String.format("Results#%d,%d#%s,%s#%d,%d",
                    player1.id, player2.id, pos1, pos2, payoff[0], payoff[1]));
            send(resultsMsg);

            player1.addToTotalPayoff(payoff[0]);
            player2.addToTotalPayoff(payoff[1]);
        }

        // private void roundOver(PlayerInformation player) {
        //     player.finishRound(parameters.inflationRate);
        //     String roundOverMessage = String.format("RoundOver#%d#%.2f#%.2f#%.2f#%.2f#%.2f",
        //             player.id, player.roundReward, player.currentReward,
        //             parameters.inflationRate, player.Shares, sharesPrice);

        //     gui.logLine(roundOverMessage + " round is over");

        //     ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        //     msg.addReceiver(player.aid);
        //     msg.setContent(roundOverMessage);
        //     send(msg);
        // }

        public int[] getReward(String player1, String player2) {
            if ("C".equals(player1) && "C".equals(player2)) return new int[]{2, 2};
            if ("C".equals(player1) && "D".equals(player2)) return new int[]{0, 4};
            if ("D".equals(player1) && "C".equals(player2)) return new int[]{4, 0};
            return new int[]{0, 0};
        }

        @Override
        public boolean done() {
            return true;
        }
    }


    public static class GameParametersStruct {
        int N = 2; // Numero de Jogadores
        int R = 500; // Numero de Rodadas
        double F = 0.01;
    }
    
    public static class PlayerInformation {
        AID aid;
        int id;
        int decisionD = 0;
        int decisionC = 0;
        int totalPayoff = 0; // Score total

        public PlayerInformation(AID a, int i) {
            aid = a;
            id = i;
        }

        // Renomeado de addToRoundReward para algo mais geral
        public void addToTotalPayoff(int payoffAmount) {
            totalPayoff += payoffAmount;
        }

        public void incrementDecision(String move) {
            if ("C".equals(move)) {
                decisionC++;
            } else if ("D".equals(move)) {
                decisionD++;
            }
        }
    }
}
