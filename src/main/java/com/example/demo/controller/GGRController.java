package com.example.demo.controller;

import com.example.demo.dto.Player;
import com.example.demo.service.GetLoserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GGRController {

    @Autowired
    private GetLoserService ggrService;

    @GetMapping(value = {"/", ""})
    public Player getBiggestLoser() {
        return ggrService.getPlayerWithLargestGGR();

    }
}
