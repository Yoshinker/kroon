package kroon;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;

// Librairies pour les évênements
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

// Librairies pour les élements de la fenêtre
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import javax.swing.WindowConstants;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public final class Fenetre extends JFrame implements ActionListener
{
    private Carte[] mes_cartes;
    private Carte ma_carte;
    
    private int STAT_EGLISE = 50;
    private int STAT_PEUPLE = 50;
    private int STAT_ARMEE = 50;
    private int STAT_FINANCE = 50;
    
    private JButton bouton1, bouton2, bouton_debut;
    private JLabel titre, eglise, peuple, armee, finance, nom, nom_eglise, nom_peuple, nom_armee, nom_finance, label_compteur;
    private Panneau pan;
    private PanneauIntro pan_debut;
    private Container contenant;
    private JTextArea texte, explication;
    
    private int compteur_annees = 0;
    private boolean isBeginning = true;
    private boolean isPlaying = false;
    private boolean isEnd = false;
    
    private MouseAdapter button_hover;
    
    private PointerInfo informations_souris;
    private Point position_souris;
    private int x, y;
    
    private Robot mon_robot;
    private Sound son_bruitages;
    
    public Fenetre(Carte[] cartes) throws UnsupportedAudioFileException, IOException, LineUnavailableException
    {        
        // Cette fonction est ce qu'on appelle un constructeur
        // Elle est appellé une seule fois : au moment où la fenêtre est créer (dans le main)
        
        // Paramètre de la fenêtre
        setTitle("Kroon - Projet ISN"); // Titre
        setSize(1000,700); // Taille
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Permet de fermer la fenêtre avec la croix
        setResizable(false); // Pas redimensionable
        
        // On stocke le tableaux de cartes dans une variable utilisable dans la fenêtre
        mes_cartes = cartes;
        choisirCarte(mes_cartes);
        
        // On ajoute le son en arrière plan
        Sound son_background = new Sound();
        son_background.setSon("src/sound/background.wav");
        son_background.infinity();
        
        // Déclaration du panel de départ
        pan_debut = new PanneauIntro();
        pan_debut.setBackground(new Color(234, 180, 156));
        
        // Déclaration de la couche de contenu
        contenant = getContentPane();
        titre = new JLabel("Kroon");
        
        explication = new JTextArea("Vous êtes le souverain d'un royaume médiéval,"
                + " votre but est de rester au pouvoir le plus longtemps possible."
                + " L'église, le peuple, l'armée et la finance sont des caractéristiques"
                + " de votre royaume. Votre objectif sera de maintenir l'équilibre"
                + " entre ces 4 valeurs. Bonne chance jeune Roi !");
        
        bouton_debut = new JButton("OK");
        bouton_debut.setEnabled(true);
        bouton_debut.addActionListener(this);
        
        titre.setBounds(450, 50, 100, 100);
        titre.setFont(new Font("Verdana", Font.BOLD | Font.ITALIC, 15));
        bouton_debut.setBounds(400, 600, 200, 50);
        explication.setBounds(100, 250, 800, 400);
        explication.setEditable(false);  
        explication.setCursor(null);  
        explication.setOpaque(false);  
        explication.setFocusable(false);
        explication.setLineWrap(true);
        explication.setWrapStyleWord(true);
        explication.setFont(explication.getFont().deriveFont(30f));
        
        pan_debut.setLayout(null);
        
        pan_debut.add(bouton_debut);
        pan_debut.add(titre);
        pan_debut.add(explication);
        
        contenant.add(pan_debut);
        
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e)
    {
        // Fonction appelée quand le joueurs clique sur un bouton
        // Elle agit différemment selon le bouton et selon si la partie est en cours ou finie
        
        // Obtention de la source du click
        Object source = e.getSource();
        
        if  (source == bouton1)
        {
            // Bouton de gauche
            if (isPlaying)
            {
                try {
                    // Si la partie est en cours
                    // Action "oui"
                    son_bruitages.setSon("src/sound/" + ma_carte.getChemin_son());
                }
                // Gestion des différentes erreurs possibles
                catch (UnsupportedAudioFileException ex) 
                {
                    Logger.getLogger(Fenetre.class.getName()).log(Level.SEVERE, null, ex);
                } 
                catch (IOException ex) 
                {
                    Logger.getLogger(Fenetre.class.getName()).log(Level.SEVERE, null, ex);
                } 
                catch (LineUnavailableException ex) 
                {
                    Logger.getLogger(Fenetre.class.getName()).log(Level.SEVERE, null, ex);
                }
                son_bruitages.start();
                changerStats(ma_carte.getOui_eglise(), ma_carte.getOui_peuple(), ma_carte.getOui_armee(), ma_carte.getOui_finance());
            }
            else
            {
                // Si c'est l'écran de fin
                // Fermer la fenêtre
                dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            }
        }
        else if (source == bouton2)
        {
            // Bouton de droite
            if (isPlaying)
            {
                try {
                    // Si la partie est en cours
                    // Bouton non
                    
                    // Modification du son à jouer
                    son_bruitages.setSon("src/sound/" + ma_carte.getChemin_son());
                }
                // Gestion des différentes erreurs possibles
                catch (UnsupportedAudioFileException ex) 
                {
                    Logger.getLogger(Fenetre.class.getName()).log(Level.SEVERE, null, ex);
                } 
                catch (IOException ex) 
                {
                    Logger.getLogger(Fenetre.class.getName()).log(Level.SEVERE, null, ex);
                } 
                catch (LineUnavailableException ex) 
                {
                    Logger.getLogger(Fenetre.class.getName()).log(Level.SEVERE, null, ex);
                }
                // Jouer le son
                son_bruitages.start();
                
                // Changer les statistiques
                changerStats(ma_carte.getNon_eglise(), ma_carte.getNon_peuple(), ma_carte.getNon_armee(), ma_carte.getNon_finance());    
            }
            else
            {
                // Si c'est l'écran de fin
                // Relancer une partie
                restart();
            }
        }
        else if (source == bouton_debut) // Si le bouton pressé est celui de l'écran d'introduction
        {
            // On indique que l'introduction est terminé et que la partie commence
            isBeginning = false;
            isPlaying = true;
            
            // On supprime le panneau contenant les éléments de l'introduction
            contenant.remove(pan_debut);
            
            // On crée les éléments
            creerElements();
        }
        
        if (check(STAT_EGLISE, STAT_PEUPLE, STAT_ARMEE, STAT_FINANCE))
        {
            // Si les statistiques sont toutes entre 0 et 100
            // Le jeu continue
            
            // On incrémente le compteur d'années
            compteur_annees += 1;
            
            // On choisit aléatoirement une nouvelle carte, et on l'ajoute à notre panneau
            choisirCarte(mes_cartes);
            pan.setImgCarte(ma_carte.getChemin_image());
            refresh();
            
            // On sauvegarde la positions (x, y) de la souris au moment du click
            informations_souris = MouseInfo.getPointerInfo();
            position_souris = informations_souris.getLocation();
            x = (int) position_souris.getX();
            y = (int) position_souris.getY();
            
            // On déclare un robot, qui pourra modifier la position de la souris
            try {
                mon_robot = new Robot();
            }
            // Gestion d'une erreur possible
            catch (AWTException ex) 
            {
                Logger.getLogger(Fenetre.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            /*
            On fait sortir la souris du bouton, puis on la replace à sa position
            au moment du click.
            Cela a pour effet de mettre a jour la couleur des JLabel eglise, peuple, armee, finance
            en "appelant" les êvenements permettant leur coloration 
            */
            mon_robot.mouseMove(1,1);
            mon_robot.mouseMove(x, y); 
        }
        else
        {
            // Sinon l'écran de fin s'affiche
            isPlaying = false;
            pan.setImgCarte("mort.png");
            refresh();
            finish();
        }
        
    }
    
    public void creerElements()
    {
        // On ajoute les bruitages
        son_bruitages = new Sound();
        son_bruitages.setModifDB(-6f);
        
        /*
        On s'occupe maintenant de créer tous les éléments de l'interface (boutons, textes)
        et de les ajouter sur la fenêtre
        Les valeurs de ses éléments sont défini dans la fonction "refresh" qui sera utilisé
        plusieurs fois dans la partie (à chaque nouvelle carte affichée)
        */
        
        // Déclaration du panel
        pan = new Panneau(ma_carte.getChemin_image());
        
        // Déclaration du texte
        texte = new JTextArea();
        nom = new JLabel();
        
        // Déclaration des labels des statistiques
        eglise = new JLabel();
        peuple = new JLabel();
        armee = new JLabel();
        finance = new JLabel();
        
        // Déclaration des noms des statistiques
        nom_eglise = new JLabel("Eglise");
        nom_peuple = new JLabel("Peuple");
        nom_armee = new JLabel("Armee");
        nom_finance = new JLabel("Finance");
        
        // Déclaration du label du compteur d'années, en changeant la taille de son texte
        label_compteur = new JLabel();
        label_compteur.setFont(label_compteur.getFont().deriveFont(20f));
        
        // Création de mes évênements de bouton
        button_hover = new MouseAdapter()
        {
            public void mouseEntered(MouseEvent e)
            {
                // Fonction appelée lorsque la souris passe sur un bouton
                // Elle colore les statistiques en fonction de si elle vont augmenter (vert)
                // ou si elles vont diminuer (rouge)
                
                // On vérifie qu'on est pas sur l'écran de fin
                if (isPlaying)
                {
                    
                    // Obtention de la source
                    Object source = e.getSource();
                    
                    // Si c'est le bouton "oui"
                    if (source == bouton1)
                    {
                        if (ma_carte.getOui_eglise()>0)
                        {
                            eglise.setForeground(Color.green);
                        }
                        else if (ma_carte.getOui_eglise()<0)
                        {
                            eglise.setForeground(Color.red);
                        }

                        if (ma_carte.getOui_peuple()>0)
                        {
                            peuple.setForeground(Color.green);
                        }
                        else if (ma_carte.getOui_peuple()<0)
                        {
                            peuple.setForeground(Color.red);
                        }                               

                        if (ma_carte.getOui_armee()>0)
                        {
                            armee.setForeground(Color.green);
                        }
                        else if (ma_carte.getOui_armee()<0)
                        {
                            armee.setForeground(Color.red);
                        }            

                        if (ma_carte.getOui_finance()>0)
                        {
                            finance.setForeground(Color.green);
                        }
                        else if (ma_carte.getOui_finance()<0)
                        {
                            finance.setForeground(Color.red);
                        }
                    }
                    else // Sinon (bouton "non")
                    {
                        if (ma_carte.getNon_eglise()>0)
                        {
                            eglise.setForeground(Color.green);
                        }
                        else if (ma_carte.getNon_eglise()<0)
                        {
                            eglise.setForeground(Color.red);
                        }

                        if (ma_carte.getNon_peuple()>0)
                        {
                            peuple.setForeground(Color.green);
                        }
                        else if (ma_carte.getNon_peuple()<0)
                        {
                            peuple.setForeground(Color.red);
                        }                               

                        if (ma_carte.getNon_armee()>0)
                        {
                            armee.setForeground(Color.green);
                        }
                        else if (ma_carte.getNon_armee()<0)
                        {
                            armee.setForeground(Color.red);
                        }            

                        if (ma_carte.getNon_finance()>0)
                        {
                            finance.setForeground(Color.green);
                        }
                        else if (ma_carte.getNon_finance()<0)
                        {
                            finance.setForeground(Color.red);
                        }
                    }
                }
            }
            
            
            public void mouseExited(MouseEvent evt)
            {
                // Fonction appelé lorsque la souris sort d'un bouton
                // Elle enlève la couleur des statistiques
                
                // Décolorer
                eglise.setForeground(Color.black);
                peuple.setForeground(Color.black);
                armee.setForeground(Color.black);
                finance.setForeground(Color.black);
            }
        };
        
        // Déclaration des boutons
        bouton1 = new JButton(); // réponse oui
        bouton2 = new JButton(); // réponse non
        
        // Ajout des actions pour les boutons
        bouton1.addActionListener(this);
        bouton2.addActionListener(this);
      
        // Ajout de l'évênement "Souris sur le bouton"
        bouton1.addMouseListener(button_hover);
        bouton2.addMouseListener(button_hover);

        // Positionnement des boutons
        bouton1.setBounds(50, 300, 200, 60);
        bouton2.setBounds(750, 300, 200, 60);
        
        // Positionnement du texte
        // texte.setSize(580, 150);
        texte.setBounds(350,50,300,300);
        texte.setEditable(false);  
        texte.setCursor(null);  
        texte.setOpaque(false);  
        texte.setFocusable(false);
        texte.setLineWrap(true);
        texte.setWrapStyleWord(true);
        texte.setBounds(350,50, 300, 200);
        texte.setFont(texte.getFont().deriveFont(15f));
        
        // Positionnement des labels des statistiques
        // JLabel.setBounds(int x,int y, int largeur, int hauteur)
        eglise.setBounds(320, 610, 60, 20);
        peuple.setBounds(420, 610, 60, 20);
        armee.setBounds(520, 610, 60, 20);
        finance.setBounds(620, 610, 60, 20);
        
        // Positionnement des noms des statistiques
        // JLabel.setBounds(int x,int y, int largeur, int hauteur)
        nom_eglise.setBounds(320, 640, 60, 20);
        nom_peuple.setBounds(420, 640, 60, 20);
        nom_armee.setBounds(520, 640, 60, 20);
        nom_finance.setBounds(620, 640, 60, 20);
        
        // Positionnement du nom de la carte
        nom.setBounds(450, 540, 250, 20);
        
        // Positionnement du compteur d'années
        label_compteur.setBounds(50, 50, 300, 20);

        // Positionnement du panel
        pan.setLayout(null);

        // Ajout des composants au panel
        pan.add(bouton1);
        pan.add(bouton2);
        pan.add(texte);
        pan.add(eglise);
        pan.add(peuple);
        pan.add(armee);
        pan.add(finance);
        pan.add(nom_eglise);
        pan.add(nom_peuple);
        pan.add(nom_armee);
        pan.add(nom_finance);
        pan.add(nom);
        pan.add(label_compteur);
    
        // Ajout du panel sur la couche de contenu
        contenant.add(pan);
        
        // On définit les valeurs de nos éléments (boutons, labels)
        refresh();
    }
    
    public void choisirCarte(Carte[] cartes)
    {
        // Fonction appelée quand on doit changer de carte
        // Elle tire une carte aléatoire
        
        // Trouve un nombre aléatoire dans [0;nb_cartes-1]
        int nb_cartes = cartes.length;
        int num_carte = (int)(Math.random() * nb_cartes);
        
        // La carte affiché à l'écran change
        ma_carte = mes_cartes[num_carte];
    }
    
    public void changerStats(int stat_eglise, int stat_peuple, int stat_armee, int stat_finance)
    {
        // Fonction appelée après chaque réponse "oui" ou "non" du joueur
        // Elle modifie les statistiques du royaume
        
        // On modifie les statistiques du royaume en ajoutant celles correspondant à la réponse choisie de la carte affichée
        STAT_EGLISE += stat_eglise;
        STAT_PEUPLE += stat_peuple;
        STAT_ARMEE += stat_armee;
        STAT_FINANCE += stat_finance;
    }
    
    public void refresh()
    {
        // Fonction appelé dès qu'on doit modifier la carte a afficher
        // Elle permet de modifier les valeurs des labels et boutons selon la variable "ma_carte"
        
        // Modification du texte
        texte.setText(ma_carte.getTexte());
        
        // Modification des labels des statistiques
        eglise.setText(Integer.toString(STAT_EGLISE) + "%");
        peuple.setText(Integer.toString(STAT_PEUPLE) + "%");
        armee.setText(Integer.toString(STAT_ARMEE) + "%");
        finance.setText(Integer.toString(STAT_FINANCE) + "%");
        
        // Modification du nom de la carte
        nom.setText(ma_carte.getTitre());
        
        label_compteur.setText(Integer.toString(compteur_annees) + " ans vécus");
        
        // Modification des boutons
        bouton1.setText(ma_carte.getR_oui()); // réponse oui
        bouton2.setText(ma_carte.getR_non()); // réponse non
        
        // Rafraichissement
        pan.repaint();
    }
    
    public void finish()
    {
        // Fonction appelé quand le joueur perd
        // Elle affiche un écran de fin
        
        // On indique qu'on est sur l'écran de fin
        isEnd = true;
        
        // On modifie le texte des boutons
        bouton1.setText("Quitter");
        bouton2.setText("Rejouer");
        
        // Modification du texte
        texte.setText("Votre royaume est tombé ! Vous avez survécu " + compteur_annees + " ans.");
        
        // Raffraichissement
        pan.repaint();
    }
    
    public void restart()
    {
        // Fonction appelé lorsque le joueur clique sur "Rejouer" sur l'écran de fin
        // Elle remet les valeurs par défaut (statistiques, compteur)
        
        // Les statistiques du royaume sont remises à leur valeur par défaut
        STAT_EGLISE = 50;
        STAT_PEUPLE = 50;
        STAT_ARMEE = 50;
        STAT_FINANCE = 50;
        
        // Le compteur est réinitialiser
        compteur_annees = 0;
        
        // On indique qu'on est plus sur l'écran de fin
        isEnd = false;
        isPlaying = true;
        
        // Selection d'une carte, et le jeu recommence
        choisirCarte(mes_cartes);
        refresh();
    }
    
    public boolean check(int stat_eglise, int stat_peuple, int stat_armee, int stat_finance)
    {
        /* 
        fonction permettant de verifier si les stats sont bien entre ]0;100[ 
        et ainsi faire continuer le jeu.
        */
        
        boolean check = true;
        if (stat_eglise <=0 || stat_eglise >= 100)
        {
            check = false;
        }
        if (stat_peuple <=0 || stat_peuple >= 100)
        {
            check = false;
        }
        if (stat_armee <=0 || stat_armee >= 100)
        {
            check = false;
        }
        if (stat_finance <=0 || stat_finance >= 100)
        {
            check = false;
        }
        return check;
    }

}

/*

    A RAJOUTER :

    - gérer un fichier de sauvegarde du meilleur score
    - mode de difficulté
    - possibilité de redimensionner la fenêtre -> Recalcul de chaque position

*/