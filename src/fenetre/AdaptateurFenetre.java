/* ______________________________________________________ */
/**
 * Fichier : AdaptateurFenetre.java
 *
 * Créé le 18 mars 2014 à 14:23:22
 *
 * Auteur : NUNES Stephen
 */
package fenetre;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.SocketException;
import java.net.UnknownHostException;

import reseau.Client;
import reseau.Serveur;

/* ______________________________________________________ */
/**
 */
public class AdaptateurFenetre extends WindowAdapter
{

	/* ______________________________________________________ */
	/** Evénement déclenché lors de la fermeture de la fenêtre
	 * @param arg0
	 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosing(WindowEvent arg0)
	{
		try
		{
			Serveur.getInstance().fermerConnexion();
			Client.getInstance().fermerConnexion();
		} catch (SocketException e)
		{
			System.err.println(e);
			System.exit(-1);
		} catch (UnknownHostException e)
		{
			System.err.println(e);
			System.exit(-1);
		} catch (Exception e)
		{
			System.err.println(e);
			System.exit(-1);
		}
		System.exit(0);
	}

}

/*__________________________________________________________*/
/* Fin du fichier AdaptateurFenetre.java. */
/*__________________________________________________________*/