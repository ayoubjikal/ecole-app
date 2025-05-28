package com.ecole.management.config;

import com.ecole.management.model.Equipment;
import com.ecole.management.model.InfoEcole;
import com.ecole.management.model.Suppression;
import com.ecole.management.repository.EquipmentRepository;
import com.ecole.management.repository.InfoEcoleRepository;
import com.ecole.management.repository.SuppressionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration
@Profile("dev")
public class DataInitializer {

    @Autowired
    private InfoEcoleRepository infoEcoleRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private SuppressionRepository suppressionRepository;

    @PostConstruct
    public void initData() throws ParseException {
        // Only initialize if database is empty
        if (infoEcoleRepository.count() == 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            // Create sample ecoles
            InfoEcole ecole1 = new InfoEcole();
            ecole1.setAcademie("Académie de Rabat");
            ecole1.setDirection("Direction Provinciale de Rabat");
            ecole1.setEtablissement("École Primaire Al Mansour");
            ecole1.setNumeroDeCompte("123456789");
            ecole1.setCommune("Rabat");
            ecole1.setDateDeFondationOuRenouvellement(dateFormat.parse("2020-01-01"));
            ecole1.setNomDuPresident("Mohammed Alami");
            ecole1.setNomDuTresorier("Ahmed Bennani");
            ecole1.setTelephone("0612345678");
            ecole1.setAdresse("123 Avenue Mohammed V, Rabat");
            
            InfoEcole ecole2 = new InfoEcole();
            ecole2.setAcademie("Académie de Casablanca");
            ecole2.setDirection("Direction Provinciale de Casablanca");
            ecole2.setEtablissement("École Primaire Ibn Khaldoun");
            ecole2.setNumeroDeCompte("987654321");
            ecole2.setCommune("Casablanca");
            ecole2.setDateDeFondationOuRenouvellement(dateFormat.parse("2018-09-15"));
            ecole2.setNomDuPresident("Fatima Zohra");
            ecole2.setNomDuTresorier("Hassan El Fassi");
            ecole2.setTelephone("0698765432");
            ecole2.setAdresse("45 Rue des Écoles, Casablanca");
            
            infoEcoleRepository.save(ecole1);
            infoEcoleRepository.save(ecole2);
            
            // Create sample equipment
            Equipment eq1 = new Equipment();
            eq1.setDate(dateFormat.parse("2023-05-15"));
            eq1.setDesignation("Tables d'élèves");
            eq1.setSource_equipment("Ministère de l'Éducation");
            eq1.setNbr(50);
            eq1.setPrix_unitaire(500.00);
            eq1.setSomme(25000.00);
            eq1.setSpecialisation("Mobilier scolaire");
            eq1.setEtat("Neuf");
            eq1.setRemarque("Livraison complète");
            eq1.setEtablissement("École Primaire Al Mansour");
            
            Equipment eq2 = new Equipment();
            eq2.setDate(dateFormat.parse("2023-06-20"));
            eq2.setDesignation("Chaises d'élèves");
            eq2.setSource_equipment("Ministère de l'Éducation");
            eq2.setNbr(50);
            eq2.setPrix_unitaire(250.00);
            eq2.setSomme(12500.00);
            eq2.setSpecialisation("Mobilier scolaire");
            eq2.setEtat("Neuf");
            eq2.setRemarque("Livraison complète");
            eq2.setEtablissement("École Primaire Al Mansour");
            
            Equipment eq3 = new Equipment();
            eq3.setDate(dateFormat.parse("2023-04-10"));
            eq3.setDesignation("Ordinateurs");
            eq3.setSource_equipment("Don OCP");
            eq3.setNbr(10);
            eq3.setPrix_unitaire(5000.00);
            eq3.setSomme(50000.00);
            eq3.setSpecialisation("Informatique");
            eq3.setEtat("Bon état");
            eq3.setRemarque("Installation complète");
            eq3.setEtablissement("École Primaire Ibn Khaldoun");
            
            equipmentRepository.save(eq1);
            equipmentRepository.save(eq2);
            equipmentRepository.save(eq3);
            
            // Create sample suppressions
            Suppression sup1 = new Suppression();
            sup1.setDate(dateFormat.parse("2023-08-10"));
            sup1.setDesignation("Tables d'élèves endommagées");
            sup1.setEtablissement("École Primaire Al Mansour");
            sup1.setQuantite(5);
            sup1.setMotif("Tables cassées suite à une utilisation prolongée");
            sup1.setRemarque("Remplacement prévu");
            
            Suppression sup2 = new Suppression();
            sup2.setDate(dateFormat.parse("2023-09-05"));
            sup2.setDesignation("Ordinateur hors service");
            sup2.setEtablissement("École Primaire Ibn Khaldoun");
            sup2.setQuantite(1);
            sup2.setMotif("Panne de la carte mère, réparation impossible");
            sup2.setRemarque("À remplacer");
            
            suppressionRepository.save(sup1);
            suppressionRepository.save(sup2);
        }
    }
}