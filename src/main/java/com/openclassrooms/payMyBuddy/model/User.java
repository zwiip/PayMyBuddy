package com.openclassrooms.payMyBuddy.model;

import jakarta.persistence.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.HashSet;
import java.util.Set;

@Entity
@DynamicUpdate
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "wallet", nullable = false)
    private double wallet;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @JoinTable(
            name = "assoc_connections",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "connection_id")
    )
    private Set<User> buddies = new HashSet<>();

    public User() {}

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getWallet() {return wallet;}

    public void setWallet(double wallet) {
        this.wallet = wallet;
    }

    public Set<User> getBuddies() { return buddies; }

    public void setBuddies(Set<User> buddies) { this.buddies = buddies; }
}
