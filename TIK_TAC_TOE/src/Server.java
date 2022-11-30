package br.com.murillo.jogovelha.core;

import br.com.murillo.jogovelha.game.Constants;
import br.com.murillo.jogovelha.score.FileScoreManager;
import br.com.murillo.jogovelha.score.ScoreManager;
import br.com.murillo.jogovelha.ui.UI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ClassNotFoundException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLOutput;


public class Server {
    private static ServerSocket server;
    private static int port = 1337;
    private static Socket[] sockets = new Socket[2];
    private Board board = new Board();
    private Player[] players = new Player[Constants.SYMBOL_PLAYERS.length];
    private int currentPlayerIndex = -1;
    private ScoreManager scoreManager;

    public static void main(String[] args) throws IOException, ClassNotFoundException{
        server = new ServerSocket(port);
        UI.printText("Waiting for Player1... ");
        sockets[0] = server.accept();
        UI.printText("Waiting for Player2... ");
        sockets[1] = server.accept();
        //ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        //String message = (String) ois.readObject();
        ServerThread[] threads = new ServerThread[2];
        ServerThread t1 = new ServerThread("Player1");
        ServerThread t2 = new ServerThread("Player2");
        t1.socket = sockets[0];
        t2.socket = sockets[1];
        threads[0] = t1;
        threads[1] = t2;
        UI.printText("2 Players joined");
        for(ServerThread st: threads){
            st.start();
            st.messageIn = "Hello World";
        }
        while(true){
            String message = "";
            for(ServerThread st: threads){
                if(st.messageOut != null){
                    message = st.messageOut;
                    st.messageOut = null;
                    System.out.println(message);
                }


            }

            if(message.equalsIgnoreCase("exit"))break;
        }

        //ois.close();
        //oos.close();
        //if(message.equalsIgnoreCase("exit")) ;
        UI.printText("Server shutting down");
        server.close();
    }
    public void play(ServerThread st) throws IOException {
        scoreManager = createScoreManager();


        for (int i = 0; i < players.length; i++) {
            players[i] = createPlayer(i);
        }

        boolean gameEnded = false;
        Player currentPlayer = nextPlayer();
        Player winner = null;

        while(!gameEnded) {
            board.print();

            boolean sequenceFound;
            try {
                sequenceFound = currentPlayer.play();

            } catch (InvalidMoveException e) {
                UI.printText("ERRO: " + e.getMessage());
                continue;
            }

            if (sequenceFound) {
                gameEnded = true;
                winner = currentPlayer;

            } else if (board.isFull()) {
                gameEnded = true;

            } else {
                currentPlayer = nextPlayer();
            }
        }

        if (winner == null) {
            UI.printText("O jogo terminou empatado");

        } else {
            UI.printText("O jogador '" + winner.getName() + "' venceu o jogo!");

            scoreManager.saveScore(winner);
        }

        board.print();
        UI.printText("Fim do Jogo!");
    }
    private Player createPlayer(int index) {
        String name = UI.readInput("Jogador " + (index + 1) + " =>");
        char symbol = Constants.SYMBOL_PLAYERS[index];
        Player player = new Player(name, board, symbol);

        Integer score = scoreManager.getScore(player);

        if (score != null) {
            UI.printText("O jogador '" + player.getName() + "' ja possui " + score + " vitoria(s)!");
        }

        UI.printText("O jogador '" + name + "' vai usar o simbolo '" + symbol + "'");

        return player;
    }

    private Player nextPlayer() {
		/*
		currentPlayerIndex++;

		if (currentPlayerIndex >= players.length) {
			currentPlayerIndex = 0;
		}
		*/

        currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
        return players[currentPlayerIndex];
    }

    private ScoreManager createScoreManager() throws IOException {
        return new FileScoreManager();
    }
}
