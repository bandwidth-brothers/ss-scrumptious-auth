package com.ss.scrumptious_auth.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ss.scrumptious_auth.dto.CreateUserDto;
import com.ss.scrumptious_auth.dto.EditUserDto;
import com.ss.scrumptious_auth.entity.User;
import com.ss.scrumptious_auth.entity.UserRole;
import com.ss.scrumptious_auth.security.permissions.GetUserByIdPermission;
import com.ss.scrumptious_auth.service.UserAccountService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class UserAccountController {

	
	private final UserAccountService userAccountService;
	
	@PostMapping("/register")
	public ResponseEntity<UUID> createNewAccountCustomer(@Valid @RequestBody CreateUserDto createUserDto) {
		return createNewAccount(createUserDto, UserRole.CUSTOMER);
	}
	
	@PostMapping("/restaurants/register")
	public ResponseEntity<UUID> createNewAccountRestaurantOwner(@Valid @RequestBody CreateUserDto createUserDto) {
		return createNewAccount(createUserDto, UserRole.EMPLOYEE);
	}

	@PostMapping("/admin/register")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UUID> createNewAccountAdmin(@Valid @RequestBody CreateUserDto createUserDto) {
		return createNewAccount(createUserDto, UserRole.ADMIN);
		
	}
	
	private ResponseEntity<UUID> createNewAccount(@Valid CreateUserDto createUserDto, UserRole role) {
		User user = userAccountService.createNewAccount(createUserDto, role);
		UUID userId = user.getUserId();
		return ResponseEntity.created(URI.create("/login")).body(userId);
	}

	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userAccountService.getAllUsers();
		if (users.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(users);
    }

	@GetUserByIdPermission
	@GetMapping("/{userId}")
	public ResponseEntity<User> currentUserName(@PathVariable UUID userId) {
	   	Optional<User> user = userAccountService.findUserByUUID(userId);
		return ResponseEntity.of(user);
	}

	@GetUserByIdPermission
	@PutMapping("/{userId}")
	public ResponseEntity<User> editUserByUUID(@Valid @RequestBody EditUserDto editUserDto, @PathVariable UUID userId) {
		Optional<User> user = userAccountService.findUserByUUID(userId);
		if (user.isPresent()) {
			user.get().setEmail(editUserDto.getEmail());
			user.get().setPassword(editUserDto.getPassword());
			return ResponseEntity.ok(userAccountService.updateUser(user.get()));
		}
		return ResponseEntity.notFound().build();
	}
}
