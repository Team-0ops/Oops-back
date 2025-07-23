package Oops.backend.domain.tag.service;

import Oops.backend.domain.tag.entity.Tag;
import Oops.backend.domain.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagCommandServiceImpl implements TagCommandService{

    private final TagRepository tagRepository;

    @Override
    @Transactional
    public LinkedHashSet<Tag> findOrCreateTagsByNames(List<String> tagNames) {

        // 존재하지 않은 태그들을 필터링 후 생성
        LinkedHashSet<Tag> newTags = tagNames.stream()
                .filter((tagName) -> !tagRepository.existsTagByName(tagName))
                .map(Tag::of)
                .map(tagRepository::save) //태그들 저장
                .collect(Collectors.toCollection(()->new LinkedHashSet<>()));

        LinkedHashSet<Tag> existingTag = tagNames.stream()
                .filter(tagRepository::existsTagByName)
                .map(tagRepository::findByName)
                .collect(Collectors.toCollection(()->new LinkedHashSet<>()));

        LinkedHashSet<Tag> tags = new LinkedHashSet<>();

        tags.addAll(newTags);
        tags.addAll(existingTag);

        return tags;
    }
}
