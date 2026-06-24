package com.revature.ers.controller;

import com.revature.ers.model.Request;
import com.revature.ers.service.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST surface for reimbursement requests. Jackson serializes the whole eager graph
 * (requester, eventLocation, requestStatus) automatically; User.password stays out via
 * its @JsonIgnore.
 *
 *   GET /requests                  -> all requests
 *   GET /requests/{id}             -> one request, or 404
 *   GET /requests/requester/{uid}  -> a user's requests
 */
@RestController
@RequestMapping("/requests")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @GetMapping
    public ResponseEntity<List<Request>> getAll() {
        return ResponseEntity.ok(requestService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Request> getById(@PathVariable int id) {
        return requestService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/requester/{userId}")
    public ResponseEntity<List<Request>> getByRequester(@PathVariable int userId) {
        return ResponseEntity.ok(requestService.findByRequesterId(userId));
    }
}
