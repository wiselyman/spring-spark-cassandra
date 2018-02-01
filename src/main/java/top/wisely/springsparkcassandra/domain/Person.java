package top.wisely.springsparkcassandra.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Person {
    private final String id;
    private final String name;
    private final int age;
}
