package fenetre;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.swing.JFrame;

import reseau.Client;
import reseau.ProtocoleCommunication;

/* ______________________________________________________ */
/** Fenêtre contenant le jeu du pong
 */
public class Pong extends JFrame implements Runnable, MouseListener, MouseMotionListener, ProtocoleCommunication {


	/** Numéro de sérialisation */
	private static final long serialVersionUID = 7657998555042629676L;

	/** Thread lançant le jeu */
	private Thread runner;
	/** */
	private Image offscreeni;
	/** */
	private Graphics offscreeng;
	/** */
	private Rectangle plane;
	/** Coordonnées de la balle du pong */
	private Point ballPoint;
	/** Coordonnées du joueur 1 */
	private Point joueur1; 
	/** Coordonnées du joueur 2*/
	private Point joueur2;
	/** Coordonnées de la vitesse de la balle*/
	private Point ballSpeed;

	/** Si la partie est démarré ou non */
	private boolean start = false;
	
	/** Si la partie est terminé ou non */
	private boolean death = false;

	/** Score du joueur 1 */
	private int joueur1Score = 0;
	/** Score du joueur 2 */
	private int joueur2Score = 0;
	
	/** tuffhet */
	private int tuffhet = 8;

	/** Client du pong pour le joueur de droite */
	private Client monClient;
	
	/** Couleur du plateau */
	private Color couleurPlateau = Color.BLACK;
	
	/** Couleur des éléments de jeu */
	private Color couleurJeu = Color.WHITE;

	/** Tableau d'octet rassenblant les données reçu */
	private byte[] donneesRecu = new byte[128];
	
	/** Adresse du serveur récupéré de la fenêtre de connexion*/
	private String adresseServeur;
	
	/* ______________________________________________________ */
	/** Initialisation du jeu
	 * @param adresseServeur Adresse du serveur à laquelle il faut se connecter
	 * @param couleurPlateau Couleur du plateau
	 * @param couleurJeu Couleur du jeu
	 */
	public void initialisation(String adresseServeur, Color couleurPlateau, Color couleurJeu)
	{
		this.adresseServeur = adresseServeur;
		this.couleurPlateau = couleurPlateau;
		this.couleurJeu = couleurJeu;
		addWindowListener(new AdaptateurFenetre());
		addMouseListener(this);// ajoute des listener pour la souris
		addMouseMotionListener(this);
		setBounds(100, 100, 640, 480);
		setTitle("Le Pong sa Ping - Client");
		setVisible(true); // rend visible la frame
		offscreeni = createImage(getWidth(), getHeight());
		try {
			offscreeng = offscreeni.getGraphics();
			setBackground(this.couleurPlateau);
			ballPoint = new Point((getWidth() / 2), (getHeight() / 2)); // balle
			joueur1 = new Point((getWidth() - 35), ((getHeight() / 2) - 25)); // pav� joueur1
			joueur2 = new Point(35, ((getHeight() / 2) - 25)); //pav� joueur 2


			plane = new Rectangle(15, 15, (getWidth()), (getHeight() - 30));
			ballSpeed = new Point(0, 0);
			commencerJeu();  // Démarrage du thread
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			repaint();
		} catch (Exception e) {
			System.err.println("Erreur");
			System.exit(-1);
		}
	}
	
	/* ______________________________________________________ */
	/** Lance le jeu et le thread associé
	 */
	public void commencerJeu() {
		if (runner == null) {
			runner = new Thread(this);
			runner.start();
		}
	}
	
