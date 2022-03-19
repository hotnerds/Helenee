package com.hotnerds.diet.domain.dietFood;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DietFoods {

    @OneToMany(
            mappedBy = "diet",
            fetch = FetchType.LAZY,
            cascade = CascadeType.PERSIST,
            orphanRemoval = true
    )
    private List<DietFood> dietFoods = new ArrayList<>();

    public static DietFoods empty() {
        return new DietFoods();
    }
}
