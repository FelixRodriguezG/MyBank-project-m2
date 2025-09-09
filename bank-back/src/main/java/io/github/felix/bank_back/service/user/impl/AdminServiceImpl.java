package io.github.felix.bank_back.service.user.impl;


import io.github.felix.bank_back.dto.user.Admin.AdminCreateDTO;
import io.github.felix.bank_back.dto.user.Admin.AdminResponseDTO;
import io.github.felix.bank_back.dto.user.Admin.AdminUpdateDTO;
import io.github.felix.bank_back.model.user.Admin;
import io.github.felix.bank_back.repository.user.AdminRepository;
import io.github.felix.bank_back.service.user.interfaces.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Arrays.stream;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Override
    public List<AdminResponseDTO> getAllAdmins() {
        return adminRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public AdminResponseDTO getAdminByUsername(String username) {
        return adminRepository.findByName(username)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Admin not found with username: " + username));
    }


    @Override
    public AdminResponseDTO getAdminById(Long id) {
        return adminRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + id));
    }

    @Override
    public AdminResponseDTO createAdmin(AdminCreateDTO adminRequest) {
            Admin admin = new Admin();
            admin.setName(adminRequest.getName());
            admin.setPassword(adminRequest.getPassword());
            Admin savedAdmin = adminRepository.save(admin);
            return toDTO(savedAdmin);
    }

    @Override
    public AdminResponseDTO updateAdmin(Long id, AdminUpdateDTO adminUpdate) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found with id: " + id));

        if (adminUpdate.getName() != null) admin.setName(adminUpdate.getName());
        if (adminUpdate.getUsername() != null) admin.setUsername(adminUpdate.getUsername());
        if (adminUpdate.getPassword() != null) admin.setPassword(adminUpdate.getPassword());
        if (adminUpdate.getStatus() != null) admin.setStatus(adminUpdate.getStatus());

        Admin updatedAdmin = adminRepository.save(admin);
        return toDTO(updatedAdmin);
    }

    @Override
    public boolean deleteAdmin(Long id) {
        if (!adminRepository.existsById(id)) {
            throw new RuntimeException("Admin not found with id: " + id);
        }
        adminRepository.deleteById(id);
        return true;

    }

    // ================================================
    // Auxiliar: Convierte Admin a AdminResponseDTO
    // ================================================
    private AdminResponseDTO toDTO(Admin admin) {
        // Mapeo manual de Admin a AdminResponseDTO
        return new AdminResponseDTO(
                admin.getId(),
                admin.getName(),
                admin.getUsername(),
                admin.getRole(),
                admin.getStatus()
        );
    }
}
