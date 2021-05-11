package net.vanderkast.fs4r.service.configuration;

import net.vanderkast.fs4r.domain.Delete;
import net.vanderkast.fs4r.domain.Load;
import net.vanderkast.fs4r.domain.Move;
import net.vanderkast.fs4r.domain.Walk;
import net.vanderkast.fs4r.simple.JustDelete;
import net.vanderkast.fs4r.simple.JustLoad;
import net.vanderkast.fs4r.simple.JustMove;
import net.vanderkast.fs4r.simple.JustWalk;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class TestConfiguration {
    @Bean
    Walk beanWalk() {
        return new JustWalk();
    }

    @Bean
    Load beanLoad() {
        return new JustLoad();
    }

    @Bean
    Delete beanDelete() {
        return new JustDelete();
    }

    @Bean
    Move beanMove() {return new JustMove();}
}
