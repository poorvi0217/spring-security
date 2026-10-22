package com.spring.springSecurity;

import com.spring.springSecurity.enitity.AppUser;
import com.spring.springSecurity.enitity.Role;
import com.spring.springSecurity.repo.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    CommandLineRunner sendUser(AppUserRepository repository , PasswordEncoder encoder){
        return args -> {
            if(repository.count() > 0)
                return;
            repository.save(user("Student" , "student@gmail.com","student@123",Role.USER , encoder));
            repository.save(user("Admin","admin@gmail.com", "admin@123", Role.ADMIN,encoder));
        };
    }

    private AppUser user(String name , String email , String password , Role role , PasswordEncoder passwordEncoder){
        AppUser user = new AppUser();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        return  user;
    }
}
