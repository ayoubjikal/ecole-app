-- Initialize database schema for the Ecole Management application

-- Create info_ecole table
CREATE TABLE IF NOT EXISTS info_ecole (
    academie VARCHAR(400),
    direction VARCHAR(500),
    etablissement VARCHAR(500) PRIMARY KEY,
    numero_de_compte VARCHAR(50),
    commune VARCHAR(400),
    date_de_fondation_ou_renouvellement DATE,
    nom_du_president VARCHAR(200),
    nom_du_tresorier VARCHAR(200),
    telephone VARCHAR(20),
    adresse VARCHAR(500)
);

-- Create equipment table
CREATE TABLE IF NOT EXISTS equipment (
    code INT PRIMARY KEY IDENTITY(1,1),
    date DATE,
    designation VARCHAR(700),
    source_equipment VARCHAR(700),
    nbr INT,
    prix_unitaire DECIMAL(10, 2),
    somme DECIMAL(10, 2),
    specialisation VARCHAR(700),
    etat VARCHAR(500),
    remarque VARCHAR(800),
    etablissement VARCHAR(500),
    FOREIGN KEY (etablissement) REFERENCES info_ecole(etablissement)
);

-- Create suppression table
CREATE TABLE IF NOT EXISTS suppression (
    id INT PRIMARY KEY IDENTITY(1,1),
    date DATE,
    designation VARCHAR(700),
    etablissement VARCHAR(500),
    quantite INT,
    motif VARCHAR(1000),
    remarque VARCHAR(500)
);

-- Insert sample data for testing
INSERT INTO info_ecole (academie, direction, etablissement, numero_de_compte, commune, date_de_fondation_ou_renouvellement, nom_du_president, nom_du_tresorier, telephone, adresse)
VALUES 
('Académie de Rabat', 'Direction Provinciale de Rabat', 'École Primaire Al Mansour', '123456789', 'Rabat', '2020-01-01', 'Mohammed Alami', 'Ahmed Bennani', '0612345678', '123 Avenue Mohammed V, Rabat'),
('Académie de Casablanca', 'Direction Provinciale de Casablanca', 'École Primaire Ibn Khaldoun', '987654321', 'Casablanca', '2018-09-15', 'Fatima Zohra', 'Hassan El Fassi', '0698765432', '45 Rue des Écoles, Casablanca');

-- Insert sample equipment data
INSERT INTO equipment (date, designation, source_equipment, nbr, prix_unitaire, somme, specialisation, etat, remarque, etablissement)
VALUES
('2023-05-15', 'Tables d''élèves', 'Ministère de l''Éducation', 50, 500.00, 25000.00, 'Mobilier scolaire', 'Neuf', 'Livraison complète', 'École Primaire Al Mansour'),
('2023-06-20', 'Chaises d''élèves', 'Ministère de l''Éducation', 50, 250.00, 12500.00, 'Mobilier scolaire', 'Neuf', 'Livraison complète', 'École Primaire Al Mansour'),
('2023-04-10', 'Ordinateurs', 'Don OCP', 10, 5000.00, 50000.00, 'Informatique', 'Bon état', 'Installation complète', 'École Primaire Ibn Khaldoun'),
('2023-07-25', 'Tableaux blancs', 'Budget école', 8, 800.00, 6400.00, 'Équipement de classe', 'Neuf', 'Avec accessoires', 'École Primaire Ibn Khaldoun');

-- Insert sample suppression data
INSERT INTO suppression (date, designation, etablissement, quantite, motif, remarque)
VALUES
('2023-08-10', 'Tables d''élèves endommagées', 'École Primaire Al Mansour', 5, 'Tables cassées suite à une utilisation prolongée', 'Remplacement prévu'),
('2023-09-05', 'Ordinateur hors service', 'École Primaire Ibn Khaldoun', 1, 'Panne de la carte mère, réparation impossible', 'À remplacer');