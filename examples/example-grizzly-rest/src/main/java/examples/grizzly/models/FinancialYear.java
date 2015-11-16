package examples.grizzly.models;

import java.time.LocalDate;

public final class FinancialYear {
    public final FinancialYearId id;
    public final LocalDate startDate;
    public final LocalDate endDate;

    public FinancialYear(FinancialYearId id, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
