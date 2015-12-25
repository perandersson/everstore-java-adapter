package examples.grizzly.services;

import everstore.java.utils.Optional;
import examples.grizzly.events.FinancialYearAdded;
import examples.grizzly.models.FinancialYear;
import examples.grizzly.models.FinancialYearId;
import examples.grizzly.models.FinancialYears;
import examples.grizzly.repositories.OrgStatefulRepository;
import examples.grizzly.resources.financialyears.FinancialYearCandidate;

import javax.inject.Inject;

import static examples.grizzly.rest.ResourceUtils.conflicts;

public class FinancialYearService {

    public final OrgService orgService;

    @Inject
    public FinancialYearService(final OrgService orgService) {
        this.orgService = orgService;
    }

    public Optional<FinancialYears> getFinancialYears(final OrgStatefulRepository repository) {
        return orgService.getOrg(repository).map(org -> org.financialYears);
    }

    public Optional<FinancialYear> findFinancialYear(final OrgStatefulRepository repository, final FinancialYearId id) {
        return getFinancialYears(repository).flatMap(financialYears -> financialYears.findById(id));
    }

    public Optional<FinancialYear> addFinancialYear(final OrgStatefulRepository repository, final FinancialYearCandidate candidate) {
        return getFinancialYears(repository).map(financialYears -> {
            conflicts(financialYears.findOverlapping(candidate.startDate, candidate.endDate),
                    "You already have a a financial year within the supplied interval");

            final FinancialYearId id = new FinancialYearId();
            repository.addEvents(new FinancialYearAdded(id, candidate.startDate, candidate.endDate));
            return new FinancialYear(id, candidate.startDate, candidate.endDate);
        });
    }
}
