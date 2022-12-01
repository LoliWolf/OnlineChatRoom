package com.demo.chatbox;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Vector;

public class ChatBox {
    public JFrame frame;

    public JTextArea viewArea;

    public JLabel upLabel;

    public String chatBoxName;

    public String chatObject;

    public JList<String> fList;

    private final ArrayList<OutputStream> osChain;

    private final String port;

    public ArrayList<OutputStream> getOsChain() {
        return osChain;
    }

    public ChatBox(String name) {
        osChain = new ArrayList<>();
        chatBoxName = name + " 23456";
        port = "23456";
        frame = new JFrame("网络聊天室 " + chatBoxName);
        chatObject = "Client";
    }

    public ChatBox(OutputStream os, String port) {
        osChain = new ArrayList<>();
        osChain.add(os);
        chatObject = "Server";
        this.port = port;
        chatBoxName = "Client " + port;
        frame = new JFrame("网络聊天室 " + chatBoxName);
    }

    public void create() {
        //frame
        frame.setLayout(null);
        frame.setSize(1280, 720);
        frame.setLocation(320, 180);
        frame.getContentPane().setBackground(new Color(217, 215, 217));
        //当前对话用户名
        upLabel = new JLabel("当前对话:" + chatObject);
        upLabel.setName("upLabel");
        upLabel.setFont(new Font("宋体", upLabel.getFont().getStyle(), 40));
        upLabel.setBounds(20, 5, 900, 50);
        frame.add(upLabel);
        //聊天显示框
        viewArea = new JTextArea();
        viewArea.setEditable(false);
        JScrollPane viewScrollPane = new JScrollPane(viewArea);
        viewScrollPane.setName("viewScrollPane");
        viewScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        viewScrollPane.setBounds(10, 65, 1100, 450);
        frame.add(viewScrollPane);
        //好友列表名字
        JLabel friendTagLabel = new JLabel("我的好友:");
        friendTagLabel.setName("friendTagLabel");
        friendTagLabel.setFont(new Font("宋体", upLabel.getFont().getStyle(), 20));
        friendTagLabel.setBounds(1120, 15, 100, 40);
        frame.add(friendTagLabel);
        //好友列表
        Vector<String> flist = new Vector<>();
        flist.add(chatObject);
        JList<String> friendList = new JList<>(flist);
        this.fList = friendList;
        friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        friendList.addListSelectionListener(e -> {
//            String selectedValue = ((JList<?>) e.getSource()).getSelectedValue().toString();
//            upLabel.setText("当前对话:" + selectedValue);
//            frame.repaint();
//        });
        JScrollPane flistScrollPanel = new JScrollPane(friendList);
        flistScrollPanel.setName("flistScrollPanel");
        flistScrollPanel.setBounds(1120, 65, 135, 450);
        frame.add(flistScrollPanel);
        //输入框
        JTextArea inputTextArea = new JTextArea();
        inputTextArea.setLineWrap(true);
        JScrollPane input = new JScrollPane(inputTextArea);
        input.setName("input");
        input.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        input.setBounds(10, 525, 1100, 150);
        frame.add(input);
        //发送按钮
        JButton sendButton = new JButton("发送");
        sendButton.setName("sendButton");
        sendButton.setBackground(Color.LIGHT_GRAY);
        ;
        sendButton.setBounds(1130, 560, 120, 80);
        sendButton.addActionListener(e -> {
            String message = inputTextArea.getText();
            if ("".equals(message)) {
                return;
            }
            inputTextArea.setText("");
            if ("Client".equals(chatObject)) {
                viewArea.append(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Timestamp(System.currentTimeMillis())) + "   " + chatBoxName + '\n' + message + '\n');
                viewArea.paintImmediately(viewArea.getBounds());
                viewArea.setCaretPosition(viewArea.getText().length());
            }
            try {
                message = chatBoxName+message;
                for(OutputStream os: osChain){
                    os.write(message.getBytes());
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        frame.add(sendButton);
        frame.setVisible(true);
    }
}
