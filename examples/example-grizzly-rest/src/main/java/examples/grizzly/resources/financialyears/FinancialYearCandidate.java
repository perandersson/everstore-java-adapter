package examples.grizzly.resources.financialyears;

import java.time.LocalDate;

public final class FinancialYearCandidate {
    public final LocalDate startDate;
    public final LocalDate endDate;

    protected FinancialYearCandidate() {
        startDate = null;
        endDate = null;
    }

    public FinancialYearCandidate(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
