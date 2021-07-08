package it.xtreamdev.gestioneattivita.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .cors().disable()
                .authorizeRequests(expressionInterceptUrlRegistry -> expressionInterceptUrlRegistry
                        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .antMatchers("/api/auth/**").permitAll()
                        .antMatchers("/api/customer/**").permitAll()
                        .antMatchers("/api/admin/**").hasAuthority("ADMIN")
                        .antMatchers("/api/editor/**").hasAuthority("EDITOR")
                );
    }

}
