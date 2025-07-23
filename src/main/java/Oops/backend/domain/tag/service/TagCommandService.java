package Oops.backend.domain.tag.service;

import Oops.backend.domain.tag.entity.Tag;

import java.util.LinkedHashSet;
import java.util.List;

public interface TagCommandService {

    LinkedHashSet<Tag> findOrCreateTagsByNames(List<String> tagNames);

}
