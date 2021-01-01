package de.vloryan.rest.log.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SpringSecurityConfig {


  @Configuration
  @Profile({"default"})
  protected static class NoSecuritySecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    public void configure(WebSecurity web) throws Exception {
      web.ignoring().antMatchers("/**");
    }
  }

  @Configuration
  @Profile({"secure"})
  protected static class WebSecuritConfig extends
      WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

      auth.inMemoryAuthentication()
          .withUser("user")
          .password("{noop}password")
          .roles("USER")
          .and()
          .withUser("admin")
          .password("{noop}password")
          .roles("USER", "ADMIN");

    }

    // Secure the endpoins with HTTP Basic authentication

    @Override
    protected void configure(HttpSecurity http) throws Exception {

      http // HTTP Basic authentication
          .httpBasic()
          .and()
          .authorizeRequests()
          .antMatchers("/api/**")
          .hasRole("USER")
          .antMatchers("/h2/**")
          .hasRole("ADMIN")
          .and()
          .csrf()
          .disable()
          .formLogin()
          .disable()
          .headers()
          .frameOptions()
          .sameOrigin();
    }
  }

}
