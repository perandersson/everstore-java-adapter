package examples.grizzly.services;

import everstore.java.utils.Optional;
import examples.grizzly.models.FinancialYear;
import examples.grizzly.models.FinancialYearId;
import examples.grizzly.models.FinancialYears;
import examples.grizzly.repositories.OrgStatefulRepository;

public class FinancialYearService {

    public final OrgService orgService;

    public FinancialYearService(final OrgService orgService) {
        this.orgService = orgService;
    }

    public Optional<FinancialYears> getFinancialYears(final OrgStatefulRepository repository) {
        return orgService.getOrg(repository).map(org -> org.financialYears);
    }

    public Optional<FinancialYear> findFinancialYear(final OrgStatefulRepository repository, final FinancialYearId id) {
        return getFinancialYears(repository).flatMap(financialYears -> financialYears.findById(id));
    }
}
