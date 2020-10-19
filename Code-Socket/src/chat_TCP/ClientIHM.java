
package chat_TCP;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class ClientIHM extends Frame implements ActionListener {

    private TextArea histo, notifLog;
    private TextField inputField;

    ClientIHM(){
        setSize(800,450);//frame size 300 width and 300 height
        setLayout(null);//no layout manager
        setVisible(true);//now frame will be visible, by default not visible
        setTitle("Client Chat");
        setResizable(false);
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                ChatClient.disconnect();
                System.exit(0);
            }
        });

        histo = new TextArea(10, 50);
        histo.setBounds(30,30,740,340);
        add(histo);

        notifLog = new TextArea("", 10, 50, TextArea.SCROLLBARS_VERTICAL_ONLY);
        notifLog.setBounds(530,370,240,60);
        add(notifLog);

        inputField = new TextField(50);
        inputField.setBounds(30,370,500,60);
        inputField.addActionListener(this);
        add(inputField);
    }

    public void printMessage(String line, String senderId){
        System.out.println(senderId + " : " + line);
        histo.append(senderId + " : " + line + "\n");
    }

    public void displayNotif(String line){
        System.out.println(line);
        notifLog.append(line + "\n");
    }

    public void actionPerformed(ActionEvent event){
        System.out.println(inputField.getText());
        ChatClient.sendMessage(inputField.getText());
        inputField.setText("");
    }

}
