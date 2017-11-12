package entities.db;

import entities.servicerequests.Request;

public interface ServiceRequestDataSource {

    default void createRequest(Request request) {  }

    /**
     * Returns true if the specified request object was successfully deleted
     * @param request
     * @return
     */
    default boolean deleteRequest(Request request) {
        return false;
    }

    /**
     * Returns true if the specified request id was successfully deleted
     * @param id
     * @return
     */
    default boolean deleteRequest(String id) {
        return false;
    }

    default Request getRequest(String id) { return null; }

    default void close() { };
}
