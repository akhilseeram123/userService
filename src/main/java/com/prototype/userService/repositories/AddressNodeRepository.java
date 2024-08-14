package com.prototype.userService.repositories;

import com.prototype.userService.models.nodes.AddressNode;
import com.prototype.userService.models.nodes.UserNode;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressNodeRepository extends Neo4jRepository<AddressNode, Long> {

    @Query("MATCH (u:UserNode)-[:STAYS_AT]->(a:AddressNode) WHERE a.city = $city RETURN  u")
    List<UserNode> findUserNodesByCity(@Param("city") String city);

}
