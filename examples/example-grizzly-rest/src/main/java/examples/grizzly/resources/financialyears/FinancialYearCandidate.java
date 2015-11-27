package examples.grizzly.resources.financialyears;

import java.time.LocalDate;

import static everstore.api.validation.Validation.require;

public final class FinancialYearCandidate {
    public final LocalDate startDate;
    public final LocalDate endDate;

    protected FinancialYearCandidate() {
        startDate = null;
        endDate = null;
    }

    public FinancialYearCandidate(LocalDate startDate, LocalDate endDate) {
        require(startDate.isBefore(endDate), "The startDate must be before the endDate");

        this.startDate = startDate;
        this.endDate = endDate;
    }
}
