package examples.grizzly.service;

import everstore.api.Transaction;
import examples.grizzly.events.OrganizationCreated;
import examples.grizzly.models.FinancialYears;
import examples.grizzly.models.OrgId;
import examples.grizzly.models.Organization;
import examples.grizzly.repositories.OrgRepository;
import examples.grizzly.repositories.OrgStatefulRepository;
import examples.grizzly.resources.organization.OrganizationCandidate;
import rx.Observable;

import javax.inject.Inject;

import static examples.grizzly.resources.ResourceMapper.*;
import static rx.Observable.from;

public class OrganizationService {

    private final OrgRepository orgRepository;

    @Inject
    public OrganizationService(final OrgRepository orgRepository) {
        this.orgRepository = orgRepository;
    }

    public Observable<Organization> get(final OrgId id) {
        final OrgStatefulRepository repository = orgRepository.get(id);
        final Observable<Transaction> transaction = repository.openTransaction();

        return transaction.flatMap(repository::findOrg)
                .map(org -> {
                    notFound(org == null, () -> "Organization with id: " + id + " was not found");
                    return org;
                });
    }

    /**
     * Tries to create a new organization
     *
     * @param request
     * @return
     */
    public Observable<Organization> create(final OrganizationCandidate request) {
        final OrgId freeOrgId = new OrgId();

        final OrgStatefulRepository repository = orgRepository.get(freeOrgId);
        final Observable<Transaction> transaction = repository.openTransaction();
        return transaction.flatMap(repository::findOrg)
                .flatMap(org -> {
                    conflict(org != null, () -> "Organization: " + request + " already exists");
                    final OrganizationCreated event = new OrganizationCreated(request.name);
                    return from(repository.saveEvents(transaction, event));
                }).map(commitResult -> {
                    commitFailed(commitResult, () -> "Could not save organization: " + request);
                    return new Organization(freeOrgId, request.name, new FinancialYears());
                });
    }
}
