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
        name = "transactionApi",
        version = "v1",
        resource = "transaction",
        namespace = @ApiNamespace(
                ownerDomain = "backend.maninwork.nwsoft.com",
                ownerName = "backend.maninwork.nwsoft.com",
                packagePath = ""
        )
)
public class TransactionEndpoint {

    private static final Logger logger = Logger.getLogger(TransactionEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 200;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Transaction.class);
    }

    /**
     * Returns the {@link Transaction} with the corresponding ID.
     *
     * @param tid the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code Transaction} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "transaction/{tid}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Transaction get(@Named("tid") Long tid) throws NotFoundException {
        logger.info("Getting Transaction with ID: " + tid);
        Transaction transaction = ofy().load().type(Transaction.class).id(tid).now();
        if (transaction == null) {
            throw new NotFoundException("Could not find Transaction with ID: " + tid);
        }
        return transaction;
    }

    /**
     * Inserts a new {@code Transaction}.
     */
    @ApiMethod(
            name = "insert",
            path = "transaction",
            httpMethod = ApiMethod.HttpMethod.POST)
    public Transaction insert(Transaction transaction) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that transaction.tid has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. Long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(transaction).now();
        logger.info("Created Transaction with ID: " + transaction.getTid());

        return ofy().load().entity(transaction).now();
    }

    /**
     * Updates an existing {@code Transaction}.
     *
     * @param tid         the ID of the entity to be updated
     * @param transaction the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code tid} does not correspond to an existing
     *                           {@code Transaction}
     */
    @ApiMethod(
            name = "update",
            path = "transaction/{tid}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public Transaction update(@Named("tid") Long tid, Transaction transaction) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(tid);
        ofy().save().entity(transaction).now();
        logger.info("Updated Transaction: " + transaction);
        return ofy().load().entity(transaction).now();
    }

    /**
     * Deletes the specified {@code Transaction}.
     *
     * @param tid the ID of the entity to delete
     * @throws NotFoundException if the {@code tid} does not correspond to an existing
     *                           {@code Transaction}
     */
    @ApiMethod(
            name = "remove",
            path = "transaction/{tid}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("tid") Long tid) throws NotFoundException {
        checkExists(tid);
        ofy().delete().type(Transaction.class).id(tid).now();
        logger.info("Deleted Transaction with ID: " + tid);
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
            path = "transaction",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Transaction> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<Transaction> query = ofy().load().type(Transaction.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Transaction> queryIterator = query.iterator();
        List<Transaction> transactionList = new ArrayList<Transaction>(limit);
        while (queryIterator.hasNext()) {
            transactionList.add(queryIterator.next());
        }
        return CollectionResponse.<Transaction>builder().setItems(transactionList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }
    @ApiMethod(
            name = "listGmail",
            path = "transactionGmail",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<Transaction> listGmail(@Named("gmail") String gmail,
                                                     @Nullable @Named("cursor") String cursor,
                                                     @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        logger.info("gmail= " + gmail);
        Query<Transaction> query = ofy().load().type(Transaction.class)
                .filter("gmail",gmail).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<Transaction> queryIterator = query.iterator();
        List<Transaction> transactionList = new ArrayList<Transaction>(limit);
        while (queryIterator.hasNext()) {
            transactionList.add(queryIterator.next());
        }
        return CollectionResponse.<Transaction>builder().setItems(transactionList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }
    private void checkExists(Long tid) throws NotFoundException {
        try {
            ofy().load().type(Transaction.class).id(tid).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find Transaction with ID: " + tid);
        }
    }
}