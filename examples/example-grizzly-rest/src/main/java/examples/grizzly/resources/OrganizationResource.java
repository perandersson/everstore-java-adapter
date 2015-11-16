package examples.grizzly.resources;

import everstore.api.CommitResult;
import examples.grizzly.events.OrganizationCreated;
import examples.grizzly.models.OrgId;
import examples.grizzly.models.Organization;
import examples.grizzly.repositories.OrgRepository;
import examples.grizzly.repositories.OrgStatefulRepository;
import org.glassfish.jersey.server.ManagedAsync;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static javax.ws.rs.core.Response.status;

@Singleton
@Path("/v1/organization")
@Produces(APPLICATION_JSON)
public class OrganizationResource {

    private final OrgRepository orgRepository;

    @Inject
    public OrganizationResource(OrgRepository orgRepository) {
        this.orgRepository = orgRepository;
    }

    @GET
    @ManagedAsync
    public void findOrg(@Suspended AsyncResponse response,
                        @HeaderParam("orgId") int id) {
        response.setTimeout(1000, MILLISECONDS);

        final OrgId orgId = new OrgId(id);
        final CompletableFuture<Organization> futureOrg = orgRepository.findUser(orgId);

        futureOrg.thenApplyAsync(org -> {
            if (org == null) {
                return response.resume(status(NOT_FOUND).build());
            } else {
                return response.resume(org);
            }
        }).exceptionally(e -> response.resume(status(INTERNAL_SERVER_ERROR).entity(e).build()));
    }

    @POST
    @ManagedAsync
    public void addOrg(@Suspended AsyncResponse response,
                       @HeaderParam("orgId") int id) {
        response.setTimeout(1000, MILLISECONDS);

        final OrgId orgId = new OrgId(id);
        final OrgStatefulRepository repository = orgRepository.get(orgId);

        final CompletableFuture<Organization> futureOrg = repository.findUser();
        futureOrg.thenApplyAsync(org -> {
            if (org != null) {
                return response.resume(status(CONFLICT).build());
            } else {
                final CompletableFuture<CommitResult> commitResult =
                        repository.saveEvents(new OrganizationCreated("Name"));
                return commitResult.thenApplyAsync(result -> {
                    if (result.success) {
                        return response.resume(status(OK).build());
                    } else {
                        return response.resume(status(INTERNAL_SERVER_ERROR).build());
                    }
                });
            }
        }).exceptionally(e -> response.resume(status(INTERNAL_SERVER_ERROR).entity(e).build()));
    }


}
