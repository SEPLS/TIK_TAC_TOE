package br.com.murillo.jogovelha.core;

import br.com.murillo.jogovelha.ui.UI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException, InterruptedException {
        InetAddress host = InetAddress.getLocalHost();
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;

        socket = new Socket(host.getHostName(), 1337);
        //oos = new ObjectOutputStream(socket.getOutputStream());
        UI.printText("Sending request to Server Socket");
        ois = new ObjectInputStream(socket.getInputStream());
        String message = (String) ois.readObject();
        UI.printText(message);
        while(true){
            String mess = UI.readInputClient("", socket);

            if(mess.equalsIgnoreCase("exit")) break;
        }
        ois.close();
        oos.close();
        Thread.sleep(100);

    }
}
