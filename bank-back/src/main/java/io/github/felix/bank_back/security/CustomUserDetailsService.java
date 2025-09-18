package io.github.felix.bank_back.security;

import io.github.felix.bank_back.model.user.Admin;
import io.github.felix.bank_back.model.user.AccountHolder;
import io.github.felix.bank_back.model.user.enums.UserStatus;
import io.github.felix.bank_back.repository.user.AdminRepository;
import io.github.felix.bank_back.repository.user.AccountHolderRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final AccountHolderRepository accountHolderRepository;

    public CustomUserDetailsService(AdminRepository adminRepository,
                                    AccountHolderRepository accountHolderRepository) {
        this.adminRepository = adminRepository;
        this.accountHolderRepository = accountHolderRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Primero busca Admin por username
        Admin admin = adminRepository.findByUsername(username).orElse(null);
        if (admin != null) {
            boolean enabled = admin.getStatus() == UserStatus.ACTIVE;
            return User.withUsername(admin.getUsername())
                    .password(admin.getPassword())
                    .roles("ADMIN")
                    .disabled(!enabled)
                    .build();
        }
        // Luego busca AccountHolder por name (se usa como username)
        AccountHolder holder = accountHolderRepository.findByName(username).orElse(null);
        if (holder != null) {
            boolean enabled = holder.getStatus() == UserStatus.ACTIVE;
            return User.withUsername(holder.getName())
                    .password(holder.getPassword())
                    .roles("ACCOUNT_HOLDER")
                    .disabled(!enabled)
                    .build();
        }
        throw new UsernameNotFoundException("Usuario no encontrado: " + username);
    }
}

