package com.petcare.model;

import java.sql.Timestamp;

/**
 * Pet entity representing Animals registered in the veterinary system.
 */
public class Pet {
    private int id;
    private int customerId;
    private String customerName; // Populated for joined display
    private String name;
    private String species;      // e.g. "Dog", "Cat", "Bird", "Rabbit"
    private String breed;
    private int age;
    private String gender;       // "Male" or "Female"
    private double weight;       // in kg
    private String medicalNotes;
    private Timestamp createdAt;

    public Pet() {}

    public Pet(int id, int customerId, String name, String species, String breed, int age, String gender, double weight, String medicalNotes) {
        this.id = id;
        this.customerId = customerId;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.age = age;
        this.gender = gender;
        this.weight = weight;
        this.medicalNotes = medicalNotes;
    }

    public Pet(int customerId, String name, String species, String breed, int age, String gender, double weight, String medicalNotes) {
        this.customerId = customerId;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.age = age;
        this.gender = gender;
        this.weight = weight;
        this.medicalNotes = medicalNotes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }

    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public String getMedicalNotes() { return medicalNotes; }
    public void setMedicalNotes(String medicalNotes) { this.medicalNotes = medicalNotes; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return name + " (" + species + " - " + breed + ")";
    }
}
