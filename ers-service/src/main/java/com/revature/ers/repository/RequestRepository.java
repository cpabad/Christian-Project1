package com.revature.ers.repository;

import com.revature.ers.model.EventLocation;
import com.revature.ers.model.Request;
import com.revature.ers.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * The whole monolith RequestRepositoryImpl (~185 lines of Session/try/catch/commit/rollback
 * per method) collapses to this. findById/findAll/save/delete come from JpaRepository; the
 * custom finders below are derived from method names by Spring Data:
 *   - findByRequester                          -> WHERE requesteruserid = ?
 *   - findByRequesterAndRequestStatus_StatusId -> WHERE requesteruserid = ? AND statusid = ?
 *       (statusId 2 = pending, 1 = resolved - same as the monolith's two finders)
 *   - findByEventDateAndEventLocationAndRequester -> the natural-key lookup after insert
 */
@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {

    List<Request> findByRequester(User requester);

    // navigates Request.requester.userId -> WHERE requesteruserid = ? (handy for a REST path id)
    List<Request> findByRequester_UserId(int userId);

    List<Request> findByRequesterAndRequestStatus_StatusId(User requester, int statusId);

    Optional<Request> findByEventDateAndEventLocationAndRequester(
            LocalDate eventDate, EventLocation eventLocation, User requester);
}
