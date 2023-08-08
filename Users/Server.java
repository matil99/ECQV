package Users;

import Certificate.X509;
import Certificate.X509Factory;
import CryptoUtilities.EllipticCurve;
import CryptoUtilities.EllipticCurvePoint;
import CryptoUtilities.HashFunction;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Server
{
    private final String serverIdentifier; /*Identyfikator urzędu certyfikującego*/
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter output;
    private BufferedReader input;
    private final EllipticCurve ellipticCurve; /*Krzywa eliptyczna wykorzystywana do wyznaczenia certyfikatu*/
    private final Random rng;
    private final String hashName;  /*Nazwa wykorzystywanej funkcji skrótu*/
    private final X509Factory x509Factory; /*Fabryka certyfiaktów*/
    private BigInteger serverPrivateKey;
    private EllipticCurvePoint serverPublicKey;
    public Server(String identifier, String curveName, String hashName, boolean condition) throws Exception
    {
        this.serverIdentifier = identifier;
        this.x509Factory = new X509Factory(identifier);
        log("Utworzono fabrykę certyfikatów", condition);
        try {
            this.ellipticCurve = new EllipticCurve(curveName);
            log("Zainicjowano krzywą: " + ellipticCurve, condition);
            this.rng = new Random();
            log("Zainicjowano generator losowy", condition);
            this.hashName = hashName;
        } catch (Exception e) {
            throw new Exception("Zadana krzywa nie istnieje. Nie udało uruchomić się serwera.");
        }
    }
    public void setup(boolean condition) throws Exception
    {
        String pathToKeys = ".\\resources\\Keys\\";
        if (!new File(pathToKeys + serverIdentifier + "_" + ellipticCurve.getName() + ".txt").exists())
        {
            log("Urząd nie posiada kluczy. Generacja kluczy", condition);
            this.serverPrivateKey = new BigInteger(ellipticCurve.getN().bitCount(), rng).mod(ellipticCurve.getN());
            log("Wygenerowany klucz prywatny: " + serverPrivateKey.toString(16), condition);
            this.serverPublicKey = ellipticCurve.getG().mult(serverPrivateKey);
            log("Wygenerowany klucz publiczny: " + serverPublicKey, condition);
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathToKeys + serverIdentifier + "_" + ellipticCurve.getName() + ".txt"), StandardCharsets.UTF_8)))
            {
                writer.write(ellipticCurve.getName() + "\n");
                writer.write(serverPrivateKey.toString(16) + "\n");
                writer.write(serverPublicKey.getX().toString(16) + ";" + serverPublicKey.getY().toString(16));
                log("Zapisano klucze do pliku", condition);
            }
        }
        else
        {
            log("Urząd posiada klucze. Odczyt kluczy z pliku", condition);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(pathToKeys + serverIdentifier + "_" + ellipticCurve.getName() + ".txt")))) {
                if (!reader.readLine().equals(ellipticCurve.getName())) throw new Exception("Wrong key pair");
                this.serverPrivateKey = new BigInteger(reader.readLine(), 16);
                log("Odczytany klucz prywatny: " + serverPrivateKey.toString(16), condition);
                String[] point = reader.readLine().split(";");
                this.serverPublicKey = new EllipticCurvePoint(new BigInteger(point[0], 16), new BigInteger(point[1], 16), ellipticCurve);
                log("Odczytany klucz publiczny: " + serverPublicKey, condition);
            }
        }
    }
    public void start(boolean condition) throws Exception
    {
        this.serverSocket = new ServerSocket(6666);
        log("Serwer uruchomiony", condition);
    }
    public void connect(boolean condition) throws Exception
    {
        log("Oczekiwanie na klienta", condition);
        this.clientSocket = serverSocket.accept();
        this.output = new PrintWriter(clientSocket.getOutputStream(), true);
        this.input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        log("Połączono z nowym klientem", condition);
        output.println("0;" + ellipticCurve.getName() + ";" + serverPublicKey.getX() + ";" + serverPublicKey.getY());
        log("Opublikowano klucz publiczny: " + serverPublicKey, condition);
    }
    public void run(boolean condition) throws Exception
    {
        this.serverSocket = new ServerSocket(6666);
        log("Serwer uruchomiony", condition);
        log("Oczekiwanie na klienta", condition);
        this.clientSocket = serverSocket.accept();
        this.output = new PrintWriter(clientSocket.getOutputStream(), true);
        this.input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        log("Połączono z nowym klientem", condition);
        output.println("0;" + ellipticCurve.getName() + ";" + serverPublicKey.getX() + ";" + serverPublicKey.getY());
        log("Opublikowano klucz publiczny: " + serverPublicKey, condition);
        ECQV_certGenerate(condition);
        tearConnection(condition);
        stop(condition);
    }
    public void ECQV_certGenerate(boolean condition) throws Exception
    {
        BigInteger e = null, k = null;
        X509 cert = null;
        String[] msg = input.readLine().split(";");
        if (!msg[0].equals("1")) throw new Exception("Typ wiadomości inny niż oczekiwano. Połączenie zerwane");
        String clientIdentifier = msg[1];
        log("Odebrano żądanie certyfikatu od: " + clientIdentifier, condition);
        EllipticCurvePoint R = new EllipticCurvePoint(new BigInteger(msg[2], 10), new BigInteger(msg[3], 10), ellipticCurve);
        log("Odebrany punkt R: " + R, condition);
        EllipticCurvePoint checkPoint = new EllipticCurvePoint(ellipticCurve);
        while (checkPoint.equals(new EllipticCurvePoint(ellipticCurve)))
        {
            k = new BigInteger(ellipticCurve.getN().bitCount(), rng).mod(ellipticCurve.getN());
            log("Wylosowano wartość k: " + k.toString(16), condition);
            EllipticCurvePoint tmpPoint = ellipticCurve.getG().mult(k);
            log("Wyznaczony punkt tymczasowy: " + tmpPoint, condition);
            EllipticCurvePoint P = R.add(tmpPoint);
            log("Wyznaczony punkt P: " + P, condition);
            cert = x509Factory.create(hashName, 4, clientIdentifier, "ECDH/" + ellipticCurve.getName(), P);
            log("Wygenerowano certyfikat", condition);
            e = new BigInteger(HashFunction.hash(hashName, x509Factory.encode(cert)), 16);
            log("Wyznaczona wartość e: " + e.toString(16), condition);
            checkPoint = P.mult(e).add(serverPublicKey);
        }
        BigInteger r = ((e.multiply(k)).add(serverPrivateKey)).mod(ellipticCurve.getN());
        log("Wyznaczona wartość r: " + r.toString(16), condition);
        output.println("2;" + r + ";" + x509Factory.encode(cert));
        log("Wysłano: (" + r.toString(16) + "; Certyfikat)", condition);
    }

    public void tearConnection(boolean condition) throws Exception
    {
        input.close();
        output.close();
        clientSocket.close();
        log("Zakończono połączenie z klientem", condition);
    }
    public void stop(boolean condition) throws Exception
    {
        serverSocket.close();
        log("Zakończono prace servera", condition);
    }

    public BigInteger getServerPrivateKey()
    {
        return serverPrivateKey;
    }

    public EllipticCurvePoint getServerPublicKey()
    {
        return serverPublicKey;
    }

    public EllipticCurve getEllipticCurve()
    {
        return ellipticCurve;
    }


    public void log(String msg, boolean condition)
    {
        if (condition)
        {
            System.out.println(serverIdentifier + ": " + msg + ".");
        }
    }
}
