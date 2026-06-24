package com.revature.ers.service;

import com.revature.ers.model.Request;
import com.revature.ers.repository.RequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RequestService {

    private final RequestRepository requestRepository;

    public RequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    public List<Request> findAll() {
        return requestRepository.findAll();
    }

    public Optional<Request> findById(int id) {
        return requestRepository.findById(id);
    }

    public List<Request> findByRequesterId(int userId) {
        return requestRepository.findByRequester_UserId(userId);
    }
}
