package examples.grizzly.repositories;

import everstore.api.Adapter;
import everstore.api.Transaction;
import examples.grizzly.events.FinancialYearAdded;
import examples.grizzly.events.OrganizationCreated;
import examples.grizzly.models.FinancialYear;
import examples.grizzly.models.FinancialYears;
import examples.grizzly.models.OrgId;
import examples.grizzly.models.Organization;
import rx.Observable;

import static rx.Observable.from;

public class OrgStatefulRepository extends StatefulRepository<Organization> {
    private final OrgId id;

    public OrgStatefulRepository(final Adapter adapter, final OrgId id) {
        super(adapter, "/java/grizzly/org-" + id.value);
        this.id = id;
    }

    /**
     * Retrieve the user managed by this repository
     *
     * @return A potential user
     */
    public Observable<Organization> findOrg(final Transaction transaction) {
        return from(findState(transaction));
    }

    @Override
    protected Organization parseEvent(Organization state, Object event) {
        if (event instanceof OrganizationCreated) {
            final OrganizationCreated uc = (OrganizationCreated) event;
            return new Organization(id, uc.name, new FinancialYears());
        } else if (event instanceof FinancialYearAdded) {
            final FinancialYearAdded fya = (FinancialYearAdded) event;
            return state.addFinancialYear(new FinancialYear(fya.id, fya.startDate, fya.endDate));
        }

        throw new IllegalArgumentException("Supplied event: " + event + " is not handled");
    }
}
