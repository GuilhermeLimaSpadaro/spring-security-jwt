package br.com.guilhermespadaro.controller;

import br.com.guilhermespadaro.domain.User;
import br.com.guilhermespadaro.dto.PasswordUpdateRequest;
import br.com.guilhermespadaro.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User request) {
        User user = service.create(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(user.getId()).toUri();
        return ResponseEntity.created(uri).body(user);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        User user = service.findById(id);
        return ResponseEntity.ok().body(user);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok().body(service.update(id, user));
    }

    @PutMapping(value = "/{id}/password")
    public ResponseEntity<User> updatePassword(@PathVariable Long id, @RequestBody PasswordUpdateRequest request) {
        return ResponseEntity.ok().body(service.updatePassword(id, request.previousPassword(), request.newPassword()));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}