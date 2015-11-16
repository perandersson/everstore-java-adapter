package examples.grizzly.resources.financialyears;

import everstore.api.CommitResult;
import examples.grizzly.events.FinancialYearAdded;
import examples.grizzly.models.FinancialYear;
import examples.grizzly.models.FinancialYearId;
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
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.status;

@Singleton
@Path("/v1/financialyear")
@Produces(APPLICATION_JSON)
public class FinancialYearRepository {

    private final OrgRepository orgRepository;

    @Inject
    public FinancialYearRepository(OrgRepository orgRepository) {
        this.orgRepository = orgRepository;
    }

    @GET
    @ManagedAsync
    public void getFinancialYears(@Suspended AsyncResponse response,
                                  @HeaderParam("orgId") int id) {
        response.setTimeout(1000, MILLISECONDS);

        final OrgId orgId = new OrgId(id);
        final CompletableFuture<Organization> futureOrg = orgRepository.findUser(orgId);

        futureOrg.thenApplyAsync(org -> {
            if (org == null) {
                return response.resume(status(NOT_FOUND).build());
            } else {
                return response.resume(org.financialYears);
            }
        }).exceptionally(e -> response.resume(status(INTERNAL_SERVER_ERROR).entity(e).build()));
    }

    @GET
    @Path("/{id: \\d+}")
    @ManagedAsync
    public void getFinancialYear(@Suspended AsyncResponse response,
                                 @HeaderParam("orgId") int id,
                                 @PathParam("id") long financialYearId) {
        final OrgId orgId = new OrgId(id);
        final CompletableFuture<Organization> futureOrg = orgRepository.findUser(orgId);

        futureOrg.thenApplyAsync(org -> {
            final Optional<FinancialYear> financialYear =
                    org.financialYears.findById(new FinancialYearId(financialYearId));
            return financialYear.map(response::resume)
                    .orElseGet(() -> response.resume(status(NOT_FOUND).build()));
        }).exceptionally(e -> response.resume(status(INTERNAL_SERVER_ERROR).entity(e).build()));
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @ManagedAsync
    public void addFinancialYears(@Suspended final AsyncResponse response,
                                  @HeaderParam("orgId") final int id,
                                  final FinancialYearCandidate candidate) {
        response.setTimeout(1000, MILLISECONDS);

        final OrgId orgId = new OrgId(id);
        final OrgStatefulRepository repository = orgRepository.get(orgId);
        CompletableFuture<CommitResult> commitResult =
                repository.saveEvents(new FinancialYearAdded(repository.getNextFinancialYearId(),
                        candidate.startDate, candidate.endDate));

        commitResult.thenApplyAsync((result) -> {
            if (result.success) {
                return
            } else {
                return
            }
        }).exceptionally(e -> response.resume(status(INTERNAL_SERVER_ERROR).entity(e).build()));


        final CompletableFuture<Organization> futureUser = orgRepository.findUser(orgId);

        futureUser.thenApplyAsync(org -> {
            if (org == null) {
                return response.resume(status(NOT_FOUND).build());
            } else {
                return response.resume(org.financialYears);
            }
        }).exceptionally(e -> response.resume(status(INTERNAL_SERVER_ERROR).entity(e).build()));
    }
}
