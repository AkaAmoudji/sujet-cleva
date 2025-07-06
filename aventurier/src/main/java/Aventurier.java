
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Aventurier {
    private char[][] carte;
    private int largeur;
    private int hauteur;

    /**
     * Position actuelle du personnage
     */
    private int x, y;

    public Aventurier() {
    }

    /**
     * Charge la carte depuis un fichier texte
     */
    public void chargerCarte() throws IOException {

        List<String> lignes = new ArrayList<>();
        try (InputStream inputStream = this.getClass().getResourceAsStream("carte.txt");
             BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                lignes.add(ligne);
            }
        }catch (IOException ioe){
            throw new IllegalArgumentException("Le fichier de carte est introuvable");
        }

        if (lignes.isEmpty()) {
            throw new IllegalArgumentException("Le fichier de carte est vide");
        }

        // Déterminer les dimensions de la carte
        hauteur = lignes.size();
        largeur = lignes.stream().mapToInt(String::length).max().orElse(0);

        // Créer la matrice de la carte
        carte = new char[hauteur][largeur];

        // Remplir la carte
        for (int i = 0; i < hauteur; i++) {
            String ligne = lignes.get(i);
            for (int j = 0; j < largeur; j++) {
                if (j < ligne.length()) {
                    carte[i][j] = ligne.charAt(j);
                } else {
                    carte[i][j] = ' ';
                }
            }
        }

        System.out.println("Carte chargée avec succès");
    }

    private void verifierExistenceFichier(String fichier) {
        final File f = new File(fichier);
        if (!f.exists()) {
            throw new IllegalArgumentException("Le fichier en entrée n'existe pas");
        }

        if (f.isDirectory()) {
            throw new IllegalArgumentException("C'est un repertoire");
        }
    }

    /**
     * Charge les déplacements depuis un fichier
     */
    public void chargerDeplacements(String cheminFichierDeplacement) throws IOException {

        System.out.println("Le chemin du fichier du déplacement: " + cheminFichierDeplacement);

        verifierExistenceFichier(cheminFichierDeplacement);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(cheminFichierDeplacement), StandardCharsets.UTF_8))) {

            // Lire la première ligne (coordonnées initiales)
            String coordonnees = reader.readLine();
            if (coordonnees == null) {
                throw new IllegalArgumentException("Fichier de déplacements invalide");
            }

            String[] coords = coordonnees.split(",");
            if (coords.length != 2) {
                throw new IllegalArgumentException("Format de coordonnées invalide");
            }

            x = Integer.parseInt(coords[0].trim());
            y = Integer.parseInt(coords[1].trim());

            // Vérifier que la position initiale est valide
            if (!estPositionValide(x, y)) {
                throw new IllegalArgumentException("La position initiale (" + x + "," + y + ") de l'aventurier est invalide");
            }

            // Lire la deuxième ligne (déplacements)
            String deplacements = reader.readLine();
            if (deplacements == null) {
                throw new IllegalArgumentException("Aucun déplacement spécifié");
            }

            // Exécuter les déplacements
            for (char direction : deplacements.toCharArray()) {
                deplacer(direction);
            }
        }
    }

    /**
     * Vérifie si une position est valide (dans les limites et pas sur un obstacle)
     */
    private boolean estPositionValide(int x, int y) {
        // Vérifier les limites de la carte
        if (x < 0 || x >= largeur || y < 0 || y >= hauteur) {
            return false;
        }

        // Vérifier si la case n'est pas un bois impénétrable
        return carte[y][x] != '#';
    }

    /**
     * Déplace le personnage dans une direction donnée
     */
    private void deplacer(char direction) {
        int nouveauX = x;
        int nouveauY = y;

        switch (Character.toUpperCase(direction)) {
            case 'N': // Nord
                nouveauY--;
                break;
            case 'S': // Sud
                nouveauY++;
                break;
            case 'E': // Est
                nouveauX++;
                break;
            case 'O': // Ouest
                nouveauX--;
                break;
            default:
                System.err.println("Direction invalide: " + direction);
                return;
        }

        // Vérifier si le déplacement est valide
        if (estPositionValide(nouveauX, nouveauY)) {
            x = nouveauX;
            y = nouveauY;
        } else {
            System.out.println("Déplacement impossible vers (" + nouveauX + "," + nouveauY + ")");
        }
    }

    /**
     * Retourne la position actuelle du personnage
     */
    public String getPositionActuelle() {
        return "(" + x + "," + y + ")";
    }

    /**
     * Méthode principale pour tester le programme
     */
    public static void main(String[] args) {
        try {
            Aventurier aventurier = new Aventurier();

            // Charger la carte
            aventurier.chargerCarte();

            Scanner myObj = new Scanner(System.in);
            System.out.println("Entrez le fichier de déplacement de l'aventurier : ");
            String cheminFichier = myObj.nextLine();

            // Charger et exécuter les déplacements
            aventurier.chargerDeplacements(cheminFichier);

            // Afficher le résultat
            System.out.println("Position finale du personnage: " + aventurier.getPositionActuelle());

        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture des fichiers: " + e.getMessage());
            System.exit(1);
        } catch (NumberFormatException e) {
            System.err.println("Erreur de format dans les coordonnées: " + e.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println("Erreur: " + e.getMessage());
            System.exit(1);
        }
    }
}