	/* ______________________________________________________ */
	/** Définition de la méthode run
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try
		{
			monClient = Client.getInstance();
		} catch (UnknownHostException e)
		{
			System.err.println("Nom du serveur inconnu " + e);
			try
			{
				monClient.fermerConnexion();
			} catch (SocketException e1)
			{
				System.err.println("Problème de fermeture de socket client" + e);
			}
			System.exit(-1);
		} catch (Exception e) {
			System.err.println("Erreur Exception" + e);
			try
			{
				monClient.fermerConnexion();
			} catch (SocketException e1)
			{
				System.err.println("Problème de fermeture de socket client" + e);
			}
			System.exit(-1);
		}

		try
		{
			monClient.setAdresseServeur(InetAddress.getByName(this.adresseServeur));
		} catch (UnknownHostException e1)
		{
			System.err.println("Nom de domaine inconnue " + e1);
		}
		while (true) {
			checkJoueur1();
			checkJoueur2();
			checkWalls(); //
			//moveBall(); // calcul d�placement de la balle
			//moveJoueur2(joueur2.y + 25); //ia du joueur 2 � enlever
			try {
				monClient.envoyerCoordonnees(joueur1, start);
				donneesRecu = monClient.recevoirPaquet();
				traiterDonnees(donneesRecu);
			} catch (SocketTimeoutException e)
			{

			} catch (Exception e) {
				System.err.println("Erreur de transmission de paquet coté client " + e);
			}

			repaint();
			try {
				Thread.sleep(10); //pause pour ralentir le jeu
			} catch (InterruptedException e) {
			};
		} 
	}

	/* ______________________________________________________ */
	/** Traite les données reçu par paquet UDP
	 * @param entree Le tableau d'octet reçu
	 */
	private void traiterDonnees(byte[] entree)
	{
		String chaineRecu = new String(entree);
		String[] resultat = chaineRecu.split(SEPARATEUR_CHAINE);
		byte isStarted = Byte.parseByte(resultat[0].trim());
		if (isStarted == 2)
		{
			joueur1Score = Integer.parseInt(resultat[1].trim());
			joueur2Score = Integer.parseInt(resultat[2].trim());
		}
		else
		{
			int raquetX = Integer.parseInt(resultat[1].trim());
			int raquetY = Integer.parseInt(resultat[2].trim());
			int ballX = Integer.parseInt(resultat[3].trim());
			int ballY = Integer.parseInt(resultat[4].trim());
			int ballSpeedX = Integer.parseInt(resultat[5].trim());
			int ballSpeedY = Integer.parseInt(resultat[6].trim()); 

			joueur2 = new Point(raquetX, raquetY);
			ballPoint = new Point(ballX, ballY);
			ballSpeed = new Point(ballSpeedX, ballSpeedY);


			if (isStarted == 1)
			{
				if (start == false)
				{
					ballSpeed.x = 4;
					ballSpeed.y = 2;
					start = true; // demarre le jeu
				}
			}
			else if (isStarted == 0)
			{
				start = false;
			}
		}
	}
	
	/* ______________________________________________________ */
	/** Définition de l'action lorsqu'on bouge la souris
	 * @param e événement survenu
	 */
	public void mouseMoved(MouseEvent e) {
		joueur1.y = (e.getY() - 25);
		repaint();
	}
	
	/* ______________________________________________________ 
	*//** Gestion du mouvement de la balle
	 *//*
	private void moveBall() {
		ballPoint.x = (ballPoint.x + ballSpeed.x);
		ballPoint.y = (ballPoint.y + ballSpeed.y);
	}*/

	
	/*private void moveJoueur2(int enemyPos) {
		int dist = java.lang.Math.abs(ballPoint.y - enemyPos);
		if (ballSpeed.x < 0) {
			if (enemyPos < (ballPoint.y - 3))
				joueur2.y = (joueur2.y + dist / tuffhet);
			else if (enemyPos > (ballPoint.y + 3))
				joueur2.y = (joueur2.y - dist / tuffhet);
		} else {
			if (enemyPos < (getHeight() / 2 - 3))
				joueur2.y = (joueur2.y + 2);
			else if (enemyPos > (getHeight() / 2 + 3))
				joueur2.y = (joueur2.y - 2);
		}
	}*/

	/* ______________________________________________________ */
	/** Gère la colision entre la balle et le joueur 1 
	 */
	private void checkJoueur1() {
		if (ballSpeed.x < 0)
			return;
		if ((ballPoint.x + ballSpeed.x) >= joueur1.x - 6
				& (ballPoint.x < joueur1.x))
			if ((ballPoint.y + 10  > joueur1.y
					& ballPoint.y < (joueur1.y + 50))) {
				int racketHit = (ballPoint.y - (joueur1.y + 25));
				ballSpeed.y = (ballSpeed.y + (racketHit / 7));
				ballSpeed.x = (ballSpeed.x * -1);
			}
	}
	
	/* ______________________________________________________ */
	/** Gère la colision entre la balle et le joueur 2
	 */
	private void checkJoueur2() {
		if (ballSpeed.x > 0)
			return;
		if ((ballPoint.x + ballSpeed.x) <= joueur2.x + 4
				& (ballPoint.x > joueur2.x))
			if ((ballPoint.y + 10 > joueur2.y & ballPoint.y < (joueur2.y +
					50))) {
				int racketHit = (ballPoint.y - (joueur2.y + 25));
				ballSpeed.y = (ballSpeed.y + (racketHit / 7));
				ballSpeed.x = (ballSpeed.x * -1);
			}
	}

