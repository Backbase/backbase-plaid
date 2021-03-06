package com.backbase.proto.plaid.repository;

import com.backbase.proto.plaid.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * This class enables the use of and access to the Item database where data on the banking Item is stored.
 */
@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    /**
     * Checks if an item exists by searching for it in the item database using it's ID.
     *
     * @param itemId ID of the item being searched for
     * @return true if the item exists in the database false if it doesn't
     */
    boolean existsByItemId(String itemId);

    /**
     * Finds an item in the item table using its ID.
     *
     * @param itemId identifies the item being searched for
     * @return the item object with the matching ID if it is present if not nothing is returned
     */
    Optional<Item> findByItemId(String itemId);


    List<Item> findAllByCreatedBy(String userId);

    List<Item> findAllByCreatedByAndState(String userId, String state);



}
