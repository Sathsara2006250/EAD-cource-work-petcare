package com.petcare.service;

import com.petcare.dao.PetDAO;
import com.petcare.exception.ValidationException;
import com.petcare.model.Pet;
import com.petcare.util.ValidationHelper;
import java.util.List;

/**
 * Business Service for Pet operations.
 */
public class PetService {

    private final PetDAO petDAO;

    public PetService() {
        this.petDAO = new PetDAO();
    }

    public PetService(PetDAO petDAO) {
        this.petDAO = petDAO;
    }

    public List<Pet> getAllPets() {
        return petDAO.findAll();
    }

    public Pet getPetById(int id) {
        return petDAO.findById(id);
    }

    public List<Pet> getPetsByCustomerId(int customerId) {
        return petDAO.findByCustomerId(customerId);
    }

    public List<Pet> searchPets(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return petDAO.findAll();
        }
        return petDAO.search(keyword.trim());
    }

    public boolean savePet(Pet pet) throws ValidationException {
        validatePet(pet);
        if (pet.getId() > 0) {
            return petDAO.update(pet);
        } else {
            return petDAO.create(pet);
        }
    }

    public boolean deletePet(int id) {
        return petDAO.delete(id);
    }

    public int getTotalPetsCount() {
        return petDAO.count();
    }

    private void validatePet(Pet p) throws ValidationException {
        if (p == null) {
            throw new ValidationException("Pet object cannot be null.");
        }
        if (p.getCustomerId() <= 0) {
            throw new ValidationException("Please select a valid pet owner / customer.");
        }
        ValidationHelper.requireNonEmpty(p.getName(), "Pet Name");
        ValidationHelper.requireNonEmpty(p.getSpecies(), "Species");
        ValidationHelper.requireNonEmpty(p.getBreed(), "Breed");
        if (p.getAge() < 0 || p.getAge() > 50) {
            throw new ValidationException("Pet age must be between 0 and 50 years.");
        }
        ValidationHelper.requireNonEmpty(p.getGender(), "Gender");
        if (p.getWeight() <= 0 || p.getWeight() > 200.0) {
            throw new ValidationException("Pet weight must be a positive number between 0.1 and 200 kg.");
        }
    }
}
