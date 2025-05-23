package gadgetify.server.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @Nationalized
    @Column(name = "name")
    @NotNull(message = "Name is required")
    @NotBlank(message = "Name is required")
    private String name;

    @ColumnDefault("1")
    @Column(name = "status")
    private Boolean status = true;

    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER)
    private Set<CategoryTranslation> categoryTranslations = new LinkedHashSet<>();


    @PostLoad
    public void handle() {
        String locale = LocaleContextHolder.getLocale().getLanguage();
        if (this.categoryTranslations != null && !locale.equalsIgnoreCase("vi")) {
            var localizedName = categoryTranslations.stream()
                    .filter(categoryTranslation -> categoryTranslation.getLanguage().equals(locale))
                    .findFirst()
                    .map(CategoryTranslation::getTranslatedName)
                    .orElse(name);
            this.setName(localizedName);
        }
    }
    public Category toBasicResponse() {
        this.setCategoryTranslations(null);
        return this;
    }
}