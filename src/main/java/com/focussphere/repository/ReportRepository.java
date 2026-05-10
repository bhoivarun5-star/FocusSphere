package com.focussphere.repository;

import com.focussphere.model.Report;
import com.focussphere.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByUserOrderByReportYearDescReportMonthDesc(User user);
    Optional<Report> findByUserAndReportMonthAndReportYear(User user, Integer month, Integer year);
}
