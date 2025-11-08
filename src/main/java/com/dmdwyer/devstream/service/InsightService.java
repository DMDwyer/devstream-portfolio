package com.dmdwyer.devstream.service;

import com.dmdwyer.devstream.entity.Insight;
import com.dmdwyer.devstream.repository.InsightRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InsightService {
	private final InsightRepository repository;

	public InsightService(InsightRepository repository) {
		this.repository = repository;
	}

	public List<Insight> getAll() {
		return repository.findAll();
	}

	public Insight save(Insight insight) {
		return repository.save(insight);
	}
}
