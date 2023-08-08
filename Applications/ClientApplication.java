package Applications;

import Users.Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**/

public class ClientApplication extends JFrame implements ActionListener
{
    private boolean busy;
    private final JTextField tClientName;
    private final JButton bCommit;
    public ClientApplication()
    {
        setSize(500,250);
        setTitle("Elliptic Curve Qu Vanstone - Client");
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
        JLabel lClientName = new JLabel("Identyfikator: ");
        lClientName.setBounds(50,50,150,25);
        add(lClientName);
        tClientName = new JTextField("Client");
        tClientName.setBounds(200, 50, 250, 25);
        add(tClientName);
        bCommit = new JButton("Zatwierdź");
        bCommit.setBounds(350, 175, 100, 25);
        add(bCommit);
        bCommit.addActionListener(this);
        busy = false;
    }
    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
        if (source.equals(bCommit) && !busy)
        {
            Client client = new Client(tClientName.getText(), true);
            busy = true;
            try
            {
                client.setup(true);
                client.ECQV_certRequest(true);
                client.ECQV_certPublicKeyExtraction(true);
                client.stop(true);
                busy = false;
            } catch (Exception error)
            {
                JOptionPane.showMessageDialog(this,error.getMessage(),"Uwaga!", JOptionPane.ERROR_MESSAGE);
                busy = false;
            }
        }
    }
    public static void main(String[] args)
    {
        ClientApplication clientApplication = new ClientApplication();
        clientApplication.repaint();
    }

}
