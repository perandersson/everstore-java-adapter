package examples.grizzly.resources.financialyears;

import everstore.api.CommitResult;
import everstore.java.utils.Optional;
import examples.grizzly.models.FinancialYear;
import examples.grizzly.models.FinancialYearId;
import examples.grizzly.models.FinancialYears;
import examples.grizzly.repositories.OrgStatefulRepository;
import examples.grizzly.repositories.factory.RepositoryParam;
import examples.grizzly.services.FinancialYearService;
import org.glassfish.jersey.server.ManagedAsync;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

import static examples.grizzly.rest.ResourceUtils.handleGet;
import static examples.grizzly.rest.ResourceUtils.handlePost;
import static examples.grizzly.rest.ResourceUtils.validateCommit;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Singleton
@Path("/v1/financialyears")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class FinancialYearRepository {

    private final FinancialYearService financialYearService;

    @Inject
    public FinancialYearRepository(final FinancialYearService financialYearService) {
        this.financialYearService = financialYearService;
    }

    @GET
    @ManagedAsync
    public void getFinancialYears(@Suspended AsyncResponse response,
                                  @RepositoryParam Optional<OrgStatefulRepository> statefulRepository) {
        handleGet(response, statefulRepository.flatMap(repository -> {
            final Optional<FinancialYears> financialYears = financialYearService.getFinancialYears(repository);
            repository.close();
            return financialYears;
        }));
    }

    @GET
    @Path("/{id: \\d+}")
    @ManagedAsync
    public void getFinancialYear(@Suspended AsyncResponse response,
                                 @RepositoryParam Optional<OrgStatefulRepository> statefulRepository,
                                 @PathParam("id") final FinancialYearId financialYearId) {
        handleGet(response, statefulRepository.flatMap(repository -> {
            final Optional<FinancialYear> financialYear = financialYearService.findFinancialYear(repository, financialYearId);
            repository.close();
            return financialYear;
        }));
    }

    @POST
    @ManagedAsync
    public void addFinancialYear(@Suspended AsyncResponse response,
                                 @RepositoryParam Optional<OrgStatefulRepository> statefulRepository,
                                 final FinancialYearCandidate candidate) {
        handlePost(response, statefulRepository.flatMap(repository -> {
            return financialYearService.addFinancialYear(repository, candidate).flatMap(financialYear -> {
                final Optional<CommitResult> commit = repository.commit();
                return commit.<FinancialYear>map(comitResult -> {
                    validateCommit(comitResult, "Could not add financial year");
                    return financialYear;
                });
            });
        }));
    }
}
