package reseau;
/* ______________________________________________________ */
/**
 * Fichier : ProtocoleCommunication.java
 *
 * Créé le 4 févr. 2014 à 14:54:26
 *
 * Auteur : NUNES Stephen
 */

/* ______________________________________________________ */
/** Permet d'interpréter les paquets de communication entre le client et le serveur du pong
 */
public interface ProtocoleCommunication
{	
	/** Code explicitant que la partie commence */
	public static final byte PARTIE_DEMARRE = 1;
	
	/** Code explicitant que la partie est arretée */
	public static final byte PARTIE_ARRETE = 0;
	
	/** Code explicitant que ce sont les scores qui sont reçu */
	public static final byte SCORE = 2;
	
	
	
	/** Port par défaut */
	public static final int PORT = 4444;
	
	/** Permet de séparer deux données dans une chaîne de caractère */
	public static final String SEPARATEUR_CHAINE = ";";
}

/*__________________________________________________________*/
/* Fin du fichier ProtocoleCommunication.java. */
/*__________________________________________________________*/