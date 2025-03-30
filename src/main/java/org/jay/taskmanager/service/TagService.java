package org.jay.taskmanager.service;

import lombok.RequiredArgsConstructor;
import org.jay.taskmanager.entity.Tag;
import org.jay.taskmanager.entity.Task;
import org.jay.taskmanager.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TagService {

    private final TagRepository tagRepository;

    public Task setTags(Task task, Set<String> tagNames) {
        if (tagNames != null && !tagNames.isEmpty()) {
            Set<Tag> tags = tagNames.stream()
                    .map(name -> tagRepository.findByName(name)
                            .orElseGet(() -> {
                                Tag newTag = new Tag();
                                newTag.setName(name);
                                newTag.setColor("#FFFFFF");
                                return tagRepository.save(newTag);
                            }))
                    .collect(Collectors.toSet());
            task.setTags(tags);
        }
        return task;
    }
}
