package com.prototype.userService.models.nodes;

import lombok.*;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Node
public class UserNode {
    @Id
    @GeneratedValue
    private Long id;

    private String userName;

    @Relationship(type = "STAYS_AT", direction = Relationship.Direction.OUTGOING)
    private AddressNode addressNode;

}
