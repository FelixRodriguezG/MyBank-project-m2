package io.github.felix.bank_back.service.user.interfaces;

import io.github.felix.bank_back.dto.user.Admin.AdminCreateDTO;
import io.github.felix.bank_back.dto.user.Admin.AdminResponseDTO;
import io.github.felix.bank_back.dto.user.Admin.AdminUpdateDTO;

import java.util.List;

public interface AdminService {
    List<AdminResponseDTO> getAllAdmins();

    AdminResponseDTO getAdminById(Long id);

    AdminResponseDTO getAdminByUsername(String username);

    AdminResponseDTO createAdmin(AdminCreateDTO adminRequest);

    AdminResponseDTO updateAdmin(Long id, AdminUpdateDTO adminUpdate);

    boolean deleteAdmin(Long id);
}
