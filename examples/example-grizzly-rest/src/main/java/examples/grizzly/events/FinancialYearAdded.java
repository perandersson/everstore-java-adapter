package examples.grizzly.events;

import examples.grizzly.models.FinancialYearId;

import java.time.LocalDate;

import static examples.grizzly.models.FinancialYearId.ZERO;

public final class FinancialYearAdded implements FinancialYearEvent {
    public final FinancialYearId id;
    public final LocalDate startDate;
    public final LocalDate endDate;

    protected FinancialYearAdded() {
        id = ZERO;
        startDate = null;
        endDate = null;
    }

    public FinancialYearAdded(FinancialYearId id, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
