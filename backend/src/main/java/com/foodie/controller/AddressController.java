package com.foodie.controller;

import com.foodie.dto.request.AddressRequest;
import com.foodie.dto.response.AddressResponse;
import com.foodie.entity.User;
import com.foodie.service.AddressService;
import com.foodie.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@Tag(name = "Customer Addresses", description = "Endpoints for managing saved customer delivery addresses.")
public class AddressController {

    private final AddressService addressService;
    private final AuthService authService;

    public AddressController(AddressService addressService, AuthService authService) {
        this.addressService = addressService;
        this.authService = authService;
    }

    @GetMapping
    @Operation(summary = "Get list of saved delivery addresses for authenticated customer")
    public ResponseEntity<List<AddressResponse>> getAddresses() {
        User user = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(addressService.getUserAddresses(user));
    }

    @PostMapping
    @Operation(summary = "Save a new delivery address")
    public ResponseEntity<AddressResponse> addAddress(@Valid @RequestBody AddressRequest request) {
        User user = authService.getCurrentAuthenticatedUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.addAddress(user, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a saved delivery address")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        User user = authService.getCurrentAuthenticatedUser();
        addressService.deleteAddress(user, id);
        return ResponseEntity.noContent().build();
    }
}