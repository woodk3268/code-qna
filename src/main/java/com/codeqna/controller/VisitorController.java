package com.codeqna.controller;

import com.codeqna.service.VisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class VisitorController {
    private final VisitorService visitorService;

    @GetMapping("/data/daily")
    public List<Long> getDailyData() {
        return visitorService.getDailyData();
    }

    @GetMapping("/data/weekly")
    public List<Long> getWeeklyData() {
        return visitorService.getWeeklyData();
    }

    @GetMapping("/data/monthly")
    public List<Long> getMonthlyData() {
        return visitorService.getMonthlyData();
    }

//
    @GetMapping("/data/dailyBoard")
    public List<Long> getDailyDataBoard() {
        return visitorService.getDailyBoardData();
    }

    @GetMapping("/data/weeklyBoard")
    public List<Long> getWeeklyDataBoard() {
        return visitorService.getWeeklyBoardData();
    }

    @GetMapping("/data/monthlyBoard")
    public List<Long> getMonthlyDataBoard() {
        return visitorService.getMonthlyBoardData();
    }
}
