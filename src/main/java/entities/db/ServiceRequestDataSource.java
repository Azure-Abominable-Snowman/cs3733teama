package entities.db;

import entities.servicerequests.Request;

public interface ServiceRequestDataSource {

    default void submitRequest(Request request) {  }

    /**
     * Returns true if the specified request object was successfully deleted
     * @param request
     * @return
     */
    default boolean cancelRequest(Request request) {
        return false;
    }

    /**
     * Returns true if the specified request id was successfully deleted
     * @param id
     * @return
     */
    default boolean cancelRequest(String id) {
        return false;
    }

    default Request getRequest(String id) { return null; }

    default boolean fulfillRequest(String id) { return false; }

    default void close() { };
}
