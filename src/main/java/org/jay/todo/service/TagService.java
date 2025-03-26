package org.jay.todo.service;

import lombok.RequiredArgsConstructor;
import org.jay.todo.entity.Tag;
import org.jay.todo.entity.Task;
import org.jay.todo.repository.TagRepository;
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
                                // Optional: Set default color or from client (e.g., newTag.setColor("#FFFFFF"))
                                return tagRepository.save(newTag);
                            }))
                    .collect(Collectors.toSet());
            task.setTags(tags);
        }
        return task;
    }
}
