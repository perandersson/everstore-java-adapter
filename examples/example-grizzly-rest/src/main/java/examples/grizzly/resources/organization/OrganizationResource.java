package examples.grizzly.resources.organization;

import everstore.api.CommitResult;
import everstore.java.utils.Optional;
import examples.grizzly.models.OrgId;
import examples.grizzly.models.Organization;
import examples.grizzly.repositories.OrgRepository;
import examples.grizzly.repositories.OrgStatefulRepository;
import examples.grizzly.repositories.factory.RepositoryParam;
import examples.grizzly.services.OrgService;
import org.glassfish.jersey.server.ManagedAsync;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

import static examples.grizzly.rest.ResourceUtils.*;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Singleton
@Path("/v1/organizations")
@Produces(APPLICATION_JSON)
public class OrganizationResource {

    private final OrgRepository orgRepository;
    private final OrgService orgService;

    @Inject
    public OrganizationResource(OrgRepository orgRepository, OrgService orgService) {
        this.orgRepository = orgRepository;
        this.orgService = orgService;
    }

    @GET
    @ManagedAsync
    public void findOrg(@Suspended final AsyncResponse response,
                        @RepositoryParam Optional<OrgStatefulRepository> statefulRepository) {
        handleGet(response, statefulRepository.flatMap(repository -> {
            Optional<Organization> potentialOrg = orgService.getOrg(repository);
            repository.close();
            return potentialOrg;
        }));
    }

    @POST
    @ManagedAsync
    public void addOrg(@Suspended final AsyncResponse response) {
        final OrgId orgId = new OrgId();
        final Optional<OrgStatefulRepository> statefulRepository = orgRepository.get(orgId);
        handlePost(response, statefulRepository.flatMap(repository -> {
            return orgService.createOrg(repository, "Name here!!!").flatMap(org -> {
                final Optional<CommitResult> commit = repository.commit();
                return commit.<Organization>map(commitResult -> {
                    validateCommit(commitResult, "Could not create organization");
                    return org;
                });
            });
        }));
    }
}
