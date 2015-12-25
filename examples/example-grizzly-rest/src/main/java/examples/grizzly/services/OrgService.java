package examples.grizzly.services;

import everstore.java.utils.Optional;
import examples.grizzly.events.OrganizationCreated;
import examples.grizzly.models.FinancialYears;
import examples.grizzly.models.Organization;
import examples.grizzly.repositories.OrgStatefulRepository;

import static examples.grizzly.rest.ResourceUtils.conflicts;

public class OrgService {

    public Optional<Organization> getOrg(final OrgStatefulRepository repository) {
        return repository.getOrg();
    }

    public Optional<Organization> createOrg(final OrgStatefulRepository repository, final String name) {
        return repository.getOrg().map(potentialOrg -> {
            conflicts(potentialOrg, "An organization with id: " + repository.orgId);

            final OrganizationCreated event = new OrganizationCreated("Name");
            repository.addEvents(event);
            return new Organization(repository.orgId, name, new FinancialYears());
        });
    }
}
