package edu.example.service;

import edu.example.exception.EntityNotFoundException;
import edu.example.model.Subject;
import edu.example.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public List<Subject> getSubjectsByCourse(int course) {
        return subjectRepository.findSubjectsByCourse(course);
    }

    public Subject getSubject(Long id) {
        return subjectRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Subject not found"));
    }

    public Subject createSubject(String name, Integer course) {
        var subject = new Subject();
        subject.setCourse(course);
        subject.setName(name);
        return subjectRepository.save(subject);
    }

    public Subject updateSubject(Long id, String name, Integer course) {
        var subject = new Subject();
        subject.setId(id);
        subject.setCourse(course);
        subject.setName(name);
        return subjectRepository.save(subject);
    }

    public void deleteSubject(Long id) {
        subjectRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Subject not found"));
        subjectRepository.deleteById(id);
    }
}