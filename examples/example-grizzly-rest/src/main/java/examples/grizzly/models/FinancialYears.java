package examples.grizzly.models;

import java.util.ArrayList;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public class FinancialYears extends ArrayList<FinancialYear> {

    @Override
    public FinancialYears clone() {
        return (FinancialYears) super.clone();
    }

    /**
     * Find the financial year with the given id
     *
     * @param id The financial years id
     * @return
     */
    public Optional<FinancialYear> findById(final FinancialYearId id) {
        for (FinancialYear financialYear : this) {
            if (financialYear.id.equals(id)) {
                return of(financialYear);
            }
        }
        return empty();
    }
}
