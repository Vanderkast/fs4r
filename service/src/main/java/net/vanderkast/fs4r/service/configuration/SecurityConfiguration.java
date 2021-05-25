package net.vanderkast.fs4r.service.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private static final String AUTHORITY_READ = "READ";
    private static final String AUTHORITY_WRITE = "WRITE";

    @Override
    protected void configure(HttpSecurity security) throws Exception {
        security.cors()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .antMatchers("/api/v1/main/walk/**", "/api/v1/main/load/**", "/api/v1/main/download/**")
                .hasAuthority(AUTHORITY_READ)

                .antMatchers("/api/v1/main/move/**", "/api/v1/main/delete/**", "/api/v1/main/upload/**", "api/v1/lock")
                .hasAuthority(AUTHORITY_WRITE);
        security.httpBasic();
        security.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Autowired
    public void users(AuthenticationManagerBuilder auth) throws Exception { // todo pass passwords via properties
        auth.inMemoryAuthentication()
                .withUser("regular")
                .password("{bcrypt}$2y$12$xznUZSKJX3t1Hy1iY5T6GeOL9iGcVxeiFCokOs.tFPdarSImsaMKS")
                .authorities(AUTHORITY_WRITE, AUTHORITY_READ)

                .and()
                .withUser("reader")
                .password("{bcrypt}$2y$12$4z8y0T6R.5aYu7HpqzPkE.pQF9twkbeSHnY5UoOEDMtKCbh0KPJ4q")
                .authorities(AUTHORITY_READ);
    }
}
