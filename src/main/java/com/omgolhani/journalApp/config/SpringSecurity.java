package com.omgolhani.journalApp.config;

import com.omgolhani.journalApp.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringSecurity{

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http.authorizeHttpRequests(request ->request             // tells spring security to start authorizing requests.
                .requestMatchers("/public/**").permitAll()      // Specifies that HTTP requests matching the pattern /public/** should be permitted for all users, whether authenticated or not.
                .requestMatchers("/journal/**", "/user/**").authenticated()     // Specifies that HTTP requests matching either /journal/** or /user/** paths must be authenticated. This means users accessing these endpoints need to provide valid credentials.
                .requestMatchers("/admin/**").hasRole("ADMIN")   // Specifies that HTTP requests matching the path /admin/** should be restricted to users who have the role of "ADMIN". This implies that only users authenticated with the role "ADMIN" can access these endpoints.
                .anyRequest().authenticated())                            // Ensures that any other HTTP requests (not covered by previous rules) must also be authenticated. This acts as a catch-all rule for any requests that don't match /public/**, /journal/**, /user/**, or /admin/**.
                .httpBasic(Customizer.withDefaults())                     // Configures HTTP Basic authentication for the application using default settings provided by Customizer.withDefaults().
                .csrf(AbstractHttpConfigurer::disable)                    // Disables CSRF (Cross-Site Request Forgery) protection for the application. CSRF protection is typically used to prevent unauthorized commands from being transmitted automatically from a user's browser.
                .build();                                                 // Constructs and returns the SecurityFilterChain based on the configurations applied using the http object.
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
