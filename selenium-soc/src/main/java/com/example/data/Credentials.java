package com.example.data;

public class Credentials {
    private String username;
    private String password;

    private Credentials() {}

    public static Builder builder(){ return new Builder(); }

    public static class Builder {
        private final Credentials c = new Credentials();
        public Builder validTheInternetUser() {
            c.username = "tomsmith";
            c.password = "SuperSecretPassword!";
            return this;
        }
        public Builder username(String u) { c.username = u; return this; }
        public Builder password(String p) { c.password = p; return this; }
        public Credentials build() { return c; }
    }

    public String username(){ return username; }
    public String password(){ return password; }
}
