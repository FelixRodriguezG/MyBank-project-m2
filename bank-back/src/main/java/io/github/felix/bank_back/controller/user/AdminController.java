package io.github.felix.bank_back.repository.account;


import io.github.felix.bank_back.dto.user.Admin.AdminCreateDTO;
import io.github.felix.bank_back.dto.user.Admin.AdminResponseDTO;
import io.github.felix.bank_back.dto.user.Admin.AdminUpdateDTO;
import io.github.felix.bank_back.service.user.interfaces.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/admins")
@Validated
@Tag(name = "Administradores", description = "Operaciones relacionadas con usuarios administradores")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Operation(summary = "Listar todos los administradores", description = "Devuelve la lista de todos los administradores")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Administradores encontrados")
    })
    @GetMapping
    public ResponseEntity<List<AdminResponseDTO>> getAllAdmins() {
        List<AdminResponseDTO> admins = adminService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }

    @Operation(summary = "Obtener un administrador por ID", description = "Devuelve el administrador correspondiente al ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Administrador encontrado"),
            @ApiResponse(responseCode = "404", description = "Administrador no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AdminResponseDTO> getAdminById(
            @Parameter(description = "ID del administrador") @PathVariable Long id) {
        AdminResponseDTO admin = adminService.getAdminById(id);
        if (admin == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(admin);
    }

    @Operation(summary = "Crear un nuevo administrador", description = "Crea un administrador con los datos proporcionados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Administrador creado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PostMapping
    public ResponseEntity<AdminResponseDTO> createAdmin(@Valid @RequestBody AdminCreateDTO adminRequest) {
        AdminResponseDTO created = adminService.createAdmin(adminRequest);
        return ResponseEntity.status(201).body(created);
    }

    @Operation(summary = "Actualizar un administrador", description = "Actualiza los datos de un administrador existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Administrador actualizado"),
            @ApiResponse(responseCode = "404", description = "Administrador no encontrado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AdminResponseDTO> updateAdmin(
            @Parameter(description = "ID del administrador") @PathVariable Long id,
            @Valid @RequestBody AdminUpdateDTO adminUpdate) {
        AdminResponseDTO updated = adminService.updateAdmin(id, adminUpdate);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Eliminar un administrador", description = "Elimina un administrador por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Administrador eliminado"),
            @ApiResponse(responseCode = "404", description = "Administrador no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(
            @Parameter(description = "ID del administrador a eliminar") @PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
