package mk.das.finki.designandarchitectureproject.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Company {

    private String name;
    private String code;
    private List<Share> shares;

    public Company(String name, String code) {
        this.name = name;
        this.code = code;
        this.shares = new ArrayList<>();
    }
}
