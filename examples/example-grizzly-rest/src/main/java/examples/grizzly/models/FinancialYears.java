package examples.grizzly.models;

import everstore.java.utils.Optional;

import java.time.LocalDate;
import java.util.ArrayList;

import static everstore.java.utils.Optional.empty;
import static everstore.java.utils.Optional.of;


public class FinancialYears extends ArrayList<FinancialYear> {

    @Override
    public FinancialYears clone() {
        return (FinancialYears) super.clone();
    }

    /**
     * Find the financial year with the given id
     *
     * @param id The financial years id
     * @return A potential financial year. Is empty if not found
     */
    public Optional<FinancialYear> findById(final FinancialYearId id) {
        for (FinancialYear financialYear : this) {
            if (financialYear.id.equals(id)) {
                return of(financialYear);
            }
        }
        return empty();
    }

    public Optional<FinancialYear> findOverlapping(final LocalDate startDate, final LocalDate endDate) {
        for (final FinancialYear financialYear : this) {
            if (startDate.isBefore(financialYear.startDate) && financialYear.endDate.isBefore(endDate)) {
                return of(financialYear);
            }
            if (financialYear.startDate.equals(startDate) && financialYear.endDate.equals(endDate)) {
                return of(financialYear);
            }
        }

        return empty();
    }
}
