/* ______________________________________________________ */
/**
 * Fichier : FenetreConnexion.java
 *
 * Créé le 11 mars 2014 à 16:19:18
 *
 * Auteur : NUNES Stephen
 */
package fenetre;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


/* ______________________________________________________ */
/** Fenêtre de connexion
 */
public class FenetreConnexion extends JFrame implements ActionListener
{

	/** Numéro de sérialisation */
	private static final long serialVersionUID = 5579032282783334994L;

	/** Bouton permettant de se connecter en tant que serveur */
	private JButton boutonServeur;

	/** Bouton permettant de se connecter en tant que client */
	private JButton boutonClient;

	/** Bouton permettant de choisir la couleur du plateau */
	private JButton boutonChoixCouleurPlateau;

	/** Bouton permettant de choisir la couleur du jeu */
	private JButton boutonChoixCouleurJeu;

	/** Couleur du plateau */
	private Color couleurPlateau = Color.BLACK;

	/** Couleur des éléments de jeu */
	private Color couleurJeu = Color.WHITE;

	/** Zone de texte du nom du serveur */
	private JTextField zoneTexteServeur;

	/* ______________________________________________________ */
	/** Contructeur de la fenêtre
	 */
	public FenetreConnexion()
	{
		super("Le Pong ça Ping");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		creerComposantsFenetre();
		pack();
	}

	/* ______________________________________________________ */
	/** Crée les composants de la fenêtre et les ajoute à celle-ci
	 */
	private void creerComposantsFenetre()
	{
		this.getContentPane().add(creerPanneauPrincipal(), BorderLayout.CENTER);

	}


	/* ______________________________________________________ */
	/** Crée le panneau principal
	 * @return le panneau et ses composants
	 */
	private JPanel creerPanneauPrincipal()
	{
		JPanel panneauPrincipal = new JPanel(new GridLayout(4, 1));
		panneauPrincipal.add(creerPanneauTitre());
		panneauPrincipal.add(creerPanneauChoixCouleur());
		panneauPrincipal.add(creerPanneauAdresseServeur());
		panneauPrincipal.add(creerPanneauBoutonsLancement());
		return panneauPrincipal;
	}

	/* ______________________________________________________ */
	/** Panneau contenant le titre
	 * @return le panneau et son composant
	 */
	private JPanel creerPanneauTitre()
	{
		JPanel panneauTitre = new JPanel(new FlowLayout());
		JLabel titre = new JLabel("Le Pong ça Ping");
		titre.setFont(new Font("Segoe UI", 1, 22));
		panneauTitre.add(titre);
		return panneauTitre;
	}

	/* ______________________________________________________ */
	/** Crée le panneau contenant les deux boutons permettant de lancer la partie
	 * @return le panneau et ses composants
	 */
	private JPanel creerPanneauBoutonsLancement()
	{
		JPanel panneauBoutonsLancement = new JPanel(new FlowLayout());
		boutonServeur = new JButton("Lancer la partie en serveur");
		boutonClient = new JButton("Lancer la partie en client");
		boutonServeur.addActionListener(this);
		boutonClient.addActionListener(this);
		panneauBoutonsLancement.add(boutonServeur);
		panneauBoutonsLancement.add(boutonClient);
		return panneauBoutonsLancement;
	}

	/* ______________________________________________________ */
	/** Crée le panneau contenant la saisie de l'adresse du serveur si c'est un client qui se connecte
	 * @return le panneau et ses composants
	 */
	private JPanel creerPanneauAdresseServeur()
	{
		JPanel panneauAdresseServeur = new JPanel(new FlowLayout());
		zoneTexteServeur = new JTextField();
		zoneTexteServeur.setPreferredSize(new Dimension(100, 22));
		panneauAdresseServeur.add(new JLabel("Adresse du serveur (à saisir seulement si vous êtes le client) "));
		panneauAdresseServeur.add(zoneTexteServeur);
		return panneauAdresseServeur;
	}

	/* ______________________________________________________ */
	/** Crée le panneau contenant le choix des couleurs
	 * @return le panneau et ses composants
	 */
	private JPanel creerPanneauChoixCouleur()
	{
		JPanel panneauChoixCouleur = new JPanel(new FlowLayout());

		boutonChoixCouleurPlateau = new JButton("Choisissez la couleur du plateau");
		boutonChoixCouleurJeu = new JButton("Choissisez la couleur du jeu");
		boutonChoixCouleurPlateau.addActionListener(this);
		boutonChoixCouleurJeu.addActionListener(this);
		panneauChoixCouleur.add(boutonChoixCouleurPlateau);
		panneauChoixCouleur.add(boutonChoixCouleurJeu);
		return panneauChoixCouleur;
	}

	/* ______________________________________________________ */
	/** Définition de la méthode permettant de récupéré les événements se déroulant sur la fenêtre
	 * @param e Evenement invoqué
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == boutonChoixCouleurPlateau)
		{
			couleurPlateau = JColorChooser.showDialog(null, "Choix de la couleur du plateau", Color.BLACK);
		}
		else if (e.getSource() == boutonChoixCouleurJeu)
		{
			couleurJeu = JColorChooser.showDialog(null, "Choix de la couleur du jeu", Color.WHITE);
		}
		else if (e.getSource() == boutonServeur)
		{
			MirrorPong pongServeur = new MirrorPong();
			pongServeur.initialisation(couleurPlateau, couleurJeu);
			dispose();
		}
		else if (e.getSource() == boutonClient)
		{
			if (zoneTexteServeur.getText().equals(""))
			{
				JOptionPane.showMessageDialog(this, "Le nom du serveur doit être renseigné", "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
			}
			else
			{
				Pong pongClient = new Pong();
				pongClient.initialisation(zoneTexteServeur.getText(), couleurPlateau, couleurJeu);
				dispose();
			}

		}
	}
}

/*__________________________________________________________*/
/* Fin du fichier FenetreConnexion.java. */
/*__________________________________________________________*/