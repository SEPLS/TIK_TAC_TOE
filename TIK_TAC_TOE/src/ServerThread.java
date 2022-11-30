package br.com.murillo.jogovelha.core;

import br.com.murillo.jogovelha.ui.UI;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerThread implements Runnable{
    private Thread t;
    private InputThread inputThread;
    private String threadName;
    public Socket socket;
    public String messageIn;
    public String messageOut;
    public ServerThread(String name){
        this.threadName = name;
    }
    @Override
    public void run() {
        while(true) {

            if (messageIn != null) {
                String s = messageIn;
                messageIn = null;

                try {
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(s);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }


            if (inputThread.stringOut != null) {
                messageOut = inputThread.stringOut;
                inputThread.stringOut = null;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void start() {
        if(t == null){
            try {
                inputThread = new InputThread(threadName + "Input");
                inputThread.socket = socket;
                inputThread.start();
                t = new Thread(this, threadName);
                t.start();

                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(UI.printGameTitle());
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }
}
