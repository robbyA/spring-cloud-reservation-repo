package net.robbya.authservice;


import java.util.stream.Stream;
import java.util.Optional;

import javax.jws.Oneway;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.security.Principal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;




@SpringBootApplication
@EnableResourceServer
@EnableDiscoveryClient
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

}

@RestController
class PrincipalRestController {
	@RequestMapping("/user")

	Principal principal (Principal principal) {
		return principal;
	}
}


@Configuration
@EnableAutoConfiguration
class OAuthConfiguration extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private  AuthenticationManager authenticationManager;

 	public OAuthConfiguration(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}


	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
				.withClient("acme")
				.secret("acmesecret")
				.authorizedGrantTypes("password")
				.scopes("openid");
	}
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(this.authenticationManager);
	}

}

@Component
class AccountsCLR implements CommandLineRunner {
	
	private final AccountRepository accountRepository;
		
	
	@Override
	public void run(String... strings) throws Exception {
		Stream.of("rob,spring", "randerson,boot", "tester,cloud")
		.map(x -> x.split(","))
		.forEach(tuple ->  this.accountRepository.save(new Account(tuple [0], tuple[1], true)));
	
	}
	
	
	@Autowired
	public AccountsCLR(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

}



@Service
class AccountUserDetailsService implements UserDetailsService {
	
	private final AccountRepository accountRepository;
	
	AccountUserDetailsService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;

	}
	

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return this.accountRepository.findByUsername(username)
				.map(account -> new User(
						account.getUsername(),
						account.getPassword(),
						account.isActive(), account.isActive(), account.isActive(), account.isActive(),
						AuthorityUtils.createAuthorityList("ROLE_ADMIN", "ROLE_USER")
						))
				.orElseThrow(()-> new UsernameNotFoundException("no user name " + username + "!"))
				;
		
	}
	
	
}


interface AccountRepository extends JpaRepository<Account, Long>{
	Optional<Account> findByUsername(String username);
}

@Entity
class Account{
	@Id
	@GeneratedValue
	private Long id;
	private String username, password;
	private boolean active;

	
	
	@Override
	public String toString() {
		return "Account [id=" + id + ", username=" + username + ", password=" + password + ", active=" + active + "]";
	}

	public Account(String username, String password, boolean active) {
		this.username = username;
		this.password = password;
		this.active = active;
	}
	
	public Account() {
		this.username = username;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	
	
	
}