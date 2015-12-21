package examples.grizzly.services;

import everstore.java.utils.Optional;
import examples.grizzly.events.OrganizationCreated;
import examples.grizzly.models.FinancialYears;
import examples.grizzly.models.Organization;
import examples.grizzly.repositories.OrgStatefulRepository;

import static examples.grizzly.rest.ResourceUtils.conflicts;
import static examples.grizzly.rest.ResourceUtils.validateCommit;

public class OrgService {

    public Optional<Organization> getOrg(final OrgStatefulRepository repository) {
        return repository.getOrg();
    }

    public Optional<Organization> createOrg(final OrgStatefulRepository repository, final String name) {
        return repository.getOrg().flatMap(potentialOrg -> {
            conflicts(potentialOrg, "An organization with id: " + repository.orgId);
            final OrganizationCreated event = new OrganizationCreated("Name");
            return repository.saveEvents(event).map(commitResult -> {
                validateCommit(commitResult, "Could not create organization");
                return new Organization(repository.orgId, name, new FinancialYears());
            });
        });
    }
}
