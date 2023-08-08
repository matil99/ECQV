package Applications;

import Users.Server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CountDownLatch;

public class ServerApplication extends JFrame implements ActionListener
{
    private final CountDownLatch latch;
    private boolean done;
    /*-----Elementy GUI-----*/
    private final JTextField tServerIdentifier;
    private final JButton bCommit;
    private JButton bStop;
    private final JComboBox cEllipticCurve;
    private final JComboBox cHashName;
    public ServerApplication()
    {
        latch = new CountDownLatch(1);
        setSize(500,250);
        setTitle("Elliptic Curve Qu Vanstone - Server");
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(false);
        JLabel lServerIdentifierLeft = new JLabel("Identyfikator: ");
        lServerIdentifierLeft.setBounds(50,50,150,25);
		add(lServerIdentifierLeft);
        tServerIdentifier = new JTextField("Urząd certyfikacyjny");
        tServerIdentifier.setBounds(200, 50, 250, 25);
        add(tServerIdentifier);
        JLabel lEllipticCurveLeft = new JLabel("Używana krzywa: ");
        lEllipticCurveLeft.setBounds(50,100,150,25);
        add(lEllipticCurveLeft);
        cEllipticCurve = new JComboBox();
        cEllipticCurve.setBounds(200, 100, 250, 25);
        cEllipticCurve.addItem("secp256r1");
        cEllipticCurve.addItem("secp256k1");
        cEllipticCurve.addItem("secp384r1");
        cEllipticCurve.addItem("secp521r1");
        add(cEllipticCurve);
        JLabel lHashNameLeft = new JLabel("Używana funkcja skrótu: ");
        lHashNameLeft.setBounds(50,150,150,25);
        add(lHashNameLeft);
        cHashName = new JComboBox();
        cHashName.setBounds(200, 150, 250, 25);
        cHashName.addItem("SHA-256");
        cHashName.addItem("SHA-384");
        cHashName.addItem("SHA-512");
        cHashName.addItem("SHA3-256");
        cHashName.addItem("SHA3-384");
        cHashName.addItem("SHA3-512");
        add(cHashName);
        bCommit = new JButton("Zatwierdź");
        bCommit.setBounds(350, 175, 100, 25);
        add(bCommit);
        bCommit.addActionListener(this);
    }
    public void run()
    {
        try
        {
            latch.await(); /*Semafor*/
            String identifier = tServerIdentifier.getText();
            String curveName = cEllipticCurve.getSelectedItem().toString();
            String hashName = cHashName.getSelectedItem().toString();
            Server server = new Server(identifier, curveName, hashName, true);
            server.setup(true);
            server.start(true);
            while (!done)
            {
                server.connect(true);
                server.ECQV_certGenerate(true);
                server.tearConnection(true);
            }
            server.stop(true);
            dispose();
        } catch(Exception e)
        {
            JOptionPane.showMessageDialog(this, e.getMessage(),"Uwaga!", JOptionPane.ERROR_MESSAGE);
        }
    }
    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
        if (source.equals(bCommit))
        {
            remove(bCommit);
            String identifier = tServerIdentifier.getText();
            remove(tServerIdentifier);
            JLabel lServerIdentifierRight = new JLabel(identifier);
            lServerIdentifierRight.setBounds(200,50,250,25);
            add(lServerIdentifierRight);
            String curveName = cEllipticCurve.getSelectedItem().toString();
            remove(cEllipticCurve);
            JLabel lEllipticCurveRight = new JLabel(curveName);
            lEllipticCurveRight.setBounds(200, 100, 250, 25);
            add(lEllipticCurveRight);
            String hashName = cHashName.getSelectedItem().toString();
            remove(cHashName);
            JLabel lHashNameRight = new JLabel(hashName);
            lHashNameRight.setBounds(200, 150, 250, 25);
            add(lHashNameRight);
            bStop = new JButton("Zakończ");
            bStop.setBounds(350, 175, 100, 25);
            add(bStop);
            bStop.addActionListener(this);
            latch.countDown();
            repaint();
        }
        if (source.equals(bStop))
        {
            done = true;
        }
    }

    public static void main(String[] args)
    {
        ServerApplication serverApplication = new ServerApplication();
        serverApplication.repaint();
        serverApplication.run();
    }

}
