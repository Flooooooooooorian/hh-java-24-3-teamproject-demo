package de.neuefische.java.backend.security;

import de.neuefische.java.backend.todo.Todo;
import de.neuefische.java.backend.todo.TodoStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${app.url}")
    private String appUrl;

    private final AppUserRepository appUserRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))
                .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .oauth2Login(o -> o.defaultSuccessUrl(appUrl))
                .logout(l -> l.logoutSuccessUrl(appUrl))
                .authorizeHttpRequests(a -> a
                        .requestMatchers(HttpMethod.GET, "/api/todo").hasAuthority("USER")
                        .requestMatchers(HttpMethod.POST, "/api/todo").authenticated()
                        .requestMatchers("/api/todo/**").authenticated()
                        .anyRequest().permitAll());

        return httpSecurity.build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
        DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();

        return userRequest -> {
            OAuth2User user = defaultOAuth2UserService.loadUser(userRequest);

            AppUser appUser = appUserRepository.findById(user.getName())
                    .orElseGet(() -> appUserRepository.save(new AppUser(
                            user.getName(),
                            user.getAttributes().get("login").toString(),
                            user.getAttributes().get("avatar_url").toString(),
                            "USER")));

            return new DefaultOAuth2User(
                    List.of(new SimpleGrantedAuthority(appUser.role())),
                    user.getAttributes(),
                    "id");
        };
    }
}
