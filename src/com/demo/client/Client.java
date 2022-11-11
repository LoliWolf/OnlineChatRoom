package com.demo.client;

import com.demo.chatbox.ChatBox;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

public class Client {
    public static String username = "user";
    public static String password = "123";

    public static ChatBox chatBox;


    public static void login() {
        JFrame frame = new JFrame("Client Login");
        frame.setLayout(null);
        frame.setSize(600, 300);
        frame.setLocation(660, 390);
        frame.getContentPane().setBackground(new Color(217, 215, 217));

        JLabel userNameLabel = new JLabel("用户名");
        userNameLabel.setBounds(200, 50, 50, 30);
        frame.add(userNameLabel);
        JTextField userNameTextField = new JTextField("user");
        userNameTextField.setBounds(255, 50, 150, 30);
        frame.add(userNameTextField);

        JLabel passwordLabel = new JLabel("密码");
        passwordLabel.setBounds(200, 100, 50, 30);
        frame.add(passwordLabel);
        JTextField passwordTextField = new JPasswordField("123");
        passwordTextField.setBounds(255, 100, 150, 30);
        frame.add(passwordTextField);

        JButton loginButton = new JButton("登录");
        loginButton.setBounds(270, 150, 60, 30);
        loginButton.addActionListener(e -> {
            try {
                authentication(frame, userNameTextField.getText(), passwordTextField.getText());
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        });
        frame.add(loginButton);
        frame.setVisible(true);
    }

    public static void authentication(JFrame frame, String username, String password) throws IOException, InterruptedException {
        username = username.trim();
        if (Client.username.equals(username) && Client.password.equals(password)) {
            frame.setVisible(false);
            chatBox();
        } else {
            JLabel wrong = new JLabel("用户名或密码不正确");
            wrong.setBounds(250, 10, 150, 30);
            wrong.setForeground(Color.red);
            frame.add(wrong);
            frame.repaint();
        }
    }

    public static void chatBox() throws IOException, InterruptedException {
        Socket socket;
        try{
            socket = new Socket("localhost", 23456);
        }catch (Exception e){
            Thread.sleep(2000);
            chatBox();
            return;
        }
        OutputStream os = socket.getOutputStream();
        InputStream is = socket.getInputStream();
        chatBox = new ChatBox(os, "Client");

        new Thread(()->chatBox.create()).start();


        SwingWorker<String, String> isThread = new SwingWorker<String, String>() {
            @Override
            protected void process(List<String> messages) {
                Client.osUpdate(messages.get(0));
            }

            @Override
            protected void done() {
                super.done();
            }

            @Override
            protected String doInBackground() throws Exception {
                while (true) {
                    byte[] bytes = new byte[8192];
                    int len = is.read(bytes);
                    String message = new String(bytes, 0, len);
                    publish(message);
                }
            }
        };
        isThread.execute();


    }

    public static void osUpdate(String message) {
        chatBox.viewArea.append(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Timestamp(System.currentTimeMillis())) + "   " + chatBox.upLabel.getText().subSequence(5, chatBox.upLabel.getText().length()) + '\n' + message + '\n');
        chatBox.viewArea.paintImmediately(chatBox.viewArea.getBounds());
        chatBox.viewArea.setCaretPosition(chatBox.viewArea.getText().length());
    }

    public static void main(String[] args) {
        login();
    }
}
