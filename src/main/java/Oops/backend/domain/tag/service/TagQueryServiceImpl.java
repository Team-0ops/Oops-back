package Oops.backend.domain.tag.service;

import Oops.backend.domain.tag.entity.Tag;
import Oops.backend.domain.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TagQueryServiceImpl implements TagQueryService{

    private final TagRepository tagRepository;

    @Override
    public Tag findTagByName(String name) {

        return tagRepository.findByName(name);

    }

    @Override
    public boolean existsTagByName(String name) {

        return tagRepository.existsTagByName(name);
    }
}
