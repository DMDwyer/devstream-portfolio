package com.dmdwyer.devstream.controller;

import com.dmdwyer.devstream.entity.Insight;
import com.dmdwyer.devstream.service.InsightService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/insights")
public class InsightController {
    private final InsightService service;

    public InsightController(InsightService service) {
        this.service = service;
    }

    @GetMapping
    public List<Insight> all() {
        return service.getAll();
    }

    @PostMapping
    public Insight create(@RequestBody Insight insight) {
        return service.save(insight);
    }
}