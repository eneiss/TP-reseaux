
package chat_TCP.src;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Classe gérant l'IHM du client de chat,
 * permettant l'envoi de messages aux autres clients,
 * ainsi que l'affichage des messages reçus.
 * @author Emma Neiss, Yann Dupont
 * @see ChatClient
 * @see ClientReceiverThread
 */
public class ClientIHM extends Frame implements ActionListener {

    /**
     * Composant affichant les messages envoyés et reçus,
     * incluant l'historique reçu au moment de la connexion.
     */
    private TextArea histo;

    /**
     * Composant affichant les notifications de connexion et déconnexion.
     */
    private TextArea notifLog;

    /**
     * Composant où l'utilisateur entre les messages à envoyer.
     */
    private TextField inputField;

    /**
     * Constructeur de ClientIHM, initialisant la fenêtre
     */
    ClientIHM(){
        setSize(800,450);
        setLayout(null);
        setVisible(true);
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

    /**
     * Affiche un message sur le champ d'historique.
     * @param line          Le message à afficher, sans métadonnées
     * @param senderId      L'ID de l'expéditeur du message
     */
    public void printMessage(String line, String senderId){
        System.out.println(senderId + " : " + line);
        histo.append(senderId + " : " + line + "\n");
    }

    /**
     * Affiche une notification sur le champ de notifications.
     * @param line          La notification à afficher
     */
    public void displayNotif(String line){
        System.out.println(line);
        notifLog.append(line + "\n");
    }

    /**
     * Gère la saisie de message par l'utilisateur.
     * Appelé lorsque l'utilisateur appuie sur Enter.
     * @param event     l'évènement déclenché par l'utilisateur
     */
    public void actionPerformed(ActionEvent event){
        System.out.println(inputField.getText());
        ChatClient.sendMessage(inputField.getText());
        inputField.setText("");
    }

}
