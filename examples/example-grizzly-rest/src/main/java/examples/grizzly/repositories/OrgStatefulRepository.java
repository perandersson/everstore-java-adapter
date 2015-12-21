package examples.grizzly.repositories;

import everstore.api.Transaction;
import everstore.java.repositories.StatefulRepository;
import everstore.java.repositories.StatefulTransactionalState;
import everstore.java.utils.Optional;
import examples.grizzly.events.FinancialYearAdded;
import examples.grizzly.events.OrganizationCreated;
import examples.grizzly.models.FinancialYear;
import examples.grizzly.models.FinancialYears;
import examples.grizzly.models.OrgId;
import examples.grizzly.models.Organization;

public class OrgStatefulRepository extends StatefulTransactionalState<Organization, StatefulRepository<Organization>> {
    public final OrgId orgId;

    public OrgStatefulRepository(final Transaction transaction,
                                 final StatefulRepository<Organization> repository,
                                 final OrgId orgId) {
        super(transaction, repository);
        this.orgId = orgId;
    }

    public Optional<Organization> getOrg() {
        return getState();
    }

    @Override
    protected Organization parseEvent(Organization state, Object event) {
        if (event instanceof OrganizationCreated) {
            final OrganizationCreated uc = (OrganizationCreated) event;
            return new Organization(orgId, uc.name, new FinancialYears());
        } else if (event instanceof FinancialYearAdded) {
            final FinancialYearAdded fya = (FinancialYearAdded) event;
            return state.addFinancialYear(new FinancialYear(fya.id, fya.startDate, fya.endDate));
        }

        throw new IllegalArgumentException("Supplied event: " + event + " is not handled");
    }
}
