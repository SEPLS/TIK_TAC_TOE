package br.com.murillo.jogovelha.core;

import br.com.murillo.jogovelha.ui.UI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class InputThread implements Runnable{
    private String threadName;
    private Thread t;
    Socket socket;
    String stringOut;
    public InputThread(String name){
        this.threadName = name;
    }
    @Override
    public void run() {
        ObjectInputStream ois = null;
        while(true) {
            try {
                ois = new ObjectInputStream(socket.getInputStream());
                stringOut = (String) ois.readObject();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void start() throws IOException {
        if(t == null){
            t = new Thread(this, threadName);
            t.start();
        }
    }
}
