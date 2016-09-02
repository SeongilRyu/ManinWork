package com.nwsoft.maninwork.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "companyApi",
        version = "v1",
        resource = "company",
        namespace = @ApiNamespace(
                ownerDomain = "backend.maninwork.nwsoft.com",
                ownerName = "backend.maninwork.nwsoft.com",
                packagePath = ""
        )
)
public class CompanyEndpoint {

    private static final Logger logger = Logger.getLogger(CompanyEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 200;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Company.class);
    }

    /**
     * Returns the {@link Company} with the corresponding ID.
     *
     * @param cid the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Company} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "company/{cid}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Company get(@Named("cid") Long cid) throws NotFoundException {
        logger.info("Getting Company with ID: " + cid);
        Company company = ofy().load().type(Company.class).id(cid).now();
        if (company == null) {
            throw new NotFoundException("Could not find Company with ID: " + cid);
        }
        return company;
    }

    /**
     * Inserts a new {@code Company}.
     */
    @ApiMethod(
            name = "insert",
            path = "company",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Company insert(Company company) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that company.cid has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(company).now();
        logger.info("Created Company with ID: " + company.getCid());

        return ofy().load().entity(company).now();
    }

    /**
     * Updates an existing {@code Company}.
     *
     * @param cid     the ID of the entity to be updated
     * @param company the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code cid} does not correspond to an existing
     *                           {@code Company}
     */
    @ApiMethod(
            name = "update",
            path = "company/{cid}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Company update(@Named("cid") Long cid, Company company) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(cid);
        ofy().save().entity(company).now();
        logger.info("Updated Company: " + company);
        return ofy().load().entity(company).now();
    }

    /**
     * Deletes the specified {@code Company}.
     *
     * @param cid the ID of the entity to delete
     * @throws NotFoundException if the {@code cid} does not correspond to an existing
     *                           {@code Company}
     */
    @ApiMethod(
            name = "remove",
            path = "company/{cid}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("cid") Long cid) throws NotFoundException {
        checkExists(cid);
        ofy().delete().type(Company.class).id(cid).now();
        logger.info("Deleted Company with ID: " + cid);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "company",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Company> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Company> query = ofy().load().type(Company.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Company> queryIterator = query.iterator();
        List<Company> companyList = new ArrayList<Company>(limit);
        while (queryIterator.hasNext()) {
            companyList.add(queryIterator.next());
        }
        return CollectionResponse.<Company>builder().setItems(companyList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }
    @ApiMethod(
            name = "listGmail",
            path = "companyGmail",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Company> listGmail(@Named("gmail") String gmail,
                                                 @Nullable @Named("cursor") String cursor,
                                                 @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        logger.info("Endpoint received user email: " + gmail);
        //https://github.com/objectify/objectify/wiki/Queries
        //Query<Cattle> query = ofy().load().type(Cattle.class).filter("email", email)
        //        .order("email").order("cow_no").limit(limit);

        Query<Company> query = ofy().load().type(Company.class).filter("gmail",gmail).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Company> queryIterator = query.iterator();
        List<Company> companyList = new ArrayList<Company>(limit);
        while (queryIterator.hasNext()) {
            companyList.add(queryIterator.next());
        }
        return CollectionResponse.<Company>builder().setItems(companyList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }
    private void checkExists(Long cid) throws NotFoundException {
        try {
            ofy().load().type(Company.class).id(cid).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Company with ID: " + cid);
        }
    }
}