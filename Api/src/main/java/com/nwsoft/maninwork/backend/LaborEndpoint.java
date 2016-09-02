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
        name = "laborApi",
        version = "v1",
        resource = "labor",
        namespace = @ApiNamespace(
                ownerDomain = "backend.maninwork.nwsoft.com",
                ownerName = "backend.maninwork.nwsoft.com",
                packagePath = ""
        )
)
public class LaborEndpoint {

    private static final Logger logger = Logger.getLogger(LaborEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 200;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Labor.class);
    }

    /**
     * Returns the {@link Labor} with the corresponding ID.
     *
     * @param lid the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Labor} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "labor/{lid}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Labor get(@Named("lid") Long lid) throws NotFoundException {
        logger.info("Getting Labor with ID: " + lid);
        Labor labor = ofy().load().type(Labor.class).id(lid).now();
        if (labor == null) {
            throw new NotFoundException("Could not find Labor with ID: " + lid);
        }
        return labor;
    }

    /**
     * Inserts a new {@code Labor}.
     */
    @ApiMethod(
            name = "insert",
            path = "labor",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Labor insert(Labor labor) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that labor.lid has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. Long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(labor).now();
        logger.info("Created Labor with ID: " + labor.getLid());

        return ofy().load().entity(labor).now();
    }

    /**
     * Updates an existing {@code Labor}.
     *
     * @param lid   the ID of the entity to be updated
     * @param labor the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code lid} does not correspond to an existing
     *                           {@code Labor}
     */
    @ApiMethod(
            name = "update",
            path = "labor/{lid}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Labor update(@Named("lid") Long lid, Labor labor) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(lid);
        ofy().save().entity(labor).now();
        logger.info("Updated Labor: " + labor);
        return ofy().load().entity(labor).now();
    }

    /**
     * Deletes the specified {@code Labor}.
     *
     * @param lid the ID of the entity to delete
     * @throws NotFoundException if the {@code lid} does not correspond to an existing
     *                           {@code Labor}
     */
    @ApiMethod(
            name = "remove",
            path = "labor/{lid}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("lid") Long lid) throws NotFoundException {
        checkExists(lid);
        ofy().delete().type(Labor.class).id(lid).now();
        logger.info("Deleted Labor with ID: " + lid);
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
            path = "labor",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Labor> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Labor> query = ofy().load().type(Labor.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Labor> queryIterator = query.iterator();
        List<Labor> laborList = new ArrayList<Labor>(limit);
        while (queryIterator.hasNext()) {
            laborList.add(queryIterator.next());
        }
        return CollectionResponse.<Labor>builder().setItems(laborList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    @ApiMethod(
            name = "listGmail",
            path = "laborGmail",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Labor> listGmail(@Named("gmail") String gmail,
                                          @Nullable @Named("cursor") String cursor,
                                          @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        logger.info("gmail= " + gmail);
        Query<Labor> query = ofy().load().type(Labor.class)
                .filter("gmail",gmail).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Labor> queryIterator = query.iterator();
        List<Labor> laborList = new ArrayList<Labor>(limit);
        while (queryIterator.hasNext()) {
            laborList.add(queryIterator.next());
        }
        return CollectionResponse.<Labor>builder().setItems(laborList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }
    private void checkExists(Long lid) throws NotFoundException {
        try {
            ofy().load().type(Labor.class).id(lid).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Labor with ID: " + lid);
        }
    }
}