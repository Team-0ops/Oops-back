package Oops.backend.domain.postGroup.entity;

import Oops.backend.domain.category.entity.Category;
import Oops.backend.domain.common.BaseEntity;
import Oops.backend.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostGroup extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category")
    private Category category;

    @OneToMany(mappedBy = "postGroup", cascade = CascadeType.ALL)
    List<Post> posts = new ArrayList<>(3);

    public Category getCategory(){
        if (this.category == null) return null;
        return this.category;
    }
}