package com.spring.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.spring.security.dto.AuthRequest;
import com.spring.security.dto.JwtResponse;
import com.spring.security.entity.RefreshToken;
import com.spring.security.entity.User;
import com.spring.security.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtService jwtService;
	
	@Autowired
	private RefreshTokenService refreshTokenService;

	@Autowired
	private AuthenticationManager authenticationManager;

	public User save(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		User savedUser = userRepository.save(user);
		return savedUser;
	}

	public String generateToken(AuthRequest authRequest) {
		String token = jwtService.generateToken(authRequest.getUsername());
		return token;
	}

	public JwtResponse authenticateAndGetToken(AuthRequest authRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
		if (authentication.isAuthenticated()) {
			RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequest.getUsername());
			JwtResponse jwtResponse = new JwtResponse(jwtService.generateToken(authRequest.getUsername()), refreshToken.getToken());
			return jwtResponse;
		} else {
			throw new UsernameNotFoundException("Not found");
		}
	}

}
