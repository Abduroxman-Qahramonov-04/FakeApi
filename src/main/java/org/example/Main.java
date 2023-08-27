package org.example;

import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class Main {
    public static void main(String[] args) {
        User user = new User("Abduroxman",12);
        System.out.println(user.getAge());
        System.out.println(user);
        Faker faker = new Faker();
    }
}
@Data
@AllArgsConstructor
@NoArgsConstructor
class User{
    String name;
    int age;
}