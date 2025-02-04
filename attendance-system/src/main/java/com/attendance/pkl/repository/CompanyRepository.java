package com.attendance.pkl.repository;

import com.attendance.pkl.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    
    List<Company> findByStatus(String status);
    List<Company> findByBusinessField(String businessField);
    List<Company> findByCity(String city);

    @Query("SELECT c FROM Company c WHERE c.isVerified = true AND c.status = 'ACTIVE'")
    List<Company> findAvailableCompanies();

    @Query("""
        SELECT c FROM Company c 
        WHERE c.maxStudents > (
            SELECT COUNT(p) FROM PKLAssignment p 
            WHERE p.company = c AND p.status = 'ACTIVE'
        )
    """)
    List<Company> findCompaniesWithVacancies();

    @Query("SELECT c FROM Company c WHERE c.isVerified = false")
    List<Company> findPendingVerification();

    @Query("""
        SELECT c FROM Company c 
        WHERE ST_Distance_Sphere(
            point(c.longitude, c.latitude),
            point(?2, ?1)
        ) <= ?3
    """)
    List<Company> findCompaniesWithinRadius(double latitude, double longitude, double radiusInMeters);

    @Query("SELECT COUNT(p) FROM Company c JOIN c.assignments p WHERE c.id = ?1 AND p.status = 'COMPLETED'")
    long countCompletedAssignments(Long companyId);
}
