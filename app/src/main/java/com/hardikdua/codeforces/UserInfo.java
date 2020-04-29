package com.hardikdua.codeforces;

public class UserInfo {
    private String name,email,college,codechef,codeforces,hackerrank;

    UserInfo()
    {

    }
    public UserInfo(String name,String college,String email,String codeforces,String codechef,String hackerrank)
    {
        this.name = name;
        this.college = college;
        this.email = email;
        this.codechef = codechef;
        this.codeforces = codeforces;
        this.hackerrank = hackerrank;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCollege() {
        return college;
    }

    public String getCodechef() {
        return codechef;
    }

    public String getCodeforces() {
        return codeforces;
    }

    public String getHackerrank() {
        return hackerrank;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public void setCodechef(String codechef) {
        this.codechef = codechef;
    }

    public void setCodeforces(String codeforces) {
        this.codeforces = codeforces;
    }

    public void setHackerrank(String hackerrank) {
        this.hackerrank = hackerrank;
    }
}
