package edu.example.web.config;

import edu.example.model.Role;
import edu.example.repository.UserRepository;
import edu.example.service.TokenService;
import edu.example.web.security.UserDetailsServiceImpl;
import edu.example.web.security.filter.JwtAuthenticationFilter;
import edu.example.web.security.jwt.JwtService;
import edu.example.web.security.provider.DbAuthenticationProvider;
import edu.example.web.security.provider.JwtAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] ALLOWED_URL_PATTERNS = { "/auth/**", "/swagger-ui/**", "/v3/api-docs/**" };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, LogoutSuccessHandler logoutHandler,
                                           AuthenticationManager authManager) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .addFilterBefore(new JwtAuthenticationFilter(authManager), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ALLOWED_URL_PATTERNS).permitAll()
                        .requestMatchers(HttpMethod.POST, "/folder/**", "/subject/**")
                        .hasAuthority(Role.MODERATOR.name())
                        .anyRequest().authenticated()
                )
                .authenticationManager(authManager)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessHandler(logoutHandler));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, List<AuthenticationProvider> providers) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        providers.forEach(authenticationManagerBuilder::authenticationProvider);

        return authenticationManagerBuilder.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return new UserDetailsServiceImpl(userRepository);
    }

    @Bean
    public AuthenticationProvider jwtAuthenticationProvider(JwtService jwtService,
                                                            UserDetailsService userDetailsService,
                                                            TokenService tokenService) {
        return new JwtAuthenticationProvider(jwtService, userDetailsService, tokenService);
    }

    @Bean
    public AuthenticationProvider dbAuthenticationProvider(UserDetailsService userDetailsService,
                                                           PasswordEncoder passwordEncoder) {
        return new DbAuthenticationProvider(userDetailsService, passwordEncoder);
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedMethods(List.of("*"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedOriginPatterns(List.of("*"));
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
