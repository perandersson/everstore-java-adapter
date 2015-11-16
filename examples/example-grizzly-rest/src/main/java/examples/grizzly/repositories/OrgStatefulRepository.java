package examples.grizzly.repositories;

import everstore.api.Adapter;
import examples.grizzly.events.FinancialYearAdded;
import examples.grizzly.events.OrganizationCreated;
import examples.grizzly.models.*;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

public class OrgStatefulRepository extends StatefulRepository<Organization> {
    private final OrgId id;
    private AtomicLong financialYearId = new AtomicLong(0);

    public OrgStatefulRepository(final Adapter adapter, final OrgId id) {
        super(adapter, "/java/grizzly/org-" + id.value);
        this.id = id;
    }

    /**
     * Retrieve the user managed by this repository
     *
     * @return A potential user
     */
    public CompletableFuture<Organization> findUser() {
        return findState();
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

    /**
     * Retrieves the next available financial year id
     *
     * @return
     */
    public FinancialYearId getNextFinancialYearId() {
        return new FinancialYearId(financialYearId.incrementAndGet());
    }
}
