package reseau;
import java.awt.Point;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/* ______________________________________________________ */
/**
 * Fichier : Serveur.java
 *
 * Créé le 4 févr. 2014 à 14:26:04
 *
 * Auteur : NUNES Stephen
 */

/* ______________________________________________________ */
/** Serveur de l'application du pong en réseau utilisant le protocol UDP
 */
public class Serveur implements ProtocoleCommunication
{
	
	/** Instance unique de la classe */
	private static Serveur instance;
	
	/** Socket du serveur*/
	private DatagramSocket socketServeur;
	
	/** Adresse du client */
	private InetAddress adresseClient;

	/** Port du client */
	private int portClient = 0;
	
	/** Flux d'entrée*/
	private byte[] entree;
	
	/** Flux de sortie */
	private byte[] sortie;
	
	/* ______________________________________________________ */
	/** Constructeur privé du Serveur
	 * @throws SocketException Problème de création de la socket du serveur
	 */
	private Serveur() throws SocketException
	{
		entree = new byte[128];
		sortie = new byte[128];
		ouvrirConnexion();
		
	}
	
	/* ______________________________________________________ */
	/** Ouvre la socket du serveur
	 * @throws SocketException Problème de création de la socket du serveur
	 */
	private void ouvrirConnexion() throws SocketException
	{
		socketServeur = new DatagramSocket(4444);
	}

	/* ______________________________________________________ */
	/** Défini le timeout de la socket pour la rendre non-bloquante
	 * @param timeout Temps d'attente
	 * @throws SocketException socket débloquée
	 * @throws SocketTimeoutException socket timeout bloquée
	 */
	public void definirTimeout(int timeout) throws SocketException, SocketTimeoutException
	{
		socketServeur.setSoTimeout(timeout);
	}
	
	
	/* ______________________________________________________ */
	/** Permet d'obtenir l'instance unique du serveur
	 * @return l'instance unique de la classe
	 * @throws SocketException Problème de création de la socket du serveur
	 */
	public static Serveur getInstance() throws SocketException
	{
		if (instance == null)
		{
			instance = new Serveur();
		}
		return instance;
	}
	
	/* ______________________________________________________ */
	/** Ferme la socket du serveur
	 * @throws SocketException socket déjà fermée
	 */
	public void fermerConnexion() throws SocketException 
	{
		if (!socketServeur.isClosed())
		{
			socketServeur.close();
		}
		else
		{
			throw new SocketException("Socket du serveur déjà fermée");
		}
	}
	
	/* ______________________________________________________ */
	/** Permet d'envoyer les coordonées de la raquet passée en paramètre
	 * @param pointEnvoye Point envoyé
	 * @param isStarted Si la partie est commencée
	 * @param ball Coordonnées de la balle
	 * @param ballSpeed Vitesse de la balle
	 * @throws Exception Erreur d'entrée/sortie lors de l'envoi d'un paquet au client
	 */
	public void envoyerCoordonnees(Point pointEnvoye, boolean isStarted, Point ball, Point ballSpeed) throws Exception
	{
		sortie = new byte[128];
		Integer coordonneesX = new Integer(pointEnvoye.x);
		Integer coordonneesY = new Integer(pointEnvoye.y);
		Integer coordonneesBalleX = new Integer(ball.x);
		Integer coordonneesBalleY = new Integer(ball.y);
		Integer coordonneesBalleSpeedX = new Integer(ballSpeed.x);
		Integer coordonneesBalleSpeedY = new Integer(ballSpeed.y);
		Byte isStartedSended;
		if (isStarted)
		{
			isStartedSended = PARTIE_DEMARRE;
		}
		else
		{
			isStartedSended = PARTIE_ARRETE;
		}		
		String chaineEnvoye = isStartedSended.toString() + SEPARATEUR_CHAINE + coordonneesX.toString() + SEPARATEUR_CHAINE + coordonneesY.toString()
				+ SEPARATEUR_CHAINE + coordonneesBalleX + SEPARATEUR_CHAINE + coordonneesBalleY + SEPARATEUR_CHAINE + coordonneesBalleSpeedX + SEPARATEUR_CHAINE + coordonneesBalleSpeedY;
		sortie = chaineEnvoye.getBytes();
		DatagramPacket paquetEnvoye = new DatagramPacket(sortie, sortie.length, adresseClient, portClient);
		socketServeur.send(paquetEnvoye);
	}
	
	/* ______________________________________________________ */
	/** Permet de recevoir les coordonées de la raquet
	 * @return le point contenant les coordonnées reçues
	 * @throws Exception Erreur d'entrée/sortie lors de l'envoi d'un paquet au client
	 */
	public byte[] recevoirPaquet() throws Exception
	{
		entree = new byte[128];
		DatagramPacket paquetRecu = new DatagramPacket(entree, entree.length);
		socketServeur.receive(paquetRecu);
		adresseClient = InetAddress.getByName(paquetRecu.getAddress().getHostName());
		portClient = paquetRecu.getPort();
		entree = paquetRecu.getData();
		return entree;
	}
	
	/* ______________________________________________________ */
	/** Retourne la valeur du champ entree.
	 * @return la valeur du champ entree.
	 */
	public byte[] getEntree()
	{
		return entree;
	}

	/* ______________________________________________________ */
	/** Modifie la valeur du champ entree.
	 * @param entree la valeur à placer dans le champ entree.
	 */
	public void setEntree(byte[] entree)
	{
		this.entree = entree;
	}

	/* ______________________________________________________ */
	/** Retourne la valeur du champ sortie.
	 * @return la valeur du champ sortie.
	 */
	public byte[] getSortie()
	{
		return sortie;
	}

	/* ______________________________________________________ */
	/** Modifie la valeur du champ sortie.
	 * @param sortie la valeur à placer dans le champ sortie.
	 */
	public void setSortie(byte[] sortie)
	{
		this.sortie = sortie;
	}

	/* ______________________________________________________ */
	/** Retourne la valeur du champ adresseClient.
	 * @return la valeur du champ adresseClient.
	 */
	public InetAddress getAdresseClient()
	{
		return adresseClient;
	}

	/* ______________________________________________________ */
	/** Modifie la valeur du champ adresseClient.
	 * @param adresseClient la valeur à placer dans le champ adresseClient.
	 */
	public void setAdresseClient(InetAddress adresseClient)
	{
		this.adresseClient = adresseClient;
	}

	/* ______________________________________________________ */
	/** Retourne la valeur du champ portClient.
	 * @return la valeur du champ portClient.
	 */
	public int getPortClient()
	{
		return portClient;
	}

	/* ______________________________________________________ */
	/** Modifie la valeur du champ portClient.
	 * @param portClient la valeur à placer dans le champ portClient.
	 */
	public void setPortClient(int portClient)
	{
		this.portClient = portClient;
	}

	/* ______________________________________________________ 
	*//** Permet de recevoir le 1er paquet
	 * @return la chaîne de caractères contenue dans le premier paquet
	 * @throws IOException Problème d'E/S
	 *//*
	public String recevoirPremierPaquet() throws IOException
	{
		entree = new byte[128];
		DatagramPacket paquetRecu = new DatagramPacket(entree, entree.length);
		socketServeur.receive(paquetRecu);
		adresseClient = InetAddress.getByName(paquetRecu.getAddress().getHostName());
		portClient = paquetRecu.getPort();
		entree = paquetRecu.getData();
		String chaineRecue = new String(entree);
		return chaineRecue;
	}*/
}

/*__________________________________________________________*/
/* Fin du fichier Serveur.java. */
/*__________________________________________________________*/