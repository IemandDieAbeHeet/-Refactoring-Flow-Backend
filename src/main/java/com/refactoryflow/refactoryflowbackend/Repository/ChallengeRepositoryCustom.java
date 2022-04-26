package com.refactoryflow.refactoryflowbackend.Repository;

import com.refactoryflow.refactoryflowbackend.Model.Challenge;
import com.refactoryflow.refactoryflowbackend.Model.Student;

import java.util.List;
import java.util.Optional;

public interface ChallengeRepositoryCustom {
    List<Challenge> findChallengeBySubject(String subject);

    List<Challenge> findChallengeByStudents(Optional<Student> student);
}