	/* ______________________________________________________ */
	/** Test si la balle a franchi la raquette ou non, donc des colisions avec les murs
	 */
	private void checkWalls() {
		if ((ballPoint.x + ballSpeed.x) <= plane.x)
			miss();
		if ((ballPoint.x + ballSpeed.x) >= (plane.width - 20))
			miss();
		if ((ballPoint.y + ballSpeed.y) <= plane.y)
			ballSpeed.y = (ballSpeed.y * -1);
		if ((ballPoint.y + ballSpeed.y) >= (plane.height + 10 ))
			ballSpeed.y = (ballSpeed.y * -1);
	}

	/* ______________________________________________________ */
	/** Appelée lorsque la raquette n'a pas touché la balle
	 */
	private void miss() {
		if (ballSpeed.x < 0) {
			joueur1Score = (joueur1Score + 1);
			if (tuffhet > 2)
				tuffhet = (tuffhet - 1);
		} else
			joueur2Score = (joueur2Score + 1);
		ballSpeed.x = (ballSpeed.x * -1);
		ballPoint.x = (ballPoint.x + ballSpeed.x);
		for (int i = 3; i > 0; i = (i - 1)) {
			death = true;
			repaint();
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
			};
			death = false;
			repaint();
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
			};
		}
		ballPoint = new Point((getWidth() / 2), (getHeight() / 2));
		ballSpeed.x = 0;
		ballSpeed.y = 0;
		start = false;
	}
	
	/* ______________________________________________________ */
	/** Redéfinition de la mise a jour de l'affichage du Graphics passé en paramètre
	 * @param g le Graphics mis à jour
	 * @see javax.swing.JFrame#update(java.awt.Graphics)
	 */
	public void update(Graphics g) {
		paint(g);
	}
	
	/* ______________________________________________________ */
	/** Redéfinition de l'affichage d'un graphics
	 * @param g le graphics affiché
	 * @see java.awt.Window#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g) {
		if (offscreeng != null) {
			offscreeng.setColor(couleurPlateau);
			offscreeng.fillRect(0, 0, getWidth(), getHeight());
			if (death == false)
				offscreeng.setColor(couleurJeu);
			else
				offscreeng.setColor(Color.red);
			offscreeng.drawString(Integer.toString(joueur2Score), 100, 35);
			offscreeng.drawString(Integer.toString(joueur1Score), 215, 35);
			if (plane != null) {
				offscreeng.clipRect(plane.x, plane.y, plane.width - 28,
						plane.height + 1);
				offscreeng.drawRect(plane.x, plane.y, plane.width - 30, plane.height);
				offscreeng.fillRect(joueur1.x, joueur1.y, 6, 50);
				offscreeng.fillRect(joueur2.x, joueur2.y, 6, 50);
				offscreeng.fillOval(ballPoint.x, ballPoint.y, 8,10) ;
			}
			g.drawImage(offscreeni, 0, 10, this);
		}
	}
	
	/* ______________________________________________________ */
	/** Définition de l'action lorsque la souris entre sur la fenêtre
	 * @param e événement survenu
	 */
	public void mouseEntered(MouseEvent e) {
	}
	
	/* ______________________________________________________ */
	/** Définition de l'action lorsque la souris sort la fenêtre
	 * @param e événement survenu
	 */
	public void mouseExited(MouseEvent e) {
	}

	/* ______________________________________________________ */
	/** Définition de l'action lorsque l'on appuie sur la souris
	 * @param e événement survenu
	 */
	public void mousePressed(MouseEvent e) {
		if (start == false) {
			ballSpeed.x = 4;
			ballSpeed.y = 2;
			start = true; // demarre le jeu
		}
	}
	
	/* ______________________________________________________ */
	/** Définition de l'action lorsqu'on clique sur la souris
	 * @param e événement survenu
	 */
	public void mouseClicked(MouseEvent e) {

	}
	
	/* ______________________________________________________ */
	/** Définition de l'action lorsque l'on relâche sur la souris
	 * @param e événement survenu
	 */
	public void mouseReleased(MouseEvent e) {
	}
	
	/* ______________________________________________________ */
	/** Définition de l'action lorsque l'on déplace en cliquant sur la souris
	 * @param e événement survenu
	 */
	public void mouseDragged(MouseEvent e) {

	}
}