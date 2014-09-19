package reseau;

import java.awt.Point;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/*__________________________________________________*/
/*___________________________________________*/
/**
 *
 * Fichier : Client.java
 *
 * Créé le 4 févr. 2014 à 14:19:17
 *
 * Auteur : Guillaume RAYNAUD
 */

/*___________________________________________*/
/**
 */
public class Client implements ProtocoleCommunication
{
	
	/** Socket client */
	private DatagramSocket socketClient = null;
	/** Adresse du serveur */
	private InetAddress adresseServeur = null;
	/** Port utilisé par le serveur */
	private int portServeur = PORT;
	/** Chaine reçue */
	/*private String chaineRecue;
	*//** Chaine émise *//*
	private String chaineEmise;*/
	/** Buffer de récupération en entrée */
	private byte[] entree = null;
	/** Buffer de préparation en sortie */
	private byte[] sortie = null;
	/** Instance unique du singleton */
	private static Client instance;
	
	
	/*___________________________________________*/
	/** Constructeur privé du singleton
	 * @throws UnknownHostException Nom du serveur inconnu
	 * @throws Exception Autre erreur de connexion
	 */
	private Client() throws UnknownHostException, Exception 
	{
		entree = new byte[1024];
		sortie = new byte[1024];
		ouvrirConnexion();
	}
	
	/*___________________________________________*/
	/** Ouvre la la socket de création de datagramme du client
	 * @throws Exception Autres erreurs
	 * @throws UnknownHostException Nom du serveur inconnu
	 */
	private void ouvrirConnexion()
	throws UnknownHostException, Exception
	{
		socketClient = new DatagramSocket();
	}

	/** Méthode d'accession à l'instance unique 
	 * @return l'instance unique
	 * @throws UnknownHostException Nom du serveur inconnu
	 * @throws Exception Autres erreurs
	 */
	public static Client getInstance() throws UnknownHostException, Exception
	{
		if(instance == null)
			instance = new Client();
		return instance;
	}
	
	/** Méthode de fermeture de la socket client
	 * @throws SocketException levée si la socket est déjà fermée
	 */
	public void fermerConnexion()
	throws SocketException
	{
		if (socketClient.isClosed())
			throw new SocketException("La socket est déjà fermée");
		socketClient.close();
	}
	
	/* ______________________________________________________ */
	/** Défini le timeout de la socket pour la rendre non-bloquante
	 * @param timeout Temps d'attente
	 * @throws SocketException socket débloquée
	 * @throws SocketTimeoutException 
	 */
	public void definirTimeout(int timeout) throws SocketException, SocketTimeoutException
	{
		socketClient.setSoTimeout(timeout);
	}
	
	/* ______________________________________________________ */
	/** Permet d'envoyer les coordonées de la raquet passée en paramètre
	 * @param pointEnvoye Point envoyé
	 * @param isStarted Si la partie est commencée
	 * @throws Exception Erreur d'entrée/sortie lors de l'envoi d'un paquet au client
	 */
	public void envoyerCoordonnees(Point pointEnvoye, boolean isStarted) throws Exception
	{
		sortie = new byte[128];
		Integer coordonneesX = new Integer(pointEnvoye.x);
		Integer coordonneesY = new Integer(pointEnvoye.y);
		Byte isStartedSended;
		if (isStarted)
		{
			isStartedSended = PARTIE_DEMARRE;
		}
		else
		{
			isStartedSended = PARTIE_ARRETE;
		}
		String chaineEnvoye = isStartedSended.toString() + SEPARATEUR_CHAINE + coordonneesX.toString() + SEPARATEUR_CHAINE + coordonneesY.toString();
		sortie = chaineEnvoye.getBytes();
		DatagramPacket paquetEnvoye = new DatagramPacket(sortie, sortie.length, adresseServeur, portServeur);
		socketClient.send(paquetEnvoye);
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
		socketClient.receive(paquetRecu);
		adresseServeur = InetAddress.getByName(paquetRecu.getAddress().getHostName());
		portServeur = paquetRecu.getPort();
		entree = paquetRecu.getData();
		return entree;
	}

	/* ______________________________________________________ */
	/** Retourne la valeur du champ adresseServeur.
	 * @return la valeur du champ adresseServeur.
	 */
	public InetAddress getAdresseServeur()
	{
		return adresseServeur;
	}

	/* ______________________________________________________ */
	/** Modifie la valeur du champ adresseServeur.
	 * @param adresseServeur la valeur à placer dans le champ adresseServeur.
	 */
	public void setAdresseServeur(InetAddress adresseServeur)
	{
		this.adresseServeur = adresseServeur;
	}
}

/*__________________________________________________*/
/* Fin du fichier Client.java.
/*__________________________________________________*/