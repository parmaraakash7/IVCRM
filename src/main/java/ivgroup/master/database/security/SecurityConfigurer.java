package ivgroup.master.database.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import ivgroup.master.database.filter.JwtRequestFilters;
import ivgroup.master.database.service.IVUserDetailsService;


@EnableWebSecurity
public class SecurityConfigurer extends WebSecurityConfigurerAdapter
{	
	@Autowired
    private JwtRequestFilters jwtRequestFilters;

	@Autowired
	private IVUserDetailsService ivUserDetailsService;
	
	@Autowired
	PasswordEncoder passwordEncoder;
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception
    {
        auth.userDetailsService(ivUserDetailsService).passwordEncoder(passwordEncoder);
    }
	
	@Override
	protected void configure(HttpSecurity http)throws Exception
	{
		http.csrf()
		.disable()
		.authorizeRequests()
		.antMatchers("/companyExecutive/login","/owner/login","/owner").permitAll()
		.anyRequest().authenticated()
        .and()
        .sessionManagement()
        .sessionCreationPolicy( SessionCreationPolicy.STATELESS );
        http.addFilterBefore( jwtRequestFilters , UsernamePasswordAuthenticationFilter.class );
	}
	
	@Bean
	public PasswordEncoder getPasswordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean()throws Exception
    {
        return super.authenticationManagerBean();
    }
	
}
