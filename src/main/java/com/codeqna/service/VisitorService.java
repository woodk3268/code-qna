package com.codeqna.service;

import com.codeqna.entity.Board;
import com.codeqna.entity.Visitor;
import com.codeqna.repository.VisitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
@RequiredArgsConstructor
public class VisitorService {
    private final VisitorRepository visitorRepository;

    //ip찍기
    public void saveIp(String ipAddr) {
        LocalDate today = LocalDate.now();

        // 날짜 리스트 조회
        List<Visitor> visitors = visitorRepository.findByIpAddr(ipAddr);

        // 최신 날짜를 찾아 비교
        boolean isNewVisit = visitors.stream()
                .map(Visitor::getVDate)
                .noneMatch(today::equals);

        if (isNewVisit) {
            Visitor newVisitor = new Visitor();
            newVisitor.setIpAddr(ipAddr);
            newVisitor.setVDate(today);
            visitorRepository.save(newVisitor);
        }
    }

    //당일 방문자 수
    public long getTodayVisitor(){
        LocalDate today = LocalDate.now();
        return visitorRepository.findByVDate(today).size();
    }

    //그래프
    public List<Long> getDailyData() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);
        List<Visitor> visitors = visitorRepository.findVisitorsBetweenDates(startDate, today);

        Map<LocalDate, Long> visitorCounts = visitors.stream()
                .collect(Collectors.groupingBy(Visitor::getVDate, Collectors.counting()));

        return IntStream.rangeClosed(0, 6)
                .mapToObj(i -> startDate.plusDays(i))
                .map(date -> visitorCounts.getOrDefault(date, 0L))
                .collect(Collectors.toList());
    }

    public List<Long> getWeeklyData() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusWeeks(3).with(DayOfWeek.MONDAY); // 4주 전부터 시작
        List<Visitor> visitors = visitorRepository.findVisitorsBetweenDates(startDate, today);

        Map<LocalDate, Long> visitorCounts = visitors.stream()
                .collect(Collectors.groupingBy(v -> v.getVDate().with(DayOfWeek.MONDAY), Collectors.counting()));

        return IntStream.rangeClosed(0, 3)
                .mapToObj(i -> startDate.plusWeeks(i))
                .map(date -> visitorCounts.getOrDefault(date, 0L))
                .collect(Collectors.toList());
    }

    public List<Long> getMonthlyData() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusMonths(11).withDayOfMonth(1); // 12개월 전부터 시작
        List<Visitor> visitors = visitorRepository.findVisitorsBetweenDates(startDate, today);

        Map<LocalDate, Long> visitorCounts = visitors.stream()
                .collect(Collectors.groupingBy(v -> v.getVDate().withDayOfMonth(1), Collectors.counting()));

        return IntStream.rangeClosed(0, 11)
                .mapToObj(i -> startDate.plusMonths(i))
                .map(date -> visitorCounts.getOrDefault(date, 0L))
                .collect(Collectors.toList());
    }
//
    public List<Long> getDailyBoardData() {
        LocalDateTime endDateTime = LocalDateTime.now();
        LocalDateTime startDateTime = endDateTime.minusDays(6); // 7일 전부터 시작
        List<Board> boards = visitorRepository.findBoardsBetweenDates(startDateTime, endDateTime);

        Map<LocalDate, Long> visitorCounts = boards.stream()
                .collect(Collectors.groupingBy(b -> b.getRegdate().toLocalDate(), Collectors.counting()));

        return IntStream.rangeClosed(0, 6)
                .mapToObj(i -> startDateTime.toLocalDate().plusDays(i))
                .map(date -> visitorCounts.getOrDefault(date, 0L))
                .collect(Collectors.toList());
    }

    public List<Long> getWeeklyBoardData() {
        LocalDateTime endDateTime = LocalDateTime.now();
        LocalDateTime startDateTime = endDateTime.minusWeeks(3).with(DayOfWeek.MONDAY); // 4주 전부터 시작
        List<Board> boards = visitorRepository.findBoardsBetweenDates(startDateTime, endDateTime);

        Map<LocalDate, Long> visitorCounts = boards.stream()
                .collect(Collectors.groupingBy(b -> b.getRegdate().toLocalDate().with(DayOfWeek.MONDAY), Collectors.counting()));

        return IntStream.rangeClosed(0, 3)
                .mapToObj(i -> startDateTime.toLocalDate().plusWeeks(i))
                .map(date -> visitorCounts.getOrDefault(date, 0L))
                .collect(Collectors.toList());
    }

    public List<Long> getMonthlyBoardData() {
        LocalDateTime endDateTime = LocalDateTime.now();
        LocalDateTime startDateTime = endDateTime.minusMonths(11).withDayOfMonth(1); // 12개월 전부터 시작
        List<Board> boards = visitorRepository.findBoardsBetweenDates(startDateTime, endDateTime);

        Map<LocalDate, Long> visitorCounts = boards.stream()
                .collect(Collectors.groupingBy(b -> b.getRegdate().toLocalDate().withDayOfMonth(1), Collectors.counting()));

        return IntStream.rangeClosed(0, 11)
                .mapToObj(i -> startDateTime.toLocalDate().plusMonths(i))
                .map(date -> visitorCounts.getOrDefault(date, 0L))
                .collect(Collectors.toList());
    }
}
