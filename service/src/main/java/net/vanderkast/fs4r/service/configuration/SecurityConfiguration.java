package net.vanderkast.fs4r.service.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static net.vanderkast.fs4r.service.rest.MainRestController.API_PATH;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private static final String AUTHORITY_READ = "READ";
    private static final String AUTHORITY_WRITE = "WRITE";

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().cors()
                .and()
                .authorizeRequests()
                .antMatchers(API_PATH + "/walk/**", API_PATH + "load/**", API_PATH + "/download/**")
                .hasAuthority(AUTHORITY_READ)

                .and()
                .authorizeRequests()
                .antMatchers(API_PATH + "/move/**", API_PATH + "/delete/**", API_PATH + "/upload/**")
                .hasAuthority(AUTHORITY_WRITE)

                .and().httpBasic();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception { // todo pass passwords via properties
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
