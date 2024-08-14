package com.prototype.userService.models.nodes;

import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Node
public class AddressNode {
    @Id
    @GeneratedValue
    private Long id;

    private String city;

    @Relationship(type = "STAYS_AT", direction = Relationship.Direction.INCOMING)
    private List<UserNode> userNodes;
}
