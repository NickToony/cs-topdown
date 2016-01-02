package com.nicktoony.cstopdown.server;

import com.nicktoony.cstopdown.services.Logger;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintStream;

public class ServerUI implements Logger {

    public interface UIListener {
        public void onClose();
    }

    private UIListener listener;
    private PrintStream con;

    public ServerUI(UIListener listener) {
        this.listener = listener;
        setup();
    }

    private void setup() {
        JFrame frame = new JFrame("Test");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(true);
        JTextArea textArea = new JTextArea(50,80);

        con = new PrintStream(new TextAreaOutputStream(textArea));
        //System.setOut(con);
        //System.setErr(con);

        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setFont(Font.getFont(Font.SANS_SERIF));
        JScrollPane scroller = new JScrollPane(textArea);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel inputpanel = new JPanel();
        inputpanel.setLayout(new FlowLayout());
        JTextField input = new JTextField(20);
        JButton button = new JButton("Enter");
        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        panel.add(scroller);
        inputpanel.add(input);
        inputpanel.add(button);
        panel.add(inputpanel);
        frame.getContentPane().add(BorderLayout.CENTER, panel);
        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        frame.setResizable(false);
        input.requestFocus();

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                listener.onClose();
            }
        });
    }

    @Override
    public void log(String string) {
        System.out.println(string);
        con.println(string);
    }

    @Override
    public void log(Exception exception) {
        exception.printStackTrace();
        con.println(exception);
    }
}