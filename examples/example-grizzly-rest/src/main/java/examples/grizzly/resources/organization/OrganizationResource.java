package examples.grizzly.resources.organization;

import examples.grizzly.models.OrgId;
import examples.grizzly.resources.EventStoreResource;
import examples.grizzly.service.OrganizationService;
import org.glassfish.jersey.server.ManagedAsync;
import rx.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Singleton
@Path("/v1/organization")
@Produces(APPLICATION_JSON)
public class OrganizationResource implements EventStoreResource {

    private final OrganizationService organizationService;

    @Inject
    public OrganizationResource(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GET
    @ManagedAsync
    public void findOrg(@Suspended AsyncResponse response,
                        @HeaderParam("orgId") final String id) {
        response.setTimeout(1000, MILLISECONDS);

        final OrgId orgId = OrgId.fromString(id);
        final Observable<OrganizationResult> result =
                organizationService.get(orgId).map(org -> new OrganizationResult(orgId, org.name));
        handleResponse(response, result);
    }

    @POST
    @ManagedAsync
    public void addOrg(@Suspended AsyncResponse response,
                       final OrganizationCandidate request) {
        response.setTimeout(1000, MILLISECONDS);
        handleResponse(response, organizationService.create(request));
    }

    //
    // Results
    //

    public static final class OrganizationResult {
        public final OrgId id;
        public final String name;

        public OrganizationResult(OrgId id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
