package net.vanderkast.fs4r.service.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@ConfigurationProperties(prefix = "fs4r.security")
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private static final String AUTHORITY_READ = "READ";
    private static final String AUTHORITY_WRITE = "WRITE";

    private final String readerPassword;
    private final String regularPassword;

    public SecurityConfiguration(@Value("${fs4r.security.reader-password:}") String readerPassword,
                                 @Value("${fs4r.security.regular-password:}") String regularPassword) {
        this.readerPassword = readerPassword == null || readerPassword.isEmpty()
                ? "{bcrypt}$2y$12$4z8y0T6R.5aYu7HpqzPkE.pQF9twkbeSHnY5UoOEDMtKCbh0KPJ4q"
                : readerPassword;
        this.regularPassword = regularPassword == null || regularPassword.isEmpty()
                ? "{bcrypt}$2y$12$5GBgieTpZsK5ASKWSlS9T.ef0ZdUlR6mLv0aRZSobQ.FtsmdwVyCa"
                : regularPassword;
    }

    @Override
    protected void configure(HttpSecurity security) throws Exception {
        security.cors()
                .and()
                .authorizeRequests()
                .antMatchers("/api/v1/main/walk/**", "/api/v1/main/load/**", "/api/v1/main/download/**")
                .hasAuthority(AUTHORITY_READ)

                .antMatchers("/api/v1/main/move/**", "/api/v1/main/delete/**", "/api/v1/main/upload/**", "api/v1/lock/**")
                .hasAuthority(AUTHORITY_WRITE);
        security.csrf().disable();
        security.httpBasic();
        security.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Autowired
    public void users(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("regular")
                .password(regularPassword)
                .authorities(AUTHORITY_WRITE, AUTHORITY_READ)

                .and()
                .withUser("reader")
                .password(readerPassword)
                .authorities(AUTHORITY_READ);
    }
}
