package com.ganeshan.authenticationsystem.config;

import com.ganeshan.authenticationsystem.service.CustomUserDetailService;
import com.ganeshan.authenticationsystem.service.CustomerOAuth2UserService;
import com.ganeshan.authenticationsystem.service.CustomerOauth2User;
import com.ganeshan.authenticationsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private CustomerOAuth2UserService customerOAuth2UserService;
    @Autowired
    private UserService userService;

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailService();
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/h2-console/**", "/resources/**", "/static/**", "/css/**", "/images/**", "/icon/**", "/fonts/**", "/vendor/**", "/js/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/authenticate/**", "/reset/**", "/oauth/**").permitAll()
                .antMatchers("/account/user/**").hasAuthority("USER")
                .antMatchers("/account/admin/**").hasAuthority("ADMIN")
                .anyRequest().authenticated()
                .and()

                //HTTPS config
                .requiresChannel().antMatchers("/account/**").requiresSecure()

                .and()
                //Remember me config
                .rememberMe().tokenRepository(persistentTokenRepository())

                .and()

                .formLogin()
                //custom login page config
                .loginPage("/authenticate/login")
                .loginProcessingUrl("/authenticate/login")
                .defaultSuccessUrl("/account/user/home", true)
                .failureUrl("/authenticate/login?error=true")

                .and()

                .oauth2Login()
                .loginPage("/authenticate/login")
                .userInfoEndpoint()
                .userService(customerOAuth2UserService)
                .and()
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                        Authentication authentication) throws IOException, ServletException {

                        CustomerOauth2User oauthUser = (CustomerOauth2User) authentication.getPrincipal();

                        userService.processOAuthPostLogin(oauthUser.getName(), oauthUser.getEmail());

                        response.sendRedirect("/account/user/home");
                    }
                })
                .and()

                .logout()
                .deleteCookies("extraCookie")
                .logoutSuccessUrl("/authenticate/login")

                .and()
                .exceptionHandling().accessDeniedPage("/authenticate/login?accessDeniedPage=true")
                .and()

                .sessionManagement()
                .maximumSessions(2)
                .expiredUrl("/authenticate/login?invalid-session=true");
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        authenticationProvider.setUserDetailsService(userDetailsService());
        return authenticationProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
        db.setDataSource(dataSource);
        return db;
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    /**
     * We need this bean for the session management. Specially if we want to control the concurrent session-control support
     * with Spring security.
     *
     * @return HttpSessionEventPublisher object
     */
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}