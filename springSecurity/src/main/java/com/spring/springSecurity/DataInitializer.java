
package com.spring.springSecurity;

import com.spring.springSecurity.enitity.AppUser;
import com.spring.springSecurity.enitity.Role;
import com.spring.springSecurity.repo.AppUserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner sendUser(
            AppUserRepository repository,
            PasswordEncoder encoder) {

        return args -> {
            System.out.println("DataInitializer started");

            long count = repository.count();
            System.out.println("Existing users: " + count);

            if (count > 0) {
                System.out.println(
                        "Users already exist. Skipping initialization."
                );
                return;
            }

            AppUser student = createUser(
                    "Student",
                    "student@gmail.com",
                    "student@123",
                    Role.USER,
                    encoder
            );

            AppUser admin = createUser(
                    "Admin",
                    "admin@gmail.com",
                    "admin@123",
                    Role.ADMIN,
                    encoder
            );

            repository.save(student);
            repository.save(admin);

            System.out.println(
                    "Default users saved successfully!"
            );
            System.out.println(
                    "Total users: " + repository.count()
            );
        };
    }

    private AppUser createUser(
            String name,
            String email,
            String password,
            Role role,
            PasswordEncoder encoder) {

        AppUser user = new AppUser();

        user.setName(name);
        user.setEmail(email.toLowerCase());
        user.setPassword(encoder.encode(password));
        user.setRole(role);

        return user;
    }
}